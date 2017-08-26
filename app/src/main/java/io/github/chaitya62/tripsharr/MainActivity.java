package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    public static String getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        String response = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch(Exception e) {
            Log.i("Error1", e.toString()+" "+urlString);
        }
        return response;
    }
    private void registerUser(final String name, final String email, final String userId) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://tripshare.codeadventure.in/TripShare/index.php/user/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //EveryThing Fine..
                        Log.i("Response", response);
                        Intent i = new Intent(getApplication(), HomeActivity.class);
                        i.putExtra("userId", userId);
                        i.putExtra("authToken", authToken);
                        startActivity(i);
                        //LoginManager.getInstance().logOut();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Volley Error..
                        Log.i("Error", error.toString());
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name",name);
                params.put("user_id",userId);
                params.put("email", email);
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private String authToken, userId, message;
    private CallbackManager callbackManager;

    private class getUser extends AsyncTask<Object, Void, Object> {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                //See if user is registered at server..
                JSONArray user = new JSONArray(getJSONObjectFromURL(params[0].toString()));
                Log.i("LOG: ", user.toString());
                try {
                    String user_id = new JSONObject(user.get(0).toString()).get("user_id").toString();
                    if(user_id == null)
                        throw new Exception("Empty User ID");
                    else
                    {
                        //Is registered..
                        Intent intent = new Intent(getApplication(), HomeActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("authToken", authToken);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    //Not registered on the server..
                    //Get data from facebook to register..
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
                                    String name =  object.get("name").toString();
                                    String email;
                                    try {
                                        email = object.get("email").toString();
                                    } catch (Exception e) {
                                        Log.i("Error", e.toString());
                                        email= "unavailable";
                                    }
                                    //Volley Call to register user..
                                    registerUser(name, email, userId);
                                } catch (Exception e) {
                                    //This should never come.. Cause could be volley..
                                    Log.i("Error", e.toString());
                                    Intent i = new Intent(getApplication(), HomeActivity.class);
                                    i.putExtra("userId", userId);
                                    i.putExtra("authToken", authToken);
                                    startActivity(i);
                                    //LoginManager.getInstance().logOut();
                                    finish();
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
                }
            } catch (Exception e) {
                Log.i("Error", e.toString());
                return null;
            }
            return null;
        }
    }
    private void updateWithToken(final AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            //New Access Token
            Log.i("Debug", currentAccessToken.getToken());
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    userId = currentAccessToken.getUserId();
                    authToken = currentAccessToken.getToken();
                    String url = "http://tripshare.codeadventure.in/TripShare/index.php/user/user_id.json/"+(!userId.isEmpty() ? userId : 1 )+"/";
                    //Change  The Thread..
                    //RequestQueue queue
                    Log.i("url : ",url);
                    new getUser().execute(url);
                }
            }, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        super.onCreate(savedInstanceState);
        //Check if already logged in..
        boolean checkFlag = false;
        try{
            AccessToken.getCurrentAccessToken().toString();
        } catch (Exception e) {
            checkFlag = true;
        }
        if( checkFlag ? (true): (AccessToken.getCurrentAccessToken().toString() == null)) {
            //Not Logged
            setContentView(R.layout.activity_main);
            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList("email"));
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    userId = loginResult.getAccessToken().getUserId();
                    authToken = loginResult.getAccessToken().getToken();
                    message = "login success";
                    Log.i("LoginAttempt", message);
                    Log.i("AuthToken", authToken);
                    Log.i("UserId", userId);
                    updateWithToken(AccessToken.getCurrentAccessToken());
                }

                @Override
                public void onCancel() {
                    message = "Login attempt canceled.";
                    Log.i("LoginAttempt", message);
                }

                @Override
                public void onError(FacebookException e) {
                    message = "Login attempt failed.";
                    Log.i("LoginAttempt", message);
                }
            });
        }
        else {
            //Logged.. Update Access Token..
            updateWithToken(AccessToken.getCurrentAccessToken());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

