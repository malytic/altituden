package com.malytic.altituden.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.malytic.altituden.HttpRequestHandler;
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
        try {
            chartElevation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inflater.inflate(R.layout.fragment_blank, container, false);

    }


    public void chartElevation() throws IOException {

        HttpRequestHandler httpReq = new HttpRequestHandler();
        location = "57.681385,11.985651";
        elevationURL = baseURL + "?locations=" + location + "&key=" + getResources().getString(R.string.google_elevation_key);
        httpReq.elevationRequest(getContext(), elevationURL);
        //InputStream is = conn.getInputStream();

        /*String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while((line = reader.readLine())!= null) {
            outputStream += line;
        }
        //Log.d("",outputStream);

    }*/
    }
}