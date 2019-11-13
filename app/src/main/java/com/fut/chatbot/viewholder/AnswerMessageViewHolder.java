package com.fut.chatbot.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fut.chatbot.R;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.util.Constants;

public class AnswerMessageViewHolder extends RecyclerView.ViewHolder {

    private View viewBotAvatar;
    private TextView textBotName;
    private TextView txtMessageBody;
    private TextView txtMessageTime;

    public AnswerMessageViewHolder(@NonNull View holder) {
        super(holder);

        viewBotAvatar = holder.findViewById(R.id.img_bot);
        textBotName = holder.findViewById(R.id.txt_bot_name);
        txtMessageBody = holder.findViewById(R.id.txt_message_body);
        txtMessageTime = holder.findViewById(R.id.txt_message_time);
    }

    public void init(Chat chat){
        Message message = Constants.GSON.fromJson(chat.getData(), Message.class);

        txtMessageBody.setText(message.getAnswer().getBody());
        txtMessageTime.setText(Constants.TIME_FORMAT.format(chat.getTime()));
    }

    public void setContinuous(boolean isContinuous){
        if(isContinuous){
            viewBotAvatar.setVisibility(View.INVISIBLE);
            textBotName.setVisibility(View.GONE);
        }else{
            viewBotAvatar.setVisibility(View.VISIBLE);
            textBotName.setVisibility(View.VISIBLE);
        }
    }
}
