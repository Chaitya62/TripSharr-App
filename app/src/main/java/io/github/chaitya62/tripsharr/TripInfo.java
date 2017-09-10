package io.github.chaitya62.tripsharr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import io.github.chaitya62.tripsharr.adapters.FragmentOneAdapter;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ankit on 9/9/17.
 */

public class TripInfo extends NavigationActivity{

    private String tripid;
    private Trip trip;
    public void viewMap(View view)
    {
        Toast.makeText(TripInfo.this, "We did it!", Toast.LENGTH_SHORT).show();
    }
    protected void onCreate(Bundle savedInstanceState) {
        tripid = getIntent().getStringExtra("Tripid");
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_trip_info,frameLayout);

        String url = "http://tripshare.codeadventure.in/TripShare/index.php/Trip/trips/"+ tripid;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("Responsev", "" + response);
                        Trip trip;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                        trip = new Trip(response.getJSONObject(i));
                                        ActionBar actionbar = getSupportActionBar();
                                        actionbar.setTitle(trip.getName());
                                        TextView description = (TextView) findViewById(R.id.title_description);
                                        description.setText(trip.getDescription());
                                        TextView stars = (TextView) findViewById(R.id.profile_stars);
                                        stars.setText(Long.toString(trip.getNoOfStars()));
                                        TextView forks = (TextView) findViewById(R.id.profile_forks);
                                        forks.setText(Long.toString(trip.getNoOfForks()));
                                        Toast.makeText(TripInfo.this, trip.getName(), Toast.LENGTH_SHORT).show();
                                        Log.i("name",trip.getName());
                                        Log.i("Desc",trip.getDescription());
                                        Toast.makeText(TripInfo.this, trip.getDescription(), Toast.LENGTH_SHORT).show();
                                        Log.v("Response i", "" + response.getJSONObject(i));

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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);

    }

}
