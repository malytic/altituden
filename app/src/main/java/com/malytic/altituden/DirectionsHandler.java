package com.malytic.altituden;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by William on 2016-02-09.
 */
public class DirectionsHandler {

    public static PolylineOptions GetDirectionsPolyline(LatLng origin, LatLng dest) {
        URL url = null;
        try {
         } catch (MalformedURLException e) {
            Log.e("yolo","Failed to make URL");
            e.printStackTrace();
        }
        JSONObject json;
        PolylineOptions result;

        json = stringToJson(connect(url));

        List<LatLng> path;
        try {
            path = PolyUtil.decode((String) json.get("overview_polyline"));
            result = new PolylineOptions().addAll(path);
            return result;

        }catch (JSONException e) {
            Log.e("yolo","Failed to decode polyline");
            e.printStackTrace();
        }
        return null;
    }

    private static String connect(URL url) {
        try {
            Log.e("yolo","Trying to connect");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        }catch (MalformedURLException e1) {
            e1.printStackTrace();
            Log.e("yolo", "Bad url");
        } catch (IOException e1) {
            e1.printStackTrace();
            Log.e("yolo", "ioexception");
        }
        return null;

    }
    private static JSONObject stringToJson(String stringToParse){
        try {
            return new JSONObject(stringToParse);
        } catch (JSONException e) {
            Log.e("stringToJson", "Failed to convert string to JSON");
        }
        return null;
    }
}