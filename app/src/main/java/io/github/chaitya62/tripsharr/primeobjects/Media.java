package io.github.chaitya62.tripsharr.primeobjects;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ankit on 29/8/17.
 */

public class Media implements Serializable{

    private long id;
    private long coordinateId;
    private String url;
    private boolean type;

    public Media() {
        id = -1;
        coordinateId = -1;
        url = null;
        type = false;
    }

    public Media(JSONObject media) {
        this();
        try {
            id = Long.parseLong(media.get("id").toString());
            coordinateId = Long.parseLong(media.get("coordinate_id").toString());
            url = media.getString("url");
            type = Boolean.parseBoolean(media.getString("type"));
        } catch (Exception e) {
            Log.i("Error", e.toString());
        }
    }

    public Media(long coordinateId, String url, boolean type) {
        this();
        this.coordinateId = coordinateId;
        this.url = url;
        this.type = type;
    }

    public Map<String, String> getParams() throws Exception {
        Map<String, String> params  = new HashMap<>();
        if(id != -1) params.put("id", Long.toString(id));
        params.put("type", Boolean.toString(type));
        if(coordinateId != -1) params.put("coordinate_id", Long.toString(coordinateId));
        else throw new Exception("Coordinate Id not set.. ");
        if(url != null) params.put("url", url);
        else throw new Exception("Url not set.. ");
        return params;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCoordinateId() {
        return coordinateId;
    }

    public void setCoordinateId(long coordinateId) {
        this.coordinateId = coordinateId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
