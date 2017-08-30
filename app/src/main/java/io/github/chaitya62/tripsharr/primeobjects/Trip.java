package io.github.chaitya62.tripsharr.primeobjects;

import android.util.Pair;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chaitya62 on 24/8/17.
 */
public class Trip implements Serializable{
    private long id;
    private long oldTripId;
    private long userId;
    private String name;
    private String description;
    private Pair<Double, Double> startCoordinate;
    private Pair<Double, Double> endCoordinate;
    private boolean isComplete,isPublic;
    private long noOfStars,noOfForks,ownerId;


    public Trip(){
        id = -1;
        userId = -1;
        name = "STuff";
        description = null;
        oldTripId = 0;
        startCoordinate = new Pair<Double, Double>(-1.0 , -1.0);
        endCoordinate = new Pair<Double, Double>(-1.0, -1.0);
        noOfStars = 0;
        noOfForks = 0;
        isComplete = false;
        isPublic = false;
        ownerId = 0;
    }

    public Trip(JSONObject trip) throws Exception{
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
            noOfStars = Long.parseLong(trip.get("no_of_stars").toString());
            noOfForks = Long.parseLong(trip.get("no_of_forks").toString());
            isComplete = Boolean.parseBoolean(trip.get("is_complete").toString());
            isPublic = Boolean.parseBoolean(trip.get("is_public").toString());
            oldTripId = Long.parseLong(trip.get("old_trip_id").toString());
            ownerId = Long.parseLong(trip.get("owner_id").toString());
        }catch(Exception e){
            throw e;
        }
    }

    public Trip(long id, long userId, long oldTripId, String name, String description, Pair<Double, Double> startCoordinate, Pair<Double, Double> endCoordinate, boolean isComplete, boolean isPublic, long noOfStars, long noOfForks, long ownerId) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
        this.isComplete = isComplete;
        this.isPublic = isPublic;
        this.oldTripId = oldTripId;
        this.noOfStars = noOfStars;
        this.noOfForks = noOfForks;
        this.ownerId = ownerId;
    }


    public String getName(){
        return name;
    }

    public Map<String, String> getParams() throws Exception{
        Map<String, String> params = new HashMap<String, String>();
        if(id != -1 ) params.put("id", Long.toString(id));
        if(oldTripId != 0) params.put("old_trip_id", Long.toString(id));
        if(noOfForks != 0) params.put("no_of_forks", Long.toString(id));
        if(noOfStars != 0) params.put("no_of_stars", Long.toString(id));
        if(ownerId != 0) params.put("owner_id", Long.toString(ownerId));
        params.put("is_public", Boolean.toString(isPublic));
        params.put("is_complete", Boolean.toString(isComplete));
        if(userId != -1){
            params.put("user_id", Long.toString(userId));
        }else throw new Exception("must set a valid user_id");
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

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public long getNoOfStars() {
        return noOfStars;
    }

    public long getNoOfForks() {
        return noOfForks;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public void setNoOfStars(long noOfStars) {
        this.noOfStars = noOfStars;
    }

    public void setNoOfForks(long noOfForks) {
        this.noOfForks = noOfForks;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getOldTripId() {
        return oldTripId;
    }

    public void setOldTripId(long oldTripId) {
        this.oldTripId = oldTripId;
    }
}