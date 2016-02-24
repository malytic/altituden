package com.malytic.altituden.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.malytic.altituden.MainActivity;
import com.malytic.altituden.R;

public class FormFragment extends Fragment {

    private Spinner spinnerGender;
    private int gender;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private EditText editWeight;
    private EditText editAge;
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
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();
        editWeight = (EditText) getView().findViewById(R.id.form_weight);
        editAge = (EditText) getView().findViewById(R.id.form_age);

        addListenerToSpinner();
        addListenerToSaveButton();
        addListenerToRestoreButton();
        restoreValues();
    }

    /**
     * Adds listener to gender-spinner.
     * Female is referred to as 0 and male as 1
     */
    public void addListenerToSpinner() {
        spinnerGender = (Spinner) getView().findViewById(R.id.form_gender);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                if (position == 0) {
                    gender = 0;
                } else {
                    gender = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * Adds listener to save-button
     * Clears all old settings and saves the new settings
     * Has try/catch clause to handle if its not and int
     * entered in the textbox. Shows toast
     */
    public void addListenerToSaveButton() {
        (getView().findViewById(R.id.form_buttonSave)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
                        MainActivity.pathData.updateCalorieCount(getContext());
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Age not valid", Toast.LENGTH_SHORT).show();
                    valid = false;
                }

                if(valid)
                    Toast.makeText(getActivity(), "Your settings have been saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adds listener to restore-button
     * Clears all old settings
     * Shows toast
     */
    public void addListenerToRestoreButton() {
        (getView().findViewById(R.id.form_buttonRestore)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editWeight.setText(""+standardWeight);
                editAge.setText(""+standardAge);

                spinnerGender.setSelection(standardGender);

                //Enter standard values
                editor.putInt("weight", standardWeight);
                editor.putInt("age", standardAge);
                editor.putInt("gender", standardGender);

                Toast.makeText(getActivity(),
                        "Your settings have been restored to standard. Now save if you want to keep changes.",
                        Toast.LENGTH_SHORT).show();
            }
        });
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

        if (preferences.getInt("gender", -1) == -1) {
            editor.putInt("gender", 1);
            spinnerGender.setSelection(1);
        }else {
            spinnerGender.setSelection(preferences.getInt("gender", -1));
        }

        editor.apply();
    }
}