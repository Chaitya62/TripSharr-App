package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.primeobjects.User;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by mikasa on 26/8/17.
 */

public class NavigationActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TripAdapter tripAdapter;
    ActionBarDrawerToggle actionBarDrawerToggle;
    static int loaded[] = new int[10];
    ActionBar actionBar;
    Toolbar toolbar;

    public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
        private List<Trip> tripList;

        public TripAdapter(List<Trip> contactList) {
            this.tripList = contactList;
        }

        @Override
        public int getItemCount() {
            return tripList.size();
        }

        @Override
        public void onBindViewHolder(TripViewHolder tripViewHolder, int i) {
            Trip trip = tripList.get(i);
            tripViewHolder.cName.setText(trip.getName());
            tripViewHolder.cTitle.setText(trip.getName());
            tripViewHolder.cDesc.setText(trip.getDescription());
            RoundedBitmapDrawable mDrawable = createRoundedBitmapDrawableWithBorder(BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_button_icon));
            tripViewHolder.cImage.setImageDrawable(mDrawable);
        }

        @Override
        public TripViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.trip_feed_card, viewGroup, false);
            return new TripViewHolder(itemView);
        }

        public void add(Trip trip) {
            
        }

        public class TripViewHolder extends RecyclerView.ViewHolder {
            protected TextView cTitle;
            protected TextView cName;
            protected TextView cDesc;
            protected ImageView cImage;

            public TripViewHolder(View v) {
                super(v);
                cTitle =  (TextView) v.findViewById(R.id.card_title);
                cName = (TextView)  v.findViewById(R.id.card_name);
                cDesc = (TextView)  v.findViewById(R.id.card_description);
                cImage = (ImageView) v.findViewById(R.id.card_image);
            }
        }
    }

    private RoundedBitmapDrawable createRoundedBitmapDrawableWithBorder(Bitmap bitmap){
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int borderWidthHalf = 10;
        int bitmapRadius = Math.min(bitmapWidth,bitmapHeight)/2;
        int bitmapSquareWidth = Math.min(bitmapWidth,bitmapHeight);
        int newBitmapSquareWidth = bitmapSquareWidth+borderWidthHalf;
        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth,newBitmapSquareWidth,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        canvas.drawColor(Color.RED);
        int x = borderWidthHalf + bitmapSquareWidth - bitmapWidth;
        int y = borderWidthHalf + bitmapSquareWidth - bitmapHeight;
        canvas.drawBitmap(bitmap, x, y, null);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidthHalf*2);
        borderPaint.setColor(Color.WHITE);
        canvas.drawCircle(canvas.getWidth()/2, canvas.getWidth()/2, newBitmapSquareWidth/2, borderPaint);
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),roundedBitmap);
        roundedBitmapDrawable.setAntiAlias(true);
        return roundedBitmapDrawable;
    }

    private void prepareFeeds(int type) {
        VolleySingleton volleySingleton = VolleySingleton.getInstance(getApplicationContext());
        int limit = 10;
        String url = "";
        if(type == 0)
            url = getResources().getString(R.string.host) + "index.php/feed/feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]);
        if(type == 1)
            url = getResources().getString(R.string.host) + "index.php/feed/starred_feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]);
        if(type == 2)
            url = getResources().getString(R.string.host) + "index.php/feed/forked_feeds/" + Integer.toString(limit) + "/" + Integer.toString(loaded[type]);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Debug", response.toString());
                        loaded[0] += response.length();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error", error.toString());
                    }
                }
        );
        volleySingleton.addToRequestQueue(jsonArrayRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.profilepic:
                        Intent i = new Intent(getApplicationContext(), ContactsContract.Profile.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.create_trip:
                        i = new Intent(getApplicationContext(), CreateTripActivity.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.view_trip:
                        i = new Intent(NavigationActivity.this,ViewTripActivity.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.ongoing_trip:
                        i = new Intent(NavigationActivity.this,OnGoingTripActivity.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.share_trip:
                        i = new Intent(NavigationActivity.this,ShareTripActivity.class);
                        startActivity(i);
                        drawerLayout.closeDrawers();
                        break;


                }
                return false;
            }
        });
        View header = navigationView.getHeaderView(0);
        Intent i = getIntent();
        User user = (User)i.getSerializableExtra("user");
        if(user == null) {
            Log.i("Error", "No user found");
            finish();
        }
        TextView name = (TextView) header.findViewById(R.id.profile_name);
        TextView email = (TextView)header.findViewById(R.id.profile_email);
        name.setText(user.getName());
        if(!user.getEmail().equals("unavailable"))
            email.setText(user.getEmail());
        else
            email.setText("");
        prepareFeeds(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }

    public void goToProfileActivity(View view){
        Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(profile);
    }

}
