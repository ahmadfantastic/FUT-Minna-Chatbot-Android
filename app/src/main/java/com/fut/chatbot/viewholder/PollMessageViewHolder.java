package com.fut.chatbot.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fut.chatbot.R;
import com.fut.chatbot.adapter.PollItemAdapter;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Contributor;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.model.Poll;
import com.fut.chatbot.util.AppExecutors;
import com.fut.chatbot.util.CircleImageTransformation;
import com.fut.chatbot.util.Constants;
import com.squareup.picasso.Picasso;

public class PollMessageViewHolder extends RecyclerView.ViewHolder {

    private ImageView imgContributor;
    private TextView txtContributorName;
    private TextView txtPollTitle;
    private TextView txtPollBody;
    private TextView txtMessageTime;
    private ListView listViewItems;

    public PollMessageViewHolder(@NonNull View holder) {
        super(holder);

        imgContributor = holder.findViewById(R.id.img_contributor);
        txtContributorName = holder.findViewById(R.id.txt_contributor_name);
        txtPollTitle = holder.findViewById(R.id.txt_poll_title);
        txtPollBody = holder.findViewById(R.id.txt_poll_body);
        txtMessageTime = holder.findViewById(R.id.txt_message_time);
        listViewItems = holder.findViewById(R.id.list_view_items);
    }

    public void init(Context context, Chat chat, PollItemAdapter.PollItemClickListener listener){
        Message message = Constants.GSON.fromJson(chat.getData(), Message.class);
        Contributor contributor = message.getPoll().getContributor();
        Poll poll = message.getPoll();
        String imageUrl = Constants.IMAGE_BASE + contributor.getImageUrl();

        loadPicture(imageUrl);
        imgContributor.setOnClickListener(view -> loadPicture(imageUrl));

        txtContributorName.setText(contributor.getName());
        txtPollTitle.setText(poll.getTitle());
        txtPollBody.setText(poll.getBody());
        txtMessageTime.setText(Constants.TIME_FORMAT.format(chat.getTime()));

        PollItemAdapter pollItemAdapter = new PollItemAdapter(context, poll, listener);
        listViewItems.setAdapter(pollItemAdapter);
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
