package com.example.magaz.hackaton_toyamatokanava.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.magaz.hackaton_toyamatokanava.R;
import com.example.magaz.hackaton_toyamatokanava.model.PitValidationService;

/**
 * @author Markin Andrey on 13.10.2017.
 */
public class MainFragment extends Fragment {
    private double mLongitude;
    private double mLatitude;
    private double mShakeRatio;
    private TextView mTestTV;

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
        Intent intentService = new Intent(getContext(), PitValidationService.class);
        getActivity().startService(intentService);

        IntentFilter statusBroadcastFilter = new IntentFilter(PitValidationService.Constants.BROADCAST_ACION);
        LocationBroadcastReceiver broadcastReceiver = new LocationBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, statusBroadcastFilter);
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

    private class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mLongitude = intent.getDoubleExtra(PitValidationService.Constants.SEND_LONGITUDE_EXTRA, 0);
            mLatitude = intent.getDoubleExtra(PitValidationService.Constants.SEND_LATITUDE_EXTRA, 0);
            mTestTV.setText(mLongitude + " " + mLatitude);
        }
    }
}
