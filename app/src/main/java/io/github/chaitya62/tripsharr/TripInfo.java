package io.github.chaitya62.tripsharr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.util.ArrayList;

import io.github.chaitya62.tripsharr.ongoingtrips.OnGoingTripActivity;
import io.github.chaitya62.tripsharr.ongoingtrips.OngoingMapActivity;
import io.github.chaitya62.tripsharr.primeobjects.Coordinates;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.FontManager;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

import static io.github.chaitya62.tripsharr.R.color.colorAccent;
import static io.github.chaitya62.tripsharr.utils.NetworkUtils.context;

/**
 * Created by ankit on 9/9/17.
 */

public class TripInfo extends NavigationActivity{

    private String tripid;
    private String s[] = new String[5];
    private ImageView imageView;

    public void viewMap(View view) {
        //Toast.makeText(TripInfo.this, "We did it!", Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), OngoingMapActivity.class);
        SharedPrefs.getEditor().putString("selongtripid", tripid);
        SharedPrefs.getEditor().commit();
        Log.v("shared"," " + SharedPrefs.getPrefs().getString("tripid","1"));
        startActivity(i);
    }

    protected void onCreate(Bundle savedInstanceState) {
        tripid = SharedPrefs.getPrefs().getString("selongtripid","1");
        Log.v("Tripid",tripid);
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_trip_info,frameLayout);
        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.trip_info), iconFont);

        String url = getString(R.string.host)+"index.php/Trip/trips/"+ tripid;
        String mediaUrl = getString(R.string.host)+"index.php/Trip/random_media/"+tripid;
        //imageView = (ImageView)findViewById(R.id.media_display);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("Response stuff", "" + response);
                        Trip trip;
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                trip = new Trip(response.getJSONObject(i));
                                ActionBar actionbar = getSupportActionBar();
                                actionbar.setTitle(trip.getName());
                                TextView stars = (TextView) findViewById(R.id.profile_stars);
                                stars.setText(Long.toString(trip.getNoOfStars()));
                                TextView forks = (TextView) findViewById(R.id.profile_forks);
                                forks.setText(Long.toString(trip.getNoOfForks()));
                                Log.i("stars here: ", trip.getNoOfStars()+"");
                                //Toast.makeText(TripInfo.this, trip.getName(), Toast.LENGTH_SHORT).show();
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
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("hellerr",""+error);
            }
        });


        // who the fuck will do this ? @Showndarya
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);



        final ArrayList<TimelineRow> timelineList = new ArrayList<>();
        Handler handler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message message) {
                if(message.what == 0) {
                    Log.i("Debug", "Got Checkpoints in Activity");
                    ArrayList<Coordinates> checkpoints = (ArrayList<Coordinates>)message.obj;
                    int id = 0;
                    for(Coordinates checkpoint : checkpoints) {
                        TimelineRow timelineRow = new TimelineRow(id++);
                        timelineRow.setDate(checkpoint.getTimestamp());
                        timelineRow.setTitle(checkpoint.getName());
                        Log.v("Tname",checkpoint.getName());
                        timelineRow.setTitleColor(Color.BLACK);
                        timelineRow.setDateColor(Color.BLACK);
                        timelineRow.setDescriptionColor(Color.BLACK);
                        timelineRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.timeline));
                        timelineRow.setImageSize(40);
                        timelineRow.setDescription(checkpoint.getDescription());
                        timelineRow.setBellowLineColor(colorAccent);
                        timelineRow.setBellowLineSize(3);
                        timelineList.add(timelineRow);
                    }
                    ArrayAdapter<TimelineRow> listAdapter = new TimelineViewAdapter(getApplicationContext(), 0, timelineList, false);
                    ListView listview = (ListView) findViewById(R.id.timeline);
                    listview.setAdapter(listAdapter);
                }
            }
        };
        OngoingMapActivity.handler = handler;
        OngoingMapActivity.prepareCheckpoints(Long.valueOf(tripid));

    }

}