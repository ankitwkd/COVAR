package com.example.covar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    Context context;

    //Firebase authentication
    private FirebaseAuth mAuth;

    //Sign up fields
    private EditText editFullName, editUsername, editPassword, editAge, editMobileNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        hideActionBars();
        setStatusBarColor();

        context = getApplicationContext();

        // Initialize fields
        editFullName = findViewById(R.id.fullName);
        editUsername = findViewById(R.id.username);
        editPassword = findViewById(R.id.password);
        editAge = findViewById(R.id.age);
        editMobileNum = findViewById(R.id.mobileNumber);

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

    private void hideActionBars() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void signUp(View view) {
        //TODO validation
        /*if(!isValid()){
            return;
        }*/
        String username = editUsername.getText().toString();
        //Make it a valid email address
        username = username.concat("@covar.com");
        String password = editPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, navigate to login screen
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null) {
            Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT)
                    .show();
            //TODO fill username automatically by passing username here.
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(context, "Sign up failed | Please retry later", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}