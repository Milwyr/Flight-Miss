var restify = require('restify');
var builder = require('botbuilder');
var rest = require('rest');

//          static string format
//          YYYY-MM-DDTHH:MM:SSZ  2016-10-23T04:38:00Z

function getYear(date) {
    return date.substring(0,4);
}

function getMonth(date) {
    return date.substring(5,7);
}

function getDay(date) {
    return date.substring(9,11);
}

function getHour(date) {
    return date.substring(12,14);
}

function getMinute(date) {
    return date.substring(15,17);
}

function getRoundedDate(date) {
    return new Date(getYear(date), getMonth(date), getDay(date), getHour(date), getMinute(date))
}

function getDateString(date) {
    return date.getYear() + "-" + date.getMonth() + "-" + date.getDate() + "T" + 
        date.getHours() + ":" + date.getMinutes() + ":00Z";
}

function addOneHour(date) {
    var hour = parseInt(date.substring(12,13)) + 1;
    return "2015" + date.substring(4,12) + hour + date.substring(13);
}

function addOneDay(date) {
    var day = parseInt(date.substring(9,10)) + 2;
    return "2015" + date.substring(4,9) + day + date.substring(10);
}

function addOneYear(data) {
    var year = "2016";
    return year + data.substring(4);
}

function findNextNFlightsUrl(n, date) {
    var s = date;
    var url = "http://52.27.248.59/api/v0/schedule/records?limit=" + n;
    // var plusOneHour = new Date(2015, getMonth(s), getDay(s), getHour(s) + 1, getMinute(s));
    // var tomorrow = new Date(2015, getMonth(s), getDay(s) + 1, getHour(s), getMinute(s));
    url += "&legOriginAirport=HKG";
    url += "&legDestinationAirport=TPE";
    url += "&scheduledDepartureTimeMin=" + addOneHour(s);
    url += "&scheduledDepartureTimeMax=" + addOneDay(s) + "&sortAsc=scheduledDepartureTime";
    return url;
}

function bookingString(booking) {
    return String(booking.carrierCode) + " "
                + booking.flightNumber + " " 
                + addOneYear(booking.scheduledDepartureTime) + " "
                + addOneYear(booking.scheduledArrivalTime);
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
        session.privateConversationData.date = getDateString(new Date());
        builder.Prompts.confirm(session, 'Do you want to rebook your flight now?');
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
        rest(findNextNFlightsUrl(1,session.privateConversationData.date)).then(function(response) {
            var entity = JSON.parse(response.entity);
            if (entity.success) {
                session.privateConversationData.booking = entity.data[0];
                builder.Prompts.confirm(session, 'The next available flight will be departing at ' + 
                    addOneYear(entity.data[0].scheduledDepartureTime) + '. Is that ok?');    
            }
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
        rest(findNextNFlightsUrl(6,session.privateConversationData.date)).then(function(response) {
            var entity = JSON.parse(response.entity);
            if (entity.success) {
                var options = [];
                session.privateConversationData.bookingOptions = [];
                for (var i = 0; i < 6; i++) {
                    if (entity.data[i]) {
                        var str = bookingString(entity.data[i]);
                        options.push(str);
                        session.privateConversationData.bookingOptions.push(entity.data[i]);
                    }
                }
                builder.Prompts.choice(session, 'We have found the following options to replace your booking.',
                    options);    
            }
        });
        
    },
    function(session, results) {
        session.privateConversationData.booking = session.privateConversationData.bookingOptions[results.response.index];
        builder.Prompts.confirm(session, 'You have chosen the following flight: ' + results.response.entity + '. Would you like to book now?');
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
        session.send('Details for your booking: ' + bookingString(session.privateConversationData.booking) + '\nBoarding Pass can be viewed anytime in the Cathay app.');
        session.send('Apologies for the delay. Please enjoy the following voucher of 150HKD for any participating restaurants in the Hong Kong airport.');
        session.beginDialog('/navigate');
    }
]);

bot.dialog('/navigate', [
    function (session) {
        var ticksDept = Date.parse(addOneYear(session.privateConversationData.booking.scheduledDepartureTime));
        var ticksNow = new Date().getTime();
        var difHours = Math.round(Number((ticksDept - ticksNow) / (1000 * 60 * 60)));
        builder.Prompts.choice(session, 'Your boarding time is in ' + difHours + ' hours. Where would you like to go?',
        ['Gate', 'Eat']);
    },
    function (session, results) {
        if (results.response.entity === 'Gate') {
            session.send('Follow the escalators up to the 6th floor. Turn right and continue past the Zara until you see Gate 16 on your left.');
        } else {
            builder.Prompts.text(session, "What would you like to eat? Thai, Sushi, BBQ, something quick or local..");
        }
    },
    function(session, results) {
        if (results.response) {
            var rsp = results.response.toLowerCase();

            var msg = "I recommend the Macao Harbour Restaraunt nearby the 6th floor food court.";
            if (rsp.indexOf('sushi') !== -1 || rsp.indexOf('jap') !== -1) {
                msg = "ITAMAE Sushi is great! Head up to the 6th floor and turn left. Continue until you reach the South Concourse. The sushi place is across from Gate 1.";
            } else if (rsp.indexOf('thai') !== -1) {
                msg = "There's a good Thai place here on the 5th floor! Turn left and go past the Lacoste and Levi's stores. Thai Chiu will be on your right.";
            } else if (rsp.indexOf('korea') !== -1) {
                msg = "Keep going straight past the check-in counters and you'll find Sorabol Korean Cuisine on the left.";
            } else if (rsp.indexOf('viet') !== -1) {
                msg = "There is a Vietnamese restaraunt directly opposite the Check-in counters. Keep going straight and you can't miss it.";
            } else if (rsp.indexOf('chinese') !== -1 || rsp.indexOf('quick') !== -1) {
                msg = "If you want some fast Chinese take-out, head to the food court on the 6th floor and look for Pepper Lunch Express.";
            } else if (rsp.indexOf('local') !== -1 || rsp.indexOf('hk') !== -1 
                        || rsp.indexOf('hong kong') !== -1 || rsp.indexOf('tasty') !== -1
                        || rsp.indexOf('yummy') !== -1) {
                msg = "For some delicious Hong Kong style hotpot, go to King's Garden! You'll find it in the middle of the food court on the 6th floor.";
            } else if (rsp.indexOf('bbq') !== -1 || rsp.indexOf('barbeque') !== -1 || rsp.indexOf('meat') !== -1) {
                msg = "There's a small bbq spot in the back of the food court on the right. Head to the 6th floor to find it.";
            } else if (rsp.indexOf('sandwich') !== -1) {
                msg = "You can find a Subway on this floor. Head left until you see the Levi's shop. Take another left and you should see the Subway immediately.";
            } else if (rsp.indexOf('food court') !== -1 || rsp.indexOf('not sure') !== -1 
                        || rsp.indexOf('dunno') !== -1 || rsp.indexOf('anything') !== -1
                        || rsp.indexOf('you choose') !== -1) {
                msg = "Go take a look around the food court on the 6th floor. There are plenty of options. Follow the escalators on your right.";
            }

            session.send(msg);
        }
    }
]);
