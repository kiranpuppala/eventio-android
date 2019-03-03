package com.app.kiranpuppala.event.profile;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.Request;
import com.app.kiranpuppala.event.R;
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.ResponseCallback;
import com.app.kiranpuppala.event.onboard.GetInActivity;
import com.app.kiranpuppala.event.utils.Constants;
import com.app.kiranpuppala.event.utils.Session;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.app.kiranpuppala.event.network.AuthUtils.isTokenValid;

public class UserAccountActivity extends AppCompatActivity {

    private View username , email,password, regdno, degree, branch, mobile, updateProfile, galleryPick;
    private ImageView eventImage;
    private String profilePictureUrl="";
    private static final int PICK_IMAGE_REQUEST = 101;
    private JSONObject profileObject = new JSONObject();
    private String authToken = "";
    private AmazonS3Client s3;
    private File profilePictureFile;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.galleryPick:
                    showFileChooser();
                    break;
                case R.id.updateProfile:
                    updateProfile();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayout linearLayout = findViewById(R.id.userAccount);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.APPEARING);
        linearLayout.setLayoutTransition(layoutTransition);
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.APPEARING);


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        regdno = findViewById(R.id.regno);
        degree = findViewById(R.id.degree);
        branch = findViewById(R.id.branch);
        mobile = findViewById(R.id.mobile);
        updateProfile = findViewById(R.id.updateProfile);
        galleryPick = findViewById(R.id.galleryPick);
        eventImage = findViewById(R.id.eventImage);
        galleryPick.setOnClickListener(onClickListener);
        updateProfile.setOnClickListener(onClickListener);

        ((EditText)(password.findViewById(R.id.descEdit))).setTransformationMethod(new PasswordTransformationMethod());


        TextView second_name_text = (username.findViewById(R.id.title));
        second_name_text.setText("User Name");

        TextView regd_txt = regdno.findViewById(R.id.title);
        regd_txt.setText("Registration no");

        TextView degree_text = degree.findViewById(R.id.title);
        degree_text.setText("Degree");

        TextView desc_txt = branch.findViewById(R.id.title);
        desc_txt.setText("Branch");

        TextView mobile_text = mobile.findViewById(R.id.title);
        mobile_text.setText("Mobile");

        TextView email_text = email.findViewById(R.id.title);
        email_text.setText("Email");

        TextView password_text = password.findViewById(R.id.title);
        password_text.setText("Password");


        (username.findViewById(R.id.editContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable(username);
            }
        });

        (regdno.findViewById(R.id.editContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable(regdno);
            }
        });

        (degree.findViewById(R.id.editContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable(degree);
            }
        });

        (branch.findViewById(R.id.editContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable(branch);
            }
        });

        (mobile.findViewById(R.id.editContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable(mobile);
            }
        });

        (email.findViewById(R.id.editContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable(email);
            }
        });

        (password.findViewById(R.id.editContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable(password);
            }
        });

        if(getIntent()!=null&&getIntent().hasExtra(Constants.KEY_AUTH_TOKEN)){
            authToken = getIntent().getStringExtra(Constants.KEY_AUTH_TOKEN);
            setProfile();
        }

    }


    private void setProfile(){
        String user_id = Session.get(getApplicationContext(),"user_id");
        Map<String,String> headers = new HashMap<>();
        headers.put("authorization",authToken);

        ApiClient.makeRequest(UserAccountActivity.this, null,headers, Request.Method.GET, ApiClient.GET_PROFILE_PATH + "?user_id=" + user_id, new ResponseCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    Log.e("RESPONSEEEEEEE",jsonObject.toString());
                    if(jsonObject.getInt("code") == 200){
                        JSONObject response = (JSONObject)jsonObject.get("response");
                        Glide.with(getBaseContext()).load(response.get("profile_picture")).into(eventImage);
                        ((TextView)(username.findViewById(R.id.descDisplay))).setText(response.getString("user_name"));
                        ((TextView)(regdno.findViewById(R.id.descDisplay))).setText(response.getString("reg_no"));
                        ((TextView)(degree.findViewById(R.id.descDisplay))).setText(response.getString("degree"));
                        ((TextView)(branch.findViewById(R.id.descDisplay))).setText(response.getString("branch"));
                        ((TextView)(mobile.findViewById(R.id.descDisplay))).setText(response.getString("mobile"));
                        ((TextView)(email.findViewById(R.id.descDisplay))).setText(response.getString("email"));
                        ((TextView)(password.findViewById(R.id.descDisplay))).setText("Edit Password");

                        ((EditText)(username.findViewById(R.id.descEdit))).setText(response.getString("user_name"));
                        ((EditText)(regdno.findViewById(R.id.descEdit))).setText(response.getString("reg_no"));
                        ((EditText)(degree.findViewById(R.id.descEdit))).setText(response.getString("degree"));
                        ((EditText)(branch.findViewById(R.id.descEdit))).setText(response.getString("branch"));
                        ((EditText)(mobile.findViewById(R.id.descEdit))).setText(response.getString("mobile"));
                        ((EditText)(email.findViewById(R.id.descEdit))).setText(response.getString("email"));


                        profileObject = response;
                        Toast.makeText(UserAccountActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(UserAccountActivity.this, "EXCEPITON", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void makeEditable(View v) {
        TextView textView = (v.findViewById(R.id.descDisplay));
        EditText editText = (v.findViewById(R.id.descEdit));
        if(v.getId()!=R.id.password)
        editText.setText(textView.getText());
        textView.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        updateProfile.setVisibility(View.VISIBLE);
    }

    private void showFileChooser() {
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("aspectX", 1);
        pickImageIntent.putExtra("aspectY", 1);
        pickImageIntent.putExtra("scale", true);
        pickImageIntent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(pickImageIntent, PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    private void updateProfile() {
        try {
            profileObject.put("user_id", Session.get(getApplicationContext(), "user_id"));
            profileObject.put("user_name", ((EditText) (username.findViewById(R.id.descEdit))).getText());
            profileObject.put("reg_no", ((EditText) (regdno.findViewById(R.id.descEdit))).getText());
            profileObject.put("degree", ((EditText) (degree.findViewById(R.id.descEdit))).getText());
            profileObject.put("branch", ((EditText) (branch.findViewById(R.id.descEdit))).getText());
            profileObject.put("mobile", ((EditText) (mobile.findViewById(R.id.descEdit))).getText());
            profileObject.put("email", ((EditText) (email.findViewById(R.id.descEdit))).getText());
            profileObject.put("password", ((EditText) (password.findViewById(R.id.descEdit))).getText());
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
              ApiClient.uploadtos3(getApplicationContext(), profilePictureFile, new ResponseCallback() {
                    @Override
                    public void onResponse(int response, String url) {
                        try{
                            if (response == ApiClient.RESPONSE_CODE.SUCCESS) {
                                profilePictureUrl = url;
                            }

                            profileObject.put("profile_picture", url);

                            Map<String,String> headers = new HashMap<>();
                            headers.put("authorization",authToken);

                            ApiClient.makeRequest(UserAccountActivity.this, profileObject,headers, Request.Method.POST, ApiClient.EDIT_PROFILE_PATH, new ResponseCallback() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    if(jsonObject.optInt("code",0)==200){
                                        Toast.makeText(UserAccountActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(UserAccountActivity.this, "Updation Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            Uri selectedImageURI = data.getData();
            profilePictureFile = new File(getRealPathFromURI(selectedImageURI));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Bitmap lastBitmap = null;
                lastBitmap = bitmap;
                eventImage.setImageBitmap(lastBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}


