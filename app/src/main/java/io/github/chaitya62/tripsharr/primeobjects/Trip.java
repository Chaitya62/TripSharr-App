package io.github.chaitya62.tripsharr.primeobjects;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
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
    private String userName; // only for feeds not for database
    private String description;
    private boolean isComplete,isPublic,isStarred, isForked;
    private long noOfStars,noOfForks,ownerId, startCoordinateId, endCoordinateId;


    public Trip(){
        id = -1;
        userId = -1;
        name = "STuff";
        description = null;
        oldTripId = 0;
        noOfStars = 0;
        noOfForks = 0;
        userName = "";
        startCoordinateId = 0;
        endCoordinateId = 0;
        isComplete = false;
        isPublic = false;
        ownerId = 0;
    }

    public Trip(String name,String desc){
        this.name=name;
        this.description=desc;
    }


    public Trip(JSONObject trip) throws Exception{
        this();
        try {
            id = Long.parseLong(trip.get("id").toString());
            userId = Long.parseLong(trip.get("user_id").toString());
            name = trip.get("name").toString();
            description = trip.get("description").toString();
            startCoordinateId =  trip.getLong("start_coordinate_id");
            endCoordinateId =   trip.getLong("end_coordinate_id");
           // Log.i("STARS : ", trip.get("no_of_stars")+"");
            noOfStars = Long.parseLong(trip.get("no_of_stars").toString());
            noOfForks = Long.parseLong(trip.get("no_of_forks").toString());
            isComplete = trip.getBoolean("is_complete");
            isPublic = Boolean.parseBoolean(trip.get("is_public").toString());
            try{
                isForked = trip.getBoolean("is_forked");
                isStarred = trip.getBoolean("is_starred");

            }catch(Exception e){
                Log.i("DEBUG :", "THIS WILL SHOW IF FORKED OR STARRED IS NOT SET FROM SERVER IN FEEDS");
            }
            try{
                userName = trip.getString("user_name");
            }catch(Exception e){
                Log.i("DEBUG: ", "THIS WILL SHOW IF USERNAME IS NOT SET FROM SERVER IN FEEDS");
            }
            if(!trip.get("old_trip_id").toString().equals("null"))
                oldTripId = Long.parseLong(trip.get("old_trip_id").toString());
            if(!trip.get("owner_id").toString().equals("null"))
                ownerId = Long.parseLong(trip.get("owner_id").toString());
        }catch(Exception e){
            Log.i("Error", "JsonObject To Trip: "+trip.toString()+" "+e.toString());
            throw e;
        }
    }

    public Trip(long id, long userId, long oldTripId, String name, String description, long startCoordinateId, long endCoordinateId, boolean isComplete, boolean isPublic, long noOfStars, long noOfForks, long ownerId) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.startCoordinateId = startCoordinateId;
        this.endCoordinateId = endCoordinateId;
        this.isComplete = isComplete;
        this.isPublic = isPublic;
        this.oldTripId = oldTripId;
        this.noOfStars = noOfStars;
        this.noOfForks = noOfForks;
        this.ownerId = ownerId;
    }


    public String getName(){
        return this.name;
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

        return params;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }


    public long getStartCoordinateId() {
        return startCoordinateId;
    }

    public void setStartCoordinateId(long startCoordinateId) {
        this.startCoordinateId = startCoordinateId;
    }

    public long getEndCoordinateId() {
        return endCoordinateId;
    }

    public void setEndCoordinateId(long endCoordinateId) {
        this.endCoordinateId = endCoordinateId;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isForked() {
        return isForked;
    }

    public void setForked(boolean forked) {
        isForked = forked;
    }
}