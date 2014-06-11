package tn.opendata.tainan311.georeportv2.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by vincent on 2014/6/6.
 */
public class Request implements Parcelable {
    public static final String STATUS_OPEN = "open";
    public static final String STATUS_CLOSE = "closed";

    private String service_request_id;
    private String title;
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

    private String lon;
    private String media_url;

    public String getService_request_id() {
        return service_request_id;
    }

    public void setService_request_id(String service_request_id) {
        this.service_request_id = service_request_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_notes() {
        return status_notes;
    }

    public void setStatus_notes(String status_notes) {
        this.status_notes = status_notes;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_code() {
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Recipient> getAgency_responsible() {
        return agency_responsible;
    }

    public void setAgency_responsible(List<Recipient> agency_responsible) {
        this.agency_responsible = agency_responsible;
    }

    public String getService_notice() {
        return service_notice;
    }

    public void setService_notice(String service_notice) {
        this.service_notice = service_notice;
    }

    public String getRequested_datetime() {
        return requested_datetime;
    }

    public void setRequested_datetime(String requested_datetime) {
        this.requested_datetime = requested_datetime;
    }

    public String getUpdated_datetime() {
        return updated_datetime;
    }

    public void setUpdated_datetime(String updated_datetime) {
        this.updated_datetime = updated_datetime;
    }

    public String getExpected_datetime() {
        return expected_datetime;
    }

    public void setExpected_datetime(String expected_datetime) {
        this.expected_datetime = expected_datetime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_id() {
        return address_id;
    }

    public void setAddress_id(String address_id) {
        this.address_id = address_id;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getMedia_url() {
        return media_url;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    private Request(Parcel in) {
        service_request_id = in.readString();
        title = in.readString();
        status = in.readString();
        status_notes = in.readString();
        service_name = in.readString();
        service_code = in.readString();
        description = in.readString();
        agency_responsible = Lists.newArrayList();
        in.readTypedList(agency_responsible, Recipient.CREATOR);
        service_notice = in.readString();
        requested_datetime = in.readString();
        updated_datetime = in.readString();
        expected_datetime = in.readString();
        address = in.readString();
        address_id = in.readString();
        zipcode = in.readString();
        lat = in.readString();
        lon = in.readString();
        media_url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(service_request_id);
        parcel.writeString(title);
        parcel.writeString(status);
        parcel.writeString(status_notes);
        parcel.writeString(service_name);
        parcel.writeString(service_code);
        parcel.writeString(description);
        parcel.writeTypedList(agency_responsible);
        parcel.writeString(service_notice);
        parcel.writeString(requested_datetime);
        parcel.writeString(updated_datetime);
        parcel.writeString(expected_datetime);
        parcel.writeString(address);
        parcel.writeString(address_id);
        parcel.writeString(zipcode);
        parcel.writeString(lat);
        parcel.writeString(lon);
        parcel.writeString(media_url);
    }

    private static class Recipient implements Parcelable {
        private String recipient;

        public String getRecipient() {
            return recipient;
        }

        public static final Parcelable.Creator<Recipient> CREATOR = new Parcelable.Creator<Recipient>() {
            public Recipient createFromParcel(Parcel in) {
                return new Recipient(in);
            }

            public Recipient[] newArray(int size) {
                return new Recipient[size];
            }
        };

        private Recipient(Parcel in) {
            recipient = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(recipient);
        }
    }

    public Request(){

    }

    @Override
    public String toString() {
        return service_request_id + "@" + status;
    }
}
