package sg.edu.rp.c346.eventful_organiser;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by 15017420 on 28/5/2017.
 */

public class NavHeaderMain extends AppCompatActivity {

    TextView tvDisplayUser;
    TextView tvDisplayEmail;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);
    }
}
