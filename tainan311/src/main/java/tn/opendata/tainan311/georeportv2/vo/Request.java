package tn.opendata.tainan311.georeportv2.vo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by vincent on 2014/6/6.
 */
public class Request {
    private String service_request_id;
    private String status;
    private String status_notes;


    private String service_name;
    private String service_code;
    private String description;
    private List<Recipient> agency_responsible;
    private String service_notice;
    private String requested_datetime;
    private String updated_datetime;
    private String expected_datetime;
    private String address;
    private String address_id;
    private String zipcode;
    private String lat;
    @SerializedName("long")
    private String lon;
    private String media_url;

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getService_code() {
        return service_code;
    }

    public String getDescription() {
        return description;
    }
    private static class Recipient {
        private String recipient;

        public String getRecipient() {
            return recipient;
        }
    }

    public Request(){

    }

    @Override
    public String toString() {
        return service_request_id + "@" + status;
    }
}
