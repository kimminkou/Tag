package com.example.yehyunee.tagapplication.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * local db 사용
 */
public class PreshareUtil {

    private static PreshareUtil instance = null;
    private Activity activity;

    private PreshareUtil() {

    }

    public static PreshareUtil getInstance() {
        if (instance == null) {
            instance = new PreshareUtil();
        }
        return instance;
    }

    public void setStringPref(Context context, String name, String value) {
        SharedPreferences prefs = context.getSharedPreferences("putString", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public String getStringPref(Context context, String name, String value) {
        SharedPreferences pref = context.getSharedPreferences("putString", Activity.MODE_PRIVATE);
        String key = pref.getString(name, value);

        return key;
    }

    public void setIntPref(Context context, String name, int value) {
        SharedPreferences prefs = context.getSharedPreferences("putInt", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public Integer getIntPref(Context context, String name, int value) {
        SharedPreferences pref = context.getSharedPreferences("putInt", Activity.MODE_PRIVATE);
        int key = pref.getInt(name, value);

        return key;
    }

}
