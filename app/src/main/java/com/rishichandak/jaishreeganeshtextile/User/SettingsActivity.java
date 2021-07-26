package com.rishichandak.jaishreeganeshtextile.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rishichandak.jaishreeganeshtextile.Common.LoginSignup.RetailerStartUpScreen;
import com.rishichandak.jaishreeganeshtextile.PrevalentUser.Prevalent;
import com.rishichandak.jaishreeganeshtextile.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userNameEditText, addressEditText;
    private TextView profileChangeTextBtn, closeTextButton, saveTextButton;

    private Uri imageUri;
    private String myUrl = "";
    private StorageReference storageProfilePicReference;
    private StorageTask uploadTask;
    private String checker= "";

    RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageView = findViewById(R.id.settings_profile_image);
        fullNameEditText = findViewById(R.id.settings_full_name);
        userNameEditText = findViewById(R.id.settings_username);
        addressEditText = findViewById(R.id.settings_address);
        progressBar = findViewById(R.id.settings_progress_bar);

        profileChangeTextBtn = findViewById(R.id.profile_image_change_btn);
        closeTextButton = findViewById(R.id.close_settings_btn);
        saveTextButton = findViewById(R.id.update_account_settings_btn);

        storageProfilePicReference = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        userInfoDisplay(profileImageView, fullNameEditText, userNameEditText, addressEditText);

        closeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,UserDashboard.class));
                finish();
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                // Adding crop image feature
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsActivity.this,UserDashboard.class));
        finish();
    }

    private void updateOnlyUserInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("fullName",fullNameEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("username",userNameEditText.getText().toString());
        reference.child(Prevalent.currentOnlineUser.getPhoneNo()).updateChildren(userMap);


        startActivity(new Intent(getApplicationContext(), UserDashboard.class));
        Toast.makeText(SettingsActivity.this, "Profile Information Updated :)", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        }
        else {
            Toast.makeText(this, "Error ... Try Again !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {

        if(TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userNameEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage(){

        progressBar.setVisibility(View.VISIBLE);

        if(imageUri != null){
            final StorageReference fileRef = storageProfilePicReference
                    .child(Prevalent.currentOnlineUser.getPhoneNo() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("fullName",fullNameEditText.getText().toString());
                        userMap.put("address",addressEditText.getText().toString());
                        userMap.put("username",userNameEditText.getText().toString());
                        userMap.put("image",myUrl);
                        reference.child(Prevalent.currentOnlineUser.getPhoneNo()).updateChildren(userMap);

                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(SettingsActivity.this,UserDashboard.class));
                        Toast.makeText(SettingsActivity.this, "Profile Information Updated :)", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SettingsActivity.this, "Error !!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Image not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEditText, EditText userNameEditText, EditText addressEditText) {

        Log.d("CURR USERRRR",Prevalent.currentOnlineUser.getFullName());
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhoneNo());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    if(snapshot.child("image").exists())
                    {
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("fullName").getValue().toString();
                        String userName = snapshot.child("username").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        if(!image.equals("")) {
                            Picasso.get().load(image).into(profileImageView);
                        }
                        fullNameEditText.setText(name);
                        userNameEditText.setText(userName);
                        addressEditText.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}