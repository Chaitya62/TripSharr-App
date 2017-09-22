package io.github.chaitya62.tripsharr.primeobjects;

import android.os.Parcelable;
import android.util.Pair;

import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chaitya62 on 24/8/17.
 */

public class Coordinates{
    private long id, tripId;
    private String name, description;
    private Pair<Double, Double> point;
    private Timestamp timestamp;
    private int imageCount, videoCount;
    private int oldCoordinateId;


    public Coordinates() {
        id = -1;
        tripId = -1;
        name = "Default";
        description = "Default";
        point = new Pair<>(-1.0, -1.0);
        timestamp = Timestamp.valueOf("1998-05-22 19:00:00.0");
        imageCount = 0;
        videoCount = 0;
        oldCoordinateId = 0;
    }

    public Coordinates(JSONObject coordinate) throws Exception {
        this();
        try {
            id = Long.parseLong(coordinate.get("id").toString());
            tripId = Long.parseLong(coordinate.get("trip_id").toString());
            name = coordinate.get("name").toString();
            description = coordinate.get("description").toString();
            double x, y;
            x = Double.parseDouble(coordinate.get("x_co").toString());
            y = Double.parseDouble(coordinate.get("y_co").toString());
            point = new Pair<>(x, y);
            timestamp = Timestamp.valueOf(coordinate.get("priority").toString());
            imageCount = Integer.parseInt(coordinate.get("image_count").toString());
            videoCount = Integer.parseInt(coordinate.get("video_count").toString());
            oldCoordinateId = Integer.parseInt(coordinate.get("old_coordinate_id").toString());
        } catch (Exception e) {
            throw e;
        }
    }

    public Coordinates (long id, long tripId, String name, String description, Pair<Double, Double> point, Timestamp timestamp, int imageCount, int videoCount, int oldCoordinateId) {
        this.id = id;
        this.tripId = tripId;
        this.name = name;
        this.description = description;
        this.point = point;
        this.timestamp = timestamp;
        this.imageCount = imageCount;
        this.videoCount = videoCount;
        this.oldCoordinateId = oldCoordinateId;
    }

    @Override
    public String toString() {
        return point.toString();
    }


    public long getId() {
        return id;
    }

    public long getTripId() {
        return tripId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Pair<Double, Double> getPoint() {
        return point;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getImageCount() {
        return imageCount;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPoint(Pair<Double, Double> point) {
        this.point = point;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public Map< String, String > getParams() throws Exception {
        Map< String, String > params = new HashMap<>();
        if (id != -1) params.put("id", Long.toString(id));
        if (tripId == -1) throw new Exception("TripId Not set");
        else params.put("trip_id", Long.toString(tripId));
        params.put("name",name);
        if (description != null) params.put("description", toString());
        if (point.first.equals(-1.0) || point.second.equals(-1.0)) throw new Exception("Point Not Set");
        else {
            params.put("x_co", point.first.toString());
            params.put("y_co", point.second.toString());
        }
        params.put("priority", timestamp.toString());
        params.put("image_count", Integer.toString(imageCount));
        params.put("video_count", Integer.toString(videoCount));
        params.put("old_coordinate_id", Integer.toString(oldCoordinateId));
        return params;
    }

    public int getOldCoordinateId() {
        return oldCoordinateId;
    }

    public void setOldCoordinateId(int oldCoordinateId) {
        this.oldCoordinateId = oldCoordinateId;
    }
}
