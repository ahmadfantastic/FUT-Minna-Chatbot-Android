package com.fut.chatbot.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.fut.chatbot.R;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Extra;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AnswerMessageWithImageAndExtraViewHolder extends RecyclerView.ViewHolder {

    private View viewBotAvatar;
    private TextView textBotName;
    private TextView txtMessageBody;
    private TextView txtMessageTime;
    private ImageView imgExtra;

    private ImageButton imgBtnExtra1;
    private ImageButton imgBtnExtra2;

    public AnswerMessageWithImageAndExtraViewHolder(@NonNull View holder) {
        super(holder);

        viewBotAvatar = holder.findViewById(R.id.img_bot);
        textBotName = holder.findViewById(R.id.txt_bot_name);
        txtMessageBody = holder.findViewById(R.id.txt_message_body);
        txtMessageTime = holder.findViewById(R.id.txt_message_time);
        imgExtra = holder.findViewById(R.id.img_extra);

        imgBtnExtra1 = holder.findViewById(R.id.img_btn_extra_1);
        imgBtnExtra2 = holder.findViewById(R.id.img_btn_extra_2);
    }

    public void init(Context context, Chat chat, ClickListener listener){
        Message message = Constants.GSON.fromJson(chat.getData(), Message.class);

        txtMessageBody.setText(message.getAnswer().getBody());
        txtMessageTime.setText(Constants.TIME_FORMAT.format(chat.getTime()));imgBtnExtra1.setVisibility(View.GONE);

        imgBtnExtra1.setVisibility(View.GONE);
        imgBtnExtra2.setVisibility(View.GONE);

        List<Extra> extras = new ArrayList<>();
        for(Extra extra: message.getAnswer().getExtras()){
            if(extra.getType() == Extra.ExtraType.IMAGE){
                loadPicture(extra.getData());
                imgExtra.setOnClickListener(view -> loadPicture(extra.getData()));
            }else{
                extras.add(extra);
            }
        }

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
