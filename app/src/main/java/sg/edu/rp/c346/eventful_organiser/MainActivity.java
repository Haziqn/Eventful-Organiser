package sg.edu.rp.c346.eventful_organiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
       implements NavigationView.OnNavigationItemSelectedListener, Home.OnFragmentInteractionListener, pastFragment.OnFragmentInteractionListener, liveFragment.OnFragmentInteractionListener{

    FirebaseAuth mAuth;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            mAuth = FirebaseAuth.getInstance();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Upload_Event.class);
                    startActivity(intent);
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View header = navigationView.getHeaderView(0);
            TextView textViewUsername = (TextView) header.findViewById(R.id.tvDisplayUser);
            TextView textViewUserEmail = (TextView) header.findViewById(R.id.tvDisplayEmail);
            ImageView imageViewUserDP = (ImageView) header.findViewById(R.id.ivUserDp);
            final FirebaseUser user = mAuth.getCurrentUser();
            String email = user.getEmail().toString();
//            String username = user.getDisplayName().toString();

            textViewUserEmail.setText(email);
//            textViewUsername.setText(username);

            //replace the activity_main with Home(fragment) layout
            Home home = new Home();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(
                    R.id.content_main,
                    home,
                    home.getTag()
            ).commit();
        }

        @Override
        public void onBackPressed() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.nav_editprofile) {
                // Handle the camera action
                Intent i = new Intent(MainActivity.this, editProfileActivity.class);
                startActivity(i);
            } else if (id == R.id.nav_logout) {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(MainActivity.this);

                myBuilder.setTitle("Log Out");
                myBuilder.setMessage("Are you sure?");
                myBuilder.setCancelable(false);
                myBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(MainActivity.this, SignIn.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    }
                });
                myBuilder.setNegativeButton("Cancel", null);

                AlertDialog myDialog = myBuilder.create();
                myDialog.show();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

        @Override
        public void onFragmentInteraction(Uri uri) {

        }
}
