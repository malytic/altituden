package com.malytic.altituden.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.malytic.altituden.MainActivity;
import com.malytic.altituden.classes.HttpRequestHandler;
import com.malytic.altituden.R;
import com.malytic.altituden.events.ElevationUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class GraphFragment extends Fragment {
    private String baseURL = "https://maps.googleapis.com/maps/api/elevation/json";
    private HttpRequestHandler httpReq;
    private GraphView graph = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);

        super.onStart();
        updateGraph();
    }

    public void updateGraph() {

        graph = (GraphView) getView().findViewById(R.id.graph);
        graph.setVisibility(View.INVISIBLE);
        graph.removeAllSeries();
        if(MainActivity.pathData.elevation != null && MainActivity.pathData.elevation.size() > 0) {
            int dataSetSize = MainActivity.pathData.elevation.size();
            float dx = MainActivity.pathData.length / dataSetSize;
            DataPoint[] dataPoints = new DataPoint[dataSetSize];
            for (int i = 0; i < dataPoints.length; i++) {
                dataPoints[i] = new DataPoint(dx * i, MainActivity.pathData.elevation.get(i));
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            series.setBackgroundColor(Color.parseColor("BLUE"));
            graph.getViewport().setMaxX(MainActivity.pathData.length);
            graph.getViewport().setMinX(0);
            double max = 0;
            for(Double elev : MainActivity.pathData.elevation) {
                if (elev > max) max = elev;
            }
            graph.getViewport().setMaxY(max*1.1);
            graph.getViewport().setMinY(0);
            graph.getViewport().setYAxisBoundsManual(true);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.addSeries(series);
            graph.setVisibility(View.VISIBLE);
        } else {
        }
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();
        updateGraph();
    }
    @Subscribe
    public void onElevationUpdateEvent(ElevationUpdateEvent event) {
        updateGraph();
    }
}