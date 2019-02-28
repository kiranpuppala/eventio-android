package com.app.kiranpuppala.event.network;

import android.accounts.Account;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class AuthUtils {

    public static Boolean isTokenValid(final Context mContext, String token){
        try{
            final Map<String,String> reqHeaders = new HashMap<>();
            reqHeaders.put("authorization",token);

            FutureTask apiTask = new FutureTask(new Callable() {
                @Override
                public Object call() throws Exception {
                    return new ApiClient().makeSyncRequest(mContext,null,reqHeaders,ApiClient.VALIDATE_TOKEN);
                }
            });

            new Thread(apiTask).start();


            JsonObject resObj = new Gson().fromJson((String)apiTask.get(), JsonObject.class);
            if(Integer.parseInt(resObj.get("code")+"")==200){
                return true;
            }else{
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static String getAuthToken(final Context mContext,Account account, String password){
        try {
            final Map<String,String> req = new HashMap<>();
            req.put("email",account.name);
            req.put("password",password);

            FutureTask apiTask = new FutureTask(new Callable() {
                @Override
                public Object call() throws Exception {
                    return new ApiClient().makeSyncRequest(mContext,req,null,ApiClient.LOGIN_PATH);
                }
            });

            new Thread(apiTask).start();

            JsonObject resObj = new Gson().fromJson((String)apiTask.get(), JsonObject.class);
            if(Integer.parseInt(resObj.get("code")+"")==200){
                return ((JsonObject)resObj.get("response")).get("token").getAsString();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}
