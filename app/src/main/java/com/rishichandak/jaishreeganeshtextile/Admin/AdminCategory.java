package com.rishichandak.jaishreeganeshtextile.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.rishichandak.jaishreeganeshtextile.Common.LoginSignup.RetailerStartUpScreen;
import com.rishichandak.jaishreeganeshtextile.R;
import com.rishichandak.jaishreeganeshtextile.User.UserDashboard;

// Here we display all products that are being sold
public class AdminCategory extends AppCompatActivity {

    private ImageView pendant, lace, ribbon, cloth, womenWear, beads, button;

    private Button logOutBtn, checkOrdersBtn, maintainProductsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_admin_category);

        logOutBtn = findViewById(R.id.admin_logout_btn);
        checkOrdersBtn = findViewById(R.id.check_orders_btn);
        maintainProductsBtn = findViewById(R.id.maintain_btn);

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RetailerStartUpScreen.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        checkOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserDashboard.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });

        pendant = findViewById(R.id.pendant);
        lace = findViewById(R.id.lace);
        ribbon = findViewById(R.id.ribbon);
        cloth = findViewById(R.id.cloth);
        womenWear = findViewById(R.id.woman_cloth);
        beads = findViewById(R.id.beads);
        button = findViewById(R.id.button);


        pendant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminAddNewProduct.class);
                intent.putExtra("category","pendant");
                startActivity(intent);
            }
        });

        lace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminAddNewProduct.class);
                intent.putExtra("category","lace");
                startActivity(intent);
            }
        });

        ribbon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminAddNewProduct.class);
                intent.putExtra("category","ribbon");
                startActivity(intent);
            }
        });

        cloth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminAddNewProduct.class);
                intent.putExtra("category","cloth");
                startActivity(intent);
            }
        });

        womenWear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminAddNewProduct.class);
                intent.putExtra("category","women wear");
                startActivity(intent);
            }
        });

        beads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminAddNewProduct.class);
                intent.putExtra("category","beads");
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AdminAddNewProduct.class);
                intent.putExtra("category","button");
                startActivity(intent);
            }
        });
    }
}