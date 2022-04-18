package com.example.mingle.matches;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mingle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;

    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        // get ID of current user
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // recycler setup
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mMatchesAdapter = new MatchesAdapter(getMatchesData(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        getMatchesId();

    }

    private void getMatchesId() {
        //get reference to user ID key in db
        DatabaseReference matchesDb = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(currentUserId).child("Connections").child("Matches");
        matchesDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot match: snapshot.getChildren()){
                        getMatchInfo(match.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getMatchInfo(String key) {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(key);
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String userId = snapshot.getKey();
                    String name = "";
                    String profilePicUrl = "";
                    if(snapshot.child("name").getValue() != null){
                        name = snapshot.child("name").getValue().toString();
                    }
                    if(snapshot.child("profilePicUrl").getValue() != null){
                        profilePicUrl = snapshot.child("profilePicUrl").getValue().toString();
                    }

                    Matches obj = new Matches(userId, name, profilePicUrl);
                    matchesResults.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private ArrayList<Matches> matchesResults = new ArrayList<Matches>();
    private List<Matches> getMatchesData() {
        return matchesResults;
    }
}