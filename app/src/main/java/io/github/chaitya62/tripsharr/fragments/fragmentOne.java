package io.github.chaitya62.tripsharr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.RecyclerTouchListener;
import io.github.chaitya62.tripsharr.TripInfo;
import io.github.chaitya62.tripsharr.adapters.FeedAdapter;
import io.github.chaitya62.tripsharr.adapters.FragmentOneAdapter;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.NetworkUtils;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by mushu on 8/29/17.
 */

public class fragmentOne extends Fragment{

    private List<Trip> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public fragmentOne() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.textView);
        if(!NetworkUtils.isNetworkAvailable()) {
            Snackbar snackbar = Snackbar
                    .make(v.findViewById(R.id.coordinator_fragment1), "No Internet Connection", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        prepdata();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new FragmentOneAdapter(getApplicationContext(),list));
        //return inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Trip trip = list.get(position);
                Toast.makeText(getApplicationContext(),trip.getDescription(),Toast.LENGTH_SHORT).show();
                SharedPrefs.getEditor().putString("selongtripid",""+trip.getId());
                SharedPrefs.getEditor().commit();
                Log.v("statusv",""+trip.isComplete());
                SharedPrefs.getEditor().putBoolean("tripstatus",true);
                SharedPrefs.getEditor().commit();
                Intent i = new Intent(getApplicationContext(),TripInfo.class);
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        return v;


    }
    public void prepdata(){

        String url = "http://tripshare.codeadventure.in/TripShare/index.php/Trip/tripsOf/"+ Long.toString(SharedPrefs.getPrefs().getLong("user_id", 1));
        Log.v("url",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("Responsev", "" + response);
                        Trip trip;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    if(response.getJSONObject(i).getString("is_complete").equals("1")) {
                                        ((FragmentOneAdapter) recyclerView.getAdapter()).add(new Trip(response.getJSONObject(i)));
                                        Log.v("Response i", "" + response.getJSONObject(i));
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);

    }
}