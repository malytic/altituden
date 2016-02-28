package com.malytic.altituden.events;

import org.json.JSONObject;

public class ElevationEvent {
    public JSONObject elevationResponse;

    public ElevationEvent(JSONObject elevationResponse) {
        this.elevationResponse = elevationResponse;
    }
}
