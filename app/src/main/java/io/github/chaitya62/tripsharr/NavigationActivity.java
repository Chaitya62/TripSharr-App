package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.facebook.login.LoginManager;

import io.github.chaitya62.tripsharr.ongoingtrips.OnGoingTripActivity;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by mikasa on 26/8/17.
 */

public class NavigationActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    private static Bitmap profileImage;
    private ImageView profilePic;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void getPicture() {
        if(profileImage != null) {
            profilePic.setImageBitmap(profileImage);
            return;
        }
        String profileUrl = "https://graph.facebook.com/"+SharedPrefs.getPrefs().getString("fb_id", "0")+"/picture?height=120";
        Log.i("URL", profileUrl);
        ImageRequest imageRequest = new ImageRequest(profileUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profileImage = getRoundedBitmap(response);
                profilePic.setImageBitmap(profileImage);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("URL", "In Volley Image Request Profile pic: "+error.toString());
            }
        });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);

        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Intent i;
                switch (item.getItemId()) {
                    case R.id.home:
                        i = new Intent(getApplicationContext(), FeedsActivity.class);
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



                }
                return false;
            }
        });
        View header = navigationView.getHeaderView(0);
        profilePic = (ImageView) header.findViewById(R.id.profilepic);
        getPicture();
        TextView name = (TextView) header.findViewById(R.id.profile_name);
        TextView email = (TextView)header.findViewById(R.id.profile_email);
        name.setText(SharedPrefs.getPrefs().getString("user_name", null));
        if(!SharedPrefs.getPrefs().getString("email", null).equals("unavailable"))
            email.setText(SharedPrefs.getPrefs().getString("email", null));
        else
            email.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       // if (id == R.id.action_settings) {
//            return true;
//        }
        if(id == R.id.logout) {
            LoginManager.getInstance().logOut();
            SharedPrefs.getEditor().clear();
            SharedPrefs.getEditor().commit();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }

    public void goToProfileActivity(View view){
        Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
        profile.putExtra("user_id",SharedPrefs.getPrefs().getLong("user_id", 1));
        startActivity(profile);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

}
