package com.malytic.altituden;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malytic.altituden.Events.ElevationEvent;
import com.malytic.altituden.Events.DirectionsEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * Created by William on 2016-02-12.
 */
public class HttpRequestHandler {



    private RequestQueue queue;
    private Response.Listener<JSONObject> directionsResponseListener;
    private Response.Listener<JSONObject> altitudeResponseListener;
    private Response.ErrorListener errorListener;


    public HttpRequestHandler(Context context) {

        // Init Volley queue
        queue = Volley.newRequestQueue(context);
        // init response and error listeners
        directionsResponseListener  = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //directions response
                EventBus.getDefault().post(new DirectionsEvent(response));
            }
        };
        altitudeResponseListener  = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // altitude response
                EventBus.getDefault().post(new ElevationEvent(response));
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // send errorEvent to event bus?
            }
        };
    }

    public void directionsRequest(String inUrl) {
        String url = inUrl;

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, directionsResponseListener, errorListener);
        queue.add(jsObjRequest);
    }

    public void elevationRequest(String inUrl) {
        String url = inUrl;

        // Request a string response from the provided URL.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, altitudeResponseListener, errorListener);
        // Add the request to the RequestQueue.
        queue.add(jsObjRequest);
    }
}