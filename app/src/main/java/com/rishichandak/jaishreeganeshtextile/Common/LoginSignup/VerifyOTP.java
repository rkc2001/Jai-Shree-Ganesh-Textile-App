package com.rishichandak.jaishreeganeshtextile.Common.LoginSignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rishichandak.jaishreeganeshtextile.Database.UserHelperClass;
import com.rishichandak.jaishreeganeshtextile.R;
import com.rishichandak.jaishreeganeshtextile.User.UserDashboard;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth mAuth;

    private static final String TAG = "PhoneAuthActivity";

    ImageButton close;
    PinView pinFromUser;
    TextView otpDescriptionText;

    String codeBySystem;

    String phoneNo,fullName,email,username,password,date,gender,whatToDO,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_o_t_p);

        // hooks
        pinFromUser = findViewById(R.id.pin_view);
        otpDescriptionText = findViewById(R.id.otp_description_text);
        close = findViewById(R.id.cross_btn);
        mAuth = FirebaseAuth.getInstance();

        phoneNo = getIntent().getStringExtra("phoneNo");

        otpDescriptionText.setText("Enter One Time Password sent on \n" + phoneNo);

        // Get all values from previous screen using Intent
        fullName = getIntent().getStringExtra("fullName");
        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        date = getIntent().getStringExtra("date");
        gender = getIntent().getStringExtra("gender");
        whatToDO = getIntent().getStringExtra("whatToDO");
        address = getIntent().getStringExtra("address");

        sendVerificationCodeToUser(phoneNo);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RetailerStartUpScreen.class);
                finishAffinity();
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    private void sendVerificationCodeToUser(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.
                    Log.d(TAG, "onVerificationCompleted:" + credential);

                    String code = credential.getSmsCode();
                    if(code != null){
                        pinFromUser.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w(TAG, "onVerificationFailed", e);

                    Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                        @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d(TAG, "onCodeSent:" + verificationId);
                    super.onCodeSent(verificationId,token);
                    codeBySystem = verificationId;
                    Toast.makeText(VerifyOTP.this, "Code sent !", Toast.LENGTH_SHORT).show();
                }
    };

    public void signUp(View view){
        String otpEntered = String.valueOf(pinFromUser.getText());
        if(otpEntered.isEmpty() || otpEntered.length() < 6){
            pinFromUser.setError("Please enter valid code !");
            pinFromUser.requestFocus();
        } else {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, otpEntered);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            if(whatToDO.equals("updateData")){
                                updateOldUsersData();
                            }
                            else {
                                storeNewUsersData();
                            }
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(VerifyOTP.this, "Verification not completed! Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void updateOldUsersData() {

        // Pass phone number - this is the basis upon which we'll update the password
        Intent intent = new Intent(getApplicationContext(),SetNewPassword.class);
        intent.putExtra("phoneNo",phoneNo);
        finishAffinity();
        startActivity(intent);
        finish();
    }

    private void storeNewUsersData() {

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance(); // starts pointing to firebase database

        // to point to reference
        DatabaseReference reference = rootNode.getReference("Users");

        UserHelperClass addNewUser = new UserHelperClass(fullName,username,email,phoneNo,password,date,gender,address,"");

        reference.child(phoneNo).setValue(addNewUser);

        Toast.makeText(this, "Successfully Signed Up !!", Toast.LENGTH_SHORT).show();
        finishAffinity();
        startActivity(new Intent(getApplicationContext(), SignUpSuccessMessage.class));
        finish();
    }
}