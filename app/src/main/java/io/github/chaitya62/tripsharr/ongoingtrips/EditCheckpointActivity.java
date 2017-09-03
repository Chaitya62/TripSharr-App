package io.github.chaitya62.tripsharr.ongoingtrips;

import android.appwidget.AppWidgetProvider;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.ViewTripActivity;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by mikasa on 3/9/17.
 */

public class EditCheckpointActivity extends AppCompatActivity {

    String tripid,chkptid;
    EditText upnameet,updescet;
    String chkptUrl = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/update/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_checkpoint);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        upnameet = (EditText) findViewById(R.id.upnameet);
        updescet = (EditText) findViewById(R.id.updescet);

        tripid = getIntent().getStringExtra("Tripid");
        chkptid = getIntent().getStringExtra("Chkptid");

        myToolbar.setTitle("Edit Checkpoint");
        setSupportActionBar(myToolbar);

        getCheckpointDetail();
    }

    public void getCheckpointDetail(){
        String url = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/coordinates/"+chkptid;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.v("chkpt", "" + response);
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        upnameet.setText(jsonObject.getString("name"));
                        updescet.setText(jsonObject.getString("description"));
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
            case R.id.ivname:
                Map<String,String> hm = new HashMap<>();
                hm.put("id",chkptid);
                hm.put("name",upnameet.getText().toString().trim());
                JSONObject jsonObject = new JSONObject(hm);
                Toast.makeText(EditCheckpointActivity.this,"Update Successful",Toast.LENGTH_SHORT).show();

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, chkptUrl, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("chkres", "" + response);
                        try {
                            if (response.getString("request").equals("Success")) {
                                Toast.makeText(EditCheckpointActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
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
            case R.id.ivdesc:
                hm = new HashMap<>();
                hm.put("id",chkptid);
                hm.put("description",updescet.getText().toString().trim());
                jsonObject = new JSONObject(hm);
                jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, chkptUrl, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(EditCheckpointActivity.this,"Update Successful",Toast.LENGTH_SHORT).show();
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
            case R.id.ivmedia:
                Toast.makeText(EditCheckpointActivity.this,"media",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
