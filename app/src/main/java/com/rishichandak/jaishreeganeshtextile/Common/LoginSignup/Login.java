package com.rishichandak.jaishreeganeshtextile.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.rishichandak.jaishreeganeshtextile.Admin.AdminAddNewProduct;
import com.rishichandak.jaishreeganeshtextile.Admin.AdminCategory;
import com.rishichandak.jaishreeganeshtextile.Database.UserHelperClass;
import com.rishichandak.jaishreeganeshtextile.PrevalentUser.Prevalent;
import com.rishichandak.jaishreeganeshtextile.R;
import com.rishichandak.jaishreeganeshtextile.User.UserDashboard;

import io.paperdb.Paper;

public class Login extends AppCompatActivity {

    ImageButton backBtn;
    TextInputLayout phoneNo, password;
    CountryCodePicker countryCodePicker;
    private CheckBox chkBoxRememberMe;
    Button loginButton, forgetPasswordLink;
    RelativeLayout progressBar;

    TextView adminLink, notAdminLink;

    // As same login activity is used for Admin
    private String parentDbName = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_retailer_login);

        countryCodePicker = findViewById(R.id.login_country_code_picker);
        backBtn = findViewById(R.id.login_back_button);
        phoneNo = findViewById(R.id.login_phone_number);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_btn);
        forgetPasswordLink = findViewById(R.id.forget_password_link);
        progressBar = findViewById(R.id.login_progress_bar);

        chkBoxRememberMe = findViewById(R.id.remember_me_check_box);
        adminLink = findViewById(R.id.admin_panel_link);
        notAdminLink = findViewById(R.id.non_admin_panel_link);

        Paper.init(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Admin Login");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admin";
            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

        forgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgetPassword.class);
                startActivity(intent);
            }
        });
    }

    public void callSignUpScreen(View view){

        Intent intent = new Intent(getApplicationContext(),SignUp.class);
        finishAffinity();

        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair(findViewById(R.id.sign_up_button), "transition_signup");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), RetailerStartUpScreen.class);
        finishAffinity();
        startActivity(intent);
    }

    public void letUserLogIn(View view){
        if(!validateFields()){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // get data
        String _phoneNo = phoneNo.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();

        if(_phoneNo.charAt(0) == '0'){
            _phoneNo = _phoneNo.substring(1);
        }

        String _completePhoneNo = "+"+countryCodePicker.getFullNumber()+_phoneNo;


        // Peform Database Query
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(parentDbName).child(_completePhoneNo).exists()){
                    // do not deal with error in phone no and others
                    phoneNo.setError(null);
                    phoneNo.setErrorEnabled(false);

                    // get password from the system against the specific user
                    String systemPassword = snapshot.child(parentDbName).child(_completePhoneNo).child("password").getValue(String.class);
                    if(systemPassword.equals(_password)) {
                        password.setError(null);
                        password.setErrorEnabled(false);

                        UserHelperClass userData = snapshot.child(parentDbName).child(_completePhoneNo).getValue(UserHelperClass.class);

                        if(parentDbName.equals("Admin")){
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Hello Admin ... log in was successful ..", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getApplicationContext(), AdminCategory.class));
                        }
                        else {

                            // before logging in, store values in the static variables of Prevalent Class
                            if (chkBoxRememberMe.isChecked()) {
                                Paper.book().write(Prevalent.userPhoneKey, _completePhoneNo);
                                Paper.book().write(Prevalent.userPasswordKey, _password);
                            }

                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Log in successful ..", Toast.LENGTH_SHORT).show();

                            Prevalent.currentOnlineUser = userData;

                            startActivity(new Intent(getApplicationContext(), UserDashboard.class));
                            // Using Paper Library to maintain session - writes user info to android memory
                        }
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "Password does not match !\n Enter again or Reset", Toast.LENGTH_SHORT).show();
                    }
                }
                else{

                    if(parentDbName.equals("Admin")){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "You are not an Admin !!\n Please Sign In as User", Toast.LENGTH_LONG).show();
                        finishAffinity();
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                    }
                    else if(parentDbName.equals("Users")) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Login.this, "No such user exists !!\n Please Sign Up", Toast.LENGTH_SHORT).show();
                        finishAffinity();
                        startActivity(new Intent(getApplicationContext(),SignUp.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /** Validation */
    private boolean validateFields(){
        String _phone = phoneNo.getEditText().getText().toString().trim();
        String _password = password.getEditText().getText().toString().trim();

        if(_phone.isEmpty()){
            phoneNo.setError("Phone Number cannot be empty");
            phoneNo.requestFocus();
            return false;
        }else if(_password.isEmpty()){
            password.setError("Password cannot be empty");
            password.requestFocus();
            return false;
        }else if (!Patterns.PHONE.matcher(_phone).matches()) {
            phoneNo.setError("Enter a valid phone number");
            phoneNo.requestFocus();
            return false;
        }
        else {
            return true;
        }

    }
}