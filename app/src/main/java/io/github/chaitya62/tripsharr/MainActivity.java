package io.github.chaitya62.tripsharr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {

    private String authToken, userId, message;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                userId = loginResult.getAccessToken().getUserId();
                authToken = loginResult.getAccessToken().getToken();
                message = "login success";
                Intent intent = new Intent(getApplication(), HomeActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("authToken", authToken);
                startActivity(intent);
                Log.i("LoginAttempt", message);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

