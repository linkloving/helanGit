package com.VitaBit.VitaBit.logic.dto;

/**
 * Created by Administrator on 2016/7/20.
 */
public class Profile {

    /**
     * avatar_color : #8c0295
     * first_name : Patrick
     * last_name : Berenschot
     * dob : 1916-02-25T00:00:00
     * gender : male
     * weight : null
     * height : null
     */
    private String avatar_color;
    private String first_name;
    private String last_name;
    private String dob;
    private String gender;
    private String weight;
    private String height;
    private String user_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAvatar_color() {
        return avatar_color;
    }

    public void setAvatar_color(String avatar_color) {
        this.avatar_color = avatar_color;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }
}
