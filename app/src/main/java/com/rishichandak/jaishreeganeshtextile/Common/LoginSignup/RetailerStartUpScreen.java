package com.rishichandak.jaishreeganeshtextile.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rishichandak.jaishreeganeshtextile.Database.UserHelperClass;
import com.rishichandak.jaishreeganeshtextile.PrevalentUser.Prevalent;
import com.rishichandak.jaishreeganeshtextile.R;
import com.rishichandak.jaishreeganeshtextile.User.UserDashboard;

import io.paperdb.Paper;

public class RetailerStartUpScreen extends AppCompatActivity {

    Button signUpButton, loginButton;
    RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retailer_start_up_screen);

        signUpButton = findViewById(R.id.signup_btn);
        loginButton = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.starup_screen_progress_bar);

        // Check if some user was logged in (remember me was checked)
        Paper.init(this);

        // retrieve user's data from Paper - if check box was not implemented, all values would be null
        String userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.userPasswordKey);

        if(userPhoneKey == null && userPasswordKey == null){

        }else{
            AllowAccess(userPhoneKey,userPasswordKey);
        }
    }

    private void AllowAccess(String userPhoneKey, String userPasswordKey){

        progressBar.setVisibility(View.VISIBLE);

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("Users").child(userPhoneKey).exists()){

                    // get password from the system against the specific user
                    String systemPassword = snapshot.child("Users").child(userPhoneKey).child("password").getValue(String.class);
                    if(systemPassword.equals(userPasswordKey)) {

                        UserHelperClass usersData = snapshot.child("Users").child(userPhoneKey).getValue(UserHelperClass.class);
                        Prevalent.currentOnlineUser = usersData;

                        Intent intent = new Intent(getApplicationContext(), UserDashboard.class);
                        progressBar.setVisibility(View.GONE);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Password does not match !\n Enter again or Reset", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "No such user exists !!\n Please Sign Up", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(),SignUp.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void callLoginScreen(View view){

        Intent intent = new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
    }

    public void callSignUpScreen(View view){

        startActivity(new Intent(getApplicationContext(),SignUp.class));
    }
}