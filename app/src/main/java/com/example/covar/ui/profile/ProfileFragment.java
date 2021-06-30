package com.example.covar.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.covar.R;
import com.example.covar.data.User;
import com.example.covar.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {
    private EditText editMobileNum, editAge;
    private Button btnUpdateProf;

    //Firebase database
    private DatabaseReference mDatabase;

    //Firebase authentication
    private FirebaseAuth mAuth;

    //Firebase current user
    private FirebaseUser currUser;

    private User user;

    private Validator validator;
    private TextInputLayout mobileNumLayout;
    private TextInputLayout ageLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        wireUI(root);

        //Initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        currUser = mAuth.getCurrentUser();

        validator = new Validator(getActivity());
        validator.addValidation(mobileNumLayout, "^[0-9]{3}-[0-9]{3}-[0-9]{4}$",
                "Expected: 000-000-0000");
        validator.addValidation(ageLayout, "^(?:[1-9][0-9]?|1[01][0-9]|120)$",
                "Age(1-120)");

        fillOldData();

        return root;
    }

    /**
     * Method to pre-set the fields with existing data of user.
     */
    private void fillOldData() {
        String username = currUser.getEmail().split("@")[0];
        mDatabase.child("users").child(username).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Error loading data" +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            user = task.getResult().getValue(User.class);
                            editMobileNum.setText(user.getMobileNum());
                            editAge.setText(user.getAge());
                        }
                    }
                });

    }

    /**
     * Method to wire the UI components to the layout view.
     * @param root
     */
    private void wireUI(View root) {
        editMobileNum = root.findViewById(R.id.mobileNumber);
        editAge = root.findViewById(R.id.age);
        btnUpdateProf = root.findViewById(R.id.changeProfileBtn);

        mobileNumLayout = root.findViewById(R.id.mobileNumberLayout);
        ageLayout = root.findViewById(R.id.ageLayout);

        btnUpdateProf.setOnClickListener(this::updateProfile);
    }

    /**
     * Method to update the newly entered details into the database and update the dashboard.
     * @param view
     */
    private void updateProfile(View view) {
        try {
            if (!validator.validate()) {
                return;
            }
            String newMobileNum = editMobileNum.getText().toString();
            String newAge = editAge.getText().toString();
            String username = currUser.getEmail().split("@")[0];

            user.setMobileNum(newMobileNum);
            user.setAge(newAge);
            mDatabase.child("users").child(username).setValue(user);
            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT)
                    .show();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setTitle("Home");
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Profile update failed | " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

}