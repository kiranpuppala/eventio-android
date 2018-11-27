package com.app.kiranpuppala.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.ResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;


public class GetInActivity extends AppCompatActivity {

    TextInputLayout email,firstname,lastname,password,confirmpassword;
    TextView action_button;
    TextView footer_text,footer_text_ext;
    String mode = "LOGIN"; //default
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getin_layout);
        email=findViewById(R.id.email);
        firstname=findViewById(R.id.firstname);
        lastname=findViewById(R.id.lastname);
        password=findViewById(R.id.password);
        confirmpassword=findViewById(R.id.confirmpassword);
        action_button=findViewById(R.id.action_button);
        footer_text=findViewById(R.id.footer_text);
        footer_text_ext=findViewById(R.id.footer_text_ext);

        action_button.setOnClickListener(onClickListener);
        footer_text_ext.setOnClickListener(onClickListener);

        renderAccordingToMode();
    }

    private void renderAccordingToMode(){
        int visibility;
        String text,text_ext;
        if (mode.equals("LOGIN")) {
            visibility = View.GONE;
            text = "Don't have an account?";
            text_ext = "Signup";
        }
        else {
            visibility = View.VISIBLE;
            text="Already a member?";
            text_ext="Login";
        }
        firstname.setVisibility(visibility);
        lastname.setVisibility(visibility);
        confirmpassword.setVisibility(visibility);
        footer_text.setText(text);
        footer_text_ext.setText(text_ext);
        action_button.setText(mode);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.action_button: handleAction(); break;
                case R.id.footer_text_ext:
                    mode = mode == "LOGIN" ? "SIGNUP" : "LOGIN";
                    renderAccordingToMode();
                    break;
            }
        }
    };

    private void handleAction(){
        String path = mode=="LOGIN" ? ApiClient.LOGIN_PATH : ApiClient.SIGNUP_PATH;
        try{
            JSONObject request = new JSONObject();
            request.put("email",email.getEditText().getText().toString());
            request.put("password",password.getEditText().getText().toString());
            if(mode.equals("SIGNUP")){
                request.put("first_name",firstname.getEditText().getText().toString());
                request.put("last_name",lastname.getEditText().getText().toString());
            }
            ApiClient.makeRequest(GetInActivity.this, request,Request.Method.POST,path,new ResponseCallback(){
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        Log.d("JSON RESPONSE",jsonObject.getInt("code")+"");
//                        if(jsonObject.getInt("code")==200){
                            Intent intent = new Intent(GetInActivity.this,MainActivity.class);
                            startActivity(intent);
//                        }else{
//                            Toast.makeText(GetInActivity.this,"wrong email or password",Toast.LENGTH_SHORT).show();
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }catch (JSONException e){
            e.printStackTrace();
        }
    }


}
