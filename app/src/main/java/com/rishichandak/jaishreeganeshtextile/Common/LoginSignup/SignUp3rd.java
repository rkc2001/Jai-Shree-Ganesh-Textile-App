package com.rishichandak.jaishreeganeshtextile.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.UserWriteRecord;
import com.hbb20.CountryCodePicker;
import com.rishichandak.jaishreeganeshtextile.R;

public class SignUp3rd extends AppCompatActivity {

    ImageButton backBtn;
    ScrollView scrollView;
    RelativeLayout progressBar;

    TextInputLayout phoneNumber, address;
    CountryCodePicker countryCodePicker;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_retailer_sign_up3rd);

        backBtn = findViewById(R.id.signup_back_button);
        scrollView = findViewById(R.id.signup_3rd_screen_scroll_view);
        progressBar = findViewById(R.id.sign_up_progress_bar);

        phoneNumber = findViewById(R.id.signup_phone_number);
        address = findViewById(R.id.signup_address);
        countryCodePicker = findViewById(R.id.country_code_picker);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    /** Called when the user touches the back button */
    public void goBack(View view){
        super.onBackPressed();
    }

    public void callVerifyOTPScreen(View view){
        if(!validatePhoneNumber()){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Validation successful - now move on to next activity to verify phone number and save data
        // Get all values from previous screen using Intent
        String _fullName = getIntent().getStringExtra("fullName");
        String _email = getIntent().getStringExtra("email");
        String _username = getIntent().getStringExtra("username");
        String _password = getIntent().getStringExtra("password");
        String _date = getIntent().getStringExtra("date");
        String _gender = getIntent().getStringExtra("gender");

        String _getUserEnteredPhoneNumber = phoneNumber.getEditText().getText().toString().trim();
        String _phoneNo = "+"+countryCodePicker.getFullNumber()+_getUserEnteredPhoneNumber;
        String _address = address.getEditText().getText().toString().trim();

        mDatabase.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("firebase", "Error getting data", task.getException());
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignUp3rd.this, "Data Base Error ... Please try after some time !", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(task.getResult().child(_phoneNo).exists()){
                        // user already exists
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignUp3rd.this, "User Already Exists !!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(scrollView, "transition_login_screen");

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp3rd.this, pairs);
                        finishAffinity();
                        startActivity(intent, options.toBundle());
                    }
                    else{
                        // new user sign up
                        Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        // Pass all fields to next activity
                        intent.putExtra("fullName",_fullName);
                        intent.putExtra("username",_username);
                        intent.putExtra("email",_email);
                        intent.putExtra("phoneNo",_phoneNo);
                        intent.putExtra("password",_password);
                        intent.putExtra("date",_date);
                        intent.putExtra("gender",_gender);
                        intent.putExtra("address",_address);
                        intent.putExtra("whatToDO","");

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(scrollView, "transition_OTP_screen");

                        progressBar.setVisibility(View.GONE);
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp3rd.this, pairs);
                        startActivity(intent, options.toBundle());
                    }
                }
            }
        });

    }

    private boolean validatePhoneNumber() {
        String val = phoneNumber.getEditText().getText().toString().trim();
        String val2 = address.getEditText().getText().toString().trim();

        if (val.isEmpty() || val.length() < 10) {
            phoneNumber.setError("Please enter phone number");
            return false;
        } else if (!Patterns.PHONE.matcher(val).matches()) {
            phoneNumber.setError("Enter a valid phone number");
            phoneNumber.requestFocus();
            return false;
        } else if(val2.isEmpty()){
            address.setError("Please enter a valid address");
            address.requestFocus();
            return false;
        }
        else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }
}