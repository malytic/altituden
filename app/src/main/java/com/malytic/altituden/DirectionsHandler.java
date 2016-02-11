package com.malytic.altituden;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONObject;

import java.net.URL;
import java.util.List;

/**
 * Created by William on 2016-02-09.
 */
public class DirectionsHandler {

    public static PolylineOptions GetDirectionsPolyline(LatLng origin, LatLng dest) {
        RequestQueue queue = Volley.newRequestQueue();
        return null;
    }

    private static String connect(URL url) {
        return null;

    }
    private static JSONObject stringToJson(String stringToParse){
        return null;
    }
}