package io.github.chaitya62.tripsharr;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;

import io.github.chaitya62.tripsharr.adapters.ProfileFeedAdapter;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        toolbar.setTitle("HELLO WORLD");
        toolbar.setTitleTextColor(Color.RED);
        setSupportActionBar(toolbar);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.topTrips);
        ProfileFeedAdapter profileFeedAdapter = new ProfileFeedAdapter(getApplicationContext(),new ArrayList<Trip>());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(profileFeedAdapter);
        Log.i("Problem hai bhai ", "BOHOT");

        String url = "http://tripshare.codeadventure.in/TripShare/index.php/trip/tripsOf/" + Long.toString(SharedPrefs.getPrefs().getLong("user_id", 1));

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0;i<response.length();i++){

                    try{

                    Trip newTrip = new Trip(response.getJSONObject(i));
                    ((ProfileFeedAdapter)recyclerView.getAdapter()).add(newTrip);
                    }
                    catch(Exception e){
                        Log.i("Error: ", "Trip had json errors");
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("THERE IS AN ERORR", "FATAL ERROR");
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);

//        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
//        getLayoutInflater().inflate(R.layout.activity_profile, contentFrameLayout);
//        String[] testArray = {"Android","IPhone","WindowsMobile","Blackberry",
//                "WebOS","Ubuntu","Windows7","Max OS X"};
//        ArrayAdapter testAdapter = new ArrayAdapter<>(this,
//                R.layout.trips_list_view, testArray);;
//        ListView listView = (ListView) findViewById(R.id.trips);
//        listView.setAdapter(testAdapter);

    }


    public void onClick(View view){
        super.onBackPressed();
        return;
    }
}
