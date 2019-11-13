package com.fut.chatbot.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.fut.chatbot.R;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Extra;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.util.Constants;

import java.util.List;

public class AnswerMessageWithExtraViewHolder extends RecyclerView.ViewHolder {

    private View viewBotAvatar;
    private TextView textBotName;
    private TextView txtMessageBody;
    private TextView txtMessageTime;

    private ImageButton imgBtnExtra1;
    private ImageButton imgBtnExtra2;
    private ImageButton imgBtnExtra3;

    public AnswerMessageWithExtraViewHolder(@NonNull View holder) {
        super(holder);

        viewBotAvatar = holder.findViewById(R.id.img_bot);
        textBotName = holder.findViewById(R.id.txt_bot_name);
        txtMessageBody = holder.findViewById(R.id.txt_message_body);
        txtMessageTime = holder.findViewById(R.id.txt_message_time);

        imgBtnExtra1 = holder.findViewById(R.id.img_btn_extra_1);
        imgBtnExtra2 = holder.findViewById(R.id.img_btn_extra_2);
        imgBtnExtra3 = holder.findViewById(R.id.img_btn_extra_3);
    }

    public void init(Context context, Chat chat, ClickListener listener){
        Message message = Constants.GSON.fromJson(chat.getData(), Message.class);

        txtMessageBody.setText(message.getAnswer().getBody());
        txtMessageTime.setText(Constants.TIME_FORMAT.format(chat.getTime()));

        imgBtnExtra1.setVisibility(View.GONE);
        imgBtnExtra2.setVisibility(View.GONE);
        imgBtnExtra3.setVisibility(View.GONE);

        List<Extra> extras = message.getAnswer().getExtras();

        if(extras.size() > 0) {
            Extra extra1 = extras.get(0);
            imgBtnExtra1.setImageDrawable(AppCompatResources.getDrawable(context, getExtraDrawableId(extra1.getType())));
            imgBtnExtra1.setOnClickListener(view -> listener.onExtraClicked(extra1));
            imgBtnExtra1.setVisibility(View.VISIBLE);
        }

        if(extras.size() > 1){
            Extra extra2 = extras.get(1);
            imgBtnExtra2.setImageDrawable(AppCompatResources.getDrawable(context, getExtraDrawableId(extra2.getType())));
            imgBtnExtra2.setOnClickListener(view -> listener.onExtraClicked(extra2));
            imgBtnExtra2.setVisibility(View.VISIBLE);
        }

        if(extras.size() > 2){
            Extra extra3 = extras.get(2);
            imgBtnExtra3.setImageDrawable(AppCompatResources.getDrawable(context, getExtraDrawableId(extra3.getType())));
            imgBtnExtra3.setOnClickListener(view -> listener.onExtraClicked(extra3));
            imgBtnExtra3.setVisibility(View.VISIBLE);
        }
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

    public interface ClickListener{
        void onExtraClicked(Extra extra);
    }

    private int getExtraDrawableId(Extra.ExtraType type){
        switch (type){
            case LINK:
                return R.drawable.link;
            case AUDIO:
                return R.drawable.audio;
            case VIDEO:
                return R.drawable.video;
            case LOCATION:
                return R.drawable.location;
            case PHONE:
                return R.drawable.phone;
            case WHATSAPP:
                return R.drawable.whatsapp;
            case EMAIL:
                return R.drawable.email;
            default:
                return R.drawable.fut_chatbot;
        }
    }

}
