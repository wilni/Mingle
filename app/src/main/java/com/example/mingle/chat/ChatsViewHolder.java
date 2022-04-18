package com.example.mingle.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mingle.R;

public class ChatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMsg;
    public LinearLayout mLinearLayout;

    public ChatsViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMsg = (TextView) itemView.findViewById(R.id.message);
        mLinearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
    }

    @Override
    public void onClick(View view) {
    }
}
