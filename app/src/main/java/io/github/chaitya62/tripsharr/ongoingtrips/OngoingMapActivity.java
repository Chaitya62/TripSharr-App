package io.github.chaitya62.tripsharr.ongoingtrips;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cloudinary.Coordinates;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Trip;
import io.github.chaitya62.tripsharr.utils.CoordinateComparator;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

import static io.github.chaitya62.tripsharr.utils.NetworkUtils.context;

public class OngoingMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,GoogleMap.OnMarkerClickListener {

    private List<io.github.chaitya62.tripsharr.primeobjects.Coordinates> p = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private Bitmap bitmap;
    private FloatingActionButton add,listview;
    public static Handler handler;
    private static GoogleMap mMap;
    private boolean tripstatus;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private String tripid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_map);

        add = (FloatingActionButton) findViewById(R.id.ongoing_add);
        listview = (FloatingActionButton) findViewById(R.id.listview);
        tripid = SharedPrefs.getPrefs().getString("selongtripid","1");
        tripstatus = SharedPrefs.getPrefs().getBoolean("tripstatus",false);
        Log.v("Tripid",tripid);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if(tripstatus)
            add.setVisibility(View.GONE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Log.v("map",getIntent().getStringExtra("Tripid"));
                if(mLastLocation!=null)
                {
                    io.github.chaitya62.tripsharr.primeobjects.Coordinates coordinates = new io.github.chaitya62.tripsharr.primeobjects.Coordinates();
                    Pair<Double,Double> pair = new Pair(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                    coordinates.setPoint(pair);
                    coordinates.setTripId(Long.parseLong(tripid));
                    coordinates.setTimestamp(Timestamp.valueOf(getDateTime()));
                    String url = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/add/";
                    Map<String,String> hp = new HashMap<>();
                    Intent i = new Intent(OngoingMapActivity.this,AddCheckpointActivity.class);

                    try{
                        hp = coordinates.getParams();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    add.setVisibility(View.INVISIBLE);
                    JSONObject jsonObject = new JSONObject(hp);
                    Coordinates coordinates1 = new Coordinates();
                    Log.v("maph",jsonObject.toString());
                    SharedPrefs.getEditor().putString("Chkptjson",jsonObject.toString());
                    SharedPrefs.getEditor().commit();
                    startActivity(i);
                }

            }
        });

        listview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OngoingMapActivity.this,CheckpointActivity.class);
                startActivity(i);
            }
        });
    }

    private String getDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        Log.d("MainActivity", "Current Timestamp: " + format);
        return format;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.clear();
        mMap.setOnMarkerClickListener(this);

        prepareCheckpoints();




    }

    private Marker drawMarkers(io.github.chaitya62.tripsharr.primeobjects.Coordinates ip,int curr){

        Log.v("drawmark",""+ip.getPoint().first+" "+ip.getPoint().second);

        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        Pair<Double,Double> pair = ip.getPoint();
        LatLng point = new LatLng(pair.first,pair.second);

        markerOptions.position(point);

        // Setting title for the InfoWindow
        markerOptions.title(ip.getName());

        // Setting InfoWindow contents
        markerOptions.snippet("Id: "+ip.getId());

        markerOptions.anchor(0.5f,0.5f);

        if(curr==0) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }


        if(curr>0) {

            if(curr==p.size()-1 && tripstatus) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
            PolylineOptions polylineOptions = new PolylineOptions().color(Color.rgb(0,191,255)).width((float)10.0);
            polylineOptions.add(point);
            pair = p.get(curr-1).getPoint();
            LatLng previousPoint = new LatLng(pair.first,pair.second);
            //polylineOptions.add(point,previousPoint);

            polylineOptions.add(previousPoint);
            mMap.addPolyline(polylineOptions);
        }



        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));

        //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.rectangle, ""+curr)));
        Log.v("icon",""+markerOptions.getIcon());


        //return mMap.addMarker(new MarkerOptions().position(point).snippet(""+ip.getId()).icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_head)));
        return mMap.addMarker(markerOptions);

    }

    public void prepareCheckpoints(){
        String url = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/coordinatesOf/"+tripid;
        Log.v("maphello",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("maphello", "" + response);
                        Trip trip;
                        io.github.chaitya62.tripsharr.primeobjects.Coordinates coordinates;
                        JSONObject jsonObject;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                jsonObject = response.getJSONObject(i);
                                coordinates = new io.github.chaitya62.tripsharr.primeobjects.Coordinates(jsonObject);
                                //drawMarkers(coordinates);
                                p.add(coordinates);
                                Log.v("formap",""+response.get(i));
                            }
                            Collections.sort(p,new CoordinateComparator());
                            for(int i=0;i<p.size();i++) {
                                Log.v("chkpt",""+p.get(i).getTimestamp());
                                drawMarkers(p.get(i),i);
                            }
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Marker marker : markers) {
                                builder.include(marker.getPosition());
                            }
                            LatLngBounds bounds = builder.build();
                            int padding = 0; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.moveCamera(cu);
                            mMap.animateCamera(cu);
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));


                        }
                        catch (Exception e){}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("hellerr",""+error);
            }
        });
        VolleySingleton.getInstance(OngoingMapActivity.this).addToRequestQueue(jsonArrayRequest);
    }

    public static void prepareCheckpoints(long tripid) {
        String url = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/coordinatesOf/"+tripid;
        Log.v("maphello",url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("maphello", "" + response);
                        ArrayList<io.github.chaitya62.tripsharr.primeobjects.Coordinates> checkpoints = new ArrayList<>();
                        io.github.chaitya62.tripsharr.primeobjects.Coordinates coordinates;
                        JSONObject jsonObject;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                jsonObject = response.getJSONObject(i);
                                coordinates = new io.github.chaitya62.tripsharr.primeobjects.Coordinates(jsonObject);
                                checkpoints.add(coordinates);
                                Log.v("formap",""+response.get(i));
                            }
                            Collections.sort(checkpoints,new CoordinateComparator());
                            for(int i=0;i<checkpoints.size();i++) {
                                Log.v("chkpt",""+checkpoints.get(i).getTimestamp());
                            }
                            Message message = handler.obtainMessage(0, checkpoints);
                            message.sendToTarget();
                        }
                        catch (Exception e){}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("hellerr",""+error);
            }
        });
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        if(add.getVisibility()==View.INVISIBLE)
            add.setVisibility(View.VISIBLE);
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        Log.v("location",""+location);
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        /*move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(30));*/


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(!tripstatus) {
            Intent i = new Intent(OngoingMapActivity.this, EditCheckpointActivity.class);
            String temp = marker.getSnippet();
            if(temp == null)
                return false;
            temp = temp.substring(4, temp.length());
            Log.v("temp", temp);
            SharedPrefs.getEditor().putString("selongchkptid", temp);
            SharedPrefs.getEditor().commit();
            i.putExtra("Chkptid", temp);
            startActivity(i);
        }
        return false;
    }

    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(OngoingMapActivity.this,OnGoingTripActivity.class);
        SharedPrefs.getEditor().remove("selongchkptid");
        SharedPrefs.getEditor().remove("Chkptjson");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }

    private Bitmap writeTextOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(context,11));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(context, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, xPos, yPos, paint);

        return  bm;
    }



    public static int convertToPixels(Context context, int nDP)
    {
        final float conversionScale = context.getResources().getDisplayMetrics().density;

        return (int) ((nDP * conversionScale) + 0.5f) ;

    }


}
