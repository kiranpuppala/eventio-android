package com.app.kiranpuppala.event;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.kiranpuppala.event.network.ApiClient;
import com.app.kiranpuppala.event.network.ResponseCallback;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by kiran.puppala on 4/6/18.
 */

public class CreateEventActivity extends AppCompatActivity implements LocationListener {

    private static final String LOG_TAG = "CREATE_EVENT_ACTIVITY";
    private static final int LOCATION_REQUEST = 212;
    private static final int STORAGE_REQUEST = 213;
    public LinearLayout galleryPick, createEvent;
    public ImageView toDate, fromDate, fromTime, toTime, venuePick, eventImage;
    public TextView toDateText, fromDateText, toTimeText, fromTimeText, addCoordinator;
    public CoordinatorListAdapter coordinatorAdapter;
    public ArrayList<String> values, coordinatorsList;
    public RecyclerView coordinatorListView;
    private EditText eventName, description, venue, mobile, email, tagInput, coordinatorInput;
    private int SELECT_PICTURE = 111;
    private LocationManager locationManager;
    private String fromTimeString, toTimeString, date;
    private String eventCategory = "";
    private JSONObject eventObject = new JSONObject();
    private File eventImageFile;

    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateFields()) {
                ApiClient.uploadtos3(getApplicationContext(), eventImageFile, new ResponseCallback() {
                    @Override
                    public void onResponse(final int response, String url) {
                        try {
                            if (response == ApiClient.RESPONSE_CODE.SUCCESS) {
                                eventObject.put("graphic", url);
                            } else {
                                eventObject.put("graphic", "");
                            }
                            eventObject.put("name", eventName.getText().toString());
                            eventObject.put("description", description.getText().toString());
                            eventObject.put("category", eventCategory);
                            eventObject.put("to_date", toDateText.getText().toString());
                            eventObject.put("from_date", fromDateText.getText().toString());
                            eventObject.put("to_time", toTimeString);
                            eventObject.put("from_time", fromTimeString);
                            eventObject.put("venue", venue.getText().toString());
                            eventObject.put("coordinators", coordinatorsList.toString());
                            eventObject.put("mobile", mobile.getText().toString());
                            eventObject.put("email", email.getText().toString());

                            final AccountManager am = AccountManager.get(CreateEventActivity.this);
                            Account account[] = (am.getAccountsByType(GetInActivity.ARG_ACCOUNT_TYPE));
                            if (account.length != 0) {
                                final AccountManagerFuture<Bundle> future = am.getAuthToken(account[0], GetInActivity.ARG_AUTH_TYPE, null, CreateEventActivity.this, null, null);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Bundle bnd = future.getResult();
                                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                                            Map<String, String> headers = new HashMap<>();
                                            headers.put("authorization", authtoken);
                                            ApiClient.makeRequest(CreateEventActivity.this, eventObject, headers, Request.Method.POST, ApiClient.CREATE_EVENT_PATH, new ResponseCallback() {
                                                @Override
                                                public void onResponse(JSONObject jsonObject) {
                                                    try {
                                                        int responseCode = jsonObject.getInt("code");
                                                        if (responseCode == 200) {
                                                            Toast.makeText(getBaseContext(), "SUCCESS", Toast.LENGTH_SHORT).show();
                                                        }else if (responseCode == 403){
                                                            am.invalidateAuthToken(GetInActivity.ARG_ACCOUNT_TYPE,authtoken);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }else{
                Toast.makeText(getBaseContext(),"Fill All Fields",Toast.LENGTH_SHORT).show();
            }
        }
    };
    private ProgressDialog pd;
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.galleryPick:
                    handleEventImage();
                    break;
                case R.id.createEvent:
                    break;
                case R.id.toDate:
                    handleDate("toDate");
                    break;
                case R.id.fromDate:
                    handleDate("fromDate");
                    break;
                case R.id.fromTime:
                    handleTimings("fromTime");
                    break;
                case R.id.toTime:
                    handleTimings("toTime");
                    break;
                case R.id.venuePick:
                    handleVenue();
                    break;
                case R.id.addCoordinator:
                    handleCoordinatorInput();
                    break;


            }
        }

    };
    private Spinner category_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager llms = new LinearLayoutManager(this);
        llms.setOrientation(LinearLayoutManager.HORIZONTAL);

        category_spinner = findViewById(R.id.event_category);

        final Typeface mtypeface = Typeface.createFromAsset(getBaseContext().getAssets(),
                "fonts/sf_pro_medium.otf");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getApplicationContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.event_category)) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) return false;
                else return true;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0) ((TextView) v).setTextColor(Color.GRAY);
                else ((TextView) v).setTextColor(Color.BLACK);
                ((TextView) v).setTypeface(mtypeface);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                if (position == 0) ((TextView) v).setTextColor(Color.GRAY);
                else ((TextView) v).setTextColor(Color.BLACK);
                ((TextView) v).setTypeface(mtypeface);
                return v;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        category_spinner.setAdapter(adapter);

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventCategory = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        values = new ArrayList<>();
        coordinatorsList = new ArrayList<>();
        coordinatorAdapter = new CoordinatorListAdapter(getBaseContext(), coordinatorsList);


        coordinatorListView = findViewById(R.id.coordinatorListView);
        coordinatorListView.setLayoutManager(llms);
        coordinatorListView.setAdapter(coordinatorAdapter);

        eventName = findViewById(R.id.eventName);
        description = findViewById(R.id.description);
        venue = findViewById(R.id.venue);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);

        coordinatorInput = findViewById(R.id.coordinatorInput);

        addCoordinator = findViewById(R.id.addCoordinator);
        addCoordinator.setOnClickListener(listener);

        galleryPick = findViewById(R.id.galleryPick);
        galleryPick.setOnClickListener(listener);

        fromTime = findViewById(R.id.fromTime);
        fromTime.setOnClickListener(listener);

        toTime = findViewById(R.id.toTime);
        toTime.setOnClickListener(listener);

        galleryPick = findViewById(R.id.galleryPick);
        galleryPick.setOnClickListener(listener);

        createEvent = findViewById(R.id.createEvent);
        createEvent.setOnClickListener(submitListener);

        toDate = findViewById(R.id.toDate);
        toDate.setOnClickListener(listener);
        fromDate = findViewById(R.id.fromDate);
        fromDate.setOnClickListener(listener);
        fromTime = findViewById(R.id.fromTime);
        toTime = findViewById(R.id.toTime);
        venuePick = findViewById(R.id.venuePick);
        venuePick.setOnClickListener(listener);
        eventImage = findViewById(R.id.eventImage);
        toDateText = findViewById(R.id.toDateText);
        fromDateText = findViewById(R.id.fromDateText);
        fromTimeText = findViewById(R.id.fromTimeText);
        toTimeText = findViewById(R.id.toTimeText);

        pd = new ProgressDialog(this);
        pd.setMessage("Fetching current location");
    }

    private boolean isEmpty(String str) {
        return (str == null || str.isEmpty());
    }

    private boolean validateFields() {
        if (!isEmpty(eventName.getText().toString())) {
            if (!isEmpty(description.getText().toString())) {
                if (!isEmpty(venue.getText().toString())) {
                    if (!isEmpty(mobile.getText().toString())) {
                        if (!isEmpty(email.getText().toString())) {
                            if (!isEmpty(eventCategory)) {
                                if (!isEmpty(toTimeString)) {
                                    if (!isEmpty(fromTimeString)) {
                                        if(!isEmpty(toDateText.getText().toString())){
                                            if(!isEmpty(fromDateText.getText().toString())){
                                                if(eventImageFile!=null){
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private void handleTimings(final String type) {
        final Calendar c = Calendar.getInstance();
        final int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (type.equals("fromTime")) {
                            fromTimeString = (changeTimeFormat(hourOfDay + ":" + mMinute));
                            fromTimeText.setText(changeTimeFormat(hourOfDay + ":" + minute));
                        } else if (type.equals("toTime")) {
                            toTimeString = (changeTimeFormat(hourOfDay + ":" + mMinute));
                            toTimeText.setText(changeTimeFormat(hourOfDay + ":" + minute));
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    public String changeTimeFormat(String time) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("hh:mm aa").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    private void handleEventImage() {
        if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateEventActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_REQUEST);
        } else {
            pickEventImage();
        }
    }

    private void pickEventImage(){
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageIntent.setType("image/*");
        pickImageIntent.putExtra("aspectX", 1);
        pickImageIntent.putExtra("aspectY", 1);
        pickImageIntent.putExtra("scale", true);
        pickImageIntent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(pickImageIntent, SELECT_PICTURE);
    }

    private void handleDate(final String mode) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        date = dayOfMonth + "-" + monthOfYear + "-" + year;
                        if (mode.equals("toDate"))
                            toDateText.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
                        else
                            fromDateText.setText(dayOfMonth + "-" + monthOfYear + "-" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void handleCoordinatorInput() {
        String tag = coordinatorInput.getText().toString();
        if (!isEmpty(tag)) {
            coordinatorsList.add(tag);
            coordinatorAdapter.notifyDataSetChanged();
        }
    }


    private void handleVenue() {
        pd.show();
        try {
            if (ContextCompat.checkSelfPermission(CreateEventActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CreateEventActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST);
            } else {
                requestLocation();
            }

        } catch (SecurityException e) {
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Glide.with(CreateEventActivity.this).load(data.getData()).into((ImageView) findViewById(R.id.eventImage));
            Uri selectedImageURI = data.getData();
            eventImageFile = new File(getRealPathFromURI(selectedImageURI));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void requestLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        } catch (SecurityException se) {
            se.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();
                } else {

                }
                return;
            }
            case STORAGE_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickEventImage();
                } else {

                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() != 0) {
                String address = addresses.get(0).getAddressLine(0);
                venue.setText(address);
                locationManager.removeUpdates(this);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        pd.hide();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
