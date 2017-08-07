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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAccount extends AppCompatActivity {

    EditText editTextName, editTextNum, editTextSite, editTextAddress, editTextDesc, editTextAcra;
    TextView textViewEmail;
    Button btnUpdate;
    CircleImageView imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase, databaseReference;
    DatabaseReference mOrganiser;
    StorageReference Storage;
    private Uri uri = null;
    public String downloadUrl = "";
    final int GALLERY_REQUEST = 1;
    ProgressDialog mProgress;

    FirebaseUser user;
    String uid;

    private GoogleMap map;

    List<Address> addressList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                int permissionCheck = ContextCompat.checkSelfPermission(UserAccount.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                // Add a marker in Sydney and move the camera
                LatLng singapore = new LatLng(1.3553794, 103.8677444);
                map.addMarker(new MarkerOptions().position(singapore).title("Singapore"));
                map.moveCamera(CameraUpdateFactory.newLatLng(singapore));
                if (ActivityCompat.checkSelfPermission(UserAccount.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UserAccount.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Account");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabase = databaseReference.child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();
        mOrganiser = databaseReference.child("ORGANISER");

        mProgress = new ProgressDialog(this);

        btnUpdate = (Button)findViewById(R.id.btnUpdate);

        editTextName = (EditText)findViewById(R.id.etName);
        editTextNum = (EditText)findViewById(R.id.etNum);
        editTextSite = (EditText)findViewById(R.id.etSite);
        editTextDesc = (EditText)findViewById(R.id.etDesc);
        editTextAcra = (EditText)findViewById(R.id.etAcra);
        editTextAddress = (EditText)findViewById(R.id.etAddress);

        textViewEmail = (TextView)findViewById(R.id.tvEmail);
        imageButton = (CircleImageView) findViewById(R.id.imageButtonUser);

        user = mAuth.getCurrentUser();
        uid = user.getUid();

        DatabaseReference mDatabaseRef = mOrganiser.child(uid);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean hasImage = dataSnapshot.hasChild("image");
                Boolean hasName = dataSnapshot.hasChild("user_name");
                Boolean hasNumber = dataSnapshot.hasChild("contact_num");
                Boolean hasSite = dataSnapshot.hasChild("site");
                Boolean hasDesc = dataSnapshot.hasChild("description");
                Boolean hasAcra = dataSnapshot.hasChild("acra");
                Boolean hasAddress = dataSnapshot.hasChild("address");
                Boolean hasEmail = dataSnapshot.hasChild("email");

                if (hasImage && hasName && hasNumber && hasSite && hasDesc && hasAcra && hasAddress && hasEmail) {
                    String image = dataSnapshot.child("image").getValue().toString();
                    String user_name = dataSnapshot.child("user_name").getValue().toString();
                    String contact_num = dataSnapshot.child("contact_num").getValue().toString();
                    String site = dataSnapshot.child("site").getValue().toString();
                    String description = dataSnapshot.child("description").getValue().toString();
                    String acra = dataSnapshot.child("acra").getValue().toString();
                    String address = dataSnapshot.child("address").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    editTextName.setText(user_name);
                    editTextNum.setText(contact_num);
                    editTextSite.setText(site);
                    editTextDesc.setText(description);
                    editTextAcra.setText(acra);
                    editTextAddress.setText(address);
                    textViewEmail.setText(email);
                    Picasso.with(UserAccount.this).load(image).into(imageButton);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
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

    }

    public void onMapSearch(View view) {

        String location = editTextAddress.getText().toString();


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

    public void updateUserInfo() {

        final String name = editTextName.getText().toString().trim();
        final String num = editTextNum.getText().toString().trim();
        final String site = editTextSite.getText().toString().trim();
        final String desc = editTextDesc.getText().toString().trim();
        final String acra = editTextAcra.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();

        StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());
        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    mOrganiser.child(uid).child("user_name").setValue(name);
                    mOrganiser.child(uid).child("contact_num").setValue(num);
                    mOrganiser.child(uid).child("site").setValue(site);
                    mOrganiser.child(uid).child("description").setValue(desc);
                    mOrganiser.child(uid).child("acra").setValue(acra);
                    mOrganiser.child(uid).child("address").setValue(address);
                    downloadUrl = task.getResult().getDownloadUrl().toString();
                    mOrganiser.child(uid).child("image").setValue(downloadUrl);

                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            uri = data.getData();

            imageButton.setImageURI(uri);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                uri = result.getUri();
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
