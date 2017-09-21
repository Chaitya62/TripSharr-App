package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

public class EditTripActivity extends AppCompatActivity {

    Toolbar myToolbar;
    EditText name,desc;
    String tripId;
    String tripUrl = "http://tripshare.codeadventure.in/TripShare/index.php/trip/update/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);
        tripId = SharedPrefs.getPrefs().getString("selongtripid","1");
        Log.v("tripid",""+tripId);

        myToolbar = (Toolbar)findViewById(R.id.my_toolbar2);
        name = (EditText) findViewById(R.id.uptnameet);
        desc = (EditText) findViewById(R.id.uptdescet);

        myToolbar.setTitle("Edit Trip");
        setSupportActionBar(myToolbar);

        getTripDetail();
    }

    public void getTripDetail(){

        String url = "http://tripshare.codeadventure.in/TripShare/index.php/Trip/tripsOf/"+ Long.toString(SharedPrefs.getPrefs().getLong("user_id", 1));
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.v("chkpt", "" + response);
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        name.setText(jsonObject.getString("name"));
                        desc.setText(jsonObject.getString("description"));
                    }
                    catch (Exception e){}
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("chkerr",""+error);
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);

    }

    public void Update(View v){
        int in = v.getId();
        switch (in){
            case R.id.ivtname:
                Map<String,String> hm = new HashMap<>();
                hm.put("id",tripId);
                hm.put("name",name.getText().toString().trim());
                JSONObject jsonObject = new JSONObject(hm);
                Toast.makeText(EditTripActivity.this,"Update Successful",Toast.LENGTH_SHORT).show();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, tripUrl, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("chkres", "" + response);
                        try {
                            if (response.getString("request").equals("Success")) {
                                Toast.makeText(EditTripActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e){}
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("chkerr",""+error);
                    }
                });
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                break;
            case R.id.ivtdesc:
                hm = new HashMap<>();
                hm.put("id",tripId);
                hm.put("description",desc.getText().toString().trim());
                jsonObject = new JSONObject(hm);
                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, tripUrl, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(EditTripActivity.this,"Update Successful",Toast.LENGTH_SHORT).show();
                        Log.v("chkres", "" + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v("chkerr",""+error);
                    }
                });
                VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(EditTripActivity.this,NavigationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }
}
