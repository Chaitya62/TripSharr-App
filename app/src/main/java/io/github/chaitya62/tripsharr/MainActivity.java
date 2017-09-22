package io.github.chaitya62.tripsharr;

import android.content.Intent;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import io.github.chaitya62.tripsharr.primeobjects.User;
import io.github.chaitya62.tripsharr.utils.ExtendedAsyncTask;
import io.github.chaitya62.tripsharr.utils.NetworkUtils;
import io.github.chaitya62.tripsharr.utils.SharedPrefs;
import io.github.chaitya62.tripsharr.utils.VolleyWrapperUser;


public class MainActivity extends AppCompatActivity {

    private String authToken, fbId, message, name, email;
    private CallbackManager callbackManager;

    protected void doInBackground() {
        try {
            //See if user is registered at server..
            VolleyWrapperUser volleyWrapperUser = new VolleyWrapperUser(getApplicationContext());
            //Set handler for checking registration
            volleyWrapperUser.handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage (Message msg) {
                    try {
                        Log.i("USER : ", msg.toString());
                        User user = (User)msg.obj;
                        String fbId1 = user.getFbId();
                        Log.i("Debug", user.getFbId());
                        if(fbId1 == null)
                            throw new Exception("Empty User ID");
                        else {
                            //Is registered..
                            Intent intent = new Intent(getApplication(), FeedsActivity.class);
                            SharedPrefs.getEditor().putString("user_name", user.getName());
                            SharedPrefs.getEditor().putLong("user_id", user.getId());
                            SharedPrefs.getEditor().putString("fb_id", user.getFbId());
                            SharedPrefs.getEditor().putLong("forks", user.getForks());
                            SharedPrefs.getEditor().putLong("stars", user.getStars());
                            SharedPrefs.getEditor().putString("email", user.getEmail());
                            SharedPrefs.getEditor().commit();
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        //Not registered on the server..
                        //Get data from facebook to register..
                        ExtendedAsyncTask graph = new ExtendedAsyncTask(getApplication(),2); //Call Type 2 for Graph data..
                        graph.setContext(getApplicationContext()); //Set context
                        Log.i("Debug", "In Graph");
                        //Make a new handler to respond when data arrives..
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(final Message message) {
                                if(message.what == 1) {
                                    //Error in Graph Call
                                    Log.i("Error", "Could Not register user");
                                }
                                if(message.what == 0) {
                                    //Data arrives properly
                                    //Now register the user..
                                    Log.i("DEBUG ","Data arrives properly");
                                    VolleyWrapperUser volleyWrapperUser = new VolleyWrapperUser(getApplicationContext());
                                    //Set handler to respond on Callback
                                    volleyWrapperUser.handler = new Handler(Looper.getMainLooper()) {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            //Registered..
                                            Log.i("Info", "Registered User Successfully");
                                            User user = ((User)msg.obj);
                                            Intent i = new Intent(getApplication(), FeedsActivity.class);
                                            SharedPrefs.getEditor().putString("user_name", user.getName());
                                            SharedPrefs.getEditor().putLong("user_id", user.getId());
                                            SharedPrefs.getEditor().putString("fb_id", user.getFbId());
                                            SharedPrefs.getEditor().putLong("forks", user.getForks());
                                            SharedPrefs.getEditor().putLong("stars", user.getStars());
                                            SharedPrefs.getEditor().putString("email", user.getEmail());
                                            SharedPrefs.getEditor().commit();
                                            i.putExtra("user", user);
                                            startActivity(i);
                                            finish();
                                        }
                                    };
                                    ((User)message.obj).setFbId(fbId); // Set fbId to the received data..
                                    volleyWrapperUser.addUser((User)message.obj);//Add user
                                }
                            }
                        };
                        graph.setHandler(handler); //Set handler
                        graph.execute(); //Make Graph Call
                    }
                }
            };
            //Check if registered
            volleyWrapperUser.getUserByFbId(fbId);
        } catch (Exception e) {
            Log.i("Error", e.toString());
        }
    }

    private void updateWithToken(final AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            //New Access Token
            Log.i("Debug", currentAccessToken.getToken());
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    fbId = currentAccessToken.getUserId();
                    authToken = currentAccessToken.getToken();
                    //Change  The Thread..
                    //RequestQueue queue
                    doInBackground();
                }
            }, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        new SharedPrefs(getApplicationContext());
        new NetworkUtils(getApplicationContext());
        //LoginManager.getInstance().logOut();
        callbackManager = CallbackManager.Factory.create();
        super.onCreate(savedInstanceState);
        //Check if already logged in..
        startApp();
        // Past this point Internet is available..
    }

    private void startApp() {
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
                    fbId = loginResult.getAccessToken().getUserId();
                    authToken = loginResult.getAccessToken().getToken();
                    message = "login success";
                    Log.i("LoginAttempt", message);
                    Log.i("AuthToken", authToken);
                    Log.i("UserId", fbId);
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
            Intent i = new Intent(getApplication(), FeedsActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

