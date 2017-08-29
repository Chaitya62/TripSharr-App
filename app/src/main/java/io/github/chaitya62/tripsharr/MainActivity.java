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
import io.github.chaitya62.tripsharr.utils.VolleyWrapperUser;


public class MainActivity extends AppCompatActivity {

    private String authToken, userId, message, name, email;
    private CallbackManager callbackManager;

    protected void doInBackground() {
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
                            Intent intent = new Intent(getApplication(), NavigationActivity.class);
                            intent.putExtra("userId", user.getUserId());
                            intent.putExtra("name", user.getName());
                            intent.putExtra("email", user.getEmail());
                            intent.putExtra("authToken", authToken);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        //Not registered on the server..
                        //Get data from facebook to register..
                        ExtendedAsyncTask graph = new ExtendedAsyncTask(2);
                        graph.setContext(getApplicationContext());
                        Log.i("Debug", "In Graph");
                        Handler handler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message message) {
                                if(message.what == 1) {
                                    Log.i("Error", "Could Not register user");
                                }
                                if(message.what == 0) {
                                    VolleyWrapperUser volleyWrapperUser = new VolleyWrapperUser(getApplicationContext());
                                    volleyWrapperUser.handler = new Handler(Looper.getMainLooper()) {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            Log.i("Info", "Registered User Successfully");
                                        }
                                    };
                                    ((User)message.obj).setUserId(userId);
                                    volleyWrapperUser.addUser((User)message.obj);
                                    Intent i = new Intent(getApplication(), NavigationActivity.class);
                                    i.putExtra("userId", ((User) message.obj).getUserId());
                                    i.putExtra("name", ((User) message.obj).getName());
                                    i.putExtra("email", ((User) message.obj).getEmail());
                                    i.putExtra("authToken", authToken);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        };
                        graph.setHandler(handler);
                        graph.execute();
                    }
                }
            };
            volleyWrapperUser.getUserByUserId(userId);
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
                    userId = currentAccessToken.getUserId();
                    authToken = currentAccessToken.getToken();
                    //Change  The Thread..
                    //RequestQueue queue
                    doInBackground();
                    Intent intent = new Intent(getApplication(), NavigationActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("authToken", authToken);
                    startActivity(intent);
                    finish();
                }
            }, 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
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

