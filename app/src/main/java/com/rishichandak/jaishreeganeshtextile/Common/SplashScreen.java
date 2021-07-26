package com.rishichandak.jaishreeganeshtextile.Common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rishichandak.jaishreeganeshtextile.Common.LoginSignup.RetailerStartUpScreen;
import com.rishichandak.jaishreeganeshtextile.R;
import com.rishichandak.jaishreeganeshtextile.User.UserDashboard;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2000;

    // Variables
    Animation topAnim,bottomAnim;
    LinearLayout logoContainer;
    TextView slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        // Animation Hooks
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        logoContainer = findViewById(R.id.logo_container);
        slogan = findViewById(R.id.slogan);

        // Assign Animation
        logoContainer.setAnimation(topAnim);
        slogan.setAnimation(bottomAnim);

        // Move to next screen after animation
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, RetailerStartUpScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
            }
        },SPLASH_SCREEN);
    }
}