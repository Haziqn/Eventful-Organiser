package sg.edu.rp.c346.eventful_organiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class ViewEventParticipants extends AppCompatActivity {

    private ListView lv;
    private ArrayList<EventParticipants> arrayList;
    private ArrayAdapter<EventParticipants> arrayAdapter;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseref;

    String itemKey;

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
        String uid = auth.getCurrentUser().getUid();
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

//                EventParticipants eventParticipants = dataSnapshot.getValue(EventParticipants.class);
//                String selectedId = eventParticipants.getUid();
//                if (eventParticipants != null) {
//                    for (int i = 0; i < arrayList.size(); i++) {
//                        if (arrayList.get(i).getId().equals(selectedId)) {
//                            eventParticipants.setUid(dataSnapshot.getKey());
//                            arrayList.set(i, eventParticipants);
//                        }
//                    }
//                    aaJOIN.notifyDataSetChanged();
//
//                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i("MainActivity", "onChildRemoved()");

//                JOIN join = dataSnapshot.getValue(JOIN.class);
//                String selectedId = join.getId();
//                for(int i= 0; i < alJOIN.size(); i++) {
//                    if (alJOIN.get(i).getId().equals(selectedId)) {
//                        alJOIN.remove(i);
//                    }
//                }
//                aaJOIN.notifyDataSetChanged();


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

//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                EventParticipants eventParticipants = arrayList.get(i);  // Get the selected Student
//                String id = eventParticipants.getId();
//                String ref = eventParticipants.getRef();
//
//                Intent intent = new Intent(this, EventParticipants.class);
//                intent.putExtra("key", id);
//                intent.putExtra("ref", ref);
//                startActivity(intent);
//            }
//        });
    }
}
