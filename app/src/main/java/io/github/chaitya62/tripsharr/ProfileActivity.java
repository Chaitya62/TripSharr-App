package io.github.chaitya62.tripsharr;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_profile);
        toolbar.setTitle("HELLO WORLD");
        toolbar.setTitleTextColor(Color.RED);
        setSupportActionBar(toolbar);

//        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
//        getLayoutInflater().inflate(R.layout.activity_profile, contentFrameLayout);
//        String[] testArray = {"Android","IPhone","WindowsMobile","Blackberry",
//                "WebOS","Ubuntu","Windows7","Max OS X"};
//        ArrayAdapter testAdapter = new ArrayAdapter<>(this,
//                R.layout.trips_list_view, testArray);;
//        ListView listView = (ListView) findViewById(R.id.trips);
//        listView.setAdapter(testAdapter);

    }
}
