package com.rishichandak.jaishreeganeshtextile.Common.LoginSignup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rishichandak.jaishreeganeshtextile.R;

import java.util.Calendar;

public class SignUp2nd extends AppCompatActivity {

    //Variables
    ImageButton backBtn;
    Button next;
    TextView titleText,slideText;

    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_retailer_sign_up2nd);

        //Hooks for animation
        backBtn = findViewById(R.id.signup_back_button);
        next = findViewById(R.id.signup_next_button);
        titleText = findViewById(R.id.signup_title_text);
        slideText = findViewById(R.id.signup_slide_text);

        // Hooks for validation variables
        radioGroup = findViewById(R.id.radio_group);
        datePicker = findViewById(R.id.age_picker);
    }

    /** Called when the user touches the back button */
    public void goBack(View view){
        super.onBackPressed();
    }

    public void call3rdSignUpScreen(View view){

        if(!validateAge() | !validateGender()){
            return;
        }

        // Get all values from previous screen using Intent
        String _fullName = getIntent().getStringExtra("fullName");
        String _email = getIntent().getStringExtra("email");
        String _username = getIntent().getStringExtra("username");
        String _password = getIntent().getStringExtra("password");

        selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());
        String __gender = selectedGender.getText().toString();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        String __date = day+"/"+month+"/"+year;

        Intent intent = new Intent(getApplicationContext(), SignUp3rd.class);
        // Pass all fields to next activity
        intent.putExtra("fullName",_fullName);
        intent.putExtra("username",_username);
        intent.putExtra("email",_email);
        intent.putExtra("password",_password);
        intent.putExtra("gender",__gender);
        intent.putExtra("date",__date);

        //Add Shared Animation
        Pair[] pairs = new Pair[4];
        pairs[0] = new Pair<View,String>(backBtn, "transition_back_arrow_btn");
        pairs[1] = new Pair<View,String>(next, "transition_next_btn");
        pairs[2] = new Pair<View,String>(titleText, "transition_title_text");
        pairs[3] = new Pair(slideText, "transition_slide_text");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUp2nd.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    /** Validation Function */
    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 14) {
            Toast.makeText(this, "You are not eligible to apply", Toast.LENGTH_SHORT).show();
            return false;
        } else{
            return true;
        }
    }
}