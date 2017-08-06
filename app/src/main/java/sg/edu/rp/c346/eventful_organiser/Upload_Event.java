package sg.edu.rp.c346.eventful_organiser;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Upload_Event extends AppCompatActivity {

    EditText editTextTitle, editTextDesc, editTextHeadChief, editTextLocation, editTextAddress;
    TextView textViewStartDate, textViewStartTime, textViewEndDate, textViewEndTime, textViewOrganiser;
    Spinner spinner;
    Button btnSubmit, btnSearch;
    ImageButton imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference mDatabaseOrganiser;
    StorageReference Storage;
    private Uri uri = null;
    final int GALLERY_REQUEST = 1;
    String user_id = "";

    int years;
    int monthOfYears;
    int dayOfMonths;
    int day;
    int hour;
    int mins;
    Calendar myCalendar;
    String message = "";
    String type;
    String organiser_name;

    private GoogleMap map;

    List<Address> addressList = null;

    ProgressDialog Progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__event);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                int permissionCheck = ContextCompat.checkSelfPermission(Upload_Event.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                // Add a marker in Sydney and move the camera
                LatLng singapore = new LatLng(1.3553794, 103.8677444);
                map.addMarker(new MarkerOptions().position(singapore).title("Singapore"));
                map.moveCamera(CameraUpdateFactory.newLatLng(singapore));
                if (ActivityCompat.checkSelfPermission(Upload_Event.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(Upload_Event.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");
        mDatabaseOrganiser = FirebaseDatabase.getInstance().getReference().child("ORGANISER");
        Storage = FirebaseStorage.getInstance().getReference();
        user_id = mAuth.getCurrentUser().getUid();

        myCalendar = Calendar.getInstance();
        years = myCalendar.get(Calendar.YEAR);
        monthOfYears = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        imageButton = (ImageButton) findViewById(R.id.ibEvent);
        editTextAddress = (EditText) findViewById(R.id.etAddress);
        editTextAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Geocoder geocoder = new Geocoder(Upload_Event.this);
                try {
                    addressList = geocoder.getFromLocationName(charSequence.toString().trim(), 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                map.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextDesc = (EditText) findViewById(R.id.etDesc);
        editTextHeadChief = (EditText) findViewById(R.id.etEIC);
        editTextLocation = (EditText) findViewById(R.id.etLocation);
        editTextTitle = (EditText) findViewById(R.id.etTitle);

        textViewEndDate = (TextView) findViewById(R.id.tvEndDate);
        textViewEndTime = (TextView) findViewById(R.id.tvEndTime);
        textViewStartDate = (TextView) findViewById(R.id.tvStartDate);
        textViewStartTime = (TextView) findViewById(R.id.tvStartTime);
        textViewOrganiser = (TextView) findViewById(R.id.tvOrganiser);
        btnSearch = (Button)findViewById(R.id.searchButton);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        myCalendar.add(Calendar.DATE, 7); // number of days to add
        String advancedDate = sdf.format(myCalendar.getTime());

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 8);
        String newDate = sdf.format(c.getTime());
        SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
        String currentTime = stf.format(myCalendar.getTime());

        textViewStartDate.setText(advancedDate);
        textViewStartTime.setText(currentTime);
        textViewEndDate.setText(newDate);
        textViewEndTime.setText(currentTime);

        mDatabaseOrganiser.child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                organiser_name = dataSnapshot.child("user_name").getValue().toString();
                textViewOrganiser.setText("By " + organiser_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        textViewOrganiser.setText("By " + organiser_name);



        textViewStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Upload_Event.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                message += dayOfMonth + "/" + monthOfYear + "/" + year;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(years, monthOfYears, day, hour, mins);
                                textViewStartDate.setText(message);
                                message = "";
                            }
                        }, years, monthOfYears, day);

                datePickerDialog.show();
            }
        });

        textViewEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Upload_Event.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                message += dayOfMonth + "/" + monthOfYear + "/" + year;
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(years, monthOfYears, day, hour, mins);
                                textViewEndDate.setText(message);
                                message = "";
                            }
                        }, years, monthOfYears, day);

                datePickerDialog.show();
            }
        });

        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hours = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minutes = myCalendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Upload_Event.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                message += hourOfDay + ":" + minute + " ";
                                hour = hourOfDay;
                                mins = minute;
                                textViewStartTime.setText(message);
                                message = "";
                            }
                        }, hours, minutes, false);
                timePickerDialog.show();
            }
        });

        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hours = myCalendar.get(java.util.Calendar.HOUR_OF_DAY);
                int minutes = myCalendar.get(java.util.Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Upload_Event.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                message += hourOfDay + ":" + minute + " ";
                                hour = hourOfDay;
                                mins = minute;
                                textViewEndTime.setText(message);
                                message = "";
                            }
                        }, hours, minutes, false);
                timePickerDialog.show();
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                type = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                type = "";
            }
        });

        btnSubmit = (Button)findViewById(R.id.submitbutton);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = editTextTitle.getText().toString().trim();
                final String description = editTextDesc.getText().toString().trim();
                final String location = editTextLocation.getText().toString().trim();
                final String event_in_Charge = editTextHeadChief.getText().toString().trim();
                final String address = editTextAddress.getText().toString().trim();

                if (TextUtils.isEmpty(title)) {
                    editTextTitle.setError("Field should not be empty.");
                } else if (TextUtils.isEmpty(description)){
                    editTextDesc.setError("Field should not be empty.");
                } else if (TextUtils.isEmpty(location)){
                    editTextLocation.setError("Field should not be empty.");
                } else if (TextUtils.isEmpty(address)){
                    editTextAddress.setError("Field should not be empty.");
                } else if (TextUtils.isEmpty(event_in_Charge)){
                    editTextHeadChief.setError("Field should not be empty.");
                } else {
                    startPosting();
                }
            }

        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Create an Event");

        Progress = new ProgressDialog(this);
    }

    public void onMapSearch(View view) {

        String location = editTextAddress.getText().toString();

        if (TextUtils.isEmpty(location)) {
            editTextAddress.setError("Please input an address.");
        } else {
            if (location != null || !location.equals("")) {
                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                map.addMarker(new MarkerOptions().position(latLng).title("Marker"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            }
        }
    }

    private void startPosting() {
        Progress.setMessage("Uploading");
        Progress.show();

        final String title = editTextTitle.getText().toString().trim();
        final String description = editTextDesc.getText().toString().trim();
        final String startTime = textViewStartTime.getText().toString().trim();
        final String startDate = textViewStartDate.getText().toString().trim();
        final String endTime = textViewEndTime.getText().toString().trim();
        final String endDate = textViewEndDate.getText().toString().trim();
        final String location = editTextLocation.getText().toString().trim();
        final String event_in_Charge = editTextHeadChief.getText().toString().trim();
        final String organiser = user_id;
        Address address = addressList.get(0);
        final Double lat = address.getLatitude();
        final Double lng = address.getLongitude();

        if(fieldVerification(title,
                description,
                organiser,
                startDate,
                startTime,
                endDate,
                endTime,
                event_in_Charge,
                lat,
                lng,
                location)) {

            StorageReference filepath = Storage.child("Event_Image").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString().trim();
                    EVENT event = new EVENT();
                    event.setTitle(title);
                    event.setStartTime(startTime);
                    event.setStartDate(startDate);
                    event.setEndDate(endDate);
                    event.setEndTime(endTime);
                    event.setLat(lat);
                    event.setLng(lng);
                    event.setLocation(location);
                    event.setDescription(description);
                    event.setHead_chief(event_in_Charge);
                    event.setOrganiser(organiser);
                    event.setTimeStamp(getCurrentTimeStamp());
                    event.setStatus("active");
                    event.setImage(downloadUrl);
                    event.setEventType(type);

                    mDatabase.push().setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Progress.dismiss();
                                Intent intent = new Intent(Upload_Event.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {
                                Toast.makeText(Upload_Event.this, task.getException().toString().trim(), Toast.LENGTH_LONG);
                            }
                        }
                    });
                }
            });

            Progress.dismiss(); //loading bar
            finish();
        } else {
            Toast.makeText(Upload_Event.this, "A field or more is empty. Please try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            uri = data.getData();

            imageButton.setImageURI(uri);
        }
    }

    public static String getCurrentTimeStamp(){
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateTime = dateFormat.format(new Date()); // Find todays date

            return currentDateTime;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public Boolean fieldVerification(String title,
                                     String description,
                                     String organiser,
                                     String startDate,
                                     String startTime,
                                     String endDate,
                                     String endTime,
                                     String event_in_Charge,
                                     Double lat,
                                     Double lng,
                                     String location) {

        if (!TextUtils.isEmpty(title) &&
                !TextUtils.isEmpty(description) &&
                !TextUtils.isEmpty(organiser) &&
                !TextUtils.isEmpty(startDate) &&
                !TextUtils.isEmpty(startTime) &&
                !TextUtils.isEmpty(endDate) &&
                !TextUtils.isEmpty(endTime) &&
                !TextUtils.isEmpty(event_in_Charge) &&
                !TextUtils.isEmpty(location) &&
                lat != null &&
                lng != null &&
                uri != null) {
            return true;
        }
        return false;
    }
}