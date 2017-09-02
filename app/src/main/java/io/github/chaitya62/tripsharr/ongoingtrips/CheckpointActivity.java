package io.github.chaitya62.tripsharr.ongoingtrips;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.adapters.CheckpointAdapter;
import io.github.chaitya62.tripsharr.primeobjects.Coordinates;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by mikasa on 1/9/17.
 */

public class CheckpointActivity extends AppCompatActivity {

    int tripId;
    private List<Coordinates> coordinatesList = new ArrayList<>();
    private RecyclerView recyclerView;
    Button finishtrip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_checkpoint);

        finishtrip = (Button) findViewById(R.id.finishtrip);
        tripId = Integer.parseInt(getIntent().getStringExtra("Tripid"));

        recyclerView = (RecyclerView) findViewById(R.id.ongoing_recycler);
        prepareCheckpoints();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new CheckpointAdapter(getApplicationContext(),coordinatesList));

        finishtrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://tripshare.codeadventure.in/TripShare/index.php/trip/update/";
                Map<String, String> params = new HashMap<>();
                params.put("id",""+tripId);
                params.put("is_complete","1");
                JSONObject jsonObject = new JSONObject(params);

                Log.v("hello", "hello");

                //Call to create trip

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("uprespo : ",""+response);
                        Toast.makeText(CheckpointActivity.this,"Congratulations!",Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("upHugga : ", error.toString());
                    }
                });
                VolleySingleton.getInstance(CheckpointActivity.this).addToRequestQueue(jsonRequest);

            }
        });
    }



    public void prepareCheckpoints(){
        String url = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/coordinatesOf/"+(getIntent().getStringExtra("Tripid"));
        Log.v("hello",""+getIntent().getStringExtra("Tripid"));
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("hello", "" + response);
                        Trip trip;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    ((CheckpointAdapter) recyclerView.getAdapter()).add(new Coordinates(response.getJSONObject(i)));

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
        VolleySingleton.getInstance(CheckpointActivity.this).addToRequestQueue(jsonArrayRequest);
    }
}
