package com.flight.miss;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.flight.miss.models.ChatBotMessage;
import com.flight.miss.models.FlightInfoMessage;
import com.flight.miss.models.FlightInfoRow;
import com.flight.miss.models.PlainTextMessage;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ChatBotAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText mMessageEditText;
    private ImageButton mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseComponents();
    }

    private void initialiseComponents() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        List<ChatBotMessage> tempList = new ArrayList<>();
        List<FlightInfoRow> rows = new ArrayList<>();
        rows.add(new FlightInfoRow("F123456", "123", LocalTime.now(), LocalTime.now()));
        rows.add(new FlightInfoRow("946sff", "456", LocalTime.now(), LocalTime.now()));
        rows.add(new FlightInfoRow("sfsggg", "945", LocalTime.now(), LocalTime.now()));

        tempList.add(new PlainTextMessage("I am going home now!", false));
        tempList.add(new FlightInfoMessage("Cathay", new int[]{1, 2}, rows, false));
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
                mAdapter.add(new PlainTextMessage("New message", true));
                break;
        }
    }
}