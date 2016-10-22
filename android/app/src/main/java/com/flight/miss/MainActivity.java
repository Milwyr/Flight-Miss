package com.flight.miss;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;
    private ChatBotAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText mMessageEditText;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseComponents();
    }

    private void initialiseComponents() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        List<ChatBotMessage> tempList = new ArrayList<>();
        tempList.add(new ChatBotMessage("I am going home now!", false));
        tempList.add(new ChatBotMessage("Just arrived!", false));
        tempList.add(new ChatBotMessage("Let's have dinner!", false));

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Specify an adapter (see also next example)
        mAdapter = new ChatBotAdapter(tempList);
        mRecyclerView.setAdapter(mAdapter);

        mMessageEditText = (EditText) findViewById(R.id.message_edit_text);
        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                mAdapter.add(new ChatBotMessage("New message", true));
                break;
        }
    }
}