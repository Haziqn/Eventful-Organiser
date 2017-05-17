package sg.edu.rp.c346.eventful_organiser;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonRegister;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mProgress = new ProgressDialog(this);

        editTextName = (EditText)findViewById(R.id.etName);
        editTextEmail = (EditText)findViewById(R.id.etEmail);
        editTextPassword = (EditText)findViewById(R.id.etPw);
        editTextConfirmPassword = (EditText)findViewById(R.id.etCPw);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        final String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String password2 = editTextConfirmPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(password2)) {
            Toast.makeText(SignUp.this, "field validation works", Toast.LENGTH_SHORT).show();

            mProgress.setMessage("Signing up ...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name);
                        mProgress.dismiss();

                        Intent intent =  new Intent(SignUp.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(SignUp.this, "error signing up", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
