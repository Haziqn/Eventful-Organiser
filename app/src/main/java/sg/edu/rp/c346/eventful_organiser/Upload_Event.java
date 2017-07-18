package sg.edu.rp.c346.eventful_organiser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Upload_Event extends AppCompatActivity {

    EditText etTitle;
    EditText etDesc;
    EditText etOrganiser;
    EditText etHeadChief;
//    EditText etDate;
//    EditText etTime;
    EditText etAddress;
    EditText etPax;
    Button btnSubmit;
    ImageButton imageButton;
    DatePicker datePicker;
    TimePicker timePicker;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference mDatabaseOrganiser;
    StorageReference Storage;
    private Uri uri = null;
    final int GALLERY_REQUEST = 1;
    String user_id = "";

    int year = 0;
    int monthOfYear = 0;
    int dayOfMonth = 0;

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
                if (ActivityCompat.checkSelfPermission(Upload_Event.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Upload_Event.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        imageButton = (ImageButton) findViewById(R.id.imageButtonUser);
        etTitle = (EditText)findViewById(R.id.titleH);
        etDesc = (EditText)findViewById(R.id.descH);
        etOrganiser = (EditText)findViewById(R.id.organiserH);
        etHeadChief = (EditText)findViewById(R.id.headChiefH);
//        etDate = (EditText)findViewById(R.id.dateH);
//        etTime = (EditText)findViewById(R.id.timeH);
        etAddress = (EditText)findViewById(R.id.addressH);
        etPax = (EditText)findViewById(R.id.etPaxH);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);



        btnSubmit = (Button)findViewById(R.id.submitbutton);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
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
        EditText locationSearch = (EditText) findViewById(R.id.addressH);
        String location = locationSearch.getText().toString();


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

    private void startPosting() {
        Progress.setMessage("Uploading");
        Progress.show();

        String title_val = etTitle.getText().toString().trim();
        String desc_val = etDesc.getText().toString().trim();
        String organiser_val = etOrganiser.getText().toString().trim();
//        String date_val = etDate.getText().toString().trim();
//        String time_val = etTime.getText().toString().trim();
        String headChief_val = etHeadChief.getText().toString().trim();
        String address_val = etAddress.getText().toString().trim();
        String pax_val = etPax.getText().toString().trim();
        final DatabaseReference mPost = mDatabase.push();
        Address address = addressList.get(0);

        int hour = timePicker.getCurrentHour(); //24hr
        int min = timePicker.getCurrentMinute();

        String time = String.valueOf(hour) + ":" + String.valueOf(min);

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        String date = String.valueOf(day) + "/" + String.valueOf(month) + "/" + String.valueOf(year);

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && !TextUtils.isEmpty(organiser_val) && !TextUtils.isEmpty(headChief_val) && !TextUtils.isEmpty(address_val) && !TextUtils.isEmpty(pax_val) && uri != null) {

            mPost.child("address").setValue(address_val);
            mPost.child("date").setValue(date);
            mPost.child("time").setValue(time);
            mPost.child("description").setValue(desc_val);
            mPost.child("head_chief").setValue(headChief_val);
            mPost.child("organiser_name").setValue(organiser_val);
            mPost.child("organiser").setValue(user_id);
            mPost.child("pax").setValue(pax_val);
            mPost.child("title").setValue(title_val);
            mPost.child("status").setValue("active");
            mPost.child("timeStamp").setValue(getCurrentTimeStamp().toString().trim());
            mPost.child("longitude").setValue(address.getLongitude());
            mPost.child("latitude").setValue(address.getLatitude());

            StorageReference filepath = Storage.child("Event_Image").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mPost.child("image").setValue(downloadUrl.toString());
                    finish();

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
}
