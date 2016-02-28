package com.malytic.altituden.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.malytic.altituden.MainActivity;
import com.malytic.altituden.R;

public class ProfileFragment extends Fragment {

    private int gender;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private EditText editWeight;
    private EditText editAge;
    private RadioButton maleButton, femaleButton;
    private int standardWeight = 80;
    private int standardAge = 25;
    private int standardGender = 1;

    private int minWeight = 1;
    private int maxWeight = 1000;
    private int minAge = 1;
    private int maxAge = 110;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        editWeight = (EditText) getView().findViewById(R.id.form_weight);
        editAge = (EditText) getView().findViewById(R.id.form_age);
        maleButton = (RadioButton) getView().findViewById(R.id.radioButtonMale);
        femaleButton = (RadioButton) getView().findViewById(R.id.radioButtonFemale);

        // hide cursor
        editAge.setCursorVisible(false);
        editWeight.setCursorVisible(false);

        restoreValues();
    }

    /**
     * when a radiobutton is clicked, this method is called.
     * Listener is created in xml.
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonMale:
                if (checked)
                    gender = 1;
                break;
            case R.id.radioButtonFemale:
                if (checked)
                    gender = 0;
                break;
        }
    }

    /**
     * Clears all old settings and saves the new settings
     * Has try/catch clause to handle if its not and int
     * entered in the textbox. Shows toast
     */
    public void saveClick(View view) {
        boolean valid = true;

        editor.remove("gender");
        editor.putInt("gender", gender);
        editor.apply();

        try {
            int weight = Integer.parseInt(((EditText) getView().findViewById(R.id.form_weight)).getText().toString());
            if(weight < minWeight || weight > maxWeight){
                Toast.makeText(getActivity(), "Weight not valid", Toast.LENGTH_SHORT).show();
                valid = false;
            }else{
                editor.remove("weight");
                editor.apply();
                editor.putInt("weight", weight);
                editor.apply();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Weight not valid", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        try {
            int age = Integer.parseInt(((EditText) getView().findViewById(R.id.form_age)).getText().toString());
            if(age < minAge || age > maxAge){
                Toast.makeText(getActivity(), "Age not valid", Toast.LENGTH_SHORT).show();
                valid = false;
            }else{
                editor.remove("age");
                editor.apply();
                editor.putInt("age", age);
                editor.apply();
                MainActivity.pathData.updateCalorieCount();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Age not valid", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if(valid)
            Toast.makeText(getActivity(), "Your settings have been saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Clears all old settings
     * Shows toast
     */
    public void restoreClick(View v) {
        editWeight.setText(""+standardWeight);
        editAge.setText("" + standardAge);
        if(!maleButton.isChecked())
            maleButton.toggle();

        //Enter standard values
        editor.putInt("weight", standardWeight);
        editor.putInt("age", standardAge);
        editor.putInt("gender", standardGender);

        Toast.makeText(getActivity(),
                "Your settings have been restored to standard. Now save if you want to keep changes.",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * If there are saved values, restore these.
     * If not, set standard values
     */
    public void restoreValues() {
        if (preferences.getInt("weight", -1) == -1) {
            editor.putInt("weight", standardWeight);
        } else {
            editWeight.setText("" + preferences.getInt("weight", -1));
        }

        if (preferences.getInt("age", -1) == -1) {
            editor.putInt("age", standardAge);
        } else{
            editAge.setText("" + preferences.getInt("age", -1));
        }
        switch (preferences.getInt("gender", -1)){
            case 1:
                maleButton.toggle();
                break;
            case 0:
                femaleButton.toggle();
                break;
            default:
                editor.putInt("gender",1);
                break;
        }

        editor.apply();
    }

    public void onClick(View view) {
        editAge.setCursorVisible(false);
        editWeight.setCursorVisible(false);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        switch (view.getId()) {
            case R.id.form_weight:
                editWeight.setCursorVisible(true);
                break;
            case R.id.form_age:
                editAge.setCursorVisible(true);
                break;
            case R.id.radioButtonMale:
                onRadioButtonClicked(view);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.radioButtonFemale:
                onRadioButtonClicked(view);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.form_buttonSave:
                saveClick(view);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.form_buttonRestore:
                restoreClick(view);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            default:
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
        }
    }
}
