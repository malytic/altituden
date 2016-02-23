package com.malytic.altituden.classes;

import com.google.android.gms.maps.model.LatLng;
import com.jjoe64.graphview.series.DataPoint;

/**
 * Created by William on 2016-02-19.
 */
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
    public String toString() {
        return point.toString() + elevation;
    }
}
