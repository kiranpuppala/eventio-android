package com.app.kiranpuppala.event.network;

import org.json.JSONObject;

public abstract class ResponseCallback {
    public void onResponse(JSONObject jsonObject){};
    public void onResponse(int response,String url){};
}
