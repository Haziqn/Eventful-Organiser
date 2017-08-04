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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.BooleanResult;
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
import com.squareup.picasso.Picasso;

import static sg.edu.rp.c346.eventful_organiser.SignIn.MY_PREFS_NAME;

public class UserAccount extends AppCompatActivity {

    EditText editName, editEmail, editPassword;
    Button buttonUpdate, buttonDelete, buttonResetPassword;
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

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("PARTICIPANT");
        Storage = FirebaseStorage.getInstance().getReference();
        mOrganiser = FirebaseDatabase.getInstance().getReference().child("ORGANISER");

        mProgress = new ProgressDialog(this);

        final EditText etName = (EditText)findViewById(R.id.etName);
        final EditText etNum = (EditText)findViewById(R.id.etNum);
        final EditText etSite = (EditText)findViewById(R.id.etSite);
        final EditText etDesc = (EditText)findViewById(R.id.etDesc);
        final EditText etAcra = (EditText)findViewById(R.id.etAcra);
        final EditText etAddress = (EditText)findViewById(R.id.etAddress);
        final EditText etEmail = (EditText)findViewById(R.id.etEmail);

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
                    Picasso.with(getBaseContext()).load(image).into(imageButton);

                    ORGANISER organiser = new ORGANISER();

                    organiser.setUser_name(user_name);
                    organiser.setContact_num(Integer.parseInt(contact_num));
                    organiser.setSite(site);
                    organiser.setDescription(description);
                    organiser.setAcra(acra);
                    organiser.setAddress(address);
                    organiser.setEmail(email);
                    Picasso.with(UserAccount.this).load(image).into(imageButton);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//
//        buttonUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(UserAccount.this, "Clicked", Toast.LENGTH_SHORT).show();
//                updateUserInfo();
//            }
//        });
//
//        buttonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder myBuilder = new AlertDialog.Builder(UserAccount.this);
//
//                myBuilder.setTitle("Delete Account");
//                myBuilder.setMessage("Are you sure?");
//                myBuilder.setCancelable(false);
//                myBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        userDelete();
//                        Intent i = new Intent(UserAccount.this, SignUp.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(i);
//
//                    }
//                });
//                myBuilder.setNegativeButton("Cancel", null);
//
//                AlertDialog myDialog = myBuilder.create();
//                myDialog.show();
//
//            }
//        });
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
//        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder myBuilder = new AlertDialog.Builder(UserAccount.this);
//
//                myBuilder.setTitle("Reset Password");
//                myBuilder.setMessage("A reset password email will be sent to you");
//                myBuilder.setCancelable(false);
//                myBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        mAuth.sendPasswordResetEmail(user.getEmail());
//                    }
//                });
//                myBuilder.setNegativeButton("Cancel", null);
//
//                AlertDialog myDialog = myBuilder.create();
//                myDialog.show();
//
//            }
//        });
    }

    public void updateUserInfo() {
        Toast.makeText(UserAccount.this, "in update", Toast.LENGTH_SHORT).show();
        final String email = editEmail.getText().toString();
        final String username = editName.getText().toString();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String uid = user.getUid();

        if (uri != null) {
            user.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("UpdateAccount", "User email address updated.");
                        mDatabase.child(uid).child("email").setValue(email);
                        mDatabase.child(uid).child("user_name").setValue(username);

                        StorageReference filepath = Storage.child("User_Image").child(uri.getLastPathSegment());

                        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                                mDatabase.child(uid).child("image").setValue(downloadUrl);
                                finish();

                            }
                        });
                        Intent intent = new Intent(UserAccount.this, SignIn.class);
                        startActivity(intent);
                        Toast.makeText(UserAccount.this, "Please sign in again", Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("Update", task.getException().toString());
                    }
                }
            });
        }
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
}
