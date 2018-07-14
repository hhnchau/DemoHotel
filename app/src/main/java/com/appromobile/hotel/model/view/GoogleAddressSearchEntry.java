package com.appromobile.hotel.model.view;

/**
 * Created by xuan on 9/12/2016.
 */
public class GoogleAddressSearchEntry {
    private String description;
    private String id;
    private String place_id;
    private String main_text;
    private String secondary_text;

    public GoogleAddressSearchEntry(){

    }

    public GoogleAddressSearchEntry(String id, String place_id, String description, String main_text, String secondary_text){
        this.id = id;
        this.description = description;
        this.place_id = place_id;
        this.main_text = main_text;
        this.secondary_text = secondary_text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getSecondary_text() {
        return secondary_text;
    }

    public void setSecondary_text(String secondary_text) {
        this.secondary_text = secondary_text;
    }

    public String getMain_text() {
        return main_text;
    }

    public void setMain_text(String main_text) {
        this.main_text = main_text;
    }
}
