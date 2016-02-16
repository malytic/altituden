package com.malytic.altituden.events;

import org.json.JSONObject;

/**
 * Created by Walle on 13/02/2016.
 */
public class ElevationEvent {
    public JSONObject elevationResponse;

    public ElevationEvent(JSONObject elevationResponse) {
        this.elevationResponse = elevationResponse;
    }
}
