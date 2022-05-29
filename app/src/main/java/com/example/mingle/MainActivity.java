package com.example.mingle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mingle.chat.ChatActivity;
import com.example.mingle.matches.MatchesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Cards cardsData[];
    private arrayAdapter arrayAdapter;
    private int i;

    // UI components
    BottomNavigationView bottomNavigationView;

    //instantiate firebase Auth & db
    private FirebaseAuth mAuth;


    //string to hold user ID
    private String currentUId, matchId;
    private DatabaseReference usersDb;

    //array with card object
    private ListView listView;
    private List<Cards> rowItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        setNavBar();
        bottomNavigationView.getMenu().findItem(R.id.homeItem).setChecked(true);

        // Database references for Male and Female
        checkGender();
        rowItems = new ArrayList<Cards>();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems );

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left! You also have access to the original object. If you want to use it just cast it (String) dataObject
                Cards obj = (Cards) dataObject;
                String userId = obj.getUserID();
                usersDb.child(userId).child("Connections").child("no").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "Not",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                matchId = obj.getUserID();
                usersDb.child(matchId).child("Connections").child("yes").child(currentUId).setValue(true);
                isAMatch(matchId);
                Toast.makeText(MainActivity.this, "Hot",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here al.add("XML ".concat(String.valueOf(i))); arrayAdapter.notifyDataSetChanged(); Log.d("LIST", "notified"); i++;
            }
            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Cards obj = (Cards) dataObject;
                matchId = obj.getUserID();
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("matchId", matchId);
                startActivity(intent);
            }
        });

    }

    private void setNavBar() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.messagesItem:
                    Intent matchesIntent = new Intent(MainActivity.this, MatchesActivity.class);
                    startActivity(matchesIntent);
                    break;
                case R.id.homeItem:
                    break;
                case R.id.settingsItem:
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;
            }
            return false;
        });
    }

    public void loadMatches(){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child("gender").getValue() != null){
                    if(snapshot.exists() &&
                            !snapshot.child("Connections").child("no").hasChild(currentUId) &&
                            !snapshot.child("Connections").child("yes").hasChild(currentUId) &&
                            snapshot.child("gender").getValue().toString().equals(oppositeGender)
                    ){
                        String profilePicUrl = "default";
                        if(!snapshot.child("profilePicUrl").getValue().equals("default")){
                            profilePicUrl = snapshot.child("profilePicUrl").getValue().toString();
                        }
                        Cards item = new Cards(snapshot.getKey(), snapshot.child("name").getValue().toString(), profilePicUrl);
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private String userGender;
    private String oppositeGender;
    public void checkGender(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if(snapshot.child("gender").getValue() != null){
                        userGender = snapshot.child("gender").getValue().toString();
                        switch(userGender){
                            case "Male":
                                oppositeGender = "Female";
                                break;
                            case "Female":
                                oppositeGender = "Male";
                                break;
                        }
                        loadMatches();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void isAMatch(String id){
        DatabaseReference userConnections = usersDb.child(currentUId).child("Connections").child("yes").child(id);
        userConnections.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    //set current user's match in db
                    usersDb.child(currentUId).child("Connections").child("Matches").child(snapshot.getKey()).child("chatId").setValue(key);
                    //set match's match in db
                    usersDb.child(snapshot.getKey()).child("Connections").child("Matches").child(currentUId).child("chatId").setValue(key);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}