package com.malytic.altituden.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.malytic.altituden.R;
import com.malytic.altituden.events.ElevationUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mikae on 2016-02-19.
 */
public class FormFragment extends Fragment {

    private Spinner spinnerGender;
    private Button  btnSave;
    private EditText editWeight;
    private int weight;
    private String gender;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        //initEditText();
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onStart(){
        EventBus.getDefault().register(this);
        super.onStart();
        final Button button = (Button)getView().findViewById(R.id.buttonSave);
        initEditText();
        addListenerToSpinner();
        addListenerToButton();

    }
    public void initEditText() {

        final EditText editWeight = (EditText)getView().findViewById(R.id.ET_User);
        editWeight.setTextColor(Color.GRAY);
        editWeight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editWeight.setText("");
                editWeight.setTextColor(Color.BLACK);
                return false;
            }
        });
        }
    public void addListenerToSpinner (){
        spinnerGender = (Spinner) getView().findViewById(R.id.spinnerG);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "valt", Toast.LENGTH_LONG).show();
                gender = spinnerGender.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
    }
    public void addListenerToButton(){
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weight = Integer.parseInt(editWeight.getText().toString());
                Toast.makeText(getActivity(),weight + gender, Toast.LENGTH_LONG).show();
            }
        });
    }
 }
