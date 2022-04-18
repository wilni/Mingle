package com.example.mingle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {
    // UI components initialization
    private EditText mEmail, mPassword, mName;
    private RadioGroup mRadioGroup;
    private Button mRegister;


    // firebase Auth and listener
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        //firebase listener
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){
                    Intent i = new Intent(RegisterPage.this, MainActivity.class);
                    startActivity(i);
                }
            }
        };
        //UI initialization
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.name);
        mRegister = (Button) findViewById(R.id.registerBtn);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        // register button listener
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get Radiobutton id from radio group and retrieve text;
                int selectedId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectedId);

                if(radioButton.getText() == null){
                    Toast.makeText(RegisterPage.this, "Select Male or Female", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegisterPage.this, "Sign up Not Sucessful", Toast.LENGTH_SHORT).show();
                        }else{
                            String userID = mAuth.getCurrentUser().getUid();

                            DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://mingle-6d525-default-rtdb.firebaseio.com/").getReference()
                                    .child("Users").child(userID);
                            Map userInfo = new HashMap<>();
                            userInfo.put("name", name);
                            userInfo.put("gender", radioButton.getText().toString());
                            userInfo.put("profilePicUrl", "default");
                            mDatabase.updateChildren(userInfo);
                        }
                    }
                });
            }
        });
    }
// add and remove state listener for user signin
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}