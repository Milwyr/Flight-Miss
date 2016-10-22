var restify = require('restify');
var builder = require('botbuilder');
var rest = require('rest');

function createUrl() {
    var url = "http://52.27.248.59/api/v0/schedule/records?limit=6";
    url += "&scheduledDepartureTimeMin=2015-07-01T00:00:00Z&scheduledDepartureTimeMax=2015-09-01T00:00:00Z&sortAsc=scheduledDepartureTime";
}

// Setup Restify Server
var server = restify.createServer();
server.listen(process.env.port || process.env.PORT || 3978, function () {
   console.log('%s listening to %s', server.name, server.url); 
});

// Create bot and bind to console
var connector = new builder.ChatConnector({
    appId: process.env.MICROSOFT_APP_ID,
    appPassword: process.env.MICROSOFT_APP_PASSWORD
});
//var connector = new builder.ConsoleConnector().listen();
var bot = new builder.UniversalBot(connector);
server.post('/api/messages', connector.listen());

// Create LUIS recognizer that points at our model and add it as the root '/' dialog for our Cortana Bot.
var model = 'https://api.projectoxford.ai/luis/v1/application/preview?id=84d91a57-74ca-498e-bc58-586d8481c7de&subscription-key=c7b0f456efb24e289e1444bcf62d0a2e';
var recognizer = new builder.LuisRecognizer(model);
var dialog = new builder.IntentDialog({ recognizers: [recognizer] });

// Add intent handlers
dialog.onDefault(builder.DialogAction.send("Please contact a Customer Service Representative for more information."));
bot.dialog('/', [
    function (session) {
        builder.Prompts.confirm(session, 'Do you need help rebooking your flight?');
    },
    function (session, results) {
        if (results.response) {
            session.beginDialog('/suggestFlight');
        } else {
            session.beginDialog('/navigate');
        }
    },
    function(session) {
        session.beginDialog('/navigate');
    }
]);
bot.dialog('/suggestFlight', [
    function(session) {
        rest(createUrl()).then(function(response) {
            builder.Prompts.confirm(session, 'The next available flight will be departing at ' + 
            response.data[0].scheduledDepartureTime + '. Is that ok?');    
        });
    },
    function(session, results, next) {
        if (results.response) {
            session.beginDialog('/confirmBooking');
        } else {
            session.beginDialog('/suggestFlight/bookingOptions');
        }
    }
]);

bot.dialog('/suggestFlight/bookingOptions', [
    function(session) {
        builder.Prompts.choice(session, 'We have found the following options to replace your booking.',
        ['Today', 'Tomorrow morning', 'Tomorrow evening']);
    },
    function(session, results) {
        builder.Prompts.confirm(session, 'You have chosen the flight departing ' + results.response.entity + '. Would you like to book now?');
    },
    function(session, results) {
        if (results.response) {
            session.beginDialog('/confirmBooking');
        } else {
            session.beginDialog('/suggestFlight/bookingOptions');
        }
    }

]);

bot.dialog('/confirmBooking', [
    function(session) {
        session.send('Details for your booking:\nBoarding Pass can be viewed anytime in the Cathay app.');
        session.beginDialog('/navigate');
    }
]);

bot.dialog('/navigate', [
    function (session) {
        builder.Prompts.choice(session, 'You have x hours until your flight boards. Where would you like to go?',
        ['Gate', 'Eat']);
    },
    function (session, results) {
        if (results.response.entity === 'Gate') {
            session.send('Head towards Gate 15 and turn right at the Starbucks. Gate 40 will be on your right.');
        } else {
            session.send('Head towards Gate 12 to view nearby places to eat.');
        }
    }
]);
