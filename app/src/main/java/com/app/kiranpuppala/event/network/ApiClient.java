package com.app.kiranpuppala.event.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import java.net.HttpURLConnection;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.net.HttpURLConnection;
//import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class ApiClient {

    public static String LOGIN_PATH = "/api/login";
    public static String VALIDATE_TOKEN = "/api/validate-token";
    public static String SIGNUP_PATH = "/api/register";
    public static String EDIT_PROFILE_PATH = "/api/edit-profile";
    public static String GET_PROFILE_PATH = "/api/get-profile";
    public static String GET_PUBLIC_PROFILE_PATH = "/api/get-public-profile";
    public static String CREATE_EVENT_PATH = "/api/create-event";
    public static String JOIN_EVENT_PATH = "/api/join-event";
    public static String UPDATE_EVENT_PATH = "/api/update-event";
    public static String LIST_EVENTS_PATH = "/api/list-events";
    public static String MANAGE_EVENTS_PATH = "/api/manage-events";
    private static  String S3_URL = "https://s3.ap-south-1.amazonaws.com/homework-event/";


    public interface RESPONSE_CODE {
        int SUCCESS = 1;
        int FAILURE = 0;
    }



    public String makeSyncRequest(Context context, Map <String,String> requestPayload, Map <String,String> requestHeaders, String path){
        try {
            String url = context.getResources().getString(context.getResources().getIdentifier("api_url", "string", context.getPackageName())) + path;
            HttpURLConnection connection = (HttpURLConnection) (new URL(url).openConnection());
            connection.setRequestMethod("POST");
//            connection.setSSLSocketFactory(new TLSSocketFactory());
            if(requestHeaders!=null)
                connection.setRequestProperty("authorization", requestHeaders.get("authorization"));

            connection.setDoOutput(true);

            OutputStream stream = connection.getOutputStream();

            if(requestPayload!=null)
                stream.write(generateQueryString(requestPayload).getBytes());

            Map<String, List<String>> responseHeaders;

            int responseCode = connection.getResponseCode();
            responseHeaders = connection.getHeaderFields();
            InputStreamReader responseReader;

            if((responseCode < 200 || responseCode >= 300) && responseCode != 302) {
                responseReader = new InputStreamReader(connection.getErrorStream());
            } else {
                responseReader = new InputStreamReader(connection.getInputStream());
            }

            BufferedReader in = new BufferedReader(responseReader);
            StringBuilder response = new StringBuilder();

            String inputLine;
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            String responsePayload = response.toString();
            connection.disconnect();

            return responsePayload;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String generateQueryString(Map<String, String> queryString) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        Map.Entry e;
        for(Iterator var2 = queryString.entrySet().iterator(); var2.hasNext(); sb.append(URLEncoder.encode((String)e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode((String)e.getValue(), "UTF-8"))) {
            e = (Map.Entry)var2.next();
            if(sb.length() > 0) {
                sb.append('&');
            }
        }

        return sb.toString();
    }


    public static void makeRequest(Context context, final JSONObject request, final Map<String,String> headers, int methodType, String path, final ResponseCallback responseCallback) {
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

                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String,String> requestHeaders = new HashMap<>();
                        if(headers!=null)
                            requestHeaders=  headers;
                        return requestHeaders;
                }};
        requestQueue.add(jsonRequest);
    }

    public static void uploadtos3(final Context context, final File file, final ResponseCallback responseCallback) {
        if(file !=null){
            CognitoCachingCredentialsProvider credentialsProvider;
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    "ap-south-1:b269e812-984d-4795-a3bc-a73cb47d2fc6", // Identity Pool ID
                    Regions.AP_SOUTH_1 // Region
            );

            AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

            TransferUtility transferUtility = new TransferUtility(s3, context);
            final TransferObserver observer = transferUtility.upload(
                    "homework-event",  //this is the bucket name on S3
                    file.getName(),
                    file,
                    CannedAccessControlList.PublicRead //to make the file public
            );
            observer.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state.equals(TransferState.COMPLETED)) {
                        Toast.makeText(context,"Uploaded",Toast.LENGTH_SHORT).show();
                        responseCallback.onResponse(RESPONSE_CODE.SUCCESS,S3_URL+file.getName());

                    } else if (state.equals(TransferState.FAILED)) {
                        Toast.makeText(context,"Failed to upload",Toast.LENGTH_LONG).show();
                        responseCallback.onResponse(RESPONSE_CODE.FAILURE,null);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {

                }
            });
        }else {
            responseCallback.onResponse(RESPONSE_CODE.FAILURE,null);
        }
        }


    class TLSSocketFactory extends SSLSocketFactory {

        private SSLSocketFactory delegate;

        public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            delegate = context.getSocketFactory();
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket() throws IOException {
            return enableTLSOnSocket(delegate.createSocket());
        }

        @Override
        public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
            return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose));
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return enableTLSOnSocket(delegate.createSocket(host, port));
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return enableTLSOnSocket(delegate.createSocket(host, port));
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
        }

        private Socket enableTLSOnSocket(Socket socket) {
            if(socket != null && (socket instanceof SSLSocket)) {
                ((SSLSocket)socket).setEnabledProtocols(new String[] {"TLSv1.1", "TLSv1.2"});
            }
            return socket;
        }

    }

}



