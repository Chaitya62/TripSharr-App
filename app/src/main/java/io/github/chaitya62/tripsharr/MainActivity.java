package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

import io.github.chaitya62.tripsharr.primeobjects.User;
import io.github.chaitya62.tripsharr.utils.VolleyWrapperUser;


public class MainActivity extends AppCompatActivity {

    private void registerUser(final String name, final String email, final String userId) {
        User user = new User(name, email, userId);
        VolleyWrapperUser volleyWrapperUser = new VolleyWrapperUser(getApplicationContext());
        volleyWrapperUser.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                User user = (User)msg.obj;
            }
        };
        volleyWrapperUser.addUser(user);
    }

    private class GraphRequestClass extends AsyncTask<Object, Void, Object>{

        @Override
        protected Object doInBackground(Object[] params) {
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
                                name =  object.get("name").toString();
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
            return null;
        }
    }

    private String authToken, userId, message, name, email;
    private CallbackManager callbackManager;

    protected Object doInBackground() {
        try {
            //See if user is registered at server..
            VolleyWrapperUser volleyWrapperUser = new VolleyWrapperUser(getApplicationContext());
            volleyWrapperUser.handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage (Message msg) {
                    try {
                        User user = (User)msg.obj;
                        String userId1 = user.getUserId();
                        Log.i("Debug", user.getUserId());
                        if(userId1 == null)
                            throw new Exception("Empty User ID");
                        else {
                            //Is registered..
                            Intent intent = new Intent(getApplication(), HomeActivity.class);
                            intent.putExtra("userId", user.getUserId());
                            intent.putExtra("name", user.getName());
                            intent.putExtra("email", user.getEmail());
                            intent.putExtra("authToken", authToken);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        //Not registered on the server..
                        //Get data from facebook to register..
                        new GraphRequestClass().execute();
                    }
                }
            };
            volleyWrapperUser.getUserByUserId(userId);
        } catch (Exception e) {
            Log.i("Error", e.toString());
            return null;
        }
        return null;
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
                    //Change  The Thread..
                    //RequestQueue queue
                    doInBackground();
                    Intent intent = new Intent(getApplication(), HomeActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("authToken", authToken);
                    startActivity(intent);
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

