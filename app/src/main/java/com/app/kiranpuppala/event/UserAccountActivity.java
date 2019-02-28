package com.app.kiranpuppala.event;

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
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.ResponseCallback;
import com.app.kiranpuppala.event.utils.Session;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;

import static android.app.Activity.RESULT_OK;

public class UserAccountActivity extends AppCompatActivity {

    View firstname,lastname , email,password, regdno, degree, branch, mobile, updateProfile, galleryPick;
    ImageView eventImage;
    int PICK_IMAGE_REQUEST = 101;
    JSONObject profileObject = new JSONObject();
    AmazonS3Client s3;
    View.OnClickListener onClickListener = new View.OnClickListener() {
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

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.userAccount);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.APPEARING);
        linearLayout.setLayoutTransition(layoutTransition);
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.APPEARING);


        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
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


        TextView first_name_text = (firstname.findViewById(R.id.title));
        first_name_text.setText("First Name");

        TextView last_name_text = (lastname.findViewById(R.id.title));
        last_name_text.setText("Last Name");

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


        (firstname.findViewById(R.id.editContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable(firstname);
            }
        });

        (lastname.findViewById(R.id.editContent)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeEditable(lastname);
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

        setProfile();
    }

    private void setProfile(){
        String ref_id = Session.get(getBaseContext(),"ref_id");
        ApiClient.makeRequest(UserAccountActivity.this, null,null, Request.Method.GET, ApiClient.GET_PROFILE_PATH + "?ref_id=" + ref_id, new ResponseCallback() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try{
                    if(jsonObject.getInt("code") == 200){
                        JSONObject response = (JSONObject)jsonObject.get("response");
                        Glide.with(getBaseContext()).load(response.get("profile_picture")).into(eventImage);
                        ((TextView)(firstname.findViewById(R.id.descDisplay))).setText(response.getString("first_name"));
                        ((TextView)(lastname.findViewById(R.id.descDisplay))).setText(response.getString("last_name"));
                        ((TextView)(regdno.findViewById(R.id.descDisplay))).setText(response.getString("reg_no"));
                        ((TextView)(degree.findViewById(R.id.descDisplay))).setText(response.getString("degree"));
                        ((TextView)(branch.findViewById(R.id.descDisplay))).setText(response.getString("branch"));
                        ((TextView)(mobile.findViewById(R.id.descDisplay))).setText(response.getString("mobile"));
                        ((TextView)(email.findViewById(R.id.descDisplay))).setText(response.getString("email"));

                        ((EditText)(firstname.findViewById(R.id.descEdit))).setText(response.getString("first_name"));
                        ((EditText)(lastname.findViewById(R.id.descEdit))).setText(response.getString("last_name"));
                        ((EditText)(regdno.findViewById(R.id.descEdit))).setText(response.getString("reg_no"));
                        ((EditText)(degree.findViewById(R.id.descEdit))).setText(response.getString("degree"));
                        ((EditText)(branch.findViewById(R.id.descEdit))).setText(response.getString("branch"));
                        ((EditText)(mobile.findViewById(R.id.descEdit))).setText(response.getString("mobile"));
                        ((EditText)(email.findViewById(R.id.descEdit))).setText(response.getString("email"));


                        profileObject = response;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(UserAccountActivity.this, "EXCEPITON", Toast.LENGTH_SHORT).show();

                }

                Toast.makeText(UserAccountActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeEditable(View v) {
        TextView textView = (v.findViewById(R.id.descDisplay));
        EditText editText = (v.findViewById(R.id.descEdit));
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
            profileObject.put("ref_id", Session.get(getBaseContext(), "ref_id"));
            profileObject.put("first_name", ((EditText) (firstname.findViewById(R.id.descEdit))).getText());
            profileObject.put("last_name", ((EditText) (lastname.findViewById(R.id.descEdit))).getText());
            profileObject.put("reg_no", ((EditText) (regdno.findViewById(R.id.descEdit))).getText());
            profileObject.put("degree", ((EditText) (degree.findViewById(R.id.descEdit))).getText());
            profileObject.put("branch", ((EditText) (branch.findViewById(R.id.descEdit))).getText());
            profileObject.put("mobile", ((EditText) (mobile.findViewById(R.id.descEdit))).getText());
            profileObject.put("email", ((EditText) (email.findViewById(R.id.descEdit))).getText());
            profileObject.put("password", ((EditText) (password.findViewById(R.id.descEdit))).getText());
        }catch(Exception e){
            e.printStackTrace();
        }
        File file = null;
        try{
            file = (File) profileObject.get("file");
            profileObject.remove("file");
            if(file!=null){
                ApiClient.uploadtos3(getApplicationContext(), file, new ResponseCallback() {
                    @Override
                    public void onResponse(int response, String url) {
                        if (response == ApiClient.RESPONSE_CODE.SUCCESS) {
                            try {
                                profileObject.put("profile_picture", url);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        ApiClient.makeRequest(UserAccountActivity.this, profileObject,null, Request.Method.POST, ApiClient.EDIT_PROFILE_PATH, new ResponseCallback() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                Toast.makeText(UserAccountActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }catch (Exception e){
            ApiClient.makeRequest(UserAccountActivity.this, profileObject,null, Request.Method.POST, ApiClient.EDIT_PROFILE_PATH, new ResponseCallback() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Toast.makeText(UserAccountActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                }
            });
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
            File imageFile = new File(getRealPathFromURI(selectedImageURI));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Bitmap lastBitmap = null;
                lastBitmap = bitmap;
//                String image = getStringImage(lastBitmap);
//                Log.d("image",image);
                eventImage.setImageBitmap(lastBitmap);
                profileObject.put("file", imageFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}


