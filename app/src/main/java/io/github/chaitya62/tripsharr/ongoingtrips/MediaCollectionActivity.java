package io.github.chaitya62.tripsharr.ongoingtrips;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.primeobjects.Coordinates;
import io.github.chaitya62.tripsharr.utils.ExtendedAsyncTask;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleySingleton;

public class MediaCollectionActivity extends AppCompatActivity {

    ImageView addimg;
    Toolbar toolbar;
    static final int REQUEST_IMAGE_CAPTURE = 1,REQUEST_FILE_WRITE_ACCESS=1,REQUEST_FILE_READ_ACCESS=2;
    private Bitmap mImageBitmap;
    private File imageFile;
    private String mCurrentPhotoPath;
    private ImageView mImageView;
    private ExtendedAsyncTask media;
    private String chkptid;
    private Coordinates coordinate;
    private ProgressBar progressBar;
    private TextView textView;

    private void getCheckPoint() {
        String url = "http://tripshare.codeadventure.in/TripShare/index.php/coordinates/coordinates/"+chkptid;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.v("chkpt", "" + response);
                for(int i=0;i<response.length();i++){
                    try {
                        coordinate = new Coordinates(response.getJSONObject(i));
                        addimg.setClickable(true);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_collection);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);
        textView = (TextView) findViewById(R.id.textView3);
        checkStoragePermission();
        chkptid = SharedPrefs.getPrefs().getString("selongchkptid","1");
        Log.v("chkptid",chkptid);

        toolbar = (Toolbar)findViewById(R.id.my_toolbar2);
        addimg = (ImageView) findViewById(R.id.addmedia);
        addimg.setClickable(false);
        mImageView = (ImageView) findViewById(R.id.imgpre);

        toolbar.setTitle("Edit Media");
        setSupportActionBar(toolbar);
        getCheckPoint();

    }

    public void chooseAction(View view) {
        CharSequence colors[] = new CharSequence[] {"Camera", "Gallery"};

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
                            startActivityForResult(cameraIntent, 1);
                        }
                    }
                }
                else {
                    int checkPermission = ContextCompat.checkSelfPermission(MediaCollectionActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(checkPermission == PackageManager.PERMISSION_DENIED ) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FILE_READ_ACCESS);
                        }
                    } else {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(i, 2);
                    }
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
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
        textView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Log.v("request",""+requestCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                Log.v("photo",mCurrentPhotoPath);
                mImageView.setImageBitmap(mImageBitmap);
                ExtendedAsyncTask media = new ExtendedAsyncTask(this, 5);
                Handler handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        try{
                            int c = 1;
                            final JSONObject jsonObject = new JSONObject();
                            for(String s : (ArrayList<String>) msg.obj){
                                jsonObject.put(Integer.toString(c++), s);
                            }
                            JSONObject params = new JSONObject();
                            params.put("coordinate_id", ""+coordinate.getId());
                            params.put("urls", jsonObject);
                            Log.i("Debug", params.toString());
                            String url = getResources().getString(R.string.host)+"index.php/media/add_multiple_media/";
                            Log.i("Debug", url);
                            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("Debug", "Media Added! "+response);
                                    Toast.makeText(getApplicationContext(), "Added Media", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    textView.setVisibility(View.GONE);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("Error", "Error In uploading media " + error.toString());
                                    coordinate.setImageCount(coordinate.getImageCount()-1);
                                    progressBar.setVisibility(View.GONE);
                                    textView.setVisibility(View.GONE);
                                }
                            }) {
                                @Override
                                public Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("coordinate_id", ""+coordinate.getId());
                                    params.put("urls", jsonObject.toString());
                                    return params;
                                }
                            };
                            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                            //Do anything with the returned urls.. The urls will be generated by using trip_id+media_id
                        } catch (Exception e){
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                            coordinate.setImageCount(coordinate.getImageCount()-1);
                        }
                    }
                };
                media.setHandler(handler);
                ArrayList<Uri> mediaList = new ArrayList<>();
                mediaList.add(Uri.fromFile(imageFile));
                media.execute(mediaList, coordinate.getImageCount(), coordinate.getId());
                coordinate.setImageCount(coordinate.getImageCount()+1);
            } catch (IOException e) {
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
        if(requestCode == REQUEST_FILE_READ_ACCESS){
            if(resultCode == RESULT_OK){
                int i = 0;
                media = new ExtendedAsyncTask(getApplication(),5);
                final ArrayList<Uri> mediaList = new ArrayList<>();
                ClipData clipData = data.getClipData();
                Log.i("Debug", data.toString());
                if(clipData == null) {
                    Log.i("Debug", "clipData is null");
                    Uri img = data.getData();
                    mediaList.add(img);
                } else {
                    Log.i("count", "" + clipData.getItemCount());
                    for (; i < clipData.getItemCount(); i++) {
                        Uri uri = clipData.getItemAt(i).getUri();
                        mediaList.add(uri);
                        Log.i("THIS IS IT ", uri + "");
                    }
                }
                final int j = i;
                Handler handler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        try{
                            int c = 1;
                            final JSONObject jsonObject = new JSONObject();
                            for(String s : (ArrayList<String>) msg.obj){
                                jsonObject.put(Integer.toString(c++), s);
                            }
                            JSONObject params = new JSONObject();
                            params.put("coordinate_id", ""+coordinate.getId());
                            params.put("urls", jsonObject);
                            Log.i("Debug", params.toString());
                            String url = getResources().getString(R.string.host)+"index.php/media/add_multiple_media/";
                            Log.i("Debug", url);
                            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("Debug", "Media Added! "+response);
                                    Toast.makeText(getApplicationContext(), "Added Media", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    textView.setVisibility(View.GONE);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.i("Error", "Error In uploading media " + error.toString());
                                    coordinate.setImageCount(coordinate.getImageCount()-j);
                                    progressBar.setVisibility(View.GONE);
                                    textView.setVisibility(View.GONE);
                                }
                            }) {
                                @Override
                                public Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("coordinate_id", ""+coordinate.getId());
                                    params.put("urls", jsonObject.toString());
                                    return params;
                                }
                            };
                            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                            //Do anything with the returned urls.. The urls will be generated by using trip_id+media_id
                        }catch (Exception e){
                            e.printStackTrace();
                            coordinate.setImageCount(coordinate.getImageCount()-j);
                            progressBar.setVisibility(View.GONE);
                            textView.setVisibility(View.GONE);
                        }
                    }
                };
                media.setHandler(handler);
                media.execute(mediaList, coordinate.getImageCount(), coordinate.getId());
                coordinate.setImageCount(coordinate.getImageCount()+mediaList.size());
            }
        }
    }

    public void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(MediaCollectionActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MediaCollectionActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MediaCollectionActivity.this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_FILE_WRITE_ACCESS);

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
            case REQUEST_FILE_WRITE_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MediaCollectionActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MediaCollectionActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
