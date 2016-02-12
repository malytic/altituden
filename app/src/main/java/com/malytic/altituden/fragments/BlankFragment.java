package com.malytic.altituden.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.malytic.altituden.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class BlankFragment extends Fragment {
    private String baseURL = "https://maps.googleapis.com/maps/api/elevation/json";
    private String elevationURL;
    private String location;
    private String outputStream;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Toast.makeText(getContext(), "Hej hej", Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chartElevation();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        Toast.makeText(getContext(), "Hej hej", Toast.LENGTH_LONG).show();
        return inflater.inflate(R.layout.fragment_blank, container, false);

    }

    public void chartElevation() throws IOException {

        location = "57.681385,11.985651";
        elevationURL = baseURL + "?locations=" + location +"&key=" + "AIzaSyDVDLVd-y_ExGMGttuQdImSaZOFQVMJeT8";
        URL finishedURL = new URL(elevationURL);
        //Log.d("URL", ""+finishedURL);

        HttpURLConnection conn = (HttpURLConnection) finishedURL.openConnection();


        conn.setRequestMethod("GET");
        InputStream is = conn.getInputStream();

        Log.d("URL", "" + conn.getResponseCode());
        //int tst = conn.getResponseCode();
        // Toast.makeText(getContext(), "KOD:", Toast.LENGTH_LONG).show();

        //Toast.makeText(getContext(), conn.getResponseCode(), Toast.LENGTH_LONG).show();


        //conn.setRequestMethod("GET");
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while((line = reader.readLine())!= null) {
            outputStream += line;
        }
        //Log.d("",outputStream);

    }
}