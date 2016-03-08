package com.malytic.altituden.events;

import org.json.JSONObject;

public class DirectionsEvent {

    public final JSONObject directionsResponse;

    public DirectionsEvent(JSONObject directionsResponse) {
        this.directionsResponse = directionsResponse;
    }
}
