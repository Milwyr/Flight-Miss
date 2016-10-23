package com.flight.miss;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.flight.miss.chatbotAPI.Chatbot;
import com.flight.miss.chatbotAPI.JsonObjects.Conversation;
import com.flight.miss.chatbotAPI.JsonObjects.Message;
import com.flight.miss.chatbotAPI.JsonObjects.Messages;
import com.flight.miss.chatbotAPI.JsonObjects.OptionParser;
import com.flight.miss.models.ChatBotMessage;
import com.flight.miss.models.FlightInfoMessage;
import com.flight.miss.models.FlightInfoRow;
import com.flight.miss.models.PlainTextMessage;
import com.flight.miss.models.QRCodeMessage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.joda.time.LocalTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //region Instance variables
    private RecyclerView mRecyclerView;
    private ChatBotAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText mMessageEditText;
    private ImageButton mSendButton;

    private Chatbot bot;

    private String conversationId = "";
    private String watermark;
    private Timer timer;

    private List<Message> messages = new ArrayList<>();
    private FlightInfoMessage mFlightInfoMessage;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseComponents();
        bot = new Chatbot();
        Call<Conversation> rsp = bot.api.startConversation();

        rsp.enqueue(new Callback<Conversation>() {
            @Override
            public void onResponse(Call<Conversation> call, Response<Conversation> response) {
                if (response.code() != 200) {
                    String s = response.message();
                }
                conversationId = response.body().conversationId;
                postMessage("init");
            }

            @Override
            public void onFailure(Call<Conversation> call, Throwable t) {
                addMessage(getResources().getString(R.string.error_message_bot_not_connected));
            }
        });

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pollMessages();
            }
        }, 1000, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_show_notification) {
            // Show notification
            Intent notifyIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder taskStackBuilder =
                    TaskStackBuilder.create(this).addNextIntent(notifyIntent);
            PendingIntent pendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = new Notification.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Title")
                    .setContentText("Content")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());

            return false;
        } else if (item.getItemId() == R.id.action_boarding_pass) {
            try {
                // TODO: Change title dynamically
                Bitmap qrCode = generateQRCode("123sfsfssdfsgsdfbhfdjffjdgdfsfsfsg");
                mAdapter.add(new QRCodeMessage(qrCode, mFlightInfoMessage, true));

                // Scroll to the bottom every time when send button is clicked
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initialiseComponents() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        List<ChatBotMessage> tempList = new ArrayList<>();
        List<FlightInfoRow> rows = new ArrayList<>();
        rows.add(new FlightInfoRow("ABC", "123", LocalTime.now(), LocalTime.now()));
        rows.add(new FlightInfoRow("DEF", "456", LocalTime.now(), LocalTime.now()));
        rows.add(new FlightInfoRow("GHI", "945", LocalTime.now(), LocalTime.now()));

        tempList.add(new PlainTextMessage("I am going home now!", false));
        mFlightInfoMessage = new FlightInfoMessage("Cathay", rows, false);
        tempList.add(mFlightInfoMessage);
        tempList.add(new PlainTextMessage("I am going home now!", false));
        tempList.add(new PlainTextMessage("Just arrived!", false));

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Specify an adapter (see also next example)
        mAdapter = new ChatBotAdapter(this, tempList);
        mRecyclerView.setAdapter(mAdapter);

        mMessageEditText = (EditText) findViewById(R.id.message_edit_text);
        mSendButton = (ImageButton) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                String msg = mMessageEditText.getText().toString();
                mAdapter.add(new PlainTextMessage(msg, true));
                postMessage(msg);

                // Reset text view to be empty
                mMessageEditText.setText("");

                // Scroll to the bottom every time when send button is clicked
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                break;
        }
    }

    private void postMessage(final String msg) {
        Message message = new Message(msg);
        message.from = "testClient";
        Call<ResponseBody> rsp = bot.api.sendMessage(conversationId, message);
        rsp.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 204) {
                    // message sent. begin polling
                    //addMessage(msg);
                    new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pollMessages();
                        }
                    }, 300);
                } else {
                    String s = response.message();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                addMessage(getResources().getString(R.string.error_message_bot_not_connected));
            }
        });
    }

    private void pollMessages() {
        Call<Messages> rsp;
        if (watermark == null) {
            rsp = bot.api.getUnseenMessages(conversationId);
        } else {
            rsp = bot.api.getUnseenMessages(conversationId, watermark);
        }
        rsp.enqueue(new Callback<Messages>() {
            @Override
            public void onResponse(Call<Messages> call, Response<Messages> response) {
                if (response.code() == 200) {
                    String s = response.code() + " ";
                    if (response.body() != null && response.body().watermark != null) {
                        watermark = response.body().watermark;
                        for (Message m : response.body().messages) {
                            if (m.from.equals("cathaymissedflightbot")) {
                                OptionParser op = new OptionParser(m.text);
                                if (op.hasOptions) {
                                    String msg = op.message;
                                    Log.i("OPTIONS", op.message + " " + Arrays.toString(op.options));
                                }
                                addMessage(m.text);
                                messages.add(m);
                            }
                        }
                        s += watermark + " " + response.body().messages.length + " total messages.";
                    }
                    Log.i("POLLING", s);
                }
            }

            @Override
            public void onFailure(Call<Messages> call, Throwable t) {
                addMessage(getResources().getString(R.string.error_message_bot_not_connected));
            }
        });

    }

    private Bitmap generateQRCode(String inputData) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(inputData, BarcodeFormat.QR_CODE, 600, 600);
        Bitmap bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888);

        // Fill black and white dots in the QR code
        for (int i = 0; i < 600; i++) {//width
            for (int j = 0; j < 600; j++) {//height
                bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }

    private void addMessage(String s) {
        mAdapter.add(new PlainTextMessage(s, false));
    }
}