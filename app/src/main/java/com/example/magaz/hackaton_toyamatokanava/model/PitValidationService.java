package com.example.magaz.hackaton_toyamatokanava.model;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Markin Andrey on 13.10.2017.
 */
public class PitValidationService extends IntentService {
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private float mCurrentSpeed;
    private Timer mTimer;
    private LocationTimerTask mTimerTask;

    public PitValidationService(String name) {
        super(name);
    }

    public PitValidationService() {
        super("PitValidationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Toast.makeText(getApplicationContext(), "Соединение установлено!", Toast.LENGTH_SHORT).show();
                        findLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    private void findLocation() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {
            LocationRequest request = new LocationRequest();
            request.setInterval(3000);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient, request, new LocationListener() {
                        @Override
                        public int hashCode() {
                            return super.hashCode();
                        }

                        @Override
                        public void onLocationChanged(Location location) {
                            mCurrentLocation = location;
                            mCurrentSpeed = location.getSpeed();
                        }
                    });
            mTimer = new Timer();
            mTimerTask = new LocationTimerTask();
            mTimer.schedule(mTimerTask, 100, 3000);
        }
    }

    class LocationTimerTask extends TimerTask {

        @Override
        public void run() {
            Intent intent = new Intent(Constants.BROADCAST_ACION);
            intent.putExtra(Constants.SEND_LONGITUDE_EXTRA, mCurrentLocation.getLongitude());
            intent.putExtra(Constants.SEND_LONTITUDE_EXTRA, mCurrentLocation.getLongitude());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    public final class Constants {
        public static final String BROADCAST_ACION = "com.example.magaz.hackaton_toyamatokanava.model.PitValidationService.Broadcast";
        public static final String SEND_LONGITUDE_EXTRA = "com.example.magaz.hackaton_toyamatokanava.model.PitValidationService.Seind_Longitude_EXTRA";
        public static final String SEND_LONTITUDE_EXTRA = "com.example.magaz.hackaton_toyamatokanava.model.PitValidationService.Seind_Lontitude_EXTRA";
    }
}
