package com.example.mingle.matches;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mingle.R;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolder> {
    private List<Matches> matchesList;
    private Context context;

    public MatchesAdapter(List<Matches> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }
    @NonNull
    @Override
    public MatchesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolder viewHolder = new MatchesViewHolder(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolder holder, int position) {
        holder.matchId.setText(matchesList.get(position).getUserID());
        holder.matchId.setVisibility(View.INVISIBLE);
        holder.matchName.setText(matchesList.get(position).getName());
        if(!matchesList.get(position).getProfilePicUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getProfilePicUrl()).into(holder.matchImage);
        }
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
