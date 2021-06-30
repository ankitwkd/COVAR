package com.example.covar.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.covar.LoginActivity;
import com.example.covar.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {

    private FloatingActionButton logout;

    //Firebase authentication
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        final TextView textView = root.findViewById(R.id.text_logout);
        logout = root.findViewById(R.id.fabLogout);
        logout.setOnClickListener(this::logout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        return root;
    }

    private void logout(View view) {
        mAuth.signOut();
        getActivity().finish();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }


}