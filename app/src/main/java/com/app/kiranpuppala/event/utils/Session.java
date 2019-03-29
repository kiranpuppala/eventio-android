package com.app.kiranpuppala.event.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    public static void clear(Context context){
//        SharedPreferences spreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor spreferencesEditor = spreferences.edit();
//        spreferencesEditor.remove("user_id");
//        spreferencesEditor.remove("user_email");
//        spreferencesEditor.commit();

        SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("user_id");
        editor.remove("user_email");
        editor.commit();

    }
}
