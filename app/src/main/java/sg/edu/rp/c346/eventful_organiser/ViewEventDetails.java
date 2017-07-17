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

    TextView tvAddress, tvDesc, tvDate, tvTime, tvOrganiser, tvHeadChief;
    ImageView imageView;
    Button btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_details);

        tvDesc = (TextView)findViewById(R.id.tvDescription);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvTime = (TextView)findViewById(R.id.tvTime);
        tvOrganiser = (TextView)findViewById(R.id.tvOrganiser);
        tvHeadChief = (TextView)findViewById(R.id.tvHeadChief);
        imageView = (ImageView)findViewById(R.id.imageView2);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");

        Intent i = getIntent();
        final String itemKey = i.getStringExtra("key");

        DatabaseReference mDatabaseRef = mDatabase.child(itemKey);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EVENT event = dataSnapshot.getValue(EVENT.class);
                String title = event.getTitle().toString().trim();
                String description = event.getDescription().toString().trim();
                String image = event.getImage().toString().trim();
                String address = event.getAddress().toString().trim();
                String head_chief = event.getHead_chief().toString().trim();
                String pax = event.getPax().toString().trim();
                String organiser = event.getOrganiser().toString().trim();
                String date = event.getDate().toString().trim();
                String time = event.getTime().toString().trim();
                String timestamp = event.getTimeStamp().toString().trim();
                String organiser_name = event.getOrganiser_name().toString().trim();

                tvDate.setText("Date: " + date);
                tvTime.setText("Time: " + time);
                tvDesc.setText(description);
                tvOrganiser.setText("Organiser: " + organiser_name);
                tvHeadChief.setText("Event-in-charge: " + head_chief);
                tvAddress.setText("Location: " + "\n" + address);
                Picasso.with(getBaseContext()).load(image).into(imageView);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                getSupportActionBar().setTitle(title);



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