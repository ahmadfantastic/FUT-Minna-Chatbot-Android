package com.fut.chatbot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.fut.chatbot.R;
import com.fut.chatbot.model.Poll;
import com.fut.chatbot.model.PollItem;

public class PollItemAdapter extends ArrayAdapter<PollItem> {

    private Poll poll;
    private Context mContext;
    private PollItemClickListener listener;

    private boolean clickEnabled = true;

    public PollItemAdapter(Context context, Poll poll, PollItemClickListener listener) {
        super(context, R.layout.holder_poll_item, poll.getItems());

        this.poll = poll;
        this.mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PollItem pollItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.holder_poll_item, parent, false);
        }

        if (pollItem != null) {
            LinearLayout linearLayout = convertView.findViewById(R.id.view_item);
            ImageView imageView = convertView.findViewById(R.id.img_voted);
            TextView textView = convertView.findViewById(R.id.txt_poll_item_name);

            textView.setText(pollItem.getName());
            if (pollItem.equals(poll.getVoted())) {
                clickEnabled = false;
                linearLayout.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.text_background_selected));
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.ic_vote));
            } else{
                linearLayout.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.text_background_unselected));
                imageView.setVisibility(View.INVISIBLE);
            }
            convertView.setOnClickListener(view -> {
                if(clickEnabled){
                    clickEnabled = false;
                    imageView.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.loader_animator));
                    imageView.setVisibility(View.VISIBLE);
                    listener.onVote(position);
                }
            });
        }
        return convertView;
    }
    public interface PollItemClickListener {
        void onVote(int position);
    }
}