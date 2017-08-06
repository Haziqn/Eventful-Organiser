package sg.edu.rp.c346.eventful_organiser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static sg.edu.rp.c346.eventful_organiser.R.layout.fragment_live;

public class liveFragment extends Fragment {

    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private Query mQuery;
    private FirebaseAuth firebaseAuth;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    String itemKey;
    String organiser_name;

    public liveFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<EVENT, BlogViewHolder>(

                EVENT.class,
                R.layout.row,
                BlogViewHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, EVENT model, final int position) {

                String uid = model.getOrganiser();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ORGANISER");
                databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        organiser_name = dataSnapshot.child("user_name").getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.setTitle(model.getTitle());
                viewHolder.setImage(getActivity().getApplicationContext(), model.getImage());
                viewHolder.setLocation(model.getLocation());
                viewHolder.setOrganiser(organiser_name);

                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        AlertDialog.Builder myBuilder = new AlertDialog.Builder(getContext());

                        myBuilder.setTitle("Delete Event");
                        myBuilder.setMessage("Do you want to delete this event?");
                        myBuilder.setCancelable(false);
                        myBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseRecyclerAdapter.getRef(position).removeValue();
                            }
                        });
                        myBuilder.setNegativeButton("Cancel", null);

                        AlertDialog myDialog = myBuilder.create();
                        myDialog.show();
                        return true;
                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getContext(), ViewEventDetails.class);
                        itemKey = String.valueOf(firebaseRecyclerAdapter.getRef(position).getKey());
                        i.putExtra("key", itemKey);

                        startActivity(i);

                    }
                });
            }

        };

        mBlogList.setAdapter(firebaseRecyclerAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mBlogList.setLayoutManager(mLayoutManager);

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

            public void setTitle(String title) {
                TextView postTitle = (TextView)mView.findViewById(R.id.eventTitle);
                postTitle.setText(title);
            }

            public void setImage(Context ctx, String image) {
                ImageView post_Image = (ImageView)mView.findViewById(R.id.ivEvent);
                Picasso.with(ctx).load(image).into(post_Image);
            }

            public void setLocation(String location) {
                TextView locations = (TextView)mView.findViewById(R.id.eventAddress);
                locations.setText(location);
            }

            public void setOrganiser(String organiser) {
                TextView organisers = (TextView)mView.findViewById(R.id.eventOrganiser);
                organisers.setText(organiser);
            }

        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(fragment_live,
                container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("EVENT");
        mQuery = mDatabase.orderByChild("organiser").equalTo(uid);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mBlogList =(RecyclerView)view.findViewById(R.id.live_list);
        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
