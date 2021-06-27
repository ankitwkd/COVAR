package com.example.covar.utils;

import android.content.Context;
import android.util.Range;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.material.textfield.TextInputLayout;

import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class Validator {
    private AwesomeValidation mAwesomeValidation;
    private Context context;
    public Validator(Context context){
        mAwesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);
        this.context = context;
    }

    public void addValidation(TextInputLayout textInputLayout, String regex, String errorMsg){
        mAwesomeValidation.addValidation(textInputLayout, regex, errorMsg);
    }

    public boolean validate(){
        return mAwesomeValidation.validate();
    }

    public void setStyle(int id){
        mAwesomeValidation.setTextInputLayoutErrorTextAppearance(id);
    }
}
