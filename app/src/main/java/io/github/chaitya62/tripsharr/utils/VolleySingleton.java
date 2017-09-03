package io.github.chaitya62.tripsharr.utils;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

/**
 * Created by chaitya62 on 24/8/17.
 */


public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;
//    private Cache volleyCache;
//    private Network network;

    private VolleySingleton(Context context){
        // Specify the application context

        mContext = context;
        // Get the request queue


        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context){
        // If Instance is null then initialize new Instance
        if(mInstance == null){
            mInstance = new VolleySingleton(context);
        }
        // Return MySingleton new Instance
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        // If RequestQueue is null the initialize new RequestQueue
        if(mRequestQueue == null){
//            volleyCache = new DiskBasedCache(mContext.getCacheDir(),1024*1024);
//            network = new BasicNetwork(new HurlStack());
//            mRequestQueue = new RequestQueue(volleyCache, network);
//            mRequestQueue = Volley.newRequestQueue()
             mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        // Return RequestQueue
        return mRequestQueue;
    }

    public<T> void addToRequestQueue(Request<T> request){
        // Add the specified request to the request queue
        getRequestQueue().add(request);
    }
}

