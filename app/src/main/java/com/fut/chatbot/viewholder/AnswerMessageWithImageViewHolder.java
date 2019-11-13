package com.fut.chatbot.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fut.chatbot.R;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Extra;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.util.Constants;
import com.squareup.picasso.Picasso;

public class AnswerMessageWithImageViewHolder extends RecyclerView.ViewHolder {

    private View viewBotAvatar;
    private TextView textBotName;
    private TextView txtMessageBody;
    private TextView txtMessageTime;
    private ImageView imgExtra;

    public AnswerMessageWithImageViewHolder(@NonNull View holder) {
        super(holder);

        viewBotAvatar = holder.findViewById(R.id.img_bot);
        textBotName = holder.findViewById(R.id.txt_bot_name);
        txtMessageBody = holder.findViewById(R.id.txt_message_body);
        txtMessageTime = holder.findViewById(R.id.txt_message_time);
        imgExtra = holder.findViewById(R.id.img_extra);
    }

    public void init(Chat chat){
        Message message = Constants.GSON.fromJson(chat.getData(), Message.class);
        Extra extra = message.getAnswer().getExtras().get(0);

        loadPicture(extra.getData());
        imgExtra.setOnClickListener(view -> loadPicture(extra.getData()));

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

    private void loadPicture(String imageUrl){
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.loader_animator)
                .error(R.drawable.fut_chatbot)
                .into(imgExtra);
    }
}
