package io.github.chaitya62.tripsharr.primeObjs;

import android.util.Pair;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chaitya62 on 24/8/17.
 */
public class Trip {
    private long id;
    private long userId;
    private String name;
    private String description;
    private Pair<Double, Double> startCoordinate;
    private Pair<Double, Double> endCoordinate;
    private boolean isCompleted;

    public Trip(){
        id = -1;
        userId = -1;
        name = "STuff";
        description = null;
        startCoordinate = new Pair<Double, Double>(-1.0 , -1.0);
        endCoordinate = new Pair<Double, Double>(-1.0, -1.0);
    }

    public Trip(JSONObject trip) throws Exception {
        this();
        try {
            id = Long.parseLong(trip.get("id").toString());
            userId = Long.parseLong(trip.get("user_id").toString());
            name = trip.get("name").toString();
            description = trip.get("description").toString();
            double x,y;
            x = Double.parseDouble(trip.get("start_x").toString());
            y = Double.parseDouble(trip.get("start_y").toString());
            startCoordinate = new Pair<Double, Double>(x, y);
            x = Double.parseDouble(trip.get("end_x").toString());
            y = Double.parseDouble(trip.get("end_y").toString());
            endCoordinate = new Pair<Double, Double>(x, y);
        }catch(Exception e){
            throw e;
        }
    }

    public Trip(long id, long uid, String n, String desc, Pair<Double, Double> start, Pair<Double, Double> end) {
        this.id = id;
        userId = uid;
        name = n;
        description = desc;
        startCoordinate = start;
        endCoordinate = end;
    }

    public String getName(){
        return name;
    }

    public Map<String, String> getParams() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if(id != -1 ) params.put("id", Long.toString(id));
        if(userId != -1) {
            params.put("user_id", Long.toString(userId));
        } else throw new Exception("must set a valid user_id");
        if(name != null){
            params.put("name", name);
        }else throw new Exception("must set name");
        if(description != null){
            params.put("description", description);
        }else throw new Exception("must set description");
        if(startCoordinate.first != -1.0 && startCoordinate.second != -1.0){
            params.put("start_x", Double.toString(startCoordinate.first));
            params.put("start_y", Double.toString(startCoordinate.second));
        }else throw new Exception("Must set startCoordinate");
        if( endCoordinate.first != -1.0 && endCoordinate.second != -1.0){
            params.put("end_x", Double.toString(startCoordinate.first));
            params.put("end_y", Double.toString(startCoordinate.second));
        }else throw new Exception("Must set endCoordinate");
        return params;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Pair<Double, Double> getStartCoordinate() {
        return startCoordinate;
    }

    public void setStartCoordinate(Pair<Double, Double> startCooridnate) {
        this.startCoordinate = startCooridnate;
    }

    public Pair<Double, Double> getEndCoordinate() {
        return endCoordinate;
    }

    public void setEndCoordinate(Pair<Double, Double> endCoordinate) {
        this.endCoordinate = endCoordinate;
    }
}