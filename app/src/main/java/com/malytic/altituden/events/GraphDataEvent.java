package com.malytic.altituden.events;

import com.jjoe64.graphview.GraphView;

import java.util.List;

/**
 * Created by Robert on 16/02/2016.
 */
public class GraphDataEvent {
    List<Double> elevation;
    //List<Double> resolution;
    float pathLength;

    public GraphDataEvent(List<Double> elevation, float pathLength) {
        this.elevation = elevation;
        this.pathLength = pathLength;

    }
}
