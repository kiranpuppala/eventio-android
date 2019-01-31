package com.app.kiranpuppala.event;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by kiran.puppala on 4/6/18.
 */

public class CreateEventActivity extends AppCompatActivity implements LocationListener {

    CustomDateTimePicker custom;
    Button btnEventDateTime;
    EditText eventName, description, venue, mobile, email, tagInput,coordinatorInput;
    public LinearLayout galleryPick, createEvent;
    public ImageView toDate,fromDate, fromTime,toTime, venuePick, eventImage;
    public TextView addTag,toDateText, fromDateText, toTimeText,fromTimeText,addCoordinator;
    public TagListAdapter adapter;
    public CoordinatorListAdapter coordinatorAdapter;
    private String fromTimeString ,toTimeString,date;
    public ArrayList<String> values,coordinatorsList;
    public RecyclerView recyclerView,coordinatorListView;
    int SELECT_PICTURE = 111;
    private String selectedImagePath;
    LocationManager locationManager;
    static final int LOCATION_REQUEST = 212;
    private ProgressDialog pd;

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


        values = new ArrayList<>();
        coordinatorsList = new ArrayList<>();
        adapter = new TagListAdapter(getBaseContext(), values);
        coordinatorAdapter = new CoordinatorListAdapter(getBaseContext(), coordinatorsList);

        recyclerView = findViewById(R.id.tagList);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        coordinatorListView = findViewById(R.id.coordinatorListView);
        coordinatorListView.setLayoutManager(llms);
        coordinatorListView.setAdapter(coordinatorAdapter);

        eventName = findViewById(R.id.eventName);
        description = findViewById(R.id.description);
        venue = findViewById(R.id.venue);
        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);

        tagInput = findViewById(R.id.tagInput);
        coordinatorInput = findViewById(R.id.coordinatorInput);

        addTag = findViewById(R.id.addTag);
        addTag.setOnClickListener(listener);

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


    View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (validateFields()) {
                Toast.makeText(CreateEventActivity.this, "FIELDS FILLED IN", Toast.LENGTH_SHORT).show();
                try{
                    JSONObject json = new JSONObject();
                    json.put("event_name",eventName.getText().toString());
                    json.put("description",description.getText().toString());
                    json.put("tags",values);
                    json.put("date",date);
                    json.put("from_time",fromTimeString);
                    json.put("to_time",toTimeString);
                    json.put("venue",venue.getText().toString());
                    json.put("mobile",mobile.getText().toString());
                    json.put("email",email.getText().toString());

                    Log.d("PAYLOAD", json.toString());

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        }
    };

    public boolean isEmpty(String str) {
        return (str == null && str.isEmpty());
    }

    public boolean validateFields() {
        if (!isEmpty(eventName.getText().toString())) {
            if (!isEmpty(description.getText().toString())) {
                if (!isEmpty(venue.getText().toString())) {
                    if (!isEmpty(mobile.getText().toString())) {
                            if (!isEmpty(email.getText().toString())) {
                                if(!isEmpty(values.toString())){
                                    if(!isEmpty(toTimeString)){
                                        if(!isEmpty(fromTimeString)){
                                            if(!isEmpty(venue.getText().toString())){
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
        return false;
    }

    View.OnClickListener listener = new View.OnClickListener()

    {
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
                case R.id.addTag:
                    handleTagInput();
                    break;
                case R.id.addCoordinator:
                    handleCoordinatorInput();
                    break;


            }
        }

    };

    public void handleTimings(final String type){

        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if(type.equals("fromTime")){
                            fromTimeString = (changeTimeFormat(hourOfDay+":"+mMinute));
                            fromTimeText.setText(changeTimeFormat(hourOfDay+":"+minute));
                        }else if(type.equals("toTime")){
                            toTimeString = (changeTimeFormat(hourOfDay+":"+mMinute));
                            toTimeText.setText(changeTimeFormat(hourOfDay+":"+minute));
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    public String changeTimeFormat(String time){

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("hh:mm aa").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public void handleEventImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    public void handleDate(final String mode) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth= c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        date = dayOfMonth+"-"+monthOfYear+"-"+year;
                        if(mode.equals("toDate"))
                            toDateText.setText(dayOfMonth+"-"+monthOfYear+"-"+year);
                        else
                            fromDateText.setText(dayOfMonth+"-"+monthOfYear+"-"+year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void handleTagInput() {
        String tag = tagInput.getText().toString();
        if (!isEmpty(tag)) {
            values.add(tag);
            adapter.notifyDataSetChanged();
        }

    }

    public void handleCoordinatorInput() {
        String tag = coordinatorInput.getText().toString();
        if (!isEmpty(tag)) {
            coordinatorsList.add(tag);
            coordinatorAdapter.notifyDataSetChanged();
        }

    }

    void handleVenue() {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Glide.with(CreateEventActivity.this).load(data.getData()).into((ImageView) findViewById(R.id.eventImage));
            }
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



    public void requestLocation() {
        try{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        }catch (SecurityException se){
            se.printStackTrace();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation();

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
            if(addresses!=null && addresses.size()!=0) {
                String address = addresses.get(0).getAddressLine(0);
                venue.setText(address);
                locationManager.removeUpdates(this);
                Log.d("ADDRESS GOT",address);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        pd.hide();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Log.d("ON STATUS","alkddsfsdf");

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("ON PROVIDR ENA","alkddsfsdf");

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("ON PROVIDR DISAB","alkddsfsdf");


    }
}
