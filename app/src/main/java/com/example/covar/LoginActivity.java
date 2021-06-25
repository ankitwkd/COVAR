package com.example.covar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
    }

    private void wiringUIControls() {
        loginButton = findViewById(R.id.loginBtn);
        signUpButton = findViewById(R.id.signupBtn);
        signUpButton.setOnClickListener(this::loginClickListener);

        image = findViewById(R.id.logoImage);
        welcomeText = findViewById(R.id.logo_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

    }

    private void hideActionBars() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    private void loginClickListener(View view) {
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