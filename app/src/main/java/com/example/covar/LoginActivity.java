package com.example.covar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private Button signUpButton;
    private ImageView image;
    private TextView welcomeText;
    private TextInputLayout username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        wiringUIControls();
        hideActionBars();
        setStatusBarColor();
    }

    //https://stackoverflow.com/questions/22192291/how-to-change-the-status-bar-color-in-android
    private void setStatusBarColor() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar));
    }

    private void wiringUIControls() {
        loginButton = findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(this::loginClickListener);
        signUpButton = findViewById(R.id.signupBtn);
        signUpButton.setOnClickListener(this::signUpClickListener);

        image = findViewById(R.id.logoImage);
        welcomeText = findViewById(R.id.logo_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

    }

    private void loginClickListener(View view) {
        Intent dashboardIntent = new Intent(getApplicationContext(), Dashboard.class);
        startActivity(dashboardIntent);
    }

    private void hideActionBars() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void signUpClickListener(View view) {
        Intent signUpIntent = new Intent(getApplicationContext(), SignUp.class);

        Pair[] pairs = new Pair[5];
        pairs[0] = new Pair<View, String>(image, "logo_image");
        pairs[1] = new Pair<View, String>(welcomeText, "logo_text");
        pairs[2] = new Pair<View, String>(username, "username_transition");
        pairs[3] = new Pair<View, String>(password, "password_transition");
        pairs[4] = new Pair<View, String>(loginButton, "login_signup_transition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
        startActivity(signUpIntent, options.toBundle());
    }
}