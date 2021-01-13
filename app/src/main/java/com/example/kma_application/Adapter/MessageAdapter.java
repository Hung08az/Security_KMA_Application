package com.example.kma_application.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kma_application.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int TYPE_MESSAGE_SENT = 0;
    private static final int TYPE_MESSAGE_RECEIVED = 1;
    private static final int TYPE_IMAGE_SENT = 2;
    private static final int TYPE_IMAGE_RECEIVED = 3;
    private String phone;

    private LayoutInflater inflater;
    private List<JSONObject> messages = new ArrayList<>();

    public MessageAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public MessageAdapter(String phone, LayoutInflater inflater) {
        this.phone = phone;
        this.inflater = inflater;
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView messageTxt;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);

            messageTxt = itemView.findViewById(R.id.sentTxt);
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public SentImageHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView  messageTxt;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);


            messageTxt = itemView.findViewById(R.id.receivedTxt);
        }
    }

    private class ReceivedImageHolder extends RecyclerView.ViewHolder {

        ImageView imageView;


        public ReceivedImageHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);


        }
    }

    @Override
    public int getItemViewType(int position) {

        JSONObject message = messages.get(position);

        try {
            if (message.getString("senderPhone").equals(phone)) {

                if (message.getString("type").equals("text"))
                    return TYPE_MESSAGE_SENT;
                else
                    return TYPE_IMAGE_SENT;

            } else {

                if (message.getString("type").equals("text"))
                    return TYPE_MESSAGE_RECEIVED;
                else
                    return TYPE_IMAGE_RECEIVED;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;



        switch (viewType) {
            case TYPE_MESSAGE_SENT:
                view = inflater.inflate(R.layout.item_sent_message, parent, false);
                return new SentMessageHolder(view);
            case TYPE_MESSAGE_RECEIVED:

                view = inflater.inflate(R.layout.item_received_message, parent, false);
                return new ReceivedMessageHolder(view);

            case TYPE_IMAGE_SENT:

                view = inflater.inflate(R.layout.item_sent_image, parent, false);
                return new SentImageHolder(view);

            case TYPE_IMAGE_RECEIVED:

                view = inflater.inflate(R.layout.item_received_photo, parent, false);
                return new ReceivedImageHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        JSONObject message = messages.get(position);

        try {
            if (message.getString("senderPhone").equals(phone)) {

                if (message.getString("type").equals("text")) {

                    SentMessageHolder messageHolder = (SentMessageHolder) holder;
                    messageHolder.messageTxt.setText(message.getString("content"));

                } else {

                    SentImageHolder imageHolder = (SentImageHolder) holder;
                    Bitmap bitmap = getBitmapFromString(message.getString("content"));

                    imageHolder.imageView.setImageBitmap(bitmap);

                }

            } else {

                if (message.getString("type").equals("text")) {

                    ReceivedMessageHolder messageHolder = (ReceivedMessageHolder) holder;

                    messageHolder.messageTxt.setText(message.getString("content"));

                } else {

                    ReceivedImageHolder imageHolder = (ReceivedImageHolder) holder;

                    Bitmap bitmap = getBitmapFromString(message.getString("content"));
                    imageHolder.imageView.setImageBitmap(bitmap);

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Bitmap getBitmapFromString(String image) {

        byte[] bytes = Base64.decode(image, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addItem (JSONObject jsonObject) {
        messages.add(jsonObject);
        notifyDataSetChanged();
    }

}
