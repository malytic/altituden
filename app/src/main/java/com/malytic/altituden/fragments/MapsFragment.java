package com.malytic.altituden.fragments;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.malytic.altituden.Events.DirectionsEvent;
import com.malytic.altituden.Events.ElevationEvent;
import com.malytic.altituden.HttpRequestHandler;
import com.malytic.altituden.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private MarkerOptions origin,dest;
    private Polyline path;
    private HttpRequestHandler httpReq;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_maps_fragment, null, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        httpReq = new HttpRequestHandler(getContext());
        path = null;
        return view;

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Behöver ha koll mMap != null
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /*
    On map click, if start or destination markers are not
    already placed, place them at latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        if (origin == null) {
            origin = new MarkerOptions().position(latLng).title("Origin").draggable(true).snippet(latLng.toString());
            mMap.addMarker(origin);

        } else if (dest == null) {
            dest = new MarkerOptions().position(latLng).title("Destination").draggable(true).snippet(latLng.toString());
            mMap.addMarker(dest);
            updateDirections(origin.getPosition(), dest.getPosition());

        }else {
            // markers already placed --
        }
        updateElevation(latLng);
    }
    /*

     */
    @Override
    public void onMarkerDragStart(Marker marker) {

        if(marker.getTitle().equals(origin.getTitle())) {
            origin.position(marker.getPosition());
        } else if(marker.getTitle().equals(dest.getTitle())) {
            dest.position(marker.getPosition());
            //dest.snippet("HEJ");
        }
        marker.setSnippet(marker.getPosition().toString());
        marker.hideInfoWindow();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        if(marker.getTitle().equals(origin.getTitle())) {
            origin.position(marker.getPosition());
        } else if(marker.getTitle().equals(dest.getTitle())) {
            dest.position(marker.getPosition());
        }
        marker.setSnippet(marker.getPosition().toString());


    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        updateDirections(origin.getPosition(), dest.getPosition());
        updateElevation(marker.getPosition());
        //TODO

    }
    @Override
    public void
    onConnectionFailed(ConnectionResult result){

    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 15));
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Subscribe
    public void onDirectionsResponseEvent(DirectionsEvent event) {

        JSONArray routesArray = null;
        try {
            routesArray = event.directionsResponse.getJSONArray("routes");
        } catch (JSONException e) {
            Log.e("ERROR", "Failed to get route array from response");
            e.printStackTrace();
        }
        String encodedOverviewPolyline = null;

        // extract the JSONObject with overview_polyline
        for(int i = 0; i < routesArray.length(); i++) {
            JSONObject obj = routesArray.optJSONObject(i);
            if(obj != null) {
                try {
                    encodedOverviewPolyline = obj.optJSONObject("overview_polyline").getString("points");
                } catch (JSONException e) {
                    Log.e("ERROR", "Failed to get points string from JSONObject");
                    e.printStackTrace();
                }
            }
        }
        if(encodedOverviewPolyline != null) {
            PolylineOptions thePath = new PolylineOptions().color(Color.parseColor("#CC00CAB8")).width(15)
                    .addAll(PolyUtil.decode(encodedOverviewPolyline));
            if (path != null) path.remove();

            path = mMap.addPolyline(thePath);
        }
    }

    private void updateDirections(LatLng origin, LatLng dest) {

        StringBuilder sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin=" + origin.latitude + "," + origin.longitude);
        sb.append("&destination=" + dest.latitude + "," + dest.longitude);
        sb.append("&mode=walking");
        sb.append("&key=AIzaSyDlo4aoZrAwVkMlx10GB-TzTRUPvGiiWxI");
        httpReq.directionsRequest(sb.toString());
    }

    private void updateElevation(LatLng point) {
        String baseURL = "https://maps.googleapis.com/maps/api/elevation/json";
        StringBuilder sb = new StringBuilder();
        sb.append(baseURL);
        sb.append("?locations=" + point.latitude + "," + point.longitude);
        sb.append("&key=" + "AIzaSyDlo4aoZrAwVkMlx10GB-TzTRUPvGiiWxI");
        httpReq.elevationRequest(sb.toString());
    }

    @Subscribe
    public void onElevationResponseEvent(ElevationEvent response) {
        JSONArray elevationArray = null;
        String eString = null;
        Log.e("test", response.elevationResponse.toString());
        double altitude = 0;
        try {
            elevationArray = response.elevationResponse.getJSONArray("results");
            JSONObject eObj = elevationArray.optJSONObject(0);
            eString = eObj.getString("elevation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (eString != null) {
            altitude = Double.parseDouble(eString);
            Log.e("Altitude ", "" + altitude);
            altitude = Math.round(altitude);
            Log.e("Altitude ", "" + altitude);
        }
        Toast.makeText(getActivity(), "Altitude is: " + altitude + " meters above sea level.",
                Toast.LENGTH_LONG).show();
    }

}
