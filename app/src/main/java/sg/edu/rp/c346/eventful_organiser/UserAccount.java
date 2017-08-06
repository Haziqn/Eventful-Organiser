package sg.edu.rp.c346.eventful_organiser;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import de.hdodenhof.circleimageview.CircleImageView;

import static sg.edu.rp.c346.eventful_organiser.SignIn.MY_PREFS_NAME;

public class UserAccount extends AppCompatActivity {

    Button btnUpdate, btnChange;
    ImageButton imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    DatabaseReference mOrganiser;
    StorageReference Storage;
    private Uri uri = null;
    public String downloadUrl = "";
    final int GALLERY_REQUEST = 1;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Account");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();
        mOrganiser = FirebaseDatabase.getInstance().getReference().child("ORGANISER");

        mProgress = new ProgressDialog(this);

        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnChange = (Button)findViewById(R.id.btnChange);

        final EditText etName = (EditText)findViewById(R.id.etName);
        final EditText etNum = (EditText)findViewById(R.id.etNum);
        final EditText etSite = (EditText)findViewById(R.id.etSite);
        final EditText etDesc = (EditText)findViewById(R.id.etDesc);
        final EditText etAcra = (EditText)findViewById(R.id.etAcra);
        final EditText etAddress = (EditText)findViewById(R.id.etAddress);
        final EditText etEmail = (EditText)findViewById(R.id.etEmail);
        final EditText etPassword = (EditText)findViewById(R.id.etPassword);
        final CircleImageView imageButton = (CircleImageView) findViewById(R.id.imageButtonUser);

        etPassword.setClickable(false);

        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

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

                    etEmail.setText(email);
                    etName.setText(user_name);
                    etNum.setText(contact_num);
                    etSite.setText(site);
                    etDesc.setText(description);
                    etAcra.setText(acra);
                    etAddress.setText(address);
                    Picasso.with(UserAccount.this).load(image).into(imageButton);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });
//
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, GALLERY_REQUEST);
//            }
//        });
//
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAccount.this, ChangePassword.class);
                startActivity(intent);

            }
        });
    }

    public void updateUserInfo() {

        final EditText etName = (EditText)findViewById(R.id.etName);
        final EditText etNum = (EditText)findViewById(R.id.etNum);
        final EditText etSite = (EditText)findViewById(R.id.etSite);
        final EditText etDesc = (EditText)findViewById(R.id.etDesc);
        final EditText etAcra = (EditText)findViewById(R.id.etAcra);
        final EditText etAddress = (EditText)findViewById(R.id.etAddress);
        final EditText etEmail = (EditText)findViewById(R.id.etEmail);
        final EditText etPw = (EditText)findViewById(R.id.editTextPw);
        final EditText etConfirmPw = (EditText)findViewById(R.id.editTextConfirmPw);

        final String name = etName.getText().toString().trim();
        final String num = etNum.getText().toString().trim();
        final String site = etSite.getText().toString().trim();
        final String desc = etDesc.getText().toString().trim();
        final String acra = etAcra.getText().toString().trim();
        final String address = etAddress.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String pw = etPw.getText().toString().trim();
        final String confirmPw = etConfirmPw.getText().toString().trim();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        if (name != "" || num != "" || site != "" || desc != "" || acra != "" || address != "" || email != "" || pw != "" || confirmPw != "") {
            if (pw.equals(confirmPw)) {

                String pwDecode = decode_password(confirmPw);

                user.updatePassword(pwDecode)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("UserAccount", "User password updated.");
                                }
                            }
                        });

                user.updateEmail(pwDecode)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("UserAccount", "User email address updated.");
                                }
                            }
                        });

                mOrganiser.child(uid).child("password").setValue(pwDecode);
                mOrganiser.child(uid).child("user_name").setValue(name);
                mOrganiser.child(uid).child("contact_num").setValue(num);
                mOrganiser.child(uid).child("site").setValue(site);
                mOrganiser.child(uid).child("description").setValue(desc);
                mOrganiser.child(uid).child("acra").setValue(acra);
                mOrganiser.child(uid).child("address").setValue(address);
                mOrganiser.child(uid).child("email").setValue(email);
            } else {
                Toast.makeText(UserAccount.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(UserAccount.this, "One or more fields are empty.", Toast.LENGTH_LONG).show();
        }

//        StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());
//
//        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                downloadUrl = taskSnapshot.getDownloadUrl().toString();
//                mDatabase.child(uid).child("image").setValue(downloadUrl);
//                finish();
//            }
//        });
    }

    private String decode_password (String confirmPw) {
        SecretKeySpec sks = null;
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, sr);
            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (Exception e) {
            Toast.makeText(UserAccount.this, "AES secret key spec error", Toast.LENGTH_LONG).show();
        }

        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, sks);
            encodedBytes = c.doFinal(confirmPw.getBytes());
        } catch (Exception e) {
            Toast.makeText(UserAccount.this, "AES encryption error", Toast.LENGTH_LONG).show();
        }

        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
    }

    public void userDelete() {
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabase.child(uid).removeValue();
                            Intent intent = new Intent(UserAccount.this, SignUp.class);
                            startActivity(intent);
                            Log.d("userDelete", "User account deleted.");
                        }
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            uri = data.getData();

            imageButton.setImageURI(uri);
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
}
