package com.rishichandak.jaishreeganeshtextile.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rishichandak.jaishreeganeshtextile.R;

import io.paperdb.Paper;

public class SetNewPassword extends AppCompatActivity {

    private ImageView closeBtn;
    private TextInputLayout newPassword, confirmNewPassword;
    private Button updatePasswordBtn;
    RelativeLayout progressBar;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        closeBtn = findViewById(R.id.set_new_password_close_btn);
        newPassword = findViewById(R.id.new_password);
        confirmNewPassword = findViewById(R.id.confirm_password);
        updatePasswordBtn = findViewById(R.id.set_new_password_btn);
        progressBar = findViewById(R.id.set_new_password_progress_bar);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RetailerStartUpScreen.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    public void setNewPasswordUser(View view){

        if(!validatePassword()){
            return;
        }

        // Get data from fields
        String _newPassword = newPassword.getEditText().getText().toString().trim();
        String _phoneNumber = getIntent().getStringExtra("phoneNo");

        usersRef.child(_phoneNumber).child("password").setValue(_newPassword);

        startActivity(new Intent(getApplicationContext(),ForgetPasswordSuccessMessage.class));
        finish();
    }

    /** Validation Function */
    private boolean validatePassword() {
        String val = newPassword.getEditText().getText().toString().trim();
        String val2 = confirmNewPassword.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            newPassword.setError("Field can not be empty");
            return false;
        } else if(val.length() < 4){
            newPassword.setError("Password should contain 4 characters!");
            return false;
        } else if (!val.matches(checkPassword)) {
            newPassword.setError("Password must have 1 uppercase, 1 lowercase, 1 number & special character");
            return false;
        } else if(!val.equals(val2)){
            confirmNewPassword.setError("Passwords do not match !!");
            return false;
        } else {
            newPassword.setError(null);
            newPassword.setErrorEnabled(false);

            confirmNewPassword.setError(null);
            confirmNewPassword.setErrorEnabled(false);

            return true;
        }
    }
}