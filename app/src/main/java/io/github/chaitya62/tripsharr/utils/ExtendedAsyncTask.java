package io.github.chaitya62.tripsharr.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import io.github.chaitya62.tripsharr.primeobjects.User;

/**
 * Created by ankit on 28/8/17.
 */

public class ExtendedAsyncTask extends AsyncTask<Object, Void, Object> {

    private Handler handler;
    private Context context;
    private int callType;
    private Cloudinary cloudinary;
    private int counter = 0;

    public ExtendedAsyncTask(Context mcontext,int callType) {
        this.context = mcontext;
        this.callType = callType;
        if(callType == 1) {
            HashMap<String, String> config = new HashMap<>();
            config.put("cloud_name", "tripsharr");
            config.put("api_key", "848582555262954");
            config.put("api_secret", "oGLYQVK1y-Wfm4bF3CvLWMW_utI");
            cloudinary = new Cloudinary(config);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String s = cursor.getString(idx);
            cursor.close();
            return s;
        }
    }

    @Override
    protected Object doInBackground(Object... params) {
        if(callType == 1) {
            Intent data = (Intent)params[0];
            Uri selectedImage = data.getData();
            Log.i("STRING URL : ", selectedImage + "");
            try {
                File file = new File(getRealPathFromURI(selectedImage));
                String imageName= "upload_number"+counter;
                counter = counter + 1;
                cloudinary.uploader().upload(new FileInputStream(file), ObjectUtils.asMap("public_id", imageName));
                String s = cloudinary.url().generate(imageName+".jpg");
                Log.i("Image Url", s);
                Message msg = handler.obtainMessage(0, s);
                msg.sendToTarget();
                Log.i("Image Url", s);
            }
            catch (Exception e) {
                Log.i("Exception In Upload", e.toString());
            }
            return null;
        }
        else if(callType == 2) {
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            Log.i("Debug", "Got In");
                            if(response.getError() != null) {
                                //Couldn't get data..
                                Log.i("Error1", response.getError().toString());
                            }
                            try {
                                //Got data.. Lets Register..
                                User user = new User();
                                user.setName(object.get("name").toString());
                                try {
                                    user.setEmail(object.get("email").toString());
                                } catch (Exception e) {
                                    Log.i("Error", e.toString());
                                    user.setEmail("unavailable");
                                }
                                Message message = handler.obtainMessage(0, user);
                                message.sendToTarget();
                                //Volley Call to register user..
                            } catch (Exception e) {
                                Message message = handler.obtainMessage(1, e);
                                message.sendToTarget();
                            }
                        }
                    }
            );
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            Log.i("Debug", "In here");
            //Wait till the request is completed..
            request.executeAndWait();
            return null;
        }
        else if (callType == 3) {
            while(true) {
                if(NetworkUtils.isNetworkAvailable()) {
                    Message message = handler.obtainMessage(0);
                    message.sendToTarget();
                    break;
                }
            }
        }
        return null;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
