package com.example.covar.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.covar.R;
import com.example.covar.VaccineDetailsActivity;

public class HomeFragment extends Fragment {
    private Button fillUpButton;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        wireUI(root);
        return root;
    }

    private void wireUI(View view) {
        fillUpButton = view.findViewById(R.id.btn_home_fileup);
        fillUpButton.setOnClickListener(this::fillUpOnClickListener);
    }

    private void fillUpOnClickListener(View view) {
        Intent vaccineActivity = new Intent(getActivity(), VaccineDetailsActivity.class);
        startActivity(vaccineActivity);

    }

}