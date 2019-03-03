package com.app.kiranpuppala.event.utils;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

public class Functions {
    public static Bundle jsonToBundle(JSONObject jsonObject) throws Exception {
        Bundle bundle = new Bundle();
        JSONArray keys = jsonObject.names();
        for (int i = 0; i < keys.length(); ++i) {
            String key = keys.getString(i);
            Object value = jsonObject.optString(key,"");
            bundle.putString(key, (String) value);
        }
        return bundle;
    }
}
