package com.malytic.altituden.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.malytic.altituden.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

//    private MapView mMapView;
//    private GoogleMap googleMap;

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_fragment);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Toast.makeText(getActivity(), "This is a map", Toast.LENGTH_LONG).show();

        View view = inflater.inflate(R.layout.activity_maps_fragment, null, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Inflate the layout for this fragment
        return view; // inflater.inflate(R.layout.activity_maps_fragment, container, false);
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
        Toast.makeText(getActivity(), "Map redy, onMapReadyCallback called", Toast.LENGTH_LONG).show();
        //Behöver ha koll mMap != null
        mMap = googleMap;

        //LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        LatLng chalmers = new LatLng(57.706389, 11.938270);
        mMap.addMarker(new MarkerOptions().position(chalmers).title("Chalmers"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(chalmers));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(chalmers, 15));
    }
}