package com.example.magaz.hackaton_toyamatokanava.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.example.magaz.hackaton_toyamatokanava.model.AvtomobilePath;
import com.example.magaz.hackaton_toyamatokanava.model.PitValidationService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * @author Markin Andrey on 14.10.2017.
 */
public class MapFragment extends SupportMapFragment {
    private static final String URL_FOR_POST_REQUEST = "http://31.13.134.171:8080/";

    private GoogleMap mMap;
    private double mLongitude;
    private double mLatitude;
    private String mResponse;
    private ResponseBody mResponseBody;
    private Handler mHandler = new Handler();

    private Runnable timeUpdaterRunnable = new Runnable() {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(URL_FOR_POST_REQUEST)
                    .build();
            IGetAllCoordinatesRequest mRequest = retrofit.create(IGetAllCoordinatesRequest.class);
            Call<ResponseBody> call = mRequest.getCoordinates();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mResponse = response.toString();
                    List<AvtomobilePath> pathList = new ArrayList<AvtomobilePath>();
                    try {
                        JSONObject jsonObject = new JSONObject(mResponse);
                        JSONArray points = jsonObject.getJSONArray("points");
                        for (int i = 0; i < points.length(); i++){
                            JSONObject obj = points.getJSONObject(i);
                            AvtomobilePath avtomobilePath = new AvtomobilePath();
                            avtomobilePath.setLatitude(obj.getDouble("lat"));
                            avtomobilePath.setLongitude(obj.getDouble("lng"));
                            avtomobilePath.setLevel(obj.getInt("level"));
                            pathList.add(avtomobilePath);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    //загнать в карту
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
            mHandler.postDelayed(this, 300000);
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        IntentFilter statusBroadcastFilter = new IntentFilter(PitValidationService.Constants.BROADCAST_ACION);
        LocationBroadcastReceiver broadcastReceiver = new LocationBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, statusBroadcastFilter);
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
            }
        });
        mHandler.postDelayed(timeUpdaterRunnable, 3000);
    }

    private void updateUI() {
        if (mMap == null) {
            return;
        }

        LatLng itemPoint = new LatLng(mLatitude, mLongitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(itemPoint, 17);
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

    interface IGetAllCoordinatesRequest {
        @GET("yamakanava/getquality")
        Call<ResponseBody> getCoordinates();
    }
}
