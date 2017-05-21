package sg.edu.rp.c346.eventful_organiser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class SignUp extends AppCompatActivity {

    EditText editTextName, editTextACRA, editTextNumber, editTextEmail, editTextWebsite, editTextAddress, editTextDescription, editTextPassword, editTextConfirmPassword;
    Button buttonSignUp, buttonSignIn;
    ImageButton imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;
    private Uri uri = null;
    final int GALLERY_REQUEST = 1;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("ORGANISER");
        Storage = FirebaseStorage.getInstance().getReference();

        mProgress = new ProgressDialog(this);

        editTextName = (EditText) findViewById(R.id.etCompanyName);
        editTextACRA = (EditText)findViewById(R.id.etACRA);
        editTextNumber = (EditText)findViewById(R.id.etBusinessContactNumber);
        editTextEmail = (EditText) findViewById(R.id.etEmail);
        editTextWebsite = (EditText)findViewById(R.id.etBusinessWebsite);
        editTextAddress = (EditText)findViewById(R.id.etAddress);
        editTextDescription = (EditText)findViewById(R.id.etDescription);
        editTextPassword = (EditText) findViewById(R.id.etPw);
        editTextConfirmPassword = (EditText) findViewById(R.id.etCPw);
        imageButton = (ImageButton) findViewById(R.id.imageButtonUser);
        buttonSignUp = (Button) findViewById(R.id.btnSignUp);
        buttonSignIn = (Button) findViewById(R.id.btnSignIn);

        imageButton.setOnClickListener(new View.OnClickListener() {
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
                startRegister();
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

    }

    private void startRegister() {
        final String name = editTextName.getText().toString().trim();
        final String acra = editTextACRA.getText().toString().trim();
        final String contact_number = editTextNumber.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String website = editTextWebsite.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();
        final String description = editTextDescription.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String password2 = editTextConfirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(acra) &&
                !TextUtils.isEmpty(contact_number) &&
                !TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(website) &&
                !TextUtils.isEmpty(address) &&
                !TextUtils.isEmpty(description) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(password2)
                && password.equalsIgnoreCase(password2)
                && uri != null) {

            mProgress.setMessage("Signing up ...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

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

//                         // Decode the encoded data with AES
//                         byte[] decodedBytes = null;
//                         try {
//                             Cipher c = Cipher.getInstance("AES");
//                             c.init(Cipher.DECRYPT_MODE, sks);
//                             decodedBytes = c.doFinal(encodedBytes);
//                         } catch (Exception e) {
//                             Toast.makeText(SignUp.this, "AES decryption error", Toast.LENGTH_LONG).show();
//                         }
//                         tvdecoded.setText("[DECODED]:\n" + new String(decodedBytes) + "\n");

                        String user_id = mAuth.getCurrentUser().getUid();
                        final DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("user_name").setValue(name);
                        current_user_db.child("acra").setValue(acra);
                        current_user_db.child("business_contact_name").setValue(contact_number);
                        current_user_db.child("email").setValue(email);
                        current_user_db.child("business_site").setValue(website);
                        current_user_db.child("address").setValue(address);
                        current_user_db.child("description").setValue(description);
                        current_user_db.child("password").setValue(Base64.encodeToString(encodedBytes, Base64.DEFAULT));
                        current_user_db.child("status").setValue("active");

                        StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());

                        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Toast.makeText(SignUp.this, downloadUrl.toString(), Toast.LENGTH_LONG).show();
                                current_user_db.child("image").setValue(downloadUrl.toString());
                                finish();

                            }
                        });

                        mProgress.dismiss();

                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(SignUp.this, task.getException() + "", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(SignUp.this, "A field is empty or passwords do not match. Please try again", Toast.LENGTH_SHORT).show();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            uri = data.getData();

            imageButton.setImageURI(uri);
        }
    }
}
