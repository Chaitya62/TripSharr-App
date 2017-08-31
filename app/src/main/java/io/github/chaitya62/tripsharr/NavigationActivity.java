package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;

import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
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
    int type = 0;
    int loaded[] = new int[10];
    ActionBar actionBar;
    Toolbar toolbar;

    private void prepareFeeds() {
        VolleySingleton volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        int limit = 10;
        String url = "";
        if(type == 0)
            url = getResources().getString(R.string.host) + "index.php/feed/feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]);
        if(type == 1)
            url = getResources().getString(R.string.host) + "index.php/feed/starred_feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]);
        if(type == 2)
            url = getResources().getString(R.string.host) + "index.php/feed/forked_feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]);
        Log.i("Debug", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Debug", response.toString());
                        loaded[0] += response.length();
                        for ( int i = 0; i<response.length(); i++ ) {
                            try {
                                ((TripAdapter)recList.getAdapter()).add(new Trip(response.getJSONObject(i)));
                            } catch (Exception e) {
                                try {
                                    Log.i("Error", e.toString() + " " + response.getJSONObject(i));
                                } catch (Exception ef) {
                                    Log.i("Error", ef.toString());
                                }
                            }
                        }
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
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if(llm.findLastCompletelyVisibleItemPosition()==loaded[type]-1) {
                            Log.i("Debug", "At last element");
                            prepareFeeds();
                        }
                    }
                }
        );
        recList.setLayoutManager(llm);
        recList.setAdapter(new TripAdapter(getApplicationContext(), new ArrayList<Trip>()));
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
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
                        i = new Intent(NavigationActivity.this,BottomSheet.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;


                }
                return false;
            }
        });
        prepareFeeds();
        View header = navigationView.getHeaderView(0);
            TextView name = (TextView) header.findViewById(R.id.profile_name);
            TextView email = (TextView)header.findViewById(R.id.profile_email);
            name.setText(SharedPrefs.getPrefs().getString("user_name", null));
            if(!SharedPrefs.getPrefs().getString("email", null).equals("unavailable"))
                email.setText(SharedPrefs.getPrefs().getString("email", null));
            else
                email.setText("");
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
            SharedPrefs.getEditor().clear();
            SharedPrefs.getEditor().commit();
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
