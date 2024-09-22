package com.devm22.happyguide.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Shared {
    private static Shared shared;
    private SharedPreferences SP;
    private static final String filename = "SharedData";

    public Shared(Context context) {
        SP = context.getApplicationContext().getSharedPreferences(filename, Context.MODE_PRIVATE);
    }

    public static Shared getInstance(Context context) {
        if (shared == null) {
            shared = new Shared(context);
        }
        return shared;
    }


    public void putString(String key, String value) {
        SharedPreferences.Editor editor;
        editor = SP.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String mDefault) {
        return SP.getString(key, mDefault);
    }

    public float getFloat(String key, float mDefault) {
        return SP.getFloat(key, mDefault);
    }

    public void putFloat(String key, float num) {
        SharedPreferences.Editor editor;
        editor = SP.edit();

        editor.putFloat(key, num);
        editor.apply();
    }

    public int getInt(String key, int mDefault) {
        return SP.getInt(key, mDefault);
    }

    public void putInt(String key, int num) {
        SharedPreferences.Editor editor;
        editor = SP.edit();

        editor.putInt(key, num);
        editor.apply();
    }


    public boolean getBoolean(String key, boolean mDefault) {
        return SP.getBoolean(key, mDefault);
    }

    public void putBoolean(String key, boolean b) {
        SharedPreferences.Editor editor;
        editor = SP.edit();
        editor.putBoolean(key, b);
        editor.apply();
    }


    public void clear(){
        SharedPreferences.Editor editor;
        editor = SP.edit();

        editor.clear();
        editor.apply();
    }

    public void remove(){
        SharedPreferences.Editor editor;
        editor = SP.edit();

        editor.remove(filename);
        editor.apply();
    }
}
