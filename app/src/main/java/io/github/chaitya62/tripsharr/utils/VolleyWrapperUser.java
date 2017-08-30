package io.github.chaitya62.tripsharr.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import io.github.chaitya62.tripsharr.R;
import io.github.chaitya62.tripsharr.localDB.UserDB;
import io.github.chaitya62.tripsharr.primeobjects.User;

/**
 * Created by ankit on 27/8/17.
 */

public class VolleyWrapperUser {
    private Context context;
    private UserDB udb;
    public Handler handler;
    private User u = null;

    public VolleyWrapperUser(Context context) {
        this.context = context;
        udb = new UserDB(context);
        u = new User();
    }

    public void getUser(long id) {
        User user;
        try {
            user = udb.getById(id);
        } catch (Exception e) {
            //call get method here..
            final String url = context.getResources().getString(R.string.host)+"/user/user/"+id+"/";
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                u = new User(response);
                                Message message = handler.obtainMessage(0, u);
                                message.sendToTarget();
                                udb.save(u);
                            } catch (Exception e) {
                                u = null;
                                Log.i("Error", "Could not add the user in database. "+e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Error.Response", error.toString());
                            u = null;
                        }
                    }
            );
            RequestQueue queue = VolleySingleton.getInstance(context).getRequestQueue();
            queue.add(getRequest);
            user = null;
        }
    }

    public void addUser(final User user) {
        u = user;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.host) + "index.php/user/add/",
                new Response.Listener<String >() {
                    @Override
                    public void onResponse(String  response) {
                        try {
                            //Log.i("Response", response);
                            user.setId(Long.valueOf(response));
                            Message msg = handler.obtainMessage(0, user);
                            msg.sendToTarget();
                        } catch (Exception e) {
                            Log.i("Error", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley Error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = null;
                try {
                    params = u.getParams();
                    Log.i("Params", params.toString());
                } catch (Exception e) {
                    Log.i("Error", "user.getParams() gave error. "+e.toString());
                }
                return params;
            }
        };
        try {
            Log.i("Debug", u.getParams().toString());
        } catch (Exception e) {
            Log.i("Error", e.toString());
        }
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void updateUser(User user) {
        u = user;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.host) + "index.php/user/update/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            u = new User(new JSONObject(response));
                            udb.update(u);
                        } catch (Exception e) {
                            Log.i("Error", e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Volley Error", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = null;
                try {
                    params = u.getParams();
                } catch (Exception e) {
                    Log.i("Error", "user.getparams() gave error. "+e.toString());
                }
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void getUserByFbId(String fbId){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(context.getResources().getString(R.string.host)+"index.php/user/userfb/"+fbId,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            Log.i("Response", jsonArray.getJSONObject(0).toString());
                            u = new User(jsonArray.getJSONObject(0));
                            Message message = handler.obtainMessage(0, u);
                            message.sendToTarget();
                            udb.update(u);
                        } catch (Exception e) {
                            Log.i("Error", "User Constructor Error "+e.toString());
                            u = new User();
                            Message message = handler.obtainMessage(1, u);
                            message.sendToTarget();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.i("Error", "Volley Error: "+volleyError.toString());
                    }
                }
        );
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
