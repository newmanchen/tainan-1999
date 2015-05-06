package tn.opendata.tainan311.tainan1999.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by newman on 5/4/15.
 */
public class QueryResponse implements Parcelable {
    private String service_request_id; // 案件編號
    private String requested_datetime; // 反映日期
    private String status; // 案件狀態
    private String keyword; // 案件描述
    private String area; // 行政區
    private String service_name; // 案件類型
    private String agency; // 業管單位
    private String subproject; // 案件事項
    private String description_request; // 案件內容
    private String address_string; // 地點
    private String latitude; // 緯度
    private String longitude; // 經度
    private String service_notice; // 服務案件說明
    private String updated_datetime; // 結案日期
    private String expected_datetime; // 預計完成日期
    private List<Picture> pictures;

    public QueryResponse() {}

    public String getService_request_id() {
        return service_request_id;
    }

    public void setService_request_id(String service_request_id) {
        this.service_request_id = service_request_id;
    }

    public String getRequested_datetime() {
        return requested_datetime;
    }

    public void setRequested_datetime(String requested_datetime) {
        this.requested_datetime = requested_datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getSubproject() {
        return subproject;
    }

    public void setSubproject(String subproject) {
        this.subproject = subproject;
    }

    public String getDescription_request() {
        return description_request;
    }

    public void setDescription_request(String description_request) {
        this.description_request = description_request;
    }

    public String getAddress_string() {
        return address_string;
    }

    public void setAddress_string(String address_string) {
        this.address_string = address_string;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getService_notice() {
        return service_notice;
    }

    public void setService_notice(String service_notice) {
        this.service_notice = service_notice;
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

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public static final Parcelable.Creator<QueryResponse> CREATOR = new Parcelable.Creator<QueryResponse>() {
        public QueryResponse createFromParcel(Parcel in) {
            return new QueryResponse(in);
        }
        public QueryResponse[] newArray(int size) {
            return new QueryResponse[size];
        }
    };

    private QueryResponse(Parcel in) {
        service_request_id = in.readString();
        requested_datetime = in.readString();
        status = in.readString();
        keyword = in.readString();
        area = in.readString();
        service_name = in.readString();
        agency = in.readString();
        subproject = in.readString();
        description_request = in.readString();
        address_string = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        service_notice = in.readString();
        updated_datetime = in.readString();
        expected_datetime = in.readString();
        pictures = Lists.newArrayList();
        in.readTypedList(pictures, Picture.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(service_request_id);
        parcel.writeString(requested_datetime);
        parcel.writeString(status);
        parcel.writeString(keyword);
        parcel.writeString(area);
        parcel.writeString(service_name);
        parcel.writeString(agency);
        parcel.writeString(subproject);
        parcel.writeString(description_request);
        parcel.writeString(address_string);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(service_notice);
        parcel.writeString(updated_datetime);
        parcel.writeString(expected_datetime);
        parcel.writeTypedList(pictures);
    }

    public static class Picture implements Parcelable {
        private String description_pic; // 照片描述
        private String fileName; // 檔案名稱
        //TODO to transform from input stream to blob
//        private String file = "file"; // 檔案資料

        public Picture() {}

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDescription_pic() {
            return description_pic;
        }

        public void setDescription_pic(String description_pic) {
            this.description_pic = description_pic;
        }

        public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
            public Picture createFromParcel(Parcel in) {
                return new Picture(in);
            }
            public Picture[] newArray(int size) {
                return new Picture[size];
            }
        };

        private Picture(Parcel in) {
            description_pic = in.readString();
            fileName = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(description_pic);
            parcel.writeString(fileName);
        }
    }
}
