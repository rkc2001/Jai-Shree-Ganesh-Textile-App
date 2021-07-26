package com.rishichandak.jaishreeganeshtextile.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rishichandak.jaishreeganeshtextile.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn, deleteBtn;
    private EditText name, price, description;
    private ImageView imageView;
    private String productID;

    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productID = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        applyChangesBtn = findViewById(R.id.apply_changes_btn);
        name = findViewById(R.id.product_name_maintain);
        price = findViewById(R.id.product_price_maintain);
        description = findViewById(R.id.product_description_maintain);
        imageView = findViewById(R.id.product_image_maintain);
        deleteBtn = findViewById(R.id.delete_product_btn);

        // Receive product from the intent pid and apply changes to specific product
        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void deleteThisProduct() {

        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminMaintainProductsActivity.this, "Product removed successfully !!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),AdminCategory.class);
                finishAffinity();
                startActivity(intent);
            }
        });
    }

    private void applyChanges() {

        // get all data entered by user
        String enteredName = name.getText().toString();
        String enteredPrice = price.getText().toString();
        String enteredDescription = description.getText().toString();

        if(enteredName.equals("")){
            Toast.makeText(this, "Please enter Product Name !!", Toast.LENGTH_SHORT).show();
        }
        else if(enteredPrice.equals("")){
            Toast.makeText(this, "Please enter Product Price !!", Toast.LENGTH_SHORT).show();
        }
        else if(enteredDescription.equals("")){
            Toast.makeText(this, "Please enter Product Description !!", Toast.LENGTH_SHORT).show();
        }
        else{

            // store changes inside database
            HashMap<String,Object> productMap = new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("description",enteredDescription);
            productMap.put("price", enteredPrice);
            productMap.put("pname",enteredName);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductsActivity.this, "Changes Applied Successfully !!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),AdminCategory.class);
                        finishAffinity();
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void displaySpecificProductInfo() {

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    // retrieve info
                    String pName = snapshot.child("pname").getValue().toString();
                    String pPrice = snapshot.child("price").getValue().toString();
                    String pDescription = snapshot.child("description").getValue().toString();
                    String pImage = snapshot.child("image").getValue().toString();

                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}