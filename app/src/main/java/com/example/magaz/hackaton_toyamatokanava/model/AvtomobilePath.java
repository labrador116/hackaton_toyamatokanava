package com.example.magaz.hackaton_toyamatokanava.model;

/**
 * @author Markin Andrey on 14.10.2017.
 */
public class AvtomobilePath {
    private double mLatitude;
    private double mLongitude;
    private int mLevel;

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        mLevel = level;
    }
}
