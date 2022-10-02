package com.communication.mychatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.communication.mychatapp.R;
import com.communication.mychatapp.objectclasses.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdapterForRecyclerView extends RecyclerView.Adapter<AdapterForRecyclerView.ViewHolder>{
    private static final String TAG = "MessageAdapter";

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private List<Message> mMessage;

    public AdapterForRecyclerView(Context mContext, List<Message> messageList) {
        this.mContext = mContext;
        this.mMessage = messageList;
    }

    @NonNull
    @Override
    public AdapterForRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,viewGroup, false);
            return new AdapterForRecyclerView.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,viewGroup,false);
            return new AdapterForRecyclerView.ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Message message = mMessage.get(i);
        viewHolder.show_message.setText(message.getMessageText());

        long longtime =message.getSentTime()/1000;
//        long senttime=chats.getSent_time()/1000;
        // Date sentdate=new Date(TimeUnit.SECONDS.toMillis(senttime));
        Date date= new Date(TimeUnit.SECONDS.toMillis(longtime));
        SimpleDateFormat df2 = new SimpleDateFormat("kk:mm:ss\ndd.MM.yyyy");
        String tarih=df2.format(date).toString();
        //String sentTarih=df2.format(sentdate).toString();
        viewHolder.show_date.setText(tarih);
        // viewHolder.show_sent_date.setText(sentTarih);

    }

    @Override
    public int getItemCount() {
        return mMessage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public TextView show_date;
        // public TextView show_sent_date;

        public ViewHolder(View itemView){

            super(itemView);

            show_message = itemView.findViewById(R.id.msg_show);
            show_date = itemView.findViewById(R.id.textViewDate);
            //show_sent_date = itemView.findViewById(R.id.textViewSentDate);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (mMessage.get(position).getAlignment()==0){
            return MSG_TYPE_LEFT;
        }else {
            return MSG_TYPE_RIGHT;
        }
    }
}