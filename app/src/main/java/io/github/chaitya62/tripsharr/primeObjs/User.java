package io.github.chaitya62.tripsharr.primeObjs;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by chaitya62 on 24/8/17.
 */

public class User {
    private long id;
    private String email, name, userId;

    public User() {
        id = -1;
        email = "unavailable";
        name = null;
        userId = null;
    }

    public User(JSONObject user ) throws Exception {
        this();
        try {
            id = Long.parseLong(user.get("id").toString());
            email = user.getString("email");
            name = user.getString("name");
            userId = user.getString("user_id");
        } catch (Exception e) {
            throw e;
        }
    }

    public User(Long id, String email, String name, String userId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map< String, String > getParams() throws Exception{
        Map<String , String> params = new HashMap<>();
        if (id != -1) params.put("id", Long.toString(id));
        params.put("email", email);
        if (userId != null) params.put("user_id", userId);
        else throw new Exception("User Id Not set");
        if (name != null) params.put("name", name);
        else throw new Exception("Name not set");
        return params;
    }
}