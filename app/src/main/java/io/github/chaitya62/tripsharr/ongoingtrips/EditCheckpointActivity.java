package io.github.chaitya62.tripsharr.ongoingtrips;

import android.app.AlertDialog;
import android.appwidget.AppWidgetProvider;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.github.chaitya62.tripsharr.Manifest;
import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.ViewTripActivity;
import io.github.chaitya62.tripsharr.primeobjects.Coordinates;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

/**
 * Created by mikasa on 3/9/17.
 */

public class EditCheckpointActivity extends AppCompatActivity {

    String tripid,chkptid;
    EditText upnameet,updescet;
    String chkptUrl = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/update/";
    static final int REQUEST_IMAGE_CAPTURE = 1,REQUEST_FILE_ACCESS=1;
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath,path;
    private ImageView mImageView;
    private int imageCount;
    int permissionCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_checkpoint);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        upnameet = (EditText) findViewById(R.id.upnameet);
        updescet = (EditText) findViewById(R.id.updescet);

        mImageView = (ImageView) findViewById(R.id.imgpre);

        tripid = getIntent().getStringExtra("Tripid");
        tripid = SharedPrefs.getPrefs().getString("selongtripid","1");
        Log.v("shareded",tripid);
        chkptid = getIntent().getStringExtra("Chkptid");
        chkptid = SharedPrefs.getPrefs().getString("selongchkptid","1");
        Log.v("chkptid",chkptid);

        // Assume thisActivity is the current activity
        permissionCheck = ContextCompat.checkSelfPermission(EditCheckpointActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        checkStoragePermission();

        myToolbar.setTitle("Edit Checkpoint");
        setSupportActionBar(myToolbar);

        getCheckpointDetail();
    }

    public void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(EditCheckpointActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditCheckpointActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(EditCheckpointActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_FILE_ACCESS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FILE_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(EditCheckpointActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(EditCheckpointActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
                //Toast.makeText(EditCheckpointActivity.this,"media",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(EditCheckpointActivity.this,MediaCollectionActivity.class);

                startActivity(i);
                /*CharSequence colors[] = new CharSequence[] {"Camera", "Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pick a color");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        Log.v("which",""+which);
                        if(which==0) {
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                    Log.v("imgerror", "IOException");
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        }
                    }
                });
                builder.show();*/
                break;
        }
    }

    /*private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                Log.v("photo",mCurrentPhotoPath);
                mImageView.setImageBitmap(mImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(EditCheckpointActivity.this,CheckpointActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
    }
}
