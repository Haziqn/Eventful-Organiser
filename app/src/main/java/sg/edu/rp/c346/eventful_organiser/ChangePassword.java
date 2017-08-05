package sg.edu.rp.c346.eventful_organiser;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private static final String TAG = "ChangePassword";

    EditText etEmail;
    EditText etPassword;
    Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);
        btnChange = (Button)findViewById(R.id.btnChange);

        etEmail.setClickable(false);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChangePassword.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
//
//    public void changePassword() {
//        final EditText etEmail = (EditText)findViewById(R.id.etEmail);
//        final EditText etPassword = (EditText)findViewById(R.id.etPassword);
//
//        final String email = etEmail.getText().toString();
//        final String password = etPassword.getText().toString();
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
//
//        user.reauthenticate(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Log.d(TAG, "Password updated");
//                                    } else {
//                                        Log.d(TAG, "Error password not updated")
//                                    }
//                                }
//                            });
//                        } else {
//                            Log.d(TAG, "Error auth failed")
//                        }
//                    }
//                });
//    }
}
