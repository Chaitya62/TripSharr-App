package io.github.chaitya62.tripsharr.ongoingtrips;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import io.github.chaitya62.tripsharr.ClickListener;
import io.github.chaitya62.tripsharr.EditTripActivity;
import io.github.chaitya62.tripsharr.NavigationActivity;
import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.RecyclerTouchListener;
import io.github.chaitya62.tripsharr.adapters.MultiSelectAdapter;
import io.github.chaitya62.tripsharr.adapters.TripAdapter;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.AlertDialogHelper;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by mikasa on 27/8/17.
 */

public class OnGoingTripActivity extends NavigationActivity implements AlertDialogHelper.AlertDialogListener{

    private RecyclerView recyclerView;
    private ArrayList<Trip> tripList = new ArrayList();
    private ArrayList<Trip> multiselect_list = new ArrayList<>();
    private TripAdapter mAdapter;
    private Menu context_menu;
    boolean isMultiSelect = false;
    ActionMode mActionMode;
    MultiSelectAdapter multiSelectAdapter;
    AlertDialogHelper alertDialogHelper;
    int updateSelection=-1;
    String tripUrl="http://tripshare.codeadventure.in/TripShare/index.php/Trip/delete/";
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ongoingtrip, contentFrameLayout);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer1);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        prepareTripData();

        alertDialogHelper =new AlertDialogHelper(this);

        multiSelectAdapter = new MultiSelectAdapter(OnGoingTripActivity.this,tripList,multiselect_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new TripAdapter(getApplicationContext(),tripList));

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (isMultiSelect)
                    multi_select(position);
                else {
                    //Toast.makeText(getApplicationContext(), "Details Page", Toast.LENGTH_SHORT).show();
                    Trip trip = tripList.get(position);
                    Toast.makeText(OnGoingTripActivity.this, trip.getDescription(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(OnGoingTripActivity.this, OngoingMapActivity.class);
                    String temp = ""+trip.getId();
                    SharedPrefs.getEditor().putString("selongtripid",temp);
                    SharedPrefs.getEditor().commit();
                    Log.v("shared",temp + " " + SharedPrefs.getPrefs().getString("selongtripid","1"));
                    startActivity(i);
                    drawerLayout.closeDrawers();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                Log.v("hello","long");
                Toast.makeText(OnGoingTripActivity.this,"long press",Toast.LENGTH_SHORT).show();
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Trip>();
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                }

                multi_select(position);
            }
        }));


    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data

        ((TripAdapter) recyclerView.getAdapter()).clear();
        prepareTripData();
        ((TripAdapter) recyclerView.getAdapter()).addAll(tripList);
        swipeContainer.setRefreshing(false);
    }


    public void prepareTripData(){

        String url = "http://tripshare.codeadventure.in/TripShare/index.php/Trip/tripsOf/"+ Long.toString(SharedPrefs.getPrefs().getLong("user_id", 1));
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
                                    Log.v("list",response.getJSONObject(i).getString("is_complete"));
                                    if(response.getJSONObject(i).getString("is_complete").equals("0")) {
                                        ((TripAdapter) recyclerView.getAdapter()).add(new Trip(response.getJSONObject(i)));
                                    }

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

    }
    //Multiple seletion


    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(tripList.get(position)))
                multiselect_list.remove(tripList.get(position));
            else
                multiselect_list.add(tripList.get(position));

            if (multiselect_list.size() > 0) {
                mActionMode.setTitle("" + multiselect_list.size());
                mActionMode.invalidate();
            }
            if( multiselect_list.size()==1){
                updateSelection=position;
            }

            else
                mActionMode.setTitle("");

            refreshAdapter();

        }
    }

    public void refreshAdapter()
    {
        multiSelectAdapter.selected_tripsList=multiselect_list;
        multiSelectAdapter.tripsList=tripList;
        multiSelectAdapter.notifyDataSetChanged();
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

            if (multiselect_list.size() > 1){
                MenuItem item = menu.findItem(R.id.action_update);
                item.setVisible(false);
                return true;
            }
            else{
                MenuItem item = menu.findItem(R.id.action_update);
                item.setVisible(true);
                return true;
            }
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false,multiselect_list);
                    return true;
                case R.id.action_update:
                    Intent i = new Intent(OnGoingTripActivity.this,EditTripActivity.class);
                    i.putExtra("Tripid",""+tripList.get(updateSelection).getId());
                    startActivity(i);
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Trip>();
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
                for(Trip t : multiselect_list){
                    String tempUrl = tripUrl + t.getId();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject = new JSONObject(t.toString());
                    }
                    catch (Exception e){}
                    deleteTrip(tempUrl,jsonObject);
                    tripList.remove(t);
                    multiSelectAdapter.notifyDataSetChanged();
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



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(OnGoingTripActivity.this,NavigationActivity.class);
        SharedPrefs.getEditor().remove("selongtripid");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }

    public void deleteTrip(String url,JSONObject jsonObject){
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
