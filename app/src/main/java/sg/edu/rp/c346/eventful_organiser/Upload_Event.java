package sg.edu.rp.c346.eventful_organiser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Upload_Event extends AppCompatActivity {

    private EditText etTitle;
    private EditText etDesc;
    private Button btnSubmit;

    private DatabaseReference mDatabase;

    private ProgressDialog Progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__event);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");

        etTitle = (EditText)findViewById(R.id.titleH);
        etDesc = (EditText)findViewById(R.id.descH);
        btnSubmit = (Button)findViewById(R.id.submitbutton);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }

        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Add Tips");

        Progress = new ProgressDialog(this);
    }

    private void startPosting() {
        Progress.setMessage("Uploading");
        Progress.show();

        final String title_val = etTitle.getText().toString().trim();
        final String desc_val = etDesc.getText().toString().trim();
        DatabaseReference mPost = mDatabase.push();

        mPost.child("title").setValue(title_val);
        mPost.child("desc").setValue(desc_val);
        Progress.dismiss(); //loading bar
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                startActivity( new Intent(getActivity(), Upload_Fitness.class));
                break;
        }
        return true;
    }
}
