package com.flight.miss;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milton on 22/10/2016.
 */
public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ViewHolder> {

    private List<String> mMessages;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        protected CardView mCardView;
        protected TextView messageTextView;
        protected TextView timeStampTextView;

        public ViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            messageTextView = (TextView) v.findViewById(R.id.card_view_message_text_view);
            timeStampTextView = (TextView) v.findViewById(R.id.card_view_timestamp_text_view);
        }
    }

    public ChatBotAdapter(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            mMessages = new ArrayList<>();
        } else {
            mMessages = messages;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.messageTextView.setText(mMessages.get(position));
        holder.timeStampTextView.setText("Implement later");
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}