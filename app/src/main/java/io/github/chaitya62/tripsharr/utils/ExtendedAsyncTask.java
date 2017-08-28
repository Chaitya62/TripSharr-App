package io.github.chaitya62.tripsharr.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

/**
 * Created by ankit on 28/8/17.
 */

public class ExtendedAsyncTask extends AsyncTask<Object, Void, Object> {

    private Handler handler;
    private Context context;
    private Cloudinary cloudinary;
    private int counter = 0;

    public ExtendedAsyncTask() {
        HashMap<String, String > config = new HashMap<>();
        config.put("cloud_name", "tripsharr");
        config.put("api_key", "848582555262954");
        config.put("api_secret", "oGLYQVK1y-Wfm4bF3CvLWMW_utI");
        cloudinary = new Cloudinary(config);
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
        Integer callType = (Integer) params[0];
        if(callType.equals(1)) {
            Intent data = (Intent)params[1];
            Uri selectedImage = data.getData();
            try {
                File file = new File(getRealPathFromURI(selectedImage));
                String imageName= "upload_number"+counter;
                counter = counter + 1;
                cloudinary.uploader().upload(new FileInputStream(file), ObjectUtils.asMap("public_id", imageName));
                String s = cloudinary.url().generate(imageName+".jpg");
                Message msg = handler.obtainMessage(0, s);
                msg.sendToTarget();
                Log.i("Image Url", s);
            }
            catch (Exception e) {
                Log.i("Exception In Upload", e.toString());
            }
            return null;
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
