package com.example.magaz.hackaton_toyamatokanava.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import com.example.magaz.hackaton_toyamatokanava.R;
import com.example.magaz.hackaton_toyamatokanava.fragments.MainFragment;
import com.example.magaz.hackaton_toyamatokanava.fragments.MapFragment;
import com.example.magaz.hackaton_toyamatokanava.model.PitValidationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainApplicationActivity extends AppCompatActivity {
    private static final int REQUEST_ERROR = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_application);
        Fragment fragment = MainFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,fragment).commit();
        Fragment mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.map_container,mapFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAvailableGooglePlayServices();
    }

    private void checkAvailableGooglePlayServices(){
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS){
            Dialog errorDialog = GooglePlayServicesUtil
                    .getErrorDialog(errorCode, this, REQUEST_ERROR, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            finish();
                        }
                    });
            errorDialog.show();
        }
    }



}
