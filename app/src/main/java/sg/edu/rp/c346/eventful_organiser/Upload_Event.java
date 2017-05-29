package sg.edu.rp.c346.eventful_organiser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Upload_Event extends AppCompatActivity {

    EditText etTitle;
    EditText etDesc;
    EditText etOrganiser;
    EditText etHeadChief;
    EditText etDate;
    EditText etTime;
    EditText etAddress;
    EditText etPax;
    Button btnSubmit;
    ImageButton imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;
    private Uri uri = null;
    final int GALLERY_REQUEST = 1;

    ProgressDialog Progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__event);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");
        Storage = FirebaseStorage.getInstance().getReference();

        imageButton = (ImageButton) findViewById(R.id.imageButtonUser);
        etTitle = (EditText)findViewById(R.id.titleH);
        etDesc = (EditText)findViewById(R.id.descH);
        etOrganiser = (EditText)findViewById(R.id.organiserH);
        etHeadChief = (EditText)findViewById(R.id.headChiefH);
        etDate = (EditText)findViewById(R.id.dateH);
        etTime = (EditText)findViewById(R.id.timeH);
        etAddress = (EditText)findViewById(R.id.addressH);
        etPax = (EditText)findViewById(R.id.paxH);

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

    private void startPosting() {
        Progress.setMessage("Uploading");
        Progress.show();

        String title_val = etTitle.getText().toString().trim();
        String desc_val = etDesc.getText().toString().trim();
        String organiser_val = etOrganiser.getText().toString().trim();
        String date_val = etDate.getText().toString().trim();
        String time_val = etTime.getText().toString().trim();
        String headChief_val = etHeadChief.getText().toString().trim();
        String address_val = etAddress.getText().toString().trim();
        String pax_val = etPax.getText().toString().trim();
        final DatabaseReference mPost = mDatabase.push();

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && !TextUtils.isEmpty(organiser_val) && !TextUtils.isEmpty(date_val) && !TextUtils.isEmpty(time_val) && !TextUtils.isEmpty(headChief_val) && !TextUtils.isEmpty(address_val) && !TextUtils.isEmpty(pax_val) && uri != null) {

            mPost.child("address").setValue(address_val).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Upload_Event.this, "address successfully added", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Upload_Event.this, "failed to add address" + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            mPost.child("date").setValue(date_val).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Upload_Event.this, "date successfully added", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Upload_Event.this, "failed to add date" + task.getException(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            mPost.child("time").setValue(time_val);
            mPost.child("description").setValue(desc_val);
            mPost.child("head_chief").setValue(headChief_val);
            mPost.child("organiser").setValue(organiser_val);
            mPost.child("pax").setValue(pax_val);
            mPost.child("title").setValue(title_val);
            mPost.child("status").setValue("active");

            StorageReference filepath = Storage.child("Event_Image").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(Upload_Event.this, downloadUrl.toString(), Toast.LENGTH_LONG).show();
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
}
