package com.rishichandak.jaishreeganeshtextile.Admin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rishichandak.jaishreeganeshtextile.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProduct extends AppCompatActivity {

    // receive category from category activity
    private String categoryName, description, price, pName, saveCurentDate, saveCurrentTime;
    Button addNewProductButton;
    ImageView inputProductImage;
    EditText inputProductName, inputProductDescription, inputProductPrice;
    RelativeLayout progressBar;

    private static int galleryPick = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;

    // folder to add all product images
    private StorageReference productImagesRef;

    // new table for products
    private DatabaseReference productsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_add_new_product);

        addNewProductButton = findViewById(R.id.add_new_product);
        inputProductImage = findViewById(R.id.select_product_image);
        inputProductName = findViewById(R.id.product_name);
        inputProductDescription = findViewById(R.id.product_description);
        inputProductPrice = findViewById(R.id.product_price);
        progressBar = findViewById(R.id.add_product_progress_bar);

        categoryName = getIntent().getExtras().get("category").toString();
        productImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        inputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery to select the product image
                openGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        someActivityResultLauncher.launch(galleryIntent);
    }

    // get the result i.e. image url and store it inside firebase storage and then in firebase database
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    imageUri = data.getData();

                    // display image from uri in the image view
                    inputProductImage.setImageURI(imageUri);
                }
            });


    private void validateProductData() {

        description = inputProductDescription.getText().toString();
        pName = inputProductName.getText().toString();
        price = inputProductPrice.getText().toString();

        if(imageUri == null){
            Toast.makeText(this, "Product Image is mandatory !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Please give product description !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pName)){
            Toast.makeText(this, "Please provide the price !", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(price)){
            Toast.makeText(this, "Please write product name !", Toast.LENGTH_SHORT).show();
        }
        else{
            // everything OK - store in firebase database
            progressBar.setVisibility(View.VISIBLE);
            storeProductInformation();
        }
    }

    private void storeProductInformation() {


        // get current time - so as to display the latest products to users first
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate =  new SimpleDateFormat("MMM dd, yyyy");
        saveCurentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime =  new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        // for each product data, we need a random key - best way combine currentDay with currentTime [OR use firebase's .push() method]
        // unique key every second
        productRandomKey = saveCurentDate + saveCurrentTime;

        // First store imageUri i.e. product image inside firebase storage ... then we'll be able to store link of image in firebase DB
        // .getLastPathSegment() - adds the image (default) name
        StorageReference filePath = productImagesRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminAddNewProduct.this, "Error : " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AdminAddNewProduct.this, "Product Image uploaded successfully !!", Toast.LENGTH_SHORT).show();

                /** Once image is uploaded to firebase storage, now we have to get the link of image and store inside firebase DB - to display image to users */
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            throw task.getException();
                        }

                        // ready to get image URL
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminAddNewProduct.this, "Got the Product Image URL", Toast.LENGTH_SHORT).show();

                            /** Store all information of new product in Firebase DB */
                            saveProductInfoToDatabase();
                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(AdminAddNewProduct.this, "Error - " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });


    }

    private void saveProductInfoToDatabase() {

        HashMap<String,Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description",description);
        productMap.put("image",downloadImageUrl);
        productMap.put("category",categoryName);
        productMap.put("price", price);
        productMap.put("pname",pName);

        // Create FireBase DB reference to new Node i.e. table for all products
        productsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AdminAddNewProduct.this, "Product successfully added to DB !", Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(),AdminCategory.class);
                            finishAffinity();
                            startActivity(intent);
                            finish();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProduct.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}