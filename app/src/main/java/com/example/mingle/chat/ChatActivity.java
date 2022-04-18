package com.example.mingle.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.mingle.ProfileActivity;
import com.example.mingle.matches.Matches;
import com.example.mingle.matches.MatchesAdapter;
import com.example.mingle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatsAdapter;
    private RecyclerView.LayoutManager mChatsLayoutManager;

    private EditText mSendMsgTxt;
    private Button mSendBtn;
    private ImageView mProfilePic;

    private String currentUserId, matchId, chatId, profilePicUrl;

    DatabaseReference mUserDb, mChatDb, matchPicDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //get match Id from extra passed in view holder intent
        matchId = getIntent().getExtras().getString("matchId");
        // get ID of current user
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Database reference to get match info & chat info
        mUserDb = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId).child("Connections").child("Matches").child(matchId).child("chatId");
        mChatDb = FirebaseDatabase.getInstance().getReference().child("Chat");
        matchPicDb = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId).child("profilePicUrl");
        setProfileUrl();

        getChatId();

        // recycler setup
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatsLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mChatsAdapter = new ChatsAdapter(getChatsData(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatsAdapter);
        mRecyclerView.setLayoutManager(mChatsLayoutManager);

        mSendMsgTxt = (EditText) findViewById(R.id.messageTxt);
        mSendBtn = (Button) findViewById(R.id.sendBtn);
        mProfilePic = (ImageView) findViewById(R.id.profilePicChat);

        mProfilePic.bringToFront();

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMsg();
            }
        });
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                intent.putExtra("matchId", matchId);
                startActivity(intent);
            }
        });
    }

    private void setProfileUrl() {

        matchPicDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profilePicUrl = "default";
                if(snapshot.exists()){
                    profilePicUrl = snapshot.getValue().toString();
                    Log.d("TESTURL", profilePicUrl);
                }
                switch(profilePicUrl){
                    case "default":
                        Glide.with(getApplication()).load(R.drawable.default_icon).into(mProfilePic);
                        break;
                    default:
                        Glide.with(getApplication()).load(profilePicUrl).into(mProfilePic);
                        break;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendMsg() {
        String msgTxt = mSendMsgTxt.getText().toString();
        if(!msgTxt.isEmpty()){
           DatabaseReference messageDb =  mChatDb.push();
            Map newMsg = new HashMap();
            newMsg.put("SentBy", currentUserId);
            newMsg.put("Msg", msgTxt);
            messageDb.setValue(newMsg);
        }
        mSendMsgTxt.setText(null);
    }

    private void getChatId(){
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    chatId = snapshot.getValue().toString();
                    mChatDb = mChatDb.child(chatId);
                    getMessages();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getMessages() {
        mChatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    Log.d("TESTSNAPSHOT", snapshot.toString());
                    Log.d("TESTUID", currentUserId);
                    String msg = "";
                    String sentByUser = "";
                    Boolean msgFromUser;
                    if(snapshot.child("Msg").getValue() != null){
                        msg = snapshot.child("Msg").getValue().toString();
                    }
                    if(snapshot.child("SentBy").getValue() != null){
                        sentByUser = snapshot.child("SentBy").getValue().toString();
                    }
                    if(msg != null && sentByUser != null){
                        msgFromUser = false;
                        if(sentByUser.equals(currentUserId)){
                            msgFromUser = true;
                        }
                        Chats newMsg = new Chats(msg,msgFromUser);
                        chatsResults.add(newMsg);
                        mChatsAdapter.notifyDataSetChanged();
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

    private ArrayList<Chats> chatsResults = new ArrayList<Chats>();
    private List<Chats> getChatsData() {
        return chatsResults;
    }
}