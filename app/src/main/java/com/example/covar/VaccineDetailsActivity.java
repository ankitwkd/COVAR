package com.example.covar;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class VaccineDetailsActivity extends AppCompatActivity {

    private AutoCompleteTextView vaccineDropdown;
    ArrayList<String> vaccineNames = new ArrayList<>();
    private AutoCompleteTextView doseDropdown;
    ArrayList<String> doses = new ArrayList<>();
    private TextInputEditText dateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaccine_details);
        wireUI();


        hideActionBars();
        setStatusBarColor();
    }

    private void wireUI() {
        //VACCINE NAMES
        vaccineDropdown = findViewById(R.id.vaccine_dd);
        vaccineNames.add("Pfizer");
        vaccineNames.add("Moderna");
        ArrayAdapter<String> vaccineNamesArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, vaccineNames);
        vaccineDropdown.setAdapter(vaccineNamesArrayAdapter);
        //DOSES
        doseDropdown = findViewById(R.id.doses_dd);
        doses.add("1");
        doses.add("2");
        ArrayAdapter<String> dosesArrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, doses);
        doseDropdown.setAdapter(dosesArrayAdapter);
        //VACCINE DATE
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("VACCINATION DATE").build();

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
}