package io.github.chaitya62.tripsharr.ongoingtrips;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import io.github.chaitya62.tripsharr.MapsActivity;
import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

public class AddCheckpointActivity extends AppCompatActivity {

    EditText upnameet,updescet;
    String chkptJson;
    Toolbar myToolbar;
    ImageView done;
    String name,desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_checkpoint);

        myToolbar = (Toolbar)findViewById(R.id.my_toolbar1);

        upnameet = (EditText) findViewById(R.id.upnameet);
        updescet = (EditText) findViewById(R.id.updescet);

        done = (ImageView) findViewById(R.id.done);

        chkptJson = SharedPrefs.getPrefs().getString("Chkptjson","a");

        myToolbar.setTitle("Add Checkpoint");
        setSupportActionBar(myToolbar);

    }

    public void addCheckpoint(View view){
        String url = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/add/";
        name = upnameet.getText().toString().trim();
        desc = updescet.getText().toString().trim();
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject = new JSONObject(chkptJson);
        }
        catch(Exception e){}
        jsonObject.remove("name");
        jsonObject.remove("description");
        try {
            jsonObject.put("name", name);
            jsonObject.put("description", desc);
        }
        catch (Exception e){}

        Log.v("json",""+jsonObject);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("respoco",""+response);
                Toast.makeText(AddCheckpointActivity.this,"Added Checkpoint",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddCheckpointActivity.this,OngoingMapActivity.class));
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError){
                Log.v("errorco",""+volleyError);
            }

        });
        VolleySingleton.getInstance(AddCheckpointActivity.this).addToRequestQueue(jsonObjectRequest);
    }


}
