package io.github.chaitya62.tripsharr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by chaitya62 on 29/8/17.
 */

public class testingdeep extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent extintent = getIntent();
        String action = extintent.getAction();
        Uri data = extintent.getData();
        Log.i("DATA : ", data.toString());
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
