package io.github.chaitya62.tripsharr;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.chaitya62.tripsharr.adapters.TripAdapter;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by mikasa on 27/8/17.
 */

public class OnGoingTripActivity extends NavigationActivity {

    private RecyclerView recyclerView;
    private List<Trip> tripList = new ArrayList();
    private TripAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ongoingtrip, contentFrameLayout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        prepareTripData();


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new TripAdapter(getApplicationContext(),tripList));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Trip trip = tripList.get(position);
                Toast.makeText(OnGoingTripActivity.this,trip.getDescription(),Toast.LENGTH_SHORT).show();
            }

        }));

    }

    public void prepareTripData(){

        String url = "http://tripshare.codeadventure.in/TripShare/index.php/Trip/trips/1";
        Log.v("hello",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("hello", "" + response);
                        Trip trip;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    ((TripAdapter)recyclerView.getAdapter()).add(new Trip(response.getJSONObject(i)));
                                } catch (Exception e) {
                                    try {
                                        Log.i("Error", e.toString() + " " + response.getJSONObject(i));
                                    } catch (Exception ef) {
                                        Log.i("Error", ef.toString());
                                    }
                                }

                            }
                        }
                        catch (Exception e){}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("hellerr",""+error);
            }
        });
        VolleySingleton.getInstance(OnGoingTripActivity.this).addToRequestQueue(jsonArrayRequest);
        Log.v("sized",""+tripList.size());
        //mAdapter.notifyDataSetChanged();
    }
}
