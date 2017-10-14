package com.example.magaz.hackaton_toyamatokanava.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.example.magaz.hackaton_toyamatokanava.R;
import com.example.magaz.hackaton_toyamatokanava.activities.MainApplicationActivity;
import com.example.magaz.hackaton_toyamatokanava.model.PitValidationService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @author Markin Andrey on 14.10.2017.
 */
public class MapFragment extends SupportMapFragment {
    private GoogleMap mMap;
    private double mLongitude;
    private double mLatitude;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        IntentFilter statusBroadcastFilter = new IntentFilter(PitValidationService.Constants.BROADCAST_ACION);
        LocationBroadcastReceiver broadcastReceiver = new LocationBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, statusBroadcastFilter);
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap=googleMap;
            }
        });
    }

    private void updateUI(){
        if (mMap==null){
            return;
        }

        LatLng itemPoint = new LatLng(mLatitude, mLongitude);
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(itemPoint)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(itemPoint,17);
        mMap.animateCamera(cameraUpdate);
        MarkerOptions myMarker = new MarkerOptions()
                .position(itemPoint);
        mMap.clear();
        mMap.addMarker(myMarker);
    }

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mLongitude = intent.getDoubleExtra(PitValidationService.Constants.SEND_LONGITUDE_EXTRA, 0);
            mLatitude = intent.getDoubleExtra(PitValidationService.Constants.SEND_LATITUDE_EXTRA, 0);
            updateUI();
        }
    }
}
