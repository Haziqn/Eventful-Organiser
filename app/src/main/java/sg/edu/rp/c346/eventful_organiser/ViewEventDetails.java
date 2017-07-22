package sg.edu.rp.c346.eventful_organiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewEventDetails extends AppCompatActivity {

    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_details);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");

        Intent i = getIntent();
        final String itemKey = i.getStringExtra("key");

        DatabaseReference mDatabaseRef = mDatabase.child(itemKey);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EVENT event = dataSnapshot.getValue(EVENT.class);


                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//                getSupportActionBar().setTitle(title);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEventDetails.this, UpdateEvent.class);
                intent.putExtra("updateKey", itemKey);
                startActivity(intent);
            }
        });
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
