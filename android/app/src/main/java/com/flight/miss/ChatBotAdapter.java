package com.flight.miss;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milton on 22/10/2016.
 */
public class ChatBotAdapter extends RecyclerView.Adapter<ChatBotAdapter.ViewHolder> {

    private List<ChatBotMessage> mMessages;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        protected RelativeLayout relativeLayout;
        protected CardView mCardView;
        protected TextView messageTextView;
        protected TextView timeStampTextView;

        public ViewHolder(View v) {
            super(v);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.card_view_relative_layout);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            messageTextView = (TextView) v.findViewById(R.id.card_view_message_text_view);
            timeStampTextView = (TextView) v.findViewById(R.id.card_view_timestamp_text_view);
        }
    }

    public ChatBotAdapter(List<ChatBotMessage> messages) {
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
        ChatBotMessage message = mMessages.get(position);

        // Messages from server should align to the left, while messages from device to the right
        if (!message.getIsSentFromDevice()) {
            // Align both text views to the left
            RelativeLayout.LayoutParams relativeLayoutParams =
                    (RelativeLayout.LayoutParams) holder.relativeLayout.getLayoutParams();
            relativeLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        }

        holder.messageTextView.setText(mMessages.get(position).getText());

        // Display the time now
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
        holder.timeStampTextView.setText(formatter.print(LocalTime.now()));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void add(ChatBotMessage message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size());
    }
}