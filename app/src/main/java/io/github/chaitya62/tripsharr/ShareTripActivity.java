package io.github.chaitya62.tripsharr;

import android.os.Bundle;
import android.widget.FrameLayout;

/**
 * Created by mikasa on 27/8/17.
 */

public class ShareTripActivity extends NavigationActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_sharetrip, contentFrameLayout);
    }
}
