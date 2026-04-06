// animated splash screen that shows the Vigilante logo before launching the login screen

package com.example.vigilante;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View content = findViewById(R.id.splashContent);
        View tagline = findViewById(R.id.splashTagline);

        // logo and title scale up and fade in with a bounce
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(content, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(content, "scaleX", 0.6f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(content, "scaleY", 0.6f, 1f);

        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.playTogether(fadeIn, scaleX, scaleY);
        logoAnim.setDuration(800);
        logoAnim.setInterpolator(new OvershootInterpolator(1.2f));
        logoAnim.start();

        // tagline fades in after the logo lands
        ObjectAnimator taglineFade = ObjectAnimator.ofFloat(tagline, "alpha", 0f, 1f);
        taglineFade.setStartDelay(600);
        taglineFade.setDuration(500);
        taglineFade.start();

        // navigate to login after the animation finishes
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 2000);
    }
}
