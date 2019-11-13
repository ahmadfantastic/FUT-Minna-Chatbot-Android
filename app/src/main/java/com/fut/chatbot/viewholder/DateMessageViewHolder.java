package com.fut.chatbot.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fut.chatbot.R;
import com.fut.chatbot.database.Chat;
import com.fut.chatbot.util.Constants;

import java.util.Calendar;
import java.util.Date;

public class DateMessageViewHolder extends RecyclerView.ViewHolder {

    private TextView txtDate;

    public DateMessageViewHolder(@NonNull View holder) {
        super(holder);

        txtDate = holder.findViewById(R.id.txt_date);
    }

    public void init(Context context, Chat chat){
        Calendar calendar = Calendar.getInstance();
        Calendar calendarNow = Calendar.getInstance();
        calendar.setTime( Constants.GSON.fromJson(chat.getData(), Date.class));

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int yearNow = calendarNow.get(Calendar.YEAR);
        int monthNow = calendarNow.get(Calendar.MONTH);
        int dayNow = calendarNow.get(Calendar.DAY_OF_MONTH);

        if(year == yearNow && month == monthNow && day == dayNow){
            txtDate.setText(context.getString(R.string.prompt_today));
        }else if(year == yearNow && month == monthNow && day == dayNow-1){
            txtDate.setText(context.getString(R.string.prompt_yesterday));
        }else{
            txtDate.setText(Constants.DATE_FORMAT.format(calendar.getTime()));
        }
    }
}
