package tn.opendata.tainan311.tainan1999.api;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by newman on 5/12/15.
 *
 * 1.必填欄位：city_id、area、address_string、name、phone、service_name、subproject、description。
 *
 * 2.service_name：可新增條件【違規停車、路燈故障、噪音舉發、騎樓舉發、道路維修、交通運輸、髒亂及汙染、民生管線、動物救援】。
 *
 * 3.subproject：參考案件事項對應案件內容表。
 *
 * 4.pictures：最多三筆，檔案大小總和限制3MB以下。
 *
 * 5.fileName：需含副檔名且檔案類型限制JPG。
 *
 * 6.file：byte格式。
 */
@Root(name="root")
public class AddRequest {
    @Element(data=true)
    private String city_id; // 城市識別碼
    @Element(data=true)
    private String area; // 行政區
    @Element(data=true)
    private String address_string; // 地點
    @Element(required=false, data=true)
    private String lat; // 緯度(WGS84)
    @Element(required=false, name="long", data=true)
    private String lng; // 經度(WGS84)
    @Element(required=false, data=true)
    private String email; // E-MAIL
    @Element(required=false, data=true)
    private String device_id; // 僅用於行動裝置
    @Element(data=true)
    private String name; // 姓名
    @Element(data=true)
    private String phone; // 電話
    @Element(data=true)
    private String service_name;  // 案件類型
    @Element(data=true)
    private String subproject; // 案件事項
    @Element(data=true)
    private String description; // 案件內容
    @ElementList(required=false)
    private List<AddPicture> pictures; // 上傳照片

    public AddRequest(String city_id, String area, String address_string, String lat, String lng, String email, String device_id, String name, String phone, String service_name, String subproject, String description, List<AddPicture> pictures) {
        this.city_id = city_id;
        this.area = area;
        this.address_string = address_string;
        this.lat = lat;
        this.lng = lng;
        this.email = email;
        this.device_id = device_id;
        this.name = name;
        this.phone = phone;
        this.service_name = service_name;
        this.subproject = subproject;
        this.description = description;
        this.pictures = pictures;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress_string() {
        return address_string;
    }

    public void setAddress_string(String address_string) {
        this.address_string = address_string;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getSubproject() {
        return subproject;
    }

    public void setSubproject(String subproject) {
        this.subproject = subproject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AddPicture> getPictures() {
        return pictures;
    }

    public void setPictures(List<AddPicture> pictures) {
        this.pictures = pictures;
    }

    public static class Builder {
        private String city_id = null;
        private String area = null;
        private String address_string = null;
        private String lat = null;
        private String lng = null;
        private String email = null;
        private String device_id = null;
        private String name = null;
        private String phone = null;
        private String service_name = null;
        private String subproject = null;
        private String description = null;
        private List<AddPicture> pictures = null;

        public static Builder create() { return new Builder(); }

        public Builder setCity_id(String city_id) {
            this.city_id = city_id;
            return this;
        }

        public Builder setArea(String area) {
            this.area = area;
            return this;
        }

        public Builder setAddress_string(String address_string) {
            this.address_string = address_string;
            return this;
        }

        public Builder setLat(String lat) {
            this.lat = lat;
            return this;
        }

        public Builder setLng(String lng) {
            this.lng = lng;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setDevice_id(String device_id) {
            this.device_id = device_id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setService_name(String service_name) {
            this.service_name = service_name;
            return this;
        }

        public Builder setSubproject(String subproject) {
            this.subproject = subproject;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setPictures(List<AddPicture> pictures) {
            this.pictures = pictures;
            return this;
        }

        public AddRequest build() {
            return new AddRequest(city_id, area, address_string, lat, lng, email, device_id, name, phone, service_name, subproject, description, pictures);
        }
    }
}
