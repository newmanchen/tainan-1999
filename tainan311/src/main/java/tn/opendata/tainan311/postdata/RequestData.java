package tn.opendata.tainan311.postdata;

import android.location.Location;
import android.net.Uri;

/**
 * Created by vincent on 2014/6/10.
 */
public final class RequestData {

    // required
    private String jurisdiction_id;
    private String service_code;
    private double lon;
    private double lat;

    // optional
    private String address_string;
    private int address_id;
    private String email;
    private String device_id;
    private String account_id;
    private String fist_name;
    private String last_name;
    private String name;
    private String phone;
    private String description;
    private String media_url;


    public String getJurisdiction_id() {
        return jurisdiction_id;
    }

    public void setJurisdiction_id(String jurisdiction_id) {
        this.jurisdiction_id = jurisdiction_id;
    }

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public double getLongitude() {
        return lon;
    }

    public void setLongitude(double lon) {
        this.lon = lon;
    }

    public double getLatitude() {
        return lat;
    }

    public void setLatitude(double lat) {
        this.lat = lat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getFist_name() {
        return fist_name;
    }

    public void setFist_name(String fist_name) {
        this.fist_name = fist_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
