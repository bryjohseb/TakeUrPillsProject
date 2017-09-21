package com.una.takeurpills;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by BryJohSeb on 9/20/2017.
 */

public class User {
    public String userid;
    public String username;
    public String firstname;
    public String lastname;
    public String email;

    public User(){

    }

    public User(String userid, String username, String firstname, String lastname, String email){
        this.userid = userid;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userid",userid);
        result.put("username", username);
        result.put("firstname", firstname);
        result.put("lastname", lastname);
        result.put("email", email);
        return result;
    }
}
