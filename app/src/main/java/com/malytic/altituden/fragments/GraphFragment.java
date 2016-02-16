package com.malytic.altituden.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.malytic.altituden.HttpRequestHandler;
import com.malytic.altituden.R;

import org.greenrobot.eventbus.EventBus;


public class GraphFragment extends Fragment {
    private String baseURL = "https://maps.googleapis.com/maps/api/elevation/json";
    private HttpRequestHandler httpReq;


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

        GraphView graph = (GraphView) getView().findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {

        });
        graph.addSeries(series);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}