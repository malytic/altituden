package com.malytic.altituden.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.malytic.altituden.Events.ElevationEvent;
import com.malytic.altituden.HttpRequestHandler;
import com.malytic.altituden.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class BlankFragment extends Fragment {
    private String baseURL = "https://maps.googleapis.com/maps/api/elevation/json";
    private HttpRequestHandler httpReq;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        httpReq = new HttpRequestHandler(getContext());
        chartElevation();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);

    }


    public void chartElevation() {
        String location = "57.681385,11.985651";
        StringBuilder sb = new StringBuilder();
        sb.append(baseURL);
        sb.append("?locations=" + location);
        sb.append("&key=" + "AIzaSyDlo4aoZrAwVkMlx10GB-TzTRUPvGiiWxI");
        httpReq.elevationRequest(sb.toString());
    }

    @Subscribe
    public void onElevationResponseEvent(ElevationEvent response) {
        JSONArray elevationArray = null;
        String eString = null;
        Log.e("test",response.elevationResponse.toString());
        int altitude = 0;
        try {
            elevationArray = response.elevationResponse.getJSONArray("results");
            JSONObject eObj = elevationArray.optJSONObject(0);
            eString = eObj.getString("elevation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(eString != null) {
            altitude = Integer.parseInt(eString);
            Log.e("Altitude",Integer.toString(altitude));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}