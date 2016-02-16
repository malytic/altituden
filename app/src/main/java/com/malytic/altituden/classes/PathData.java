package com.malytic.altituden.classes;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.malytic.altituden.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 2016-02-16.
 */
public class PathData {

    private boolean isValid;
    public int length;
    public String encodedPolyline;
    public List<Double> elevation;
    public List<LatLng> points;

    public PathData(JSONObject obj) throws JSONException {
        isValid = false;
        encodedPolyline = extractEncodedPath(obj);
        elevation = null;
        points = PolyUtil.decode(encodedPolyline);
        length = extractPathLength(obj);
        isValid = true;
    }
    public PathData() {
        length = 0;
        encodedPolyline = null;
        elevation = null;
        points = null;
        isValid = false;
    }
    public void updatePath(JSONObject obj) throws JSONException {
        if(obj.getString("status").equals("OK")) {
            length = extractPathLength(obj);
            encodedPolyline = extractEncodedPath(obj);
            elevation = null;
            points = PolyUtil.decode(encodedPolyline);
            length = extractPathLength(obj);
            isValid = true;
        }
    }
    public void updateElevation(JSONObject obj) throws JSONException {
        elevation = extractElevation(obj);
    }
    public static int extractPathLength(JSONObject obj) throws JSONException {
       // System.out.println(obj.toString(4));
        JSONArray routesArray;
        routesArray = obj.getJSONArray("routes");
        JSONArray legsArray = null;
        for (int i = 0; i < routesArray.length(); i++) {
            JSONObject o = routesArray.getJSONObject(i);
            if (o != null) {
                legsArray = o.getJSONArray("legs");
            }
        }
        int pathLength = 0;
        // extract the JSONObject with overview_polyline
        if (legsArray != null) {
            for (int i = 0; i < legsArray.length(); i++) {
                JSONObject o = legsArray.optJSONObject(i);
                if (o != null) {
                    JSONObject distance = o.optJSONObject("distance");
                    pathLength = distance.getInt("value");
                }
            }
        }
        System.out.println("Pathlength: " + pathLength);
        return pathLength;
    }
    public static String extractEncodedPath(JSONObject obj) throws JSONException {
        JSONArray routesArray;
        routesArray = obj.getJSONArray("routes");
        String encodedOverviewPolyline = null;
        // extract the JSONObject with overview_polyline
        for(int i = 0; i < routesArray.length(); i++) {
            JSONObject o = routesArray.optJSONObject(i);
            if(o != null) {
                encodedOverviewPolyline = o.optJSONObject("overview_polyline").getString("points");
            }
        }
        return encodedOverviewPolyline;
    }
    public boolean isValid() {
        return isValid;
    }
    public static String getElevationUrl(String encodedPolyline, Context context) {
        String baseURL = "https://maps.googleapis.com/maps/api/elevation/json";
        StringBuilder sb = new StringBuilder();
        sb.append(baseURL);
        sb.append("?path=" + encodedPolyline);
        sb.append("&samples=" + 20);
        sb.append("&key=" + context.getResources().getString(R.string.google_server_key));
        return sb.toString();
    }
    public static String getElevationUrl(JSONObject obj, Context context) throws JSONException {

        String baseURL = "https://maps.googleapis.com/maps/api/elevation/json";
        StringBuilder sb = new StringBuilder();
        sb.append(baseURL);
        sb.append("?path=" + formatPoints(extractEncodedPath(obj)));
        sb.append("&samples=" + (int)(extractPathLength(obj) / 5));
        sb.append("&key=" + context.getResources().getString(R.string.google_server_key));
        return sb.toString();
    }
    public static String getDirectionsUrl(Context context, LatLng origin, LatLng dest) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin=" + origin.latitude + "," + origin.longitude);
        sb.append("&destination=" + dest.latitude + "," + dest.longitude);
        sb.append("&mode=walking");
        sb.append("&key=" + context.getResources().getString(R.string.google_server_key));
        return sb.toString();
    }
    public static List<Double> extractElevation(JSONObject obj) throws JSONException {
        ArrayList<Double> result = new ArrayList<>();

        JSONArray results = obj.getJSONArray("results");
        for(int i = 0; i < results.length(); i ++) {
            JSONObject o = (JSONObject)results.get(i);
            if(o != null) {
                result.add(o.getDouble("elevation"));
            }
        }
        //System.out.println(result);
        return result;
    }
    public static String formatPoints(String encodedString) {
        List<LatLng> points = PolyUtil.decode(encodedString);
        StringBuilder sb = new StringBuilder();
        for(LatLng ll : points) {
            sb.append(ll.latitude + "," + ll.longitude + "|");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
