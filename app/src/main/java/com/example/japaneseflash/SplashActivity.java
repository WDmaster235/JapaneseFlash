package com.example.japaneseflash;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends Activity {
    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds

    Animation topAnim;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sign out user so that app always starts logged out.
        FirebaseAuth.getInstance().signOut();

        // Make the window fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        image = findViewById(R.id.imageView);
        image.setAnimation(topAnim);

        // Delay and then launch MainActivity with a shared element transition
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            Pair pair = new Pair<>(image, "logo_image");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pair);
            startActivity(intent, options.toBundle());
            finish();
        }, SPLASH_TIMEOUT);
    }
}
