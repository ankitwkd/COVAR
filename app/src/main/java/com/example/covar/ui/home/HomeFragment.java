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

import com.example.covar.Dashboard;
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

import static androidx.core.content.ContextCompat.getSystemService;

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

    private static final int PERMISSION_REQUEST_CODE = 200;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        wireUI(root);

        //Initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        currUser = mAuth.getCurrentUser();
        collectData();

        download.setOnClickListener(this :: downloadAsPDF);
        reminder.setOnClickListener(this :: setReminder);
        return root;
    }

    private void wireUI(View view) {
        fillUpButton = view.findViewById(R.id.btn_home_fileup);
        fillUpButton.setOnClickListener(this::fillUpOnClickListener);
        tvHome = view.findViewById(R.id.txt_home);
        download = view.findViewById(R.id.download);
        reminder = view.findViewById(R.id.setReminder);
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

    private void setReminder(View view) {
        try {
            createNotificationChannel();
            Intent intent = new Intent(getActivity(), ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);

            long timeAtButtonClick = System.currentTimeMillis();

            long tenSecondsInMillis = 1000 * 10;

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + tenSecondsInMillis, pendingIntent);
            Toast.makeText(getActivity(), "Reminder created", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Could not create reminder | " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ReminderChannel";
            String description = "Hello channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyAnkit", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
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
                            String display_text = user.getFullName() + "\n" + user.getAge() + "\n"
                                    + user.getMobileNum() + '\n';
                            if(user.getVaccineName()!=null){
                                display_text += user.getVaccineName() + "\n";
                            }
                            if(user.getDose()!=null){
                                display_text += user.getDose() + " dose(s)\n";
                            }
                            if(user.getVaccinationDate()!=null){
                                display_text += user.getVaccinationDate() + "\n";
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