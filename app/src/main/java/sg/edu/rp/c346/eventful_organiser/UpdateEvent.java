package sg.edu.rp.c346.eventful_organiser;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateEvent extends AppCompatActivity {

    EditText etTitle;
    EditText etDesc;
    EditText etOrganiser;
    EditText etHeadChief;
    EditText etDate;
    EditText etTime;
    EditText etAddress;
    EditText etPax;
    Button btnUpdate;
    ImageButton imageButton;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    StorageReference Storage;
    private Uri uri = null;
    public String downloadUrl = "";
    final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");
        Storage = FirebaseStorage.getInstance().getReference();

        imageButton = (ImageButton) findViewById(R.id.imageButtonUser);
        etTitle = (EditText)findViewById(R.id.etTitle);
        etDesc = (EditText)findViewById(R.id.descH);
        etOrganiser = (EditText)findViewById(R.id.organiserH);
        etHeadChief = (EditText)findViewById(R.id.headChiefH);
        etDate = (EditText)findViewById(R.id.dateH);
        etTime = (EditText)findViewById(R.id.timeH);
        etAddress = (EditText)findViewById(R.id.addressH);
        etPax = (EditText)findViewById(R.id.etPaxH);
        btnUpdate = (Button)findViewById(R.id.updateButton);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");

        Intent i = getIntent();
        final String itemKey = i.getStringExtra("updateKey");

        DatabaseReference mDatabaseRef = mDatabase.child(itemKey);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                EVENT event = dataSnapshot.getValue(EVENT.class);
//                String title = event.getTitle().toString().trim();
//                String description = event.getDescription().toString().trim();
//                String image = event.getImage().toString().trim();
//                String address = event.getAddress().toString().trim();
//                String head_chief = event.getHead_chief().toString().trim();
//                String pax = event.getPax().toString().trim();
//                String organiser = event.getOrganiser_name().toString().trim();
//                String date = event.getDate().toString().trim();
//                String time = event.getTime().toString().trim();
//                String timestamp = event.getTimeStamp().toString().trim();
//
//                etDate.setText(date);
//                etTime.setText(time);
//                etDesc.setText(description);
//                etOrganiser.setText(organiser);
//                etHeadChief.setText(head_chief);
//                etAddress.setText(address);
//                etTitle.setText(title);
//                etPax.setText(pax);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                getSupportActionBar().setTitle("Update Event");

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, GALLERY_REQUEST);
                    }
                });

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateUserInfo();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateUserInfo() {
        Toast.makeText(UpdateEvent.this, "in update", Toast.LENGTH_SHORT).show();
        final String title = etTitle.getText().toString();
        final String desc = etDesc.getText().toString();
        String address = etAddress.getText().toString();
        String headchief = etHeadChief.getText().toString();
        String pax = etPax.getText().toString();
        String organiser = etOrganiser.getText().toString();
        String date = etDate.getText().toString();
        String time = etTime.getText().toString();
        Intent i = getIntent();
        final String itemKey = i.getStringExtra("updateKey");

        if (uri != null) {
                        mDatabase.child(itemKey).child("address").setValue(address);
                        mDatabase.child(itemKey).child("date").setValue(date);
                        mDatabase.child(itemKey).child("description").setValue(desc);
                        mDatabase.child(itemKey).child("head_chief").setValue(headchief);
                        mDatabase.child(itemKey).child("organiser_name").setValue(organiser);
                        mDatabase.child(itemKey).child("pax").setValue(pax);
                        mDatabase.child(itemKey).child("time").setValue(time);
                        mDatabase.child(itemKey).child("title").setValue(title);

                        StorageReference filepath = Storage.child("image").child(uri.getLastPathSegment());

                        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                mDatabase.child(itemKey).child("image").setValue(downloadUrl);
                                finish();

                            }
                        });
                        Intent intent = new Intent(UpdateEvent.this, ViewEventDetails.class);
                        startActivity(intent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
