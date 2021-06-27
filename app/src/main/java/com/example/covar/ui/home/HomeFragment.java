package com.example.covar.ui.home;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.covar.R;
import com.example.covar.VaccineDetailsActivity;
import com.example.covar.data.User;
import com.example.covar.utils.PDFUtil;
import com.example.covar.utils.ReminderBroadcast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    private Button fillUpButton;
    private FloatingActionButton download;
    private FloatingActionButton reminder;
    private TextView tvHome;

    //Firebase database
    private DatabaseReference mDatabase;

    //Firebase authentication
    private FirebaseAuth mAuth;

    //Firebase current user
    private FirebaseUser currUser;

    private User user;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        wireUI(root);

        //Initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        currUser = mAuth.getCurrentUser();

        download.setOnClickListener(this :: downloadAsPDF);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        collectData();
    }

    private void wireUI(View view) {
        fillUpButton = view.findViewById(R.id.btn_home_fileup);
        fillUpButton.setOnClickListener(this::fillUpOnClickListener);
        tvHome = view.findViewById(R.id.txt_home);
        download = view.findViewById(R.id.download);
    }

    private void downloadAsPDF(View view) {
        PDFUtil pdfUtil = new PDFUtil(getResources(), R.drawable.vaccine_splash_logo, user, R.color.app_blue, getActivity());
        if (pdfUtil.checkPermission()) {
            Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            pdfUtil.requestPermission();
        }
        if(pdfUtil.generatePDF()){
            Toast.makeText(getActivity(), "PDF file saved to Downloads", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), "Could not generate pdf file", Toast.LENGTH_SHORT).show();
        }
    }


    private void collectData() {
        String username = currUser.getEmail().split("@")[0];
        mDatabase.child("users").child(username).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Error loading data" + task.getException()
                                    .getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            user = task.getResult().getValue(User.class);

                            String display_text = "";
                            Date vaccineDate1 = null, vaccineDate2 = null;
                            if(user.getVaccinationDate1()!=null) {
                                vaccineDate1 = new Date(user.getVaccinationDate1());
                            }
                            if(user.getVaccinationDate2()!=null) {
                                vaccineDate2 = new Date(user.getVaccinationDate2());
                            }

                            if(vaccineDate1 != null && vaccineDate2 == null) {
                                display_text += "You took your first dose on ";
                                display_text += user.getVaccinationDate1() + "\n";
                                display_text += "Your tentative date for second dose is on ";
                                Calendar c = Calendar.getInstance();
                                c.setTime(vaccineDate1);
                                c.add(Calendar.DATE, 30);
                                display_text += new SimpleDateFormat("d MMMM yyyy").format(c.getTime()) + "\n";
                            }else if(vaccineDate1 != null && vaccineDate2 != null){
                                display_text += "You took your first dose on ";
                                display_text += user.getVaccinationDate1() + "\n";
                                display_text += "You took your second dose on ";
                                display_text += user.getVaccinationDate2() + "\n";
                                fillUpButton.setVisibility(View.GONE);
                            }else if(vaccineDate1 == null && vaccineDate2 == null){
                                display_text += "As per records, you have not taken vaccine. Please fill the form.";
                            }
                            tvHome.setText(display_text);
                        }
                    }
                });

    }

    private void fillUpOnClickListener(View view) {
        Intent vaccineActivity = new Intent(getActivity(), VaccineDetailsActivity.class);
        startActivity(vaccineActivity);

    }

}