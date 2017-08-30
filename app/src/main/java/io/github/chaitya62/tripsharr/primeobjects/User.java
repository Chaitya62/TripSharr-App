package io.github.chaitya62.tripsharr.primeobjects;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chaitya62 on 24/8/17.
 */

public class User {
    private long id;
    private String email, name, fbId;
    private long stars;
    private long forks;

    public User() {
        id = -1;
        email = "unavailable";
        name = null;
        fbId = null;
        stars = 0;
        forks = 0;
    }

    public User(JSONObject user ) throws Exception {
        this();
        try {
            id = Long.parseLong(user.get("id").toString());
            email = user.getString("email");
            name = user.getString("name");
            fbId = user.getString("fb_id");
            stars = user.getLong("stars");
            forks = user.getLong("forks");
        } catch (Exception e) {
            throw e;
        }
    }

    public User(Long id, String name, String email, String fbId) {
        this();
        this.id = id;
        this.email = email;
        this.name = name;
        this.fbId = fbId;
    }

    public User(String name, String email, String fbId) {
        this();
        this.email = email;
        this.name = name;
        this.fbId = fbId;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getFbId() {
        return fbId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public Map< String, String > getParams() throws Exception{
        Map<String , String> params = new HashMap<>();
        if (id != -1) params.put("id", Long.toString(id));
        params.put("email", email);
        if (fbId != null) params.put("fb_id", fbId);
        else throw new Exception("FB Id Not set");
        if (name != null) params.put("name", name);
        else throw new Exception("Name not set");
        params.put("stars", Long.toString(stars));
        params.put("forks", Long.toString(forks));
        return params;
    }

    public long getStars() {
        return stars;
    }

    public void setStars(long stars) {
        this.stars = stars;
    }

    public long getForks() {
        return forks;
    }

    public void setForks(long forks) {
        this.forks = forks;
    }
}
