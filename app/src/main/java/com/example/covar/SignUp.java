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

import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.covar.data.User;
import com.example.covar.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.Range;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    Context context;

    //Firebase authentication
    private FirebaseAuth mAuth;

    //Firebase database
    private DatabaseReference mDatabase;

    //Sign up fields
    private EditText editFullName, editUsername, editPassword, editAge, editMobileNum;

    //Text input layouts
    private TextInputLayout layoutFullName, layoutUsername, layoutPassword, layoutAge,
            layoutMobileNum;

    private Validator validator;


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

        //Initialize layouts
        layoutFullName  = findViewById(R.id.fullNameLayout);
        layoutUsername  = findViewById(R.id.usernameLayout);
        layoutPassword  = findViewById(R.id.passwordLayout);
        layoutAge       = findViewById(R.id.ageLayout);
        layoutMobileNum = findViewById(R.id.mobileNumberLayout);

        //Adding validators
        validator = new Validator(this);
        //validator.setStyle(R.style.orangeError);
        validator.addValidation(layoutFullName, "[a-zA-Z\\s]+", "Only letters and spaces allowed");
        validator.addValidation(layoutUsername, "[A-Za-z0-9]+", "Only letters and numbers allowed");
        validator.addValidation(layoutPassword, getString(R.string.regex_password), getString(R.string.err_invalid_password));
        validator.addValidation(layoutMobileNum, "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", "Expected: 000-000-0000");
        validator.addValidation(layoutAge, "^(?:[1-9][0-9]?|1[01][0-9]|120)$", "Age(1-120)");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        if(!validator.validate()){
            Toast.makeText(this, "Please fix highlighted errors", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = editUsername.getText().toString();
        //Make it a valid email address
        username = username.concat(getString(R.string.domain_name));
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
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null) {
            if(!saveUserDetails()){
                return;
            }
            Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT)
                    .show();
            //TODO fill username automatically by passing username here.
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(context, "Sign up failed | Please retry", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private boolean saveUserDetails() {
        try {
            String username = editUsername.getText().toString().toLowerCase();
            String fullName = editFullName.getText().toString();
            String age = editAge.getText().toString();
            String mobileNum = editMobileNum.getText().toString();
            User user = new User(fullName, age, mobileNum);
            mDatabase.child("users").child(username).setValue(user);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Save to DB failed" + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
        return false;
    }
}