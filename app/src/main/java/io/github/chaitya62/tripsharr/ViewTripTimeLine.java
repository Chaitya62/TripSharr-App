package io.github.chaitya62.tripsharr;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import io.github.chaitya62.tripsharr.ongoingtrips.OngoingMapActivity;
import io.github.chaitya62.tripsharr.primeobjects.Coordinates;

import java.util.ArrayList;

public class ViewTripTimeLine extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip_time_line);
        final ArrayList<TimelineRow> timelineList = new ArrayList<>();
        Handler handler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message message) {
                if(message.what == 0) {
                    Log.i("Debug", "Got Checkpoints in Activity");
                    ArrayList<Coordinates> checkpoints = (ArrayList<Coordinates>)message.obj;
                    int id = 0;
                    for(Coordinates checkpoint : checkpoints) {
                        TimelineRow timelineRow = new TimelineRow(id++);
                        timelineRow.setDate(checkpoint.getTimestamp());
                        timelineRow.setTitle(checkpoint.getName());
                        timelineRow.setTitleColor(Color.BLACK);
                        timelineRow.setDateColor(Color.BLACK);
                        timelineRow.setDescriptionColor(Color.BLACK);
                        timelineRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.images));
                        timelineRow.setImageSize(40);
                        timelineRow.setDescription(checkpoint.getDescription());
                        timelineRow.setBellowLineColor(Color.RED);
                        timelineRow.setBellowLineSize(6);
                        timelineList.add(timelineRow);
                    }
                    ArrayAdapter<TimelineRow> listAdapter = new TimelineViewAdapter(getApplicationContext(), 0, timelineList, false);
                    ListView listview = (ListView) findViewById(R.id.timeline);
                    listview.setAdapter(listAdapter);
                }
            }
        };
        OngoingMapActivity.handler = handler;
        OngoingMapActivity.prepareCheckpoints(getIntent().getLongExtra("tripId", 0));
    }
}
