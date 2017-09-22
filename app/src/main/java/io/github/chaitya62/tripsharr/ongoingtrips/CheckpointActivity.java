package io.github.chaitya62.tripsharr.ongoingtrips;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Map;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.RecyclerTouchListener;
import io.github.chaitya62.tripsharr.ViewTripActivity;
import io.github.chaitya62.tripsharr.adapters.MultiSelectCoordinateAdapter;
import io.github.chaitya62.tripsharr.primeobjects.Coordinates;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.AlertDialogHelper;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by mikasa on 1/9/17.
 */

public class CheckpointActivity extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener {

    String tripId;
    private ArrayList<Coordinates> coordinatesList = new ArrayList<>();
    private RecyclerView recyclerView;
    Button finishtrip;
    private Menu context_menu;
    private ArrayList<Coordinates> multiselect_list = new ArrayList<>();
    private boolean isMultiSelect = false;
    private ActionMode mActionMode;
    private MultiSelectCoordinateAdapter multiSelectCoordinateAdapter;
    private AlertDialogHelper alertDialogHelper;
    private int updateSelection=-1;
    String coordinateUrl="http://tripshare.codeadventure.in/TripShare/index.php/Coordinates/delete/";
    private boolean tripstatus;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_checkpoint);

        finishtrip = (Button) findViewById(R.id.finishtrip);
        tripId = SharedPrefs.getPrefs().getString("selongtripid","1");
        tripstatus = SharedPrefs.getPrefs().getBoolean("tripstatus",false);
        Log.v("shareded",tripId);

        recyclerView = (RecyclerView) findViewById(R.id.ongoing_recycler);
        prepareCheckpoints();

        alertDialogHelper =new AlertDialogHelper(this);
        multiSelectCoordinateAdapter = new MultiSelectCoordinateAdapter(coordinatesList,multiselect_list,CheckpointActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(multiSelectCoordinateAdapter);

        if(tripstatus)
            finishtrip.setVisibility(View.GONE);

        if(!tripstatus) {

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (isMultiSelect)
                        multi_select(position);
                    else {
                        Coordinates coordinates = coordinatesList.get(position);
                        Toast.makeText(CheckpointActivity.this, coordinates.getDescription(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(CheckpointActivity.this, EditCheckpointActivity.class);
                        SharedPrefs.getEditor().putString("selongchkptid", "" + coordinates.getId());
                        SharedPrefs.getEditor().commit();
                        startActivity(i);
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    Log.v("hello", "long");
                    Toast.makeText(CheckpointActivity.this, "long press", Toast.LENGTH_SHORT).show();
                    if (!isMultiSelect) {
                        multiselect_list = new ArrayList<Coordinates>();
                        isMultiSelect = true;

                        if (mActionMode == null) {
                            mActionMode = startActionMode(mActionModeCallback);
                        }
                    }

                    multi_select(position);
                }
            }));

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
                            try {
                                if (response.getString("request").equals("Success")) {
                                    Toast.makeText(CheckpointActivity.this, "Congratulations!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(CheckpointActivity.this, ViewTripActivity.class);
                                    startActivity(i);
                                }
                            }
                            catch (Exception e){}
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


    }



    public void prepareCheckpoints(){
        String url = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/coordinatesOf/"+tripId;
        //Log.v("hello",""+getIntent().getStringExtra("Tripid"));
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("hello", "" + response);
                        Trip trip;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    ((MultiSelectCoordinateAdapter) recyclerView.getAdapter()).add(new Coordinates(response.getJSONObject(i)));

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(CheckpointActivity.this,OngoingMapActivity.class);
        SharedPrefs.getEditor().remove("selongchkptid");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(coordinatesList.get(position)))
                multiselect_list.remove(coordinatesList.get(position));
            else
                multiselect_list.add(coordinatesList.get(position));

            if (multiselect_list.size() > 0) {
                mActionMode.setTitle("" + multiselect_list.size());
                mActionMode.invalidate();
            }
            if( multiselect_list.size()==1){
                updateSelection=position;
            }
            if( multiselect_list.size()==0){
                mActionMode.setTitle("");
            }
            refreshAdapter();

        }
    }
    public void refreshAdapter()
    {
        multiSelectCoordinateAdapter.selected_coordinateslist=multiselect_list;
        multiSelectCoordinateAdapter.coordinatesArrayList=coordinatesList;
        multiSelectCoordinateAdapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {



        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.trip_selection, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            MenuItem item = menu.findItem(R.id.action_update);
            item.setVisible(false);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete Checkpoint","DELETE","CANCEL",1,false);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Coordinates>();
            refreshAdapter();
        }
    };

    // AlertDialog Callback Functions

    @Override
    public void onPositiveClick(int from) {
        if(from==1)
        {
            Log.v("hello","hello");
            if(!multiselect_list.isEmpty()){
                for(Coordinates c : multiselect_list){
                    String tempUrl = coordinateUrl + c.getId();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject = new JSONObject(c.toString());
                    }
                    catch (Exception e){}
                    deleteCheckpoint(tempUrl,jsonObject);
                    coordinatesList.remove(c);
                    multiSelectCoordinateAdapter.notifyDataSetChanged();
                }
            }

            if (mActionMode != null) {
                mActionMode.finish();
            }
            Toast.makeText(getApplicationContext(), "Delete Click", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }



    public void deleteCheckpoint(String url,JSONObject jsonObject){
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.DELETE,url,jsonObject,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("respo : ",""+response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Hugga : ", error.toString());
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);
    }



}
