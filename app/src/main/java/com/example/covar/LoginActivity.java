package com.example.covar;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login";
    private Button loginButton;
    private Button signUpButton;
    private ImageView image;
    private TextView welcomeText;
    private EditText editUsername, editPassword;

    //Firebase authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        wiringUIControls();
        hideActionBars();
        setStatusBarColor();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
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
        editUsername = findViewById(R.id.username);
        editPassword = findViewById(R.id.password);
    }

    private void loginClickListener(View view) {
        String username = editUsername.getText().toString().concat(getString(R.string.domain_name));
        String password = editPassword.getText().toString();
        validateLogin(username,password);
    }

    private void validateLogin(String username, String password) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null) {
            Toast.makeText(this, "Log in successful", Toast.LENGTH_SHORT)
                    .show();
            Intent dashboardIntent = new Intent(getApplicationContext(), Dashboard.class);

            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(image, "logo_image");
            pairs[1] = new Pair<View, String>(loginButton, "login_signup_transition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
            startActivity(dashboardIntent, options.toBundle());
        }else{
            Toast.makeText(this, "Authentication failed | Incorrect credentials", Toast.LENGTH_SHORT)
                    .show();
            editPassword.getText().clear();
        }
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
        pairs[2] = new Pair<View, String>(editUsername, "username_transition");
        pairs[3] = new Pair<View, String>(editPassword, "password_transition");
        pairs[4] = new Pair<View, String>(loginButton, "login_signup_transition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
        startActivity(signUpIntent, options.toBundle());
    }
}