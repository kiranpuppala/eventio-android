package com.app.kiranpuppala.event.network;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;


public class ApiClient {

    public static String LOGIN_PATH = "/api/login";
    public static String SIGNUP_PATH = "/api/register";

    public static void makeRequest(Context context, final JSONObject request, int methodType, String path, final ResponseCallback responseCallback ){
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = context.getResources().getString(context.getResources().getIdentifier("api_url", "string", context.getPackageName())) + path;

            JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(methodType, URL, request,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            responseCallback.onResponse(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }

            }) {

            };
                requestQueue.add(jsonRequest);
    }
}

