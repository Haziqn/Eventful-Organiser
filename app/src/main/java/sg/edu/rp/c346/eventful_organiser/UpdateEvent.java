package sg.edu.rp.c346.eventful_organiser;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.robertsimoes.shareable.Shareable;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateEvent extends AppCompatActivity {

    EditText editTextTitle, editTextDesc, editTextHeadChief, editTextLocation, editTextAddress;
    TextView textViewStartDate, textViewStartTime, textViewEndDate, textViewEndTime, textViewOrganiser;
    Spinner spinner;
    Button btnSubmit;
    ImageButton imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase, mDatabaseOrganiser, databaseReference;
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

    String image;
    String itemKey;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        Intent i = getIntent();
        itemKey = i.getStringExtra("updateKey");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                int permissionCheck = ContextCompat.checkSelfPermission(UpdateEvent.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                // Add a marker in Sydney and move the camera
                LatLng singapore = new LatLng(1.3553794, 103.8677444);
                map.addMarker(new MarkerOptions().position(singapore).title("Singapore"));
                map.moveCamera(CameraUpdateFactory.newLatLng(singapore));
                if (ActivityCompat.checkSelfPermission(UpdateEvent.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(UpdateEvent.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);

            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase = databaseReference.child("EVENT");
        mDatabaseOrganiser = databaseReference.child("ORGANISER");
        Storage = FirebaseStorage.getInstance().getReference();
        user_id = user.getUid();

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
                Geocoder geocoder = new Geocoder(UpdateEvent.this);
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

        DatabaseReference mDatabaseRef = mDatabase.child(itemKey);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EVENT event = dataSnapshot.getValue(EVENT.class);
                image = dataSnapshot.child("image").getValue().toString();
                String title = dataSnapshot.child("title").getValue().toString();
                String startDate = dataSnapshot.child("startDate").getValue().toString();
                String startTime = dataSnapshot.child("startTime").getValue().toString();
                String endDate = dataSnapshot.child("endDate").getValue().toString();
                String endTime = dataSnapshot.child("endTime").getValue().toString();
                final String address = dataSnapshot.child("location").getValue().toString();
                String desc = dataSnapshot.child("description").getValue().toString();
                String headChief = dataSnapshot.child("head_chief").getValue().toString();

                Picasso.with(getBaseContext()).load(image).into(imageButton);
                editTextTitle.setText(title);
                textViewStartDate.setText(startDate);
                textViewStartTime.setText(startTime);
                textViewEndTime.setText(endTime);
                textViewEndDate.setText(endDate);
                editTextLocation.setText(address);
                editTextDesc.setText(desc);
                editTextHeadChief.setText(headChief);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateEvent.this,
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateEvent.this,
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateEvent.this,
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateEvent.this,
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

        btnSubmit = (Button)findViewById(R.id.updatebutton);

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

        getSupportActionBar().setTitle("Update Event");

        Progress = new ProgressDialog(this);

    }

    private void startPosting() {
        Progress.setMessage("Updating");
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

        if (uri != null) {
            StorageReference filepath = Storage.child("Event_Image").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    EVENT event = new EVENT();
                    Map map = new HashMap();
                    map.put("EVENT/" + itemKey + "/image", event);
                    String downloadUrl = taskSnapshot.getDownloadUrl().toString().trim();
                    event.setImage(downloadUrl);
                }

            });
        }

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
                event.setEventType(type);
        event.setImage(image);

                Map map = new HashMap();
                map.put("EVENT/" + itemKey, event);

                FirebaseDatabase.getInstance().getReference().updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Progress.dismiss();
                            Intent intent = new Intent(UpdateEvent.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(UpdateEvent.this, task.getException().toString().trim(), Toast.LENGTH_LONG);
                        }
                    }
                });


        Progress.dismiss(); //loading bar
        finish();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            uri = data.getData();

            imageButton.setImageURI(uri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_share) {

            AlertDialog.Builder myBuilder = new AlertDialog.Builder(UpdateEvent.this);

            myBuilder.setTitle("Delete Account");
            myBuilder.setMessage("Are you sure?");
            myBuilder.setCancelable(false);
            myBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    databaseReference.child("EVENT_PARTICIPANTS").child(user_id).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if(dataSnapshot.hasChildren()) {
                                Toast.makeText(UpdateEvent.this, "You have currently joined events! Please leave all current events.", Toast.LENGTH_LONG).show();
                            } else {

                                final DatabaseReference current_user_db = mDatabase.child(user_id);
                                current_user_db.child("status").setValue("deactivated").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("EditProfile", "User account deleted.");
                                                        }
                                                    }
                                                });
                                        Intent i = new Intent(UpdateEvent.this, StartActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });
            myBuilder.setNegativeButton("Cancel", null);

            AlertDialog myDialog = myBuilder.create();
            myDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
