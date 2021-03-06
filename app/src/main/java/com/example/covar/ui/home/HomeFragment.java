package com.example.covar.ui.home;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.covar.R;
import com.example.covar.VaccineDetailsActivity;
import com.example.covar.data.User;
import com.example.covar.utils.PDFUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    //Requesting permissions with 200 code, so that we can check if user approved them.
    private static final int PERMISSION_REQUEST_CODE = 200;
    PDFUtil pdfUtil;
    private Button fillUpButton;
    private FloatingActionButton download;
    private TextView tvHome;
    private TextView fullName, msg, firstDose, secondDose;
    private CardView cardView;
    //Firebase database
    private DatabaseReference mDatabase;
    //Firebase authentication
    private FirebaseAuth mAuth;
    //Firebase current user
    private FirebaseUser currUser;
    private User user;
    private ArrayList<String> pdfText;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        wireUI(root);

        //Initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        currUser = mAuth.getCurrentUser();
        download.setOnClickListener(this::downloadAsPDF);
        pdfText = new ArrayList<String>();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        collectData();
    }

    /**
     * Method to wire all the components to the fragment view.
     *
     * @param view
     */
    private void wireUI(View view) {
        fillUpButton = view.findViewById(R.id.btnFillUp);
        fillUpButton.setOnClickListener(this::fillUpOnClickListener);
        tvHome = view.findViewById(R.id.welcomeUser);
        download = view.findViewById(R.id.download);
        fullName = view.findViewById(R.id.cardfullname);
        msg = view.findViewById(R.id.msg);
        firstDose = view.findViewById(R.id.firstdose);
        secondDose = view.findViewById(R.id.seconddose);
        cardView = view.findViewById(R.id.card_view);
    }

    /**
     * Method triggered on click of download FAB button. It triggers the download
     * of all the details as pdf into the internal storage.
     *
     * @param view
     */
    private void downloadAsPDF(View view) {
        if (pdfUtil.generatePDF()) {
            Toast.makeText(getActivity(), "PDF file saved to Downloads", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Could not generate pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to fetch all the updated details of the user to display in the dashboard
     * and initialize the user object used throughout the code for validation.
     */

    private void collectData() {
        String username = currUser.getEmail().split("@")[0];
        cardView.setVisibility(getView().INVISIBLE);
        //progressBar.setVisibility(getView().VISIBLE);
        mDatabase.child("users").child(username).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Error loading data" + task.getException()
                                    .getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            user = task.getResult().getValue(User.class);
                            fullName.setText(user.getFullName().toUpperCase() + ",");
                            firstDose.setText("1st dose received:  NA");
                            secondDose.setText("2nd dose received: NA");
                            String display_text = "";
                            Date vaccineDate1 = null, vaccineDate2 = null;
                            if (user.getVaccinationDate1() != null) {
                                vaccineDate1 = new Date(user.getVaccinationDate1());
                            }
                            if (user.getVaccinationDate2() != null) {
                                vaccineDate2 = new Date(user.getVaccinationDate2());
                            }

                            if (vaccineDate1 != null && vaccineDate2 == null) {
                                display_text += "You took your first dose on ";
                                display_text += user.getVaccinationDate1() + "\n";
                                firstDose.setText("1st dose received: " + user.getVaccinationDate1());
                                display_text += "Your tentative date for second dose is on ";
                                Calendar c = Calendar.getInstance();
                                c.setTime(vaccineDate1);
                                c.add(Calendar.DATE, 30);
                                display_text += new SimpleDateFormat("d MMMM yyyy").format(c.getTime()) + "\n";
                                secondDose.setText("Tentative second dose date: " + new SimpleDateFormat("d MMMM yyyy").format(c.getTime()));
                                msg.setText("Thank you for completing your COVID-19 vaccine. Your second dose is still due.");
                            } else if (vaccineDate1 != null && vaccineDate2 != null) {
                                fillUpButton.setVisibility(View.INVISIBLE);
                                display_text += "You took your first dose on ";
                                display_text += user.getVaccinationDate1() + "\n";
                                firstDose.setText("1st dose received: " + user.getVaccinationDate1());
                                display_text += "You took your second dose on ";
                                display_text += user.getVaccinationDate2() + "\n";
                                secondDose.setText("2nd dose received: " + user.getVaccinationDate2());
                                msg.setText("Thank you for completing your COVID-19\nvaccine.");

                            } else if (vaccineDate1 == null && vaccineDate2 == null) {
                                display_text += "As per records, you have not taken vaccine.\nPlease fill the form.";
                                msg.setText(display_text);
                            }
                            pdfText = new ArrayList<String>();
                            pdfText.add(fullName.getText().toString());
                            pdfText.add(msg.getText().toString());
                            pdfText.add(firstDose.getText().toString());
                            pdfText.add(secondDose.getText().toString());
                            cardView.setVisibility(getView().VISIBLE);
                            pdfUtil = new PDFUtil(getResources(), R.drawable.vaccine_splash_logo, user, R.color.app_blue, getActivity(), pdfText);
                            if (pdfUtil.checkPermission()) {
                                //Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                            } else {
                                pdfUtil.requestPermission();
                            }
                            //tvHome.setText(display_text);
                        }
                    }
                });

    }

    /**
     * Method triggered on click of fill up form button.
     * @param view
     */
    private void fillUpOnClickListener(View view) {
        Intent vaccineActivity = new Intent(getActivity(), VaccineDetailsActivity.class);
        startActivity(vaccineActivity);
    }

    /**
     * Method to check if the permissions asked were accepted.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(getActivity(), "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}