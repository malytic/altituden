package com.malytic.altituden.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.malytic.altituden.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private MarkerOptions origin,dest;
    private Polyline polyline;
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
            polyline = mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(origin.getPosition().latitude, origin.getPosition().longitude),
                            new LatLng(dest.getPosition().latitude, dest.getPosition().longitude))
                    .width(5)
                    .color(Color.RED));

        }else{ Toast.makeText(getActivity(), "You have already places a start and end marker",
                Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        marker.hideInfoWindow();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        if(marker.getTitle().equals(origin.getTitle())) {
            origin.position(marker.getPosition());
            //origin.snippet(marker.getPosition().toString());
        } else if(marker.getTitle().equals(dest.getTitle())) {
            dest.position(marker.getPosition());
            //dest.snippet("HEJ");
        }

        polyline.remove();
        polyline = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(origin.getPosition().latitude, origin.getPosition().longitude)
                        , new LatLng(dest.getPosition().latitude, dest.getPosition().longitude))
                .width(5)
                .color(Color.RED));

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
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
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), 15));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}