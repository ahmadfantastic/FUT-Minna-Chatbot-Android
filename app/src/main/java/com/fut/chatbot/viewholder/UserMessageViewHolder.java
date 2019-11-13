package com.fut.chatbot.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.fut.chatbot.R;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Ask;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.util.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserMessageViewHolder extends RecyclerView.ViewHolder {

    private TextView txtMessageBody;
    private TextView txtMessageTime;
    private ImageView imgMessageStatus;

    public UserMessageViewHolder(@NonNull View holder) {
        super(holder);

        txtMessageBody = holder.findViewById(R.id.txt_message_body);
        txtMessageTime = holder.findViewById(R.id.txt_message_time);
        imgMessageStatus = holder.findViewById(R.id.img_message_status);
    }

    public void init(Context context, Chat chat){
        Ask ask = Constants.GSON.fromJson(chat.getData(), Ask.class);
        DateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

        txtMessageBody.setText(ask.getText());
        txtMessageTime.setText(timeFormat.format(chat.getTime()));
        if(chat.isSent()) {
            imgMessageStatus.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_sent));
        }else{
            imgMessageStatus.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_waiting));
        }
    }

}
