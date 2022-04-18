package com.example.mingle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    //Declare UI components
    private Button mSignOutBtn, mEditInfoBtn;
    private EditText mName, mBio;
    private ImageView mProfilePic, mProfileImg1, mProfileImg2;

    //Declare Firebase Auth, DB & storage
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDb;

    //declare strings to store user info
    private String userId, userName, userBio, profilePicUrl, profileImg1Url,profileImg2Url, userGender;

    //declare uri
    private Uri resultUri, resultUri2, resultUri3 ;
    //request code int for uploading img to db
    private int mRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //instantiate UI
        mSignOutBtn = (Button) findViewById(R.id.signOutBtn);
        mEditInfoBtn = (Button) findViewById(R.id.editInfoBtn);
        mName = (EditText) findViewById(R.id.editName);
        mBio = (EditText) findViewById(R.id.editBio);
        mProfilePic = (ImageView) findViewById(R.id.profilePic);
        mProfileImg1 = (ImageView) findViewById(R.id.profileImg1);
        mProfileImg2 = (ImageView) findViewById(R.id.profileImg2);

        //instantiate Firebase
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mUsersDb = FirebaseDatabase.getInstance("https://mingle-6d525-default-rtdb.firebaseio.com/").getReference()
                .child("Users").child(userId);

        getUserInfo();
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        mProfileImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
        mProfileImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });
        mEditInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
            }
        });
        mSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent i = new Intent(SettingsActivity.this, StartActivity.class);
                startActivity(i);
            }
        });

    }

    private void getUserInfo() {
        mUsersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if(map.get("name") != null){
                        userName = map.get("name").toString();
                        mName.setText(userName);
                    }
                    if(map.get("bio") != null){
                        userBio = map.get("bio").toString();
                        mBio.setText(userBio);
                        mBio.setSingleLine(false);
                        mBio.setMaxLines(8);
                    }
                    if(map.get("gender") != null){
                        userGender = map.get("gender").toString();
                    }
                    if(map.get("profilePicUrl") != null){
                        profilePicUrl = map.get("profilePicUrl").toString();
                        switch(profilePicUrl){
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.default_icon).into(mProfilePic);
                                break;
                            default:
                                Glide.with(getApplication()).load(profilePicUrl).into(mProfilePic);
                                break;
                        }

                    }
                    if(map.get("profileImg1") != null){
                        profileImg1Url = map.get("profileImg1").toString();
                        switch(profileImg1Url){
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.default_icon).into(mProfileImg1);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImg1Url).into(mProfileImg1);
                                break;
                        }

                    }
                    if(map.get("profileImg2") != null){
                        profileImg2Url = map.get("profileImg2").toString();
                        switch(profileImg2Url){
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.default_icon).into(mProfileImg2);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImg2Url).into(mProfileImg2);
                                break;
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void saveUserInfo() {
        // get input form UI pass into Map and use map to update DB data for user
        userName = mName.getText().toString();
        userBio = mBio.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", userName);
        userInfo.put("bio", userBio);
//        userInfo.put("gender", userGender);


        mUsersDb.updateChildren(userInfo);

        // set storage file to upload profile pic
        if(resultUri != null){
            Log.v("STORAGEUPLOAD","passed if stmt");
            StorageReference storageFilepath = FirebaseStorage.getInstance("gs://mingle-6d525.appspot.com")
                    .getReference().child("profilePics").child(userId);
            Bitmap bitmap = null;
            try {
                //add image to bitmap using decoder
                ImageDecoder.Source source = ImageDecoder.createSource(getApplication().getContentResolver(), resultUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // compress image and add to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream);
            byte [] data = outputStream.toByteArray();
            //upload  byte array to image storage and save it to DB on success
            //repeat this with new fileStorage and bitmap to add more photos; use new request code with startActivityForResult(intent, 1 );
            UploadTask uploadTask = storageFilepath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageFilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profilePicUrl", uri.toString());
                            mUsersDb.updateChildren(newImage);
                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }
        if(resultUri2 != null ){
            Log.v("STORAGEUPLOAD","passed if stmt");
            StorageReference storageFilepath = FirebaseStorage.getInstance("gs://mingle-6d525.appspot.com")
                    .getReference().child("profileImg1").child(userId);
            Bitmap bitmap = null;
            try {
                //add image to bitmap using decoder
                ImageDecoder.Source source = ImageDecoder.createSource(getApplication().getContentResolver(), resultUri2);
                bitmap = ImageDecoder.decodeBitmap(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // compress image and add to byte array
            ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream1);
            byte [] data = outputStream1.toByteArray();
            //upload  byte array to image storage and save it to DB on success
            //repeat this with new fileStorage and bitmap to add more photos; use new request code with startActivityForResult(intent, 1 );
            UploadTask uploadTask = storageFilepath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageFilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImg1", uri.toString());
                            mUsersDb.updateChildren(newImage);
                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }
        if(resultUri3 != null){
            Log.v("STORAGEUPLOAD","passed if stmt");
            StorageReference storageFilepath = FirebaseStorage.getInstance("gs://mingle-6d525.appspot.com")
                    .getReference().child("profileImg2").child(userId);
            Bitmap bitmap = null;
            try {
                //add image to bitmap using decoder
                ImageDecoder.Source source = ImageDecoder.createSource(getApplication().getContentResolver(), resultUri3);
                bitmap = ImageDecoder.decodeBitmap(source);
                Log.v("STORAGEUPLOAD","got bitmap " + bitmap.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // compress image and add to byte array
            ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream2);
            byte [] data = outputStream2.toByteArray();
            Log.v("STORAGEUPLOAD","got byte array " + data.toString());
            //upload  byte array to image storage and save it to DB on success
            //repeat this with new fileStorage and bitmap to add more photos; use new request code with startActivityForResult(intent, 1 );
            UploadTask uploadTask = storageFilepath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageFilepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.v("STORAGEUPLOAD","upload successful");
                            Map newImage = new HashMap();
                            newImage.put("profileImg2", uri.toString());
                            mUsersDb.updateChildren(newImage);
                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.v("STORAGEUPLOAD","upload failed " + e.toString());
                            finish();
                            return;
                        }
                    });
                }
            });
        }else{
            finish();
        }

        // set storage file to upload profile Image # 1


        // set storage file to upload profile Image # 2


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 1 && resultCode == Activity.RESULT_OK){
                final Uri imageUri = data.getData();
                resultUri = imageUri;
                mProfilePic.setImageURI(imageUri);
            }
            if(requestCode == 2 && resultCode == Activity.RESULT_OK){
                final Uri imageUri = data.getData();
                resultUri2 = imageUri;
                mProfileImg1.setImageURI(imageUri);
            }
            if(requestCode == 3 && resultCode == Activity.RESULT_OK){
                final Uri imageUri = data.getData();
                resultUri3 = imageUri;
                mProfileImg2.setImageURI(imageUri);
            }
    }
}