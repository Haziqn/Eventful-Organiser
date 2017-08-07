package sg.edu.rp.c346.eventful_organiser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {
    EditText editTextName,
            editTextACRA,
            editTextNumber,
            editTextEmail,
            editTextWebsite,
            editTextAddress,
            editTextDescription,
            editTextPassword,
            editTextConfirmPassword;
    Button buttonSignUp, buttonSearch;
    Spinner spinner;
    CircleImageView circleImageView;

    String TAG = "SignUp.java";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;
    private Uri uri = null;
    public String downloadUrl = "";
    final int GALLERY_REQUEST = 1;
    ProgressDialog mProgress;

    String name = "";
    String acra  = "";
    String contact_number = "";
    Integer cumn;
    String email = "";
    String address = "";
    String website = "";
    String description = "";
    String type = "";
    String password = "";
    String password2 = "";

    private GoogleMap map;

    List<Address> addressList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setTitle("Eventful - Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ORGANISER");
        Storage = FirebaseStorage.getInstance().getReference();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                int permissionCheck = ContextCompat.checkSelfPermission(SignUp.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                // Add a marker in Sydney and move the camera
                LatLng singapore = new LatLng(1.3553794, 103.8677444);
                map.addMarker(new MarkerOptions().position(singapore).title("Singapore"));
                map.moveCamera(CameraUpdateFactory.newLatLng(singapore));
                if (ActivityCompat.checkSelfPermission(SignUp.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SignUp.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);

            }
        });

        mProgress = new ProgressDialog(this);

        editTextName = (EditText) findViewById(R.id.etTitle);
        editTextACRA = (EditText)findViewById(R.id.etACRA);
        editTextNumber = (EditText)findViewById(R.id.etContact);
        editTextEmail = (EditText) findViewById(R.id.etEmailLogin);
        editTextWebsite = (EditText)findViewById(R.id.etSite);
        editTextAddress = (EditText)findViewById(R.id.etAddress);
        editTextDescription = (EditText)findViewById(R.id.etDescription);
        editTextPassword = (EditText) findViewById(R.id.etPwLogin);
        editTextConfirmPassword = (EditText) findViewById(R.id.etPwLogin2);
        circleImageView = (CircleImageView) findViewById(R.id.imageButtonUser);

        buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
        buttonSearch = (Button) findViewById(R.id.searchButton);

        spinner = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.business_array, android.R.layout.simple_spinner_item);
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

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setTitle("Verifying credentials");
                mProgress.setMessage("Checking");
                mProgress.show();
                name = editTextName.getText().toString().trim();
                acra = editTextACRA.getText().toString().trim();
                contact_number = editTextNumber.getText().toString().trim();
                cumn = Integer.parseInt(contact_number);
                email = editTextEmail.getText().toString().trim();
                website = editTextWebsite.getText().toString().trim();
                address = editTextAddress.getText().toString().trim();
                description = editTextDescription.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                password2 = editTextConfirmPassword.getText().toString().trim();
                if (field_verification(name, address, description, website, acra, contact_number, type, email, password, password2, uri)) {

                    LayoutInflater inflater = (LayoutInflater)
                            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout dialog =
                            (LinearLayout) inflater.inflate(R.layout.termsandconditions, null);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setTitle("Terms and Conditions")
                            .setView(dialog)
                            .setNegativeButton("I disagree", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("I agree", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    mProgress.show();
                                    startRegister();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if (uri == null) {
                    Toast.makeText(SignUp.this, "Please select an image", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUp.this, "One or more text fields is empty. Please, try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean field_verification(String name,
                                       String address,
                                       String description,
                                       String website,
                                       String acra,
                                       String contact_number,
                                       String type,
                                       String email,
                                       String password,
                                       String password2,
                                       Uri uri) {
        if (!TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(acra) &&
                !TextUtils.isEmpty(contact_number) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(website) &&
                !TextUtils.isEmpty(address) &&
                !TextUtils.isEmpty(description) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(password2) &&
                password.equalsIgnoreCase(password2) &&
                type != null &&
                uri != null) {
            startRegister();
            return true;
        }  else {

        Toast.makeText(SignUp.this, "A field is empty or passwords do not match. Please try again", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void startRegister() {
            mProgress.setTitle("Setting Up Account");
            mProgress.setMessage("Please while we create your account!");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        final String decodePassword = decode_password(password);

                        final Address addressMap = addressList.get(0);
                        final Double lat = addressMap.getLatitude();
                        final Double lng = addressMap.getLongitude();

                        String user_id = mAuth.getCurrentUser().getUid();
                        final DatabaseReference current_user_db = mDatabase.child(user_id);
                        StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());
                        filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {


                                    ORGANISER organiser = new ORGANISER();

                                    organiser.setEmail(email);
                                    organiser.setContact_num(Integer.parseInt(contact_number));
                                    organiser.setPassword(decodePassword);
                                    organiser.setStatus("active");
                                    organiser.setUser_name(name);
                                    organiser.setSite(website);
                                    organiser.setBusiness_type(type);
                                    organiser.setAcra(acra);
                                    organiser.setDescription(description);
                                    organiser.setAddress(address);
                                    organiser.setContact_num(cumn);
                                    organiser.setLat(lat);
                                    organiser.setLng(lng);
                                    downloadUrl = task.getResult().getDownloadUrl().toString();
                                    organiser.setImage(downloadUrl);
                                    current_user_db.setValue(organiser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                mProgress.dismiss();
                                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);

                                            } else {
                                                Toast.makeText(SignUp.this, task.getException().toString().trim(), Toast.LENGTH_LONG);
                                            }
                                        }
                                    });
                                } else {
                                    String error = "";
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e){
                                        error = "Requires at least 1 capital letter, 1 special character, 1 number and at least 6 characters!";
                                    } catch (FirebaseAuthInvalidCredentialsException e){
                                        error = "Invalid email";
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        error = "Email already exists";
                                    } catch (Exception e) {
                                        error = "Unknown error";
                                    }
                                    Toast.makeText(SignUp.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }

                        });

                    }
                }

            });

    }

    private String decode_password(String password) {
        SecretKeySpec sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Toast.makeText(SignUp.this, "AES secret key spec error", Toast.LENGTH_LONG).show();
        }

        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(password.getBytes());
        } catch (Exception e) {
            Toast.makeText(SignUp.this, "AES encryption error", Toast.LENGTH_LONG).show();
        }

        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            uri = data.getData();

            circleImageView.setImageURI(uri);
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