package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.github.chaitya62.tripsharr.primeobjects.User;

/**
 * Created by mikasa on 26/8/17.
 */

public class NavigationActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ActionBar actionBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.profilepic:
                        Intent i = new Intent(getApplicationContext(), ContactsContract.Profile.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.create_trip:
                        i = new Intent(getApplicationContext(), CreateTripActivity.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.view_trip:
                        i = new Intent(NavigationActivity.this,ViewTripActivity.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.ongoing_trip:
                        i = new Intent(NavigationActivity.this,OnGoingTripActivity.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.share_trip:
                        i = new Intent(NavigationActivity.this,ShareTripActivity.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;


                }
                return false;
            }
        });
        View header = navigationView.getHeaderView(0);
        Intent i = getIntent();
        User user = (User)i.getSerializableExtra("user");
        if(user == null) {
            Log.i("Error", "No user found");
            finish();
        }
        TextView name = (TextView) header.findViewById(R.id.profile_name);
        TextView email = (TextView)header.findViewById(R.id.profile_email);
        name.setText(user.getName());
        if(!user.getEmail().equals("unavailable"))
            email.setText(user.getEmail());
        else
            email.setText("");

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }

    public void goToProfileActivity(View view){
        Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(profile);
    }

}
