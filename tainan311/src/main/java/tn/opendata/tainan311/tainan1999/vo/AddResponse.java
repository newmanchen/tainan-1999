package tn.opendata.tainan311.tainan1999.vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by newman on 5/4/15.
 */
public class AddResponse implements Parcelable {
    private String token = "token"; // token
    private String service_request_id = "service_request_id"; // 案件編號
    private String service_notice = "service_notice"; // 服務案件說明
    public AddResponse() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getService_request_id() {
        return service_request_id;
    }

    public void setService_request_id(String service_request_id) {
        this.service_request_id = service_request_id;
    }

    public String getService_notice() {
        return service_notice;
    }

    public void setService_notice(String service_notice) {
        this.service_notice = service_notice;
    }

    public static final Parcelable.Creator<AddResponse> CREATOR = new Parcelable.Creator<AddResponse>() {
        public AddResponse createFromParcel(Parcel in) {
            return new AddResponse(in);
        }
        public AddResponse[] newArray(int size) {
            return new AddResponse[size];
        }
    };

    private AddResponse(Parcel in) {
        token = in.readString();
        service_request_id = in.readString();
        service_notice = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        token = parcel.readString();
        service_request_id = parcel.readString();
        service_notice = parcel.readString();
    }
}
