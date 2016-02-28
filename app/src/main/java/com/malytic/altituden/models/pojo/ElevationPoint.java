package com.malytic.altituden.models.pojo;

import com.google.android.gms.maps.model.LatLng;

public class ElevationPoint {

    private Double elevation;
    private LatLng point;

    public ElevationPoint(LatLng point, Double elevation) {
        this.elevation = elevation;
        this.point = point;
    }

    public Double getElevation() {
        return elevation;
    }

    @Override
    public String toString() {
        return point.toString() + elevation;
    }
}
