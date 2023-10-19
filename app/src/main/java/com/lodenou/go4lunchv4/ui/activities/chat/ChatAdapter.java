package com.lodenou.go4lunchv4.ui.activities.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lodenou.go4lunchv4.R;
import com.lodenou.go4lunchv4.model.Message;
import com.lodenou.go4lunchv4.ui.Utils;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    // VIEW TYPES
    private List<Message> messages;
    private Context context;


    public ChatAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.context = context;
    }




    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Message message = messages.get(position);
        MessageViewHolder holderMessage = (MessageViewHolder) holder;
        // Update message
        holderMessage. textViewMessage.setText(message.getMessage());
        // Update date
        if (message.getDateCreated() != null) {
            holderMessage.textViewDate.setText(Utils.convertDateToHour(message.getDateCreated()));
        }


        // Update profile picture ImageView
        if (message.getUserSender().getUserAvatarUrl() != null)
            Glide.with(this.context)
                    .load(message.getUserSender()
                            .getUserAvatarUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holderMessage.imageViewProfile);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateMessages(List<Message> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }


    private static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewMessage;
        private final TextView textViewDate;
        private final ImageView imageViewProfile;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.message_text);
            textViewDate = itemView.findViewById(R.id.timestamp_message);
            imageViewProfile = itemView.findViewById(R.id.user_image);
        }
    }
}
