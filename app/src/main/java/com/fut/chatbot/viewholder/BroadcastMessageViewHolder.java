package com.fut.chatbot.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fut.chatbot.R;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Contributor;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.util.CircleImageTransformation;
import com.fut.chatbot.util.Constants;
import com.squareup.picasso.Picasso;

public class BroadcastMessageViewHolder extends RecyclerView.ViewHolder {

    private ImageView imgContributor;
    private TextView txtContributorName;
    private TextView txtMessageBody;
    private TextView txtMessageTime;

    public BroadcastMessageViewHolder(@NonNull View holder) {
        super(holder);

        imgContributor = holder.findViewById(R.id.img_contributor);
        txtContributorName = holder.findViewById(R.id.txt_contributor_name);
        txtMessageBody = holder.findViewById(R.id.txt_message_body);
        txtMessageTime = holder.findViewById(R.id.txt_message_time);
    }

    public void init(Chat chat){
        Message message = Constants.GSON.fromJson(chat.getData(), Message.class);
        Contributor contributor = message.getBroadcast().getContributor();
        String imageUrl = Constants.IMAGE_BASE + contributor.getImageUrl();

        loadPicture(imageUrl);
        imgContributor.setOnClickListener(view -> loadPicture(imageUrl));

        txtContributorName.setText(contributor.getName());
        txtMessageBody.setText(message.getBroadcast().getBody());
        txtMessageTime.setText(Constants.TIME_FORMAT.format(chat.getTime()));
    }

    private void loadPicture(String imageUrl){
        Picasso.get()
                .load(imageUrl)
                .transform(new CircleImageTransformation())
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_error_image)
                .into(imgContributor);
    }

}
