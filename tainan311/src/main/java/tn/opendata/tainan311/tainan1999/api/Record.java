package tn.opendata.tainan311.tainan1999.api;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.collect.Lists;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
public class Record implements Parcelable{
    @Element private String service_request_id; //案件編號
    @Element private String requested_datetime; //反映日期

    @Element(required=false)
    private String status; //案件狀態
    @Element private String keyword; //案件描述
    @Element private String area; //行政區
    @Element(required=false)
    private String service_name; //案件類型
    @Element private String agency; //業管單位
    @Element(required=false)
    private String subproject; //案件事項
    @Element private String description; //案件內容
    @Element(required=false)
    private String address_string; //地點
    @Element(required=false)
    private double lat; //緯度
    @Element(required=false,name="long")
    private double lng; //經度
    @Element(required=false)
    private String service_notice; //服務案件說明
    @Element(required=false)
    private String updated_datetime; //結案日期
    @Element(required=false)
    private String expected_datetime; //預計完成日期
    @ElementList(name="Pictures", required=false)
    private List<Picture> pictures;    //多筆處理前照片

    public Record(){}

    public String getAddress_string() {
        return address_string;
    }

    public String getAgency() {
        return agency;
    }

    public String getArea() {
        return area;
    }

    public String getDescription() {
        return description;
    }

    public String getExpected_datetime() {
        return expected_datetime;
    }

    public String getKeyword() {
        return keyword;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public String getRequested_datetime() {
        return requested_datetime;
    }

    public String getService_name() {
        return service_name;
    }

    public String getService_notice() {
        return service_notice;
    }

    public String getService_request_id() {
        return service_request_id;
    }

    public String getStatus() {
        return status;
    }

    public String getSubproject() {
        return subproject;
    }

    public String getUpdated_datetime() {
        return updated_datetime;
    }

    public static final Parcelable.Creator<Record> CREATOR = new Parcelable.Creator<Record>() {
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    private Record(Parcel in) {
        service_request_id = in.readString();
        requested_datetime = in.readString();
        status = in.readString();
        keyword = in.readString();
        area = in.readString();
        service_name = in.readString();
        agency = in.readString();
        subproject = in.readString();
        description = in.readString();
        address_string = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
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
        parcel.writeString(description);
        parcel.writeString(address_string);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeString(service_notice);
        parcel.writeString(updated_datetime);
        parcel.writeString(expected_datetime);
        parcel.writeTypedList(pictures);
    }


}
