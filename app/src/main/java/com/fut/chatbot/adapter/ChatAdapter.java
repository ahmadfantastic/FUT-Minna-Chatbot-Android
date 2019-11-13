package com.fut.chatbot.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fut.chatbot.R;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.model.Extra;
import com.fut.chatbot.model.Message;
import com.fut.chatbot.util.Constants;
import com.fut.chatbot.viewholder.AnswerMessageViewHolder;
import com.fut.chatbot.viewholder.AnswerMessageWithExtraViewHolder;
import com.fut.chatbot.viewholder.AnswerMessageWithImageAndExtraViewHolder;
import com.fut.chatbot.viewholder.AnswerMessageWithImageViewHolder;
import com.fut.chatbot.viewholder.BroadcastMessageViewHolder;
import com.fut.chatbot.viewholder.DateMessageViewHolder;
import com.fut.chatbot.viewholder.NoneViewHolder;
import com.fut.chatbot.viewholder.PollMessageViewHolder;
import com.fut.chatbot.viewholder.UserMessageViewHolder;

import java.util.Arrays;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private enum ViewType{
        NONE,
        DATE_MESSAGE,
        USER_MESSAGE,
        ANSWER_MESSAGE,
        ANSWER_MESSAGE_WITH_IMAGE,
        ANSWER_MESSAGE_WITH_EXTRAS,
        ANSWER_MESSAGE_WITH_IMAGE_AND_EXTRAS,
        BROADCAST_MESSAGE,
        POLL_MESSAGE
    }

    private final ChatClickListener mChatClickListener;
    private List<Chat> chats;
    private Context mContext;

    private boolean[] selection;
    private int selectionCount;

    public ChatAdapter(Context context, ChatClickListener listener) {
        mContext = context;
        mChatClickListener = listener;
        selectionCount = 0;
    }

    @Override
    public int getItemViewType(int position) {
        Chat item = chats.get(position);
        if(item == null){
            return ViewType.NONE.ordinal();
        }else if(item.getType() == Chat.ChatType.DATE){
            return ViewType.DATE_MESSAGE.ordinal();
        }else if(item.getType() == Chat.ChatType.MINE){
            return ViewType.USER_MESSAGE.ordinal();
        }else if(item.getType() == Chat.ChatType.BOT){
            Message message = Constants.GSON.fromJson(item.getData(), Message.class);
            if(message.getType() == Message.MessageType.ANSWER) {
                if (message.getAnswer().getExtras() == null || message.getAnswer().getExtras().isEmpty()) {
                    return ViewType.ANSWER_MESSAGE.ordinal();
                } else {
                    boolean hasImage = false;
                    for (Extra extra : message.getAnswer().getExtras()) {
                        if (extra.getType() == Extra.ExtraType.IMAGE) {
                            hasImage = true;
                            break;
                        }
                    }
                    if (hasImage && message.getAnswer().getExtras().size() == 1) {
                        return ViewType.ANSWER_MESSAGE_WITH_IMAGE.ordinal();
                    } else if (hasImage) {
                        return ViewType.ANSWER_MESSAGE_WITH_IMAGE_AND_EXTRAS.ordinal();
                    } else {
                        return ViewType.ANSWER_MESSAGE_WITH_EXTRAS.ordinal();
                    }
                }
            }else if(message.getType() == Message.MessageType.BROADCAST){
                return ViewType.BROADCAST_MESSAGE.ordinal();
            }else{
                return ViewType.POLL_MESSAGE.ordinal();
            }
        }else{
            return ViewType.NONE.ordinal();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View holder;

        if(viewType == ViewType.USER_MESSAGE.ordinal()){
            holder = LayoutInflater.from(mContext).inflate(R.layout.holder_user_message, parent, false);
            return new UserMessageViewHolder(holder);
        }if(viewType == ViewType.DATE_MESSAGE.ordinal()){
            holder = LayoutInflater.from(mContext).inflate(R.layout.holder_date_message, parent, false);
            return new DateMessageViewHolder(holder);
        }else if(viewType == ViewType.ANSWER_MESSAGE.ordinal()){
            holder = LayoutInflater.from(mContext).inflate(R.layout.holder_answer_message, parent, false);
            return new AnswerMessageViewHolder(holder);
        }else if(viewType == ViewType.ANSWER_MESSAGE_WITH_EXTRAS.ordinal()){
            holder = LayoutInflater.from(mContext).inflate(R.layout.holder_answer_message_with_extras, parent, false);
            return new AnswerMessageWithExtraViewHolder(holder);
        }else if(viewType == ViewType.ANSWER_MESSAGE_WITH_IMAGE.ordinal()){
            holder = LayoutInflater.from(mContext).inflate(R.layout.holder_answer_message_with_image, parent, false);
            return new AnswerMessageWithImageViewHolder(holder);
        }else if(viewType == ViewType.ANSWER_MESSAGE_WITH_IMAGE_AND_EXTRAS.ordinal()){
            holder = LayoutInflater.from(mContext).inflate(R.layout.holder_answer_message_with_image_and_extras, parent, false);
            return new AnswerMessageWithImageAndExtraViewHolder(holder);
        }else if(viewType == ViewType.BROADCAST_MESSAGE.ordinal()){
            holder = LayoutInflater.from(mContext).inflate(R.layout.holder_broadcast_message, parent, false);
            return new BroadcastMessageViewHolder(holder);
        }else if(viewType == ViewType.POLL_MESSAGE.ordinal()){
            holder = LayoutInflater.from(mContext).inflate(R.layout.holder_poll_message, parent, false);
            return new PollMessageViewHolder(holder);
        }else{
            holder = LayoutInflater.from(mContext).inflate(R.layout.holder_none, parent, false);
            return new NoneViewHolder(holder);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if(viewType != ViewType.DATE_MESSAGE.ordinal() && viewType != ViewType.NONE.ordinal()){
            activateSelection(holder, position);
        }

        if(selection[position]){
            holder.itemView.setBackgroundResource(R.color.colorTransparentPurple);
        }else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        Chat chat = chats.get(position);
        if (viewType == ViewType.USER_MESSAGE.ordinal()) {
            UserMessageViewHolder userMessageViewHolder = (UserMessageViewHolder) holder;
            userMessageViewHolder.init(mContext, chat);
        } else if (viewType == ViewType.ANSWER_MESSAGE.ordinal()) {
            AnswerMessageViewHolder answerMessageViewHolder = (AnswerMessageViewHolder) holder;
            answerMessageViewHolder.init(chat);
            answerMessageViewHolder.setContinuous(isContinuous(position));
        } else if(viewType == ViewType.DATE_MESSAGE.ordinal()){
            DateMessageViewHolder dateMessageViewHolder = (DateMessageViewHolder) holder;
            dateMessageViewHolder.init(mContext, chat);
        } else if (viewType == ViewType.BROADCAST_MESSAGE.ordinal()) {
            BroadcastMessageViewHolder broadcastMessageViewHolder = (BroadcastMessageViewHolder) holder;
            broadcastMessageViewHolder.init(chat);
        } else if (viewType == ViewType.ANSWER_MESSAGE_WITH_IMAGE.ordinal()) {
            AnswerMessageWithImageViewHolder answerMessageWithImageViewHolder = (AnswerMessageWithImageViewHolder) holder;
            answerMessageWithImageViewHolder.init(chat);
            answerMessageWithImageViewHolder.setContinuous(isContinuous(position));
        } else if (viewType == ViewType.POLL_MESSAGE.ordinal()) {
            PollMessageViewHolder pollMessageViewHolder = (PollMessageViewHolder) holder;
            pollMessageViewHolder.init(mContext, chat, itemPosition ->  mChatClickListener.onVote(chat, itemPosition));
        } else if (viewType == ViewType.ANSWER_MESSAGE_WITH_EXTRAS.ordinal()) {
            AnswerMessageWithExtraViewHolder answerMessageWithExtraViewHolder = (AnswerMessageWithExtraViewHolder) holder;
            answerMessageWithExtraViewHolder.init(mContext, chat, mChatClickListener::onExtraClicked);
            answerMessageWithExtraViewHolder.setContinuous(isContinuous(position));
        } else if (viewType == ViewType.ANSWER_MESSAGE_WITH_IMAGE_AND_EXTRAS.ordinal()) {
            AnswerMessageWithImageAndExtraViewHolder answerMessageWithImageAndExtraViewHolder = (AnswerMessageWithImageAndExtraViewHolder) holder;
            answerMessageWithImageAndExtraViewHolder.init(mContext, chat, mChatClickListener::onExtraClicked);
            answerMessageWithImageAndExtraViewHolder.setContinuous(isContinuous(position));
        }

    }

    private void activateSelection(RecyclerView.ViewHolder holder, int position){
        holder.itemView.setOnLongClickListener(view -> {
            if(selectionCount == 0){
                selection[position] = true;
                selectionCount++;
                holder.itemView.setBackgroundResource(R.color.colorTransparentPurple);
                mChatClickListener.onSelectionCountChanged(selectionCount);

                return true;
            }
            return false;
        });

        holder.itemView.setOnClickListener(view -> {
            if(selectionCount > 0){
                if(selection[position]) {
                    selection[position] = false;
                    selectionCount--;
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                }else{
                    selection[position] = true;
                    selectionCount++;
                    holder.itemView.setBackgroundResource(R.color.colorTransparentPurple);
                }
                mChatClickListener.onSelectionCountChanged(selectionCount);
            }
        });
    }

    private boolean isContinuous(int currentPosition){
        boolean isContinuous = false;
        if (currentPosition > 0) {
            Chat previousItem = chats.get(currentPosition - 1);
            if(previousItem.getType() == Chat.ChatType.BOT){
                isContinuous = true;
            }
        }
        return isContinuous;
    }

    @Override
    public int getItemCount() {
        if (chats == null) {
            return 0;
        }
        return chats.size();
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
        if(selection == null || selectionCount == 0){
            selection = new boolean[chats.size()];
        }else{
            boolean[] temp = selection;
            selection = new boolean[chats.size()];
            System.arraycopy(temp, 0, selection, 0, temp.length);
        }
        notifyDataSetChanged();
    }

    public List<Chat> getChats() {
        return chats;
    }

    public boolean[] getSelection() {
        return selection;
    }

    public void clearSelections(){
        Arrays.fill(selection, false);
        notifyDataSetChanged();
    }

    public void setSelectionCount(int selectionCount) {
        this.selectionCount = selectionCount;
    }

    public interface ChatClickListener {
        void onSelectionCountChanged(int selectionCount);

        void onVote(Chat chat, int position);

        void onExtraClicked(Extra extra);
    }
}