package com.appromobile.hotel.model.view;

import org.json.JSONObject;

/**
 * Created by xuan on 8/8/2016.
 */
public class FacebookInfo {
    private String id;
    private String email;
    private String name;
    private String gender;
    private String birthday;

    public FacebookInfo(){}

    public FacebookInfo(JSONObject object){
        try {
            id = object.getString("id");
            email = object.getString("email");
            name = object.getString("name");
            gender = object.getString("gender");
            birthday = object.getString("birthday");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
