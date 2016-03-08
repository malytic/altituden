package com.malytic.altituden.events;

import org.json.JSONObject;

public class ElevationEvent {

    public final JSONObject elevationResponse;

    public ElevationEvent(JSONObject elevationResponse) {
        this.elevationResponse = elevationResponse;
    }
}
