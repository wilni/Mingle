package com.example.mingle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    //UI components
    private Button mBackBtn;
    private TextView mNameTxt, mBioTxt;
    private ImageView mProfilePic, mProfileImg1, mProfileImg2;
    //id of match for db reference
    private String mMatchId;
    //firebase db ref
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDb;
    //declare strings to store user info
    private String userId, userName, userBio, profilePicUrl, profileImg1Url,profileImg2Url, userGender;
    private Uri resultUri, resultUri2, resultUri3 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mMatchId = getIntent().getStringExtra("matchId");
        mBackBtn = (Button) findViewById(R.id.backBtn);
        mNameTxt = (TextView) findViewById(R.id.nameTxt);
        mBioTxt = (TextView) findViewById(R.id.bioTxt);
        mProfilePic = (ImageView) findViewById(R.id.profilePicChat);
        mProfileImg1 = (ImageView) findViewById(R.id.profilePageImg1);
        mProfileImg2 = (ImageView) findViewById(R.id.profilePageImg2);

        //instantiate Firebase
        mAuth = FirebaseAuth.getInstance();
        mUserDb = FirebaseDatabase.getInstance("https://mingle-6d525-default-rtdb.firebaseio.com/").getReference()
                .child("Users").child(mMatchId);

        getUserInfo();


        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getUserInfo() {
        mUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() > 0){
                    if(snapshot.child("name").getValue() != null){
                        userName = snapshot.child("name").getValue().toString();
                        mNameTxt.setText(userName);
                    }
                    if(snapshot.child("bio").getValue() != null){
                        userBio = snapshot.child("bio").getValue().toString();
                        mBioTxt.setText(userBio);
                    }
                    if(snapshot.child("gender").getValue() != null){
                        userGender = snapshot.child("gender").getValue().toString();
                    }
//                    if(snapshot.child("profilePicUrl").getValue() != null){
//                        profilePicUrl = snapshot.child("profilePicUrl").getValue().toString();
//                        switch(profilePicUrl){
//                            case "default":
//                                Glide.with(getApplication()).load(R.drawable.default_icon).into(mProfilePic);
//                                break;
//                            default:
//                                Glide.with(getApplication()).load(profilePicUrl).into(mProfilePic);
//                                break;
//                        }
//
//                    }
//                    if(snapshot.child("profileImg1").getValue() != null){
//                        profileImg1Url = snapshot.child("profileImg1").getValue().toString();
//                        switch(profileImg1Url){
//                            case "default":
//                                Glide.with(getApplication()).load(R.drawable.default_icon).into(mProfileImg1);
//                                break;
//                            default:
//                                Glide.with(getApplication()).load(profileImg1Url).into(mProfileImg1);
//                                break;
//                        }
//
//                    }
//                    if(snapshot.child("profileImg2").getValue() != null){
//                        profileImg2Url = snapshot.child("profileImg2").getValue().toString();
//                        switch(profileImg2Url){
//                            case "default":
//                                Glide.with(getApplication()).load(R.drawable.default_icon).into(mProfileImg2);
//                                break;
//                            default:
//                                Glide.with(getApplication()).load(profileImg2Url).into(mProfileImg2);
//                                break;
//                        }
//                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}