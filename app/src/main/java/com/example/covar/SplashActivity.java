package com.example.covar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private Animation topAnim, bottomAnim;
    private ImageView splashIcon;
    private TextView txtAppName, txtAppMotto;

    private static int TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        hideActionBars();
        setAnimation();
        navigateToNewActivity();
    }

    private void hideActionBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void setAnimation() {
        topAnim = AnimationUtils.loadAnimation(this, R.anim.splash_top_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.splash_bottom_anim);

        splashIcon = findViewById(R.id.splashImg);
        txtAppName = findViewById(R.id.txt_appl_name);
        txtAppMotto = findViewById(R.id.txt_app_motto);

        splashIcon.setAnimation(topAnim);
        txtAppName.setAnimation(bottomAnim);
        txtAppMotto.setAnimation(bottomAnim);
    }

    private void navigateToNewActivity() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(splashIcon, "logo_image");
            pairs[1] = new Pair<View, String>(txtAppName, "logo_text");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
            startActivity(intent, options.toBundle());
        }, TIMEOUT);
    }


}