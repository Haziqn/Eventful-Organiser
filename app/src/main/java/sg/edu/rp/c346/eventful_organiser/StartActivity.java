package sg.edu.rp.c346.eventful_organiser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private Button buttonSignIn, buttonSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        buttonSignIn = (Button) findViewById(R.id.btnSignIn);
        buttonSignUp = (Button) findViewById(R.id.btnSignUp);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, SignIn.class);
                startActivity(intent);
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}
