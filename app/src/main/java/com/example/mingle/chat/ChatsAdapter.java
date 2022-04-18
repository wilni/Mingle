package com.example.mingle.chat;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mingle.R;
import com.example.mingle.matches.Matches;

import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsViewHolder> {
    private List<Chats> chatsList;
    private Context context;

    public ChatsAdapter(List<Chats> chatsList, Context context){
        this.chatsList = chatsList;
        this.context = context;
    }
    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatsViewHolder viewHolder = new ChatsViewHolder(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        holder.mMsg.setText(chatsList.get(position).getMsg());
        if(chatsList.get(position).isMsgFromUser()){
            holder.mMsg.setGravity(Gravity.END);
            holder.mMsg.setTextColor(Color.parseColor("#FFFFFF"));
            holder.mLinearLayout.setBackgroundColor(Color.parseColor("#1982FC"));
        }else{
            holder.mMsg.setGravity(Gravity.START);
            holder.mMsg.setTextColor(Color.parseColor("#404040"));
            holder.mLinearLayout.setBackgroundColor(Color.parseColor("#F4F4F4"));

        }
    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }
}
