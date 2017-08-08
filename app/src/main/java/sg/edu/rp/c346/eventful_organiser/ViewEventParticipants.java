package sg.edu.rp.c346.eventful_organiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewEventParticipants extends AppCompatActivity {

    private ListView lv;
    private ArrayList<EventParticipants> arrayList;
    private ArrayAdapter<EventParticipants> arrayAdapter;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseref;

    String itemKey;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event_participants);

        Intent i = getIntent();
        itemKey = i.getStringExtra("key");

        lv = (ListView)findViewById(R.id.lv);

        arrayList = new ArrayList<EventParticipants>();
        arrayAdapter = new ArrayAdapter<EventParticipants>(ViewEventParticipants.this, android.R.layout.simple_list_item_1, arrayList);
        lv.setAdapter(arrayAdapter);

        auth = FirebaseAuth.getInstance();
        final String uid = auth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseref = firebaseDatabase.getReference("EVENT_PARTICIPANTS").child(uid).child(itemKey);

        databaseref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.i("MainActivity", "onChildAdded()");
                EventParticipants eventParticipants = dataSnapshot.getValue(EventParticipants.class);
                if (eventParticipants != null) {
                    eventParticipants.setUid(dataSnapshot.getKey());
                    arrayList.add(eventParticipants);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("MainActivity", "onChildChanged()");

                EventParticipants eventParticipants = dataSnapshot.getValue(EventParticipants.class);
                String selectedId = eventParticipants.getUid();
                if (eventParticipants != null) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (arrayList.get(i).getUid().equals(selectedId)) {
                            eventParticipants.setUid(dataSnapshot.getKey());
                            arrayList.set(i, eventParticipants);
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i("MainActivity", "onChildRemoved()");

                EventParticipants eventParticipants= dataSnapshot.getValue(EventParticipants.class);
                String selectedId = eventParticipants.getUid();
                for(int i= 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getUid().equals(selectedId)) {
                        arrayList.remove(i);
                    }
                }
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i("MainActivity", "onChildMoved()");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Database error occurred", databaseError.toException());

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                EventParticipants eventParticipants = arrayList.get(i);
                String id = eventParticipants.getUid();

                Intent intent = new Intent(ViewEventParticipants.this, TakeAttendance.class);
                intent.putExtra("key", itemKey);
                intent.putExtra("id", id);
                startActivity(intent);

            }
        });

    }
}
