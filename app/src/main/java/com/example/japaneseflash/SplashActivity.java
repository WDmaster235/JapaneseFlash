package com.example.japaneseflash;
import android.app.ActivityOptions;
import android.util.Pair;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {
    private static final int SPLASH_TIMEOUT = 2000; // 2 seconds

    Animation topAnim;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);

        image = findViewById(R.id.imageView);
        image.setAnimation(topAnim);


        // Delay for a few seconds, then go to MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);

            Pair[] pairs = new Pair[1];
            pairs[0] = new Pair<ImageView, String>(image, "logo_image");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
            startActivity(intent, options.toBundle());

        }, SPLASH_TIMEOUT);
    }
}
