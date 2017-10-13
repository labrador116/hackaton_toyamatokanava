package com.example.magaz.hackaton_toyamatokanava.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.magaz.hackaton_toyamatokanava.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainApplicationActivity extends AppCompatActivity {
    private static final int REQUEST_ERROR = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_application);
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
