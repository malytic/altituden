package com.malytic.altituden.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.malytic.altituden.R;
import com.malytic.altituden.models.pojo.ElevationPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores and handles all the necessary information
 * contained in a Google Directions response. It also
 * has some static functions for extracting information from
 * JSONObjects as well as some functions for building
 * http-request urls to use with Google APIs. Also handles
 * calculation of calories.
 */
public class PathData {

    private static final String TAG = PathData.class.getSimpleName();

    private boolean isValid;
    public int length;
    public String encodedPolyline;
    public List<ElevationPoint> elevation;
    public List<LatLng> points;
    public int calories;
    private Context context;

    /**
     * Creates a new PathData object from a Google Directions response.
     * @param obj JSONObject containing directions data between two points.
     * @throws JSONException if provided JSONObject is invalid.
     */
    public PathData(JSONObject obj) throws JSONException {
        isValid = false;
        encodedPolyline = extractEncodedPath(obj);
        elevation = null;
        points = PolyUtil.decode(encodedPolyline);
        length = extractPathLength(obj);
        isValid = true;
        calories = 0;
    }

    /**
     * Creates a new PathData object with all fields null.
     * Use to create an object when you don't have a directions
     * JSONObject yet. To check if the PathData object is valid
     * do yourObject.isValid(), returns true if it's valid, false otherwise.
     */
    public PathData() {
        length = 0;
        encodedPolyline = null;
        elevation = null;
        points = null;
        isValid = false;
    }

    /**
     * Updates this objects path data.
     * @param obj Google Directions response JSONObject.
     * @throws JSONException if provided JSONObject is invalid.
     */
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

    /**
     * Updates the objects elevation list and updates the calories.
     * @param obj JSONObject containing elevation information.
     * @throws JSONException if provided JSONObject is invalid.
     */
    public void updateElevation(JSONObject obj, Context context) throws JSONException {
        elevation = extractElevation(obj);
        calories = calculateCalories(this, context);
        this.context = context;

    }

    /**
     * Updates the calorie count of this path.
     */
    public void updateCalorieCount() {
        if(elevation != null && elevation.size() > 0 && context != null)
            calories = calculateCalories(this, context);
    }

    /**
     * Extracts the path length from a Google Directions response JSONObject.
     * @param obj Google Directions Response JSONObject.
     * @return Path length in meters.
     * @throws JSONException if provided JSONObject is invalid.
     */
    public static int extractPathLength(JSONObject obj) throws JSONException {
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
        return pathLength;
    }

    /**
     * Takes a JSONObject response from Google Directions API and
     * returns the encoded overview_polyline string.
     * @param obj Object containing Directions data.
     * @return String containing the overview_polyline.
     * @throws JSONException if provided JSONObject is invalid.
     */
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

    /**
     * Returns the validity of the PathData object.
     * @return true if valid, false otherwise.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Builds a string request url to Google Elevation API from
     * an encoded overview_polyline string.
     * @param encodedPolyline encoded overview_polyline.
     * @param context Necessary to get the keys from android resources.
     * @return String request url to Google Elevation API.
     */
    public static String getElevationUrl(String encodedPolyline, Context context) {
        String baseURL = "https://maps.googleapis.com/maps/api/elevation/json";
        StringBuilder sb = new StringBuilder();
        sb.append(baseURL);
        sb.append("?path=" + encodedPolyline);
        sb.append("&samples=" + 20); //TODO path_length/5
        sb.append("&key=" + context.getResources().getString(R.string.google_server_key));
        return sb.toString();
    }

    /**
     * Takes a directions response JSONObject and returns a string
     * http-request URL for every point on the path provided.
     * @param obj JSONObject response from Doogle Directions API.
     * @param context needed to get the api-key from the android resources.
     * @return complete string URL for request to Google Elevation API
     * @throws JSONException if provided JSONObject is invalid.
     */
    public static String getElevationUrl(JSONObject obj, Context context) throws JSONException {

        String baseURL = "https://maps.googleapis.com/maps/api/elevation/json";
        StringBuilder sb = new StringBuilder();
        sb.append(baseURL);
        sb.append("?path=enc:" + extractEncodedPath(obj));
        //TODO split url into several requests when samples > 500
        int samples = 512;
        sb.append("&samples=" + samples);
        sb.append("&key=" + context.getResources().getString(R.string.google_server_key));
        return sb.toString();
    }

    /**
     * Builds a string URL for a http-request to Google Directions API.
     *
     * @param context needed to get the api-key from the android resources.
     * @param origin LatLng point where the directions should start from.
     * @param dest LatLng point where the directions should end.
     * @return complete string URL for request to Google Directions API.
     */
    public static String getDirectionsUrl(Context context, LatLng origin, LatLng dest) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin=" + origin.latitude + "," + origin.longitude);
        sb.append("&destination=" + dest.latitude + "," + dest.longitude);
        sb.append("&mode=walking");
        sb.append("&key=" + context.getResources().getString(R.string.google_server_key));
        return sb.toString();
    }

    /**
     * Extracts the elevation value from a response JSONObject.
     * @param obj JSONObject response from Google Elevation API
     * containing elevation data for one or more points.
     * @return List containing all the elevation points.
     * @throws JSONException when JSONObject does not contain
     * one of the fields. Probably due to passing an invalid
     * JSONObject (an object not from Google Elevation API).
     */
    public static List<ElevationPoint> extractElevation(JSONObject obj) throws JSONException {
        ArrayList<ElevationPoint> result = new ArrayList<>();

        JSONArray results = obj.getJSONArray("results");
        for(int i = 0; i < results.length(); i ++) {
            JSONObject o = (JSONObject)results.get(i);
            if(o != null) {
                // create new ElevationPoint and add to result
                JSONObject location = o.getJSONObject("location");
                LatLng point = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                ElevationPoint ePoint = new ElevationPoint(point, o.getDouble("elevation"));
                result.add(ePoint);
            }
        }
        return result;
    }

    /**
     * Parses and formats an encoded path of LatLngs and returns a string on the
     * form of "latitude1,longitude1|latitude2,longitude2|...".
     * @param encodedString string to parse
     * @return formatted string
     */
    public static String formatPoints(String encodedString) {
        List<LatLng> points = PolyUtil.decode(encodedString);
        StringBuilder sb = new StringBuilder();
        for(LatLng ll : points) {
            sb.append(ll.latitude + "," + ll.longitude + "|");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Calculates the calories burned for a path.
     * Calories Burned = (((K1 x G) + K2) x WKG + TF) x DRK
     * where:  G is angle in degrees,
     *         WKG is weight in kilograms,
     *         TF treadmill factor (air resistance),
     *         DRK distance run in kilometers.
     * If the user is male the calories are multiplied by 0.08.
     *
     * The information about the user (weight, gender) is
     * acquired from SharedPreferences.
     *
     * @param pathData the path to calculate from
     * @return int total calories burned.
     */
    public static int calculateCalories(PathData pathData, Context context) {
        float calories = 0;
        int interpolationDistance = 100;
        float stepLength = ((float)pathData.length / (float)512);

        int step = (int)(interpolationDistance / stepLength);
        if(step < 1) step = 1;
        if(step >= 512) step = 511;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        int weight = sp.getInt("weight", -1);
        int gender = sp.getInt("gender", -1);

        boolean male = true;
        if(gender < 0) {
            male = false;
        }
        ElevationPoint start, end;
        boolean cond = true;
        double  K1, K2;
        for(int i = 0; cond; i++) {
            if(i > 512) {

            }
            int endStep = (i+1)*step;
            if(endStep >= 511) {
                endStep = 511;
                cond = false;
            }
            // find the angle between the points.
            double base = step*stepLength;
            start = pathData.elevation.get(i * step);
            end =  pathData.elevation.get(endStep);
            double angle = Math.atan((base /(end.getElevation() - start.getElevation())));
            base = Math.hypot(base, end.getElevation() - start.getElevation());

            if(angle <= -15){
                K1 = -.01;
                K2 = .5;
            } else if (angle > -15 && angle <= -10) {
                K1 = -.02;
                K2 = .35;
            } else if (angle > -10 && angle <= 0) {
                K1 = .04;
                K2 = .95;
            } else if (angle > 0 && angle <= 10) {
                K1 = .05;
                K2 = .95;
            } else if (angle > 10 && angle <= 15) {
                K1 = .07;
                K2 = .75;
            } else {
                K1 = .07;
                K2 = .75;
            }

            double pathCals = (((K1 * angle) + K2) * weight + 0.86) * base;
            calories += pathCals;
        }

        if(male) calories *= 1.08;
        return (int) calories / 1000; // per kilometer, not meter
    }
}
