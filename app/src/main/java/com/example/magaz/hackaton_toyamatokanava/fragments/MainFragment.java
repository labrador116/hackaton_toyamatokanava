package com.example.magaz.hackaton_toyamatokanava.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.magaz.hackaton_toyamatokanava.R;
import com.example.magaz.hackaton_toyamatokanava.model.PitValidationService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * @author Markin Andrey on 13.10.2017.
 */
public class MainFragment extends Fragment {
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private TextView mTestTV;
    private float mCurrentSpeed;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        checkAllPermissions();
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(@Nullable Bundle bundle) {
//                        Toast.makeText(getActivity(), "Соединение установлено!", Toast.LENGTH_SHORT).show();
//                        findLocation();
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int i) {
//
//                    }
//                })
//                .build();
        Intent intentService = new Intent(getContext(), PitValidationService.class);
        getActivity().startService(intentService);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mTestTV = (TextView) view.findViewById(R.id.testTV);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void findLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkAllPermissions();
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
                            mTestTV.setText(mCurrentLocation.getLatitude() + " "+ mCurrentLocation.getLongitude() + "\nSpeed: "+mCurrentSpeed);
                        }
                    });
        }
    }

    private void checkAllPermissions() {
        if (ActivityCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if (ActivityCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }
}
