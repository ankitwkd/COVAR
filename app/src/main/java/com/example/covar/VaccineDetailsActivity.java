package com.example.covar;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.covar.data.User;
import com.example.covar.utils.ReminderBroadcast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VaccineDetailsActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    ArrayList<String> vaccineNames = new ArrayList<>();
    ArrayList<String> doses = new ArrayList<>();
    ArrayAdapter<String> dosesArrayAdapter;
    FirebaseUser currUser;
    User user;
    private AutoCompleteTextView vaccineDropdown;
    private AutoCompleteTextView doseDropdown;
    private TextInputEditText dateLayout;
    //Firebase database
    private DatabaseReference mDatabase;
    //Firebase authentication
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_details);
        wireUI();

        //Initialize Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        currUser = mAuth.getCurrentUser();

        fillOldData();

        hideActionBars();
        setStatusBarColor();
    }

    private void wireUI() {
        //VACCINE NAMES
        vaccineDropdown = findViewById(R.id.vaccine_name);
        vaccineNames.add("Pfizer");
        vaccineNames.add("Moderna");
        ArrayAdapter<String> vaccineNamesArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, vaccineNames);
        vaccineDropdown.setAdapter(vaccineNamesArrayAdapter);
        //DOSES
        doseDropdown = findViewById(R.id.doses_dd);
        doses.add("1");
        dosesArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, doses);
        doseDropdown.setAdapter(dosesArrayAdapter);
        //VACCINE DATE
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("VACCINATION DATE")
                .build();

        dateLayout = findViewById(R.id.date_selector);
        dateLayout.setOnFocusChangeListener((x, hasFocus) -> {
            if (hasFocus) {

                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
            }
        });
        datePicker.addOnPositiveButtonClickListener(selection -> {
            dateLayout.setText(datePicker.getHeaderText());

        });


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

    /**
     * Method to load data of current user for user-linking/validation purpose.
     */
    private void fillOldData() {
        String username = currUser.getEmail().split("@")[0];
        mDatabase.child("users").child(username).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(VaccineDetailsActivity.this, "Error fetching data" + task.getException()
                                    .getMessage(), Toast.LENGTH_SHORT)
                                    .show();
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            try {
                                user = task.getResult().getValue(User.class);
                                if (user.getVaccinationDate1() != null) {
                                    dosesArrayAdapter.remove("1");
                                    dosesArrayAdapter.add("2");
                                    doseDropdown.setAdapter(dosesArrayAdapter);
                                }
                            } catch (Exception e) {
                                Toast.makeText(VaccineDetailsActivity.this, "Fetching old data failed" + e.getMessage(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                });
    }

    /**
     * Method to update the vaccination details of user in firebase real-time database
     * @param view
     */
    public void saveVaccineDetails(View view) {
        try {
            String username = currUser.getEmail().split("@")[0];
            String vaccineName = vaccineDropdown.getText().toString();
            String doseNumber = doseDropdown.getText().toString();
            String dateString = dateLayout.getText().toString();
            if (vaccineName.isEmpty() || doseNumber.isEmpty() || dateString.isEmpty()) {
                Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if(user.getVaccineName()!=null && !vaccineName.equalsIgnoreCase(user.getVaccineName())){
                Toast.makeText(this, "Please choose same vaccine as that of first dose", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if(user.getVaccinationDate1()!=null){
                Date date1 = new Date(user.getVaccinationDate1());
                Date date2 = new Date(dateString);
                if(date2.compareTo(date1)<=0){
                    Toast.makeText(this, "Please choose a date after vaccine first dose", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
            user.setVaccineName(vaccineDropdown.getText().toString());
            user.setDose(doseDropdown.getText().toString());
            if (user.getDose().equalsIgnoreCase("1")) {
                user.setVaccinationDate1(dateLayout.getText().toString());
                setReminder(user.getVaccinationDate1());
            } else if (user.getDose().equalsIgnoreCase("2")) {
                user.setVaccinationDate2(dateLayout.getText().toString());
            }
            mDatabase.child("users").child(username).setValue(user);
            Toast.makeText(VaccineDetailsActivity.this, "Saved successfully", Toast.LENGTH_SHORT)
                    .show();
            finish();
        } catch (Exception e) {
            Toast.makeText(VaccineDetailsActivity.this, "Save to DB failed" + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /**
     * Method to set reminder for tentative date for second dose.
     * @param vaccinationDate1
     */
    private void setReminder(String vaccinationDate1) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date(vaccinationDate1));
            c.add(Calendar.DATE, 30);
            String vaccineDate2 = new SimpleDateFormat("d MMMM yyyy").format(c.getTime());
            createNotificationChannel();
            Intent intent = new Intent(this, ReminderBroadcast.class);
            intent.putExtra("vaccineDate2", vaccineDate2);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            long timeAtButtonClick = System.currentTimeMillis();

            long tenSecondsInMillis = 1000 * 10;

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + tenSecondsInMillis, pendingIntent);
            Toast.makeText(this, "Reminder created", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Could not create reminder | " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to create notification channel so as to receive the notification for 2nd dose.
     */
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReminderChannel";
            String description = "Hello channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyAnkit", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Method to confirm the approval of permissions required to read/write storage.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}