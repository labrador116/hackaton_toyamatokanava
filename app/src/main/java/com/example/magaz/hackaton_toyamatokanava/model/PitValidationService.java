package com.example.magaz.hackaton_toyamatokanava.model;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author Markin Andrey on 13.10.2017.
 */
public class PitValidationService extends IntentService implements SensorEventListener {
    private static final double MINIMUM_SPEED_CONSTANT = 4.1;
    private static final String URL_FOR_POST_REQUEST = "http://31.13.134.171:8080/";

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private float mCurrentSpeed;
    private Timer mTimer;
    private Timer mSendServerTimer;
    private LocationTimerTask mTimerTask;
    private SendToServerLocationTimerTask mSendToServerLocationTimerTask;
    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private double mShakeRatio;
    private long lastUpdate = 0;

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
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                            mSendServerTimer = new Timer();
                            mSendToServerLocationTimerTask = new SendToServerLocationTimerTask();
                            mSendServerTimer.schedule(mSendToServerLocationTimerTask, 100, 1000);

                        }
                    });
            mTimer = new Timer();
            mTimerTask = new LocationTimerTask();
            mTimer.schedule(mTimerTask, 100, 3000);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                mShakeRatio = (Math.abs(x) + Math.abs(y) + Math.abs(z)) / 8.3;

                //float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

//                if (speed > SHAKE_THRESHOLD) {
//                    mXTextView.setText(String.valueOf(x));
//                    mYTextView.setText(String.valueOf(y));
//                    mZTextView.setText(String.valueOf(z));
//                    mSpeedTextView.setText(String.valueOf(speed));
//                }
//                last_x = x;
//                last_y = y;
//                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private int validateRatio() {
        int result = 0;
        if (mShakeRatio < 2) {
            result = 1;
        }
        if (mShakeRatio > 2 && mShakeRatio < 4) {
            result = 2;
        }
        if (mShakeRatio > 4) {
            result = 3;
        }
        return result;
    }

    private class LocationTimerTask extends TimerTask {

        @Override
        public void run() {
            if (mCurrentLocation != null) {
                Intent intent = new Intent(Constants.BROADCAST_ACION);
                intent.putExtra(Constants.SEND_LONGITUDE_EXTRA, mCurrentLocation.getLongitude());
                intent.putExtra(Constants.SEND_LATITUDE_EXTRA, mCurrentLocation.getLatitude());
                intent.putExtra(Constants.SEND_SPEED_EXTRA, mCurrentLocation.getSpeed());
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
    }

    private class SendToServerLocationTimerTask extends TimerTask {

        @Override
        public void run() {
           // if (mCurrentSpeed > MINIMUM_SPEED_CONSTANT) {
//                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder()
//                        .addInterceptor(interceptor)
                        .connectTimeout(3, TimeUnit.MINUTES)
                        .readTimeout(3, TimeUnit.MINUTES)
                        .writeTimeout(3, TimeUnit.MINUTES)
                        .build();
                Retrofit retrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl(URL_FOR_POST_REQUEST)
                        .build();
                ISendDataOnPostRequest postRequest = retrofit.create(ISendDataOnPostRequest.class);
                String lng = String.valueOf(mCurrentLocation.getLongitude());
                String lat = String.valueOf(mCurrentLocation.getLatitude());
                String value = String.valueOf(validateRatio());

                Call<ResponseBody> call = postRequest.sendData(lat, lng, value);
                try {
                    retrofit2.Response<ResponseBody> response = call.execute();
                    if (response.isSuccessful()) {
                        int q = 0; //test
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
           // }
        }
    }

    interface ISendDataOnPostRequest {
        @FormUrlEncoded
        @POST("yamakanava/setquality")
        Call<ResponseBody> sendData(@Field("lat") String latitude, @Field("lng") String longitude, @Field("value") String value);
    }

    public final class Constants {
        public static final String BROADCAST_ACION = "com.example.magaz.hackaton_toyamatokanava.model.PitValidationService.Broadcast";
        public static final String SEND_LONGITUDE_EXTRA = "com.example.magaz.hackaton_toyamatokanava.model.PitValidationService.Seind_Longitude_EXTRA";
        public static final String SEND_LATITUDE_EXTRA = "com.example.magaz.hackaton_toyamatokanava.model.PitValidationService.Seind_Latitude_EXTRA";
        public static final String SEND_SPEED_EXTRA = "com.example.magaz.hackaton_toyamatokanava.model.PitValidationService.Send_Speed";
        public static final String TEST_SHAKE_RATIO = "com.example.magaz.hackaton_toyamatokanava.model.PitValidationService.Shake_Ratio";
    }
}
