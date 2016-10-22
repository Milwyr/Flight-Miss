package com.flight.miss;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flight.miss.models.ChatBotMessage;
import com.flight.miss.models.FlightInfoMessage;
import com.flight.miss.models.FlightInfoRow;
import com.flight.miss.models.PlainTextMessage;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is created for a chat bot text messages.
 */
public class ChatBotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int PLAIN_TEXT = 0;
    private final int FLIGHT_INFO = 1;

    private Context mContext;
    private List<ChatBotMessage> mMessages;

    // A view holder that contains a view of plain text bot message
    private static class PlainTextViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;
        private CardView mCardView;
        private TextView messageTextView;
        private TextView timeStampTextView;

        PlainTextViewHolder(View v) {
            super(v);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.card_view_relative_layout);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            messageTextView = (TextView) v.findViewById(R.id.card_view_message_text_view);
            timeStampTextView = (TextView) v.findViewById(R.id.card_view_timestamp_text_view);
        }
    }

    // A view holder that contains a flight info bot message
    private static class FlightInfoViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private ImageView leftImageView;
        private ImageView rightImageView;
        private TextView leftImageDescriptionTextView;
        private TextView rightImageDescriptionTextView;
        private RecyclerView recyclerView;

        FlightInfoViewHolder(Context context, View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.flight_card_title);
            leftImageView = (ImageView) v.findViewById(R.id.flight_card_left_image);
            rightImageView = (ImageView) v.findViewById(R.id.flight_card_right_image);
            leftImageDescriptionTextView = (TextView) v.findViewById(R.id.flight_card_left_image_description);
            rightImageDescriptionTextView = (TextView) v.findViewById(R.id.flight_card_right_image_description);

            // Use a linear layout manager
            recyclerView = (RecyclerView) v.findViewById(R.id.flight_card_flight_table);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

//            // Specify an adapter (see also next example)
//            FlightInfoAdapter adapter = new FlightInfoAdapter(context, R.layout.flight_card_recycler_view_layout);
//            recyclerView.setAdapter(adapter);
        }
    }

    public ChatBotAdapter(Context context, List<ChatBotMessage> messages) {
        mContext = context;

        if (messages == null || messages.isEmpty()) {
            mMessages = new ArrayList<>();
        } else {
            mMessages = messages;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PLAIN_TEXT) {
            View plainTextView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_layout, parent, false);
            return new PlainTextViewHolder(plainTextView);
        } else {
            View flighInfoView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.flight_card_layout, parent, false);
            return new FlightInfoViewHolder(mContext, flighInfoView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PlainTextViewHolder) {
            processPlainText(holder, position);
        } else if (holder instanceof FlightInfoViewHolder) {
            // Specify an adapter (see also next example)
            FlightInfoViewHolder fvh = (FlightInfoViewHolder) holder;
            FlightInfoMessage flightInfoMessage = (FlightInfoMessage) mMessages.get(position);
            FlightInfoAdapter adapter = new FlightInfoAdapter(
                    R.layout.flight_card_recycler_view_layout, flightInfoMessage.getFlightInfoRows());
            fvh.recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mMessages.get(position) instanceof PlainTextMessage) {
            return PLAIN_TEXT;
        } else {
            return FLIGHT_INFO;
        }
    }

    private void processPlainText(RecyclerView.ViewHolder holder, int position) {
        PlainTextViewHolder pH = (PlainTextViewHolder) holder;
        PlainTextMessage message = (PlainTextMessage) mMessages.get(position);

        // Messages from server should align to the left, while messages from device to the right
        if (!message.getIsSentFromDevice()) {
            // Align both text views to the left
            RelativeLayout.LayoutParams relativeLayoutParams =
                    (RelativeLayout.LayoutParams) pH.relativeLayout.getLayoutParams();
            relativeLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);

            // Change background colour to be white
            int white = ResourcesCompat.getColor(mContext.getResources(), R.color.white, null);
            pH.relativeLayout.setBackgroundColor(white);

            // Change text colour to be black
            int black = ResourcesCompat.getColor(mContext.getResources(), R.color.black, null);
            pH.messageTextView.setTextColor(black);
            pH.timeStampTextView.setTextColor(black);
        }

        pH.messageTextView.setText(message.getText());

        // Display the time now
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
        pH.timeStampTextView.setText(formatter.print(LocalTime.now()));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void add(ChatBotMessage message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size());
    }

    public void add(List<ChatBotMessage> messages) {
        int startPosition = mMessages.size();
        mMessages.addAll(messages);
        notifyItemRangeInserted(startPosition, messages.size());
    }
}

class FlightInfoAdapter extends RecyclerView.Adapter<FlightInfoAdapter.ViewHolder> {

    // The resource id of flight info table
    private int resource;

    private List<FlightInfoRow> flightInfoRows;

    // The view holder that holds the textviews in four columns
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView companyTextView;
        private TextView flightTextView;
        private TextView departureTextView;
        private TextView arrivalTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            companyTextView = (TextView) itemView.findViewById(R.id.flight_card_recycler_view_column_company);
            flightTextView = (TextView) itemView.findViewById(R.id.flight_card_recycler_view_column_flight);
            departureTextView = (TextView) itemView.findViewById(R.id.flight_card_recycler_view_column_departure);
            arrivalTextView = (TextView) itemView.findViewById(R.id.flight_card_recycler_view_column_arrival);
        }
    }

    FlightInfoAdapter(int resource, List<FlightInfoRow> flightInfoRows) {
        this.resource = resource;
        this.flightInfoRows = flightInfoRows;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.flight_card_recycler_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FlightInfoRow row = this.flightInfoRows.get(position);

        holder.companyTextView.setText(row.getCompany());
        holder.flightTextView.setText(row.getFlightNumber());
        holder.departureTextView.setText("11:11");
        holder.arrivalTextView.setText("11:11");
    }

    @Override
    public int getItemCount() {
        return this.flightInfoRows.size();
    }
}