package io.github.chaitya62.tripsharr;

import android.content.Intent;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;

import io.github.chaitya62.tripsharr.adapters.ProfileFeedAdapter;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.primeobjects.User;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

public class ProfileActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String profile_user_id;
    User profileUser;
    TextView starsView,forksView;

    private void prepareFeeds(){
        String profileFeedUrl = "http://tripshare.codeadventure.in/TripShare/index.php/feed/profile/10" + "/0/" + profile_user_id + "/" + Long.toString(SharedPrefs.getPrefs().getLong("user_id", 1)) ;
        Log.i("Profile FEED Url : ", profileFeedUrl);
        JsonArrayRequest profileFeedRequest = new JsonArrayRequest(profileFeedUrl, new Response.Listener<JSONArray>() {
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


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(profileFeedRequest);


        // DONE

    }


    public void getUser(){
        String userUrl = getResources().getString(R.string.host) + "index.php/user/user/" + profile_user_id;

        JsonArrayRequest userRequest = new JsonArrayRequest(userUrl, new Response.Listener<JSONArray>() {
            @Override

            public void onResponse(JSONArray response) {
                try {
                    profileUser = new User(response.getJSONObject(0));

                } catch (Exception e) {
                    Log.i("Error", "Problem with User JSONObject constructor");
                    profileUser = new User();
                    profileUser.setId(Long.parseLong(profile_user_id));
                }
                starsView.setText(profileUser.getStars() + getString(R.string.star_unit));
                forksView.setText(profileUser.getForks() + getString(R.string.fork_unit));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("VolleyErorr","Volley Failed to fetch user");
                error.printStackTrace();
            }
        });


        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(userRequest);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // intialization
        Intent i = getIntent();
        profile_user_id  = Long.toString(i.getLongExtra("user_id",1));
        starsView = (TextView) findViewById(R.id.profile_stars);
        forksView = (TextView) findViewById(R.id.profile_forks);


        // preparing toolbar to be actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        toolbar.setTitle("HELLO WORLD");
        toolbar.setTitleTextColor(Color.RED);
        setSupportActionBar(toolbar);



        // Preparing recycler
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.topTrips);
        ProfileFeedAdapter profileFeedAdapter = new ProfileFeedAdapter(getApplicationContext(),new ArrayList<Trip>());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(profileFeedAdapter);
        getUser();
        prepareFeeds();


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
