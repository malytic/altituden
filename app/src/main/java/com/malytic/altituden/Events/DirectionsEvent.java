package com.malytic.altituden.Events;

import org.json.JSONObject;

/**
 * Created by Walle on 13/02/2016.
 */
public class DirectionsEvent {
    public JSONObject directionsResponse;

    public DirectionsEvent(JSONObject directionsResponse) {
        this.directionsResponse = directionsResponse;
    }
}
