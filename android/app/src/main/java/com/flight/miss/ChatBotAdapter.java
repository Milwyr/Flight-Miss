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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flight.miss.models.ChatBotMessage;
import com.flight.miss.models.FlightInfoMessage;
import com.flight.miss.models.FlightInfoRow;
import com.flight.miss.models.PlainTextMessage;
import com.flight.miss.models.QRCodeMessage;

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
    private final int QR_CODE = 2;

    private Context mContext;
    private List<ChatBotMessage> mMessages;

    // A view holder that contains a view of plain text bot message
    private static class PlainTextViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout relativeLayout;
        private TextView messageTextView;
        private TextView timeStampTextView;

        PlainTextViewHolder(View v) {
            super(v);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.card_view_relative_layout);
            messageTextView = (TextView) v.findViewById(R.id.card_view_message_text_view);
            timeStampTextView = (TextView) v.findViewById(R.id.card_view_timestamp_text_view);
        }
    }

    // A view holder that contains a flight info bot message
    private static class FlightInfoViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private RecyclerView recyclerView;
        private TextView timeStampTextView;

        FlightInfoViewHolder(Context context, View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.flight_card_title);

            // Use a linear layout manager
            recyclerView = (RecyclerView) v.findViewById(R.id.flight_card_flight_table);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            timeStampTextView = (TextView) v.findViewById(R.id.flight_info_timestamp_text_view);

            // Display the time now
            DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
            timeStampTextView.setText(formatter.print(LocalTime.now()));
        }
    }

    // A view holder that contains a QR code bot message
    private static class QRCodeViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private ImageView imageView;
        private RecyclerView recyclerView;
        private View flightInfoTableView;
        private TextView timeStampTextView;

        QRCodeViewHolder(Context context, View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.qr_code_image_view);
            titleTextView = (TextView) v.findViewById(R.id.qr_code_text_view);

            // Use a linear layout manager
            recyclerView = (RecyclerView) v.findViewById(R.id.flight_card_flight_table);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            flightInfoTableView = v.findViewById(R.id.qr_code_flight_info_table);

            timeStampTextView = (TextView) v.findViewById(R.id.flight_info_timestamp_text_view);

            // Display the time now
            DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
            timeStampTextView.setText(formatter.print(LocalTime.now()));
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
        } else if (viewType == FLIGHT_INFO) {
            View flighInfoView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.flight_card_layout, parent, false);
            return new FlightInfoViewHolder(mContext, flighInfoView);
        } else {
            View qrCodeView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_image_layout, parent, false);
            return new QRCodeViewHolder(mContext, qrCodeView);
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
                    mContext, flightInfoMessage.getFlightInfoRows());
            fvh.recyclerView.setAdapter(adapter);
        } else if (holder instanceof QRCodeViewHolder) {
            QRCodeViewHolder qvh = (QRCodeViewHolder) holder;
            QRCodeMessage qrCodeMessage = (QRCodeMessage) mMessages.get(position);

            qvh.titleTextView.setText(qrCodeMessage.getFlightInfoMessage().getTitle());
            qvh.imageView.setImageBitmap(qrCodeMessage.getBitmap());

            if (qrCodeMessage.getFlightInfoMessage().getFlightInfoRows() != null &&
                    qrCodeMessage.getFlightInfoMessage().getFlightInfoRows().size() > 0) {
                FlightInfoAdapter adapter = new FlightInfoAdapter(
                        mContext, qrCodeMessage.getFlightInfoMessage().getFlightInfoRows());
                qvh.flightInfoTableView.setVisibility(View.VISIBLE);
                qvh.recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatBotMessage chatBotMessage = mMessages.get(position);
        if (chatBotMessage instanceof PlainTextMessage) {
            return PLAIN_TEXT;
        } else if (chatBotMessage instanceof FlightInfoMessage) {
            return FLIGHT_INFO;
        } else {
            return QR_CODE;
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

            // Change text colour to be grey
            int grey = ResourcesCompat.getColor(mContext.getResources(), R.color.grey, null);
            pH.timeStampTextView.setTextColor(grey);
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
    private Context mContext;

    private List<FlightInfoRow> flightInfoRows;

    // The view holder that holds the textviews in four columns
    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout rootLayout;
        private TextView companyTextView;
        private TextView flightTextView;
        private TextView departureTextView;
        private TextView arrivalTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootLayout = (LinearLayout) itemView.findViewById(R.id.flight_card_flight_info_table_layout);
            companyTextView = (TextView) itemView.findViewById(R.id.flight_card_recycler_view_column_company);
            flightTextView = (TextView) itemView.findViewById(R.id.flight_card_recycler_view_column_flight);
            departureTextView = (TextView) itemView.findViewById(R.id.flight_card_recycler_view_column_departure);
            arrivalTextView = (TextView) itemView.findViewById(R.id.flight_card_recycler_view_column_arrival);
        }
    }

    FlightInfoAdapter(Context context, List<FlightInfoRow> flightInfoRows) {
        mContext = context;
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

        final int temp = position;
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Row " + temp, Toast.LENGTH_LONG).show();
            }
        });

        holder.companyTextView.setText(row.getCompany());
        holder.flightTextView.setText(row.getFlightNumber());

        // Display departure and arrival time

        holder.departureTextView.setText(row.getDepartureTime());
        holder.arrivalTextView.setText(row.getArrivalTime());
    }

    @Override
    public int getItemCount() {
        return this.flightInfoRows.size();
    }
}