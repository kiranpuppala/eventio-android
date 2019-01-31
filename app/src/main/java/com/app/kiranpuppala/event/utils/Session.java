package com.app.kiranpuppala.event.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    public static void set(Context context, String key, String value){
        SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String get(Context context, String key){
        SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return pref.getString(key,null);
    }
}
