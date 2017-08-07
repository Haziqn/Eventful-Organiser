package sg.edu.rp.c346.eventful_organiser;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.robertsimoes.shareable.Shareable;
import com.squareup.picasso.Picasso;

public class ViewEventDetails extends AppCompatActivity {

    Button btnUpdate;
    ImageView imageView, ivEmail;
    FirebaseAuth mAuth;
    private GoogleMap map;
    TextView tvTitle, tvOrganiser, tvStartDate, tvStartTime, tvEndDate, tvEndTime, tvAddress, tvDesc, tvHeadChief;
    String itemKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_details);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");
        final DatabaseReference mDatabaseOrganiser = FirebaseDatabase.getInstance().getReference().child("ORGANISER");
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        btnUpdate = (Button)findViewById(R.id.btnUp);
        imageView = (ImageView)findViewById(R.id.ivEvent) ;
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvOrganiser = (TextView)findViewById(R.id.tvOrganiser);
        tvStartDate = (TextView)findViewById(R.id.tvDate);
        tvStartTime = (TextView)findViewById(R.id.tvTime);
        tvEndDate = (TextView)findViewById(R.id.tvEndDate);
        tvEndTime = (TextView)findViewById(R.id.tvEndTime);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        tvDesc = (TextView)findViewById(R.id.tvDescription);
        tvHeadChief = (TextView)findViewById(R.id.tvHeadChief);
        ivEmail = (ImageView)findViewById(R.id.imageEmail);

        Intent i = getIntent();
        itemKey = i.getStringExtra("key");

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ORGANISER");
        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_name = dataSnapshot.child("user_name").getValue().toString();
                tvOrganiser.setText(user_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference mDatabaseRef = mDatabase.child(itemKey);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EVENT event = dataSnapshot.getValue(EVENT.class);
                String image = dataSnapshot.child("image").getValue().toString();
                String title = dataSnapshot.child("title").getValue().toString();
                String startDate = dataSnapshot.child("startDate").getValue().toString();
                String startTime = dataSnapshot.child("startTime").getValue().toString();
                String endDate = dataSnapshot.child("endDate").getValue().toString();
                String endTime = dataSnapshot.child("endTime").getValue().toString();
                final String address = dataSnapshot.child("location").getValue().toString();
                String desc = dataSnapshot.child("description").getValue().toString();
                String headChief = dataSnapshot.child("head_chief").getValue().toString();
                final Double latitude = event.getLat();
                final Double longitude = event.getLng();

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map = googleMap;

                        int permissionCheck = ContextCompat.checkSelfPermission(ViewEventDetails.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION);

                        // Add a marker in Sydney and move the camera
                        LatLng location = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(location).title(address));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                        if (ActivityCompat.checkSelfPermission(ViewEventDetails.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViewEventDetails.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        map.setMyLocationEnabled(true);

                    }
                });

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(title);
                Picasso.with(getBaseContext()).load(image).into(imageView);
                tvTitle.setText(title);
                tvStartDate.setText(startDate);
                tvStartTime.setText(startTime);
                tvEndTime.setText(endTime);
                tvEndDate.setText(endDate);
                tvAddress.setText(address);
                tvDesc.setText(desc);
                tvHeadChief.setText(headChief);

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

        ivEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String email = dataSnapshot.child("email").getValue().toString();
                        AlertDialog.Builder myBuilder = new AlertDialog.Builder(ViewEventDetails.this);

                        myBuilder.setTitle("Email");
                        myBuilder.setMessage(email);
                        myBuilder.setCancelable(true);

                        myBuilder.setPositiveButton("Dismiss", null);

                        AlertDialog myDialog = myBuilder.create();
                        myDialog.show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_share) {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");
            DatabaseReference mDatabaseRef = mDatabase.child(itemKey);
            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    EVENT event = dataSnapshot.getValue(EVENT.class);
                    String title = dataSnapshot.child("title").getValue().toString();

                    Shareable shareAction = new Shareable.Builder(ViewEventDetails.this)
                            .message("Sign up for my event " + title + " on Eventful now!")
                            .url("https://drive.google.com/open?id=0B1mWK9sVsyOoZ2FEWnVON3ZLWEk")
                            .socialChannel(Shareable.Builder.FACEBOOK)
                            .build();
                    shareAction.share();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
