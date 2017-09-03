package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import io.github.chaitya62.tripsharr.NavigationActivity;
import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

import static io.github.chaitya62.tripsharr.R.string.error;

/**
 * Created by mikasa on 26/8/17.
 */

public class CreateTripActivity extends NavigationActivity {
    Button start;
    EditText tripname;
    String tripName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_createtrip, contentFrameLayout);

        start = (Button) findViewById(R.id.start);
        tripname = (EditText) findViewById(R.id.tripname);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tripName = tripname.getText().toString().trim();
                final Trip newTrip = new Trip();
                newTrip.setName(tripName);
                newTrip.setDescription("Added from phone");
                newTrip.setUserId(SharedPrefs.getPrefs().getLong("user_id",1));
                String url = "http://tripshare.codeadventure.in/TripShare/index.php/trip/add/";
                Map<String, String> params = new HashMap<>();
                try{
                    params = newTrip.getParams();
                }catch (Exception e){
                    e.printStackTrace();
                }
                JSONObject jsonObject = new JSONObject(params);

                Log.v("hello", "hello");

                //Call to create trip

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,jsonObject,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("respo : ",""+response);
                        Intent i = new Intent(CreateTripActivity.this,MapsActivity.class);

                        String tripid= (response.toString());
                        tripid= tripid.substring(6,tripid.length()-1);
                        Log.v("crtripid",tripid+" "+tripid.length());
                        i.putExtra("Tripid",tripid);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("Hugga : ", error.toString());
                    }
                });
                VolleySingleton.getInstance(CreateTripActivity.this).addToRequestQueue(jsonRequest);

            }
        });
    }
}