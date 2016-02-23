package com.malytic.altituden.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.malytic.altituden.R;

public class FormFragment extends Fragment {

    private Spinner spinnerGender;
    private int gender;
    private SharedPreferences preferences;
    private EditText editWeight;
    private EditText editAge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editWeight = (EditText) getView().findViewById(R.id.form_weight);
        editAge = (EditText) getView().findViewById(R.id.form_age);

        initEditText();
        addListenerToSpinner();
        addListenerToSaveButton();
        addListenerToClearButton();
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
     * Initiates the EditText-boxes
     * Removes the placeholder-text and sets the
     * text to be black when touched
     */
    public void initEditText() {
        editWeight.setTextColor(Color.GRAY);
        editAge.setTextColor(Color.GRAY);

        editWeight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editWeight.setText("");
                editWeight.setTextColor(Color.BLACK);
                return false;
            }
        });

        editAge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editAge.setText("");
                editAge.setTextColor(Color.BLACK);
                return false;
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
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                try {
                    int weight = Integer.parseInt(((EditText) getView().findViewById(R.id.form_weight)).getText().toString());
                    editor.putInt("weight", weight);
                } catch (Exception e) {
                    editor.remove("weight");
                }

                try {
                    int age = Integer.parseInt(((EditText) getView().findViewById(R.id.form_age)).getText().toString());
                    editor.putInt("age", age);
                } catch (Exception e) {
                    editor.remove("age");
                }
                editor.putInt("gender", gender);
                editor.apply();

                Toast.makeText(getActivity(), "Your settings have been saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adds listener to clear-button
     * Clears all old settings
     * Shows toast
     */
    public void addListenerToClearButton() {
        (getView().findViewById(R.id.form_buttonClear)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();

                editWeight.setText("ex. 230kg");
                editWeight.setTextColor(Color.GRAY);

                editAge.setText("ex. 459 years");
                editAge.setTextColor(Color.GRAY);

                spinnerGender.setSelection(0);

                Toast.makeText(getActivity(), "Your settings have been cleared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * If there are saved values, restore these.
     */
    public void restoreValues() {
        if (!(preferences.getInt("weight", -1) == -1)) {
            editWeight.setText("" + preferences.getInt("weight", -1));
            editWeight.setTextColor(Color.BLACK);
        }
        if (!(preferences.getInt("age", -1) == -1)) {
            editAge.setText("" + preferences.getInt("age", -1));
            editAge.setTextColor(Color.BLACK);
        }
        if (!(preferences.getInt("gender", -1) == -1)) {
            spinnerGender.setSelection(preferences.getInt("gender", -1));
        }
    }

    /**
     * @return the saved age, -1 if no weight exists
     */
    public int getAge() {
        return preferences.getInt("age", -1);
    }

    /**
     * @return the saved weight, -1 if no value exists
     */
    public int getWeight() {
        return preferences.getInt("weight", -1);
    }

    /**
     * @return the saved gender, null if no value exists
     */
    public String getGender() {
        if ((preferences.getInt("gender", -1) == -1)) {
            return null;
        } else if (preferences.getInt("gender", -1) == 0) {
            return "female";
        } else {
            return "male";
        }
    }
}
