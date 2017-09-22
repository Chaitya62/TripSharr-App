package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;

import io.github.chaitya62.tripsharr.adapters.FeedAdapter;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.NetworkUtils;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

public class FeedsActivity extends NavigationActivity {



    static Resources res;
    RecyclerView recList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int type = 0;
    int loaded[] = new int[10];
    int feed_loading_flag = 0;


    private void prepareFeeds() {
        feed_loading_flag = 1;
        int limit = 10;
        Log.i("URL", "prepareFeeds Called");
        String url = "";
        if(!NetworkUtils.isNetworkAvailable()) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinator_feeds), "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        if(type == 0) {
            url = getResources().getString(R.string.host) + "index.php/feed/feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]) + "/" + Long.toString(SharedPrefs.getPrefs().getLong("user_id", 1));
        }
        else if(type == 1) {
            url = getResources().getString(R.string.host) + "index.php/feed/starred_feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]) + "/" + Long.toString(SharedPrefs.getPrefs().getLong("user_id", 1));
        }
        else if(type == 2) {
            url = getResources().getString(R.string.host) + "index.php/feed/forks_feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]) + "/" + Long.toString(SharedPrefs.getPrefs().getLong("user_id", 1));
        }
        Log.i("URL", url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("URL", response.toString());
                        loaded[type] += response.length();
                        feed_loading_flag = 0;
                        mSwipeRefreshLayout.setRefreshing(false);
                        for ( int i = 0; i<response.length(); i++ ) {
                            try {
                                ((FeedAdapter)recList.getAdapter()).add(new Trip(response.getJSONObject(i)));
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
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }





    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
            getLayoutInflater().inflate(R.layout.activity_feeds, contentFrameLayout);

        res = getResources();
        recList = (RecyclerView) findViewById(R.id.cardList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.loader_feed);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("Debug", "On Refresh");
                loaded[type] = 0;
                ((FeedAdapter)recList.getAdapter()).clear();
                prepareFeeds();
            }
        });
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if(llm.findLastCompletelyVisibleItemPosition()==loaded[type]-1) {
                            Log.i("Debug", "At last element");
                            if(feed_loading_flag == 0)
                                prepareFeeds();
                        }
                    }
                }
        );
        recList.setLayoutManager(llm);
        recList.setAdapter(new FeedAdapter(getApplicationContext(), new ArrayList<Trip>()));
        Spinner spinner = (Spinner) findViewById(R.id.feeds_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.feeds_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("URL", "SPINNER CALLED");
                ((FeedAdapter)recList.getAdapter()).clear();
                loaded[type] = 0;
                if(position == 0) {
                    type = 0;
                }
                else if(position == 1) {
                    type = 1;
                }
                else if(position == 2) {
                    type = 2;
                }
                prepareFeeds();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Debug", "Nothing Selected");
            }
        });

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(Color.BLACK);
//
//        setSupportActionBar(toolbar);



    }


    public void goToProfileActivity(View view){
        Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
        profile.putExtra("user_id",SharedPrefs.getPrefs().getLong("user_id", 1));
        startActivity(profile);
    }




}

