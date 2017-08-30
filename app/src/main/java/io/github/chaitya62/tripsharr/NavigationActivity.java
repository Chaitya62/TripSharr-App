package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.primeobjects.User;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

import com.facebook.login.LoginManager;

/**
 * Created by mikasa on 26/8/17.
 */

public class NavigationActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    static Resources res;
    RecyclerView recList;
    ActionBarDrawerToggle actionBarDrawerToggle;
    static int loaded[] = new int[10];
    ActionBar actionBar;
    Toolbar toolbar;

    private void prepareFeeds(int type) {
        VolleySingleton volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        int limit = 10;
        String url = "";
        if(type == 0)
            url = getResources().getString(R.string.host) + "index.php/feed/feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]);
        if(type == 1)
            url = getResources().getString(R.string.host) + "index.php/feed/starred_feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]);
        if(type == 2)
            url = getResources().getString(R.string.host) + "index.php/feed/forked_feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Debug", response.toString());
                        loaded[0] += response.length();
                        ArrayList<Trip> arrayList = new ArrayList<>();
                        for ( int i = 0; i<response.length(); i++ ) {
                            try {
                                arrayList.add(i, new Trip(response.getJSONObject(i)));
                            } catch (Exception e) {
                                try {
                                    Log.i("Error", e.toString() + " " + response.getJSONObject(i));
                                } catch (Exception ef) {
                                    Log.i("Error", ef.toString());
                                }
                            }
                        }
                        recList.setAdapter(new TripAdapter(getApplicationContext(), arrayList));
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }
        );
        volleySingleton.addToRequestQueue(jsonArrayRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        res = getResources();
        recList = (RecyclerView) findViewById(R.id.cardList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent i;
                switch (item.getItemId()) {
                    case R.id.home:
                        i = new Intent(getApplicationContext(), NavigationActivity.class);
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
        prepareFeeds(0);
//        View header = navigationView.getHeaderView(0);
//        Intent i = getIntent();
//        User user = (User)i.getSerializableExtra("user");

//        if(user == null) {
//            Log.i("Error", "No user found");
//            finish();
//        }
//        else{
//            TextView name = (TextView) header.findViewById(R.id.profile_name);
//            TextView email = (TextView)header.findViewById(R.id.profile_email);
//            name.setText(user.getName());
//            if(!user.getEmail().equals("unavailable"))
//                email.setText(user.getEmail());
//            else
//                email.setText("");
//        }
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
        if(id == R.id.logout) {
            LoginManager.getInstance().logOut();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

}
