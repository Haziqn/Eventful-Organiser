package sg.edu.rp.c346.eventful_organiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TakeAttendance extends AppCompatActivity {
    String id, itemKey;
    EditText editTextKey;
    Button button;
    String registration_key;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        Intent i = getIntent();
        itemKey = i.getStringExtra("key");
        id = i.getStringExtra("id");

        editTextKey = (EditText)findViewById(R.id.editText);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        registration_key = editTextKey.getText().toString().trim();

        button = (Button)findViewById(R.id.button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DatabaseReference databaseReference1 = databaseReference.child("EVENT_PARTICIPANTS").child(uid).child(itemKey).child(id);
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(registration_key)) {
                            AlertDialog.Builder myBuilder = new AlertDialog.Builder(TakeAttendance.this);

                            myBuilder.setTitle("Valid Key found");
                            myBuilder.setMessage("Mark participant as: ");
                            myBuilder.setCancelable(false);
                            myBuilder.setPositiveButton("Present", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Map map = new HashMap();
                                    map.put("EVENT_PARTICIPANTS/" + uid + "/" + itemKey + "/" + id + "/" + registration_key + "/status", "present");
                                    databaseReference.updateChildren(map);
                                }
                            });

                            myBuilder.setNeutralButton("Absent", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    databaseReference1.child(registration_key).child("status").setValue("Absent");
                                }
                            });

                            myBuilder.setNegativeButton("Left", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    databaseReference1.child(registration_key).child("status").setValue("Left");
                                }
                            });

                            AlertDialog myDialog = myBuilder.create();
                            myDialog.show();
                        } else {
                            Toast.makeText(TakeAttendance.this, "Invalid Registration Key.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
