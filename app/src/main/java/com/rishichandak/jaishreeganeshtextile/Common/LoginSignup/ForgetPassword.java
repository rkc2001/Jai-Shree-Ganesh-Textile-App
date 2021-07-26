package com.rishichandak.jaishreeganeshtextile.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.rishichandak.jaishreeganeshtextile.R;

public class ForgetPassword extends AppCompatActivity {

    private ImageView backBtn;
    private TextInputLayout phoneNumberTextField;
    private CountryCodePicker countryCodePicker;
    private Button nextBtn;
    RelativeLayout progressBar;


    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        phoneNumberTextField = findViewById(R.id.forget_password_phone_number);
        countryCodePicker = findViewById(R.id.country_code_picker);
        nextBtn = findViewById(R.id.forget_password_next_btn);
        progressBar = findViewById(R.id.forget_password_progress_bar);
        backBtn = findViewById(R.id.forget_password_back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPassword.super.onBackPressed();
                finish();
            }
        });
    }

    public void verifyPhoneNumber(View view){

        if(!validateFields()){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        String _phoneNo = phoneNumberTextField.getEditText().getText().toString().trim();
        if(_phoneNo.charAt(0) == '0'){
            _phoneNo = _phoneNo.substring(1);
        }

        final String _completePhoneNo = "+"+countryCodePicker.getFullNumber()+_phoneNo;

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(_completePhoneNo);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    progressBar.setVisibility(View.GONE);

                    phoneNumberTextField.setError(null);
                    phoneNumberTextField.setErrorEnabled(false);

                    Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
                    intent.putExtra("phoneNo", _completePhoneNo);
                    intent.putExtra("whatToDO", "updateData");
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(ForgetPassword.this, "No such user exists !! Please Sign Up", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(),RetailerStartUpScreen.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private boolean validateFields(){
        String _phone = phoneNumberTextField.getEditText().getText().toString().trim();

        if(_phone.isEmpty()){
            phoneNumberTextField.setError("Username cannot be empty");
            phoneNumberTextField.requestFocus();
            return false;
        }else if (!Patterns.PHONE.matcher(_phone).matches()) {
            phoneNumberTextField.setError("Enter a valid phone number");
            phoneNumberTextField.requestFocus();
            return false;
        }
        else {
            return true;
        }

    }
}