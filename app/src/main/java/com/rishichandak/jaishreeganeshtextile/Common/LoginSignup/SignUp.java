package com.rishichandak.jaishreeganeshtextile.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.rishichandak.jaishreeganeshtextile.R;

public class SignUp extends AppCompatActivity {

    //Variables
    ImageButton backBtn;
    Button next, login;
    TextView titleText,slideText;

    TextInputLayout fullName, username, email, password;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_retailer_sign_up);

        // Hooks
        backBtn = findViewById(R.id.signup_back_button);
        next = findViewById(R.id.signup_next_button);
        login = findViewById(R.id.signup_login_button);
        titleText = findViewById(R.id.signup_title_text);
        slideText = findViewById(R.id.signup_slide_text);

        // Hooks for getting data
        fullName = findViewById(R.id.signup_full_name);
        username = findViewById(R.id.signup_username);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), RetailerStartUpScreen.class);
        finishAffinity();
        startActivity(intent);
    }

    public void callNextSignUpScreen(View view){

        // Before calling next activity, perform all the validation ... single OR => all functions will be executed first
        if (!validateFullName() | !validateUsername() | !validateEmail() | !validatePassword()) {
            return;
        }

        Intent intent = new Intent(getApplicationContext(), SignUp2nd.class);
        intent.putExtra("fullName",fullName.getEditText().getText().toString().trim());
        intent.putExtra("username",username.getEditText().getText().toString().trim());
        intent.putExtra("email",email.getEditText().getText().toString().trim());
        intent.putExtra("password",password.getEditText().getText().toString().trim());

        //Add Shared Animation
        Pair[] pairs = new Pair[5];
        pairs[0] = new Pair<View,String>(backBtn, "transition_back_arrow_btn");
        pairs[1] = new Pair<View,String>(next, "transition_next_btn");
        pairs[2] = new Pair<View,String>(login, "transition_login_btn");
        pairs[3] = new Pair<View,String>(titleText, "transition_title_text");
        pairs[4] = new Pair(slideText, "transition_slide_text");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void callLoginFromSignUp(View view){
        finishAffinity();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    /** Validation Functions */
    private boolean validateFullName(){
        String value = fullName.getEditText().getText().toString().trim();

        if(value.isEmpty()){
            // setError - built in function for edit text fields
            fullName.setError("Field cannot be empty");
            return false;
        }else{
            fullName.setError(null);

            // to remove space for error field below the edit text
            fullName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUsername() {
        String val = username.getEditText().getText().toString().trim();
        String checkspaces = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        } else if (val.length() > 20) {
            username.setError("Username is too large!");
            return false;
        } else if (!val.matches(checkspaces)) {
            username.setError("No White spaces are allowed!");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = email.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            email.setError("Invalid Email!");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = password.getEditText().getText().toString().trim();
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
            password.setError("Field can not be empty");
            return false;
        } else if(val.length() < 4){
            password.setError("Password should contain 4 characters!");
            return false;
        } else if (!val.matches(checkPassword)) {
            password.setError("Password must have 1 uppercase, 1 lowercase, 1 number & special character");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
}