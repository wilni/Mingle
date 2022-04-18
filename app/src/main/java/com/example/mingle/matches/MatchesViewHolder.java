package com.example.mingle.matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mingle.R;
import com.example.mingle.chat.ChatActivity;

public class MatchesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView matchId, matchName;
    public ImageView matchImage;
    public MatchesViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        matchId = (TextView) itemView.findViewById(R.id.matchId);
        matchName = (TextView) itemView.findViewById(R.id.matchName);
        matchImage = (ImageView) itemView.findViewById(R.id.matchImage);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("matchId", matchId.getText().toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);

    }
}
