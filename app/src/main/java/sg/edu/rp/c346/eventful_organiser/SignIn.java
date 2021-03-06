package sg.edu.rp.c346.eventful_organiser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Eventful - Sign In");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("ORGANISER");
        mAuth = FirebaseAuth.getInstance();

        editTextPassword = (EditText)findViewById(R.id.etPwLogin);
        editTextEmail = (EditText)findViewById(R.id.etEmailLogin);
        buttonLogin = (Button)findViewById(R.id.btnLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLogin();
            }
        });

    }

    private void startLogin() {

        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            progressDialog.setMessage("Loading");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("password", password);
                        editor.commit();

                        progressDialog.dismiss();
                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Log.e("ERROR", task.getException().toString());
                        progressDialog.dismiss();
                    }

                }
            });
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
