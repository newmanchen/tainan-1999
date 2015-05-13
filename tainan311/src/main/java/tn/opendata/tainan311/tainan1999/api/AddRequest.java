package tn.opendata.tainan311.tainan1999.api;

import android.os.Build;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

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
    @Element
    private String city_id; // 城市識別碼
    @Element
    private String area; // 行政區
    @Element
    private String address_string; // 地點
    @Element(required=false)
    private String lat; // 緯度(WGS84)
    @Element(required=false,name="long")
    private String lng; // 經度(WGS84)
    @Element(required=false)
    private String email; // E-MAIL
    @Element(required=false)
    private String device_id; // 僅用於行動裝置
    @Element
    private String name; // 姓名
    @Element
    private String phone; // 電話
    @Element
    private String service_name;  // 案件類型
    @Element
    private String subproject; // 案件事項
    @Element
    private String description; // 案件內容
    @Element(required=false)
    private String pictures; // 上傳照片
    @Element(required=false)
    private String picture; // 照片
    @Element(required=false,name="description")
    private String description_pic; // 照片描述
    @Element(required=false)
    private String fileName; // 檔案名稱, 需含副檔名且檔案類型限制JPG
    @Element(required=false)
    private String file; // 檔案資料

    public AddRequest(String city_id, String area, String address_string, String lat, String lng, String email, String device_id, String name, String phone, String service_name, String subproject, String description, String pictures, String picture, String description_pic, String fileName, String file) {
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
        this.picture = picture;
        this.description_pic = description_pic;
        this.fileName = fileName;
        this.file = file;
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

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription_pic() {
        return description_pic;
    }

    public void setDescription_pic(String description_pic) {
        this.description_pic = description_pic;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
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
        private String pictures = null;
        private String picture = null;
        private String description_pic = null;
        private String fileName = null;
        private String file = null;

        public Builder create() { new Builder(); }

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

        public Builder setPictures(String pictures) {
            this.pictures = pictures;
            return this;
        }

        public Builder setPicture(String picture) {
            this.picture = picture;
            return this;
        }

        public Builder setDescription_pic(String description_pic) {
            this.description_pic = description_pic;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setFile(String file) {
            this.file = file;
            return this;
        }

        public AddRequest build() {
            return new AddRequest()
        }
    }
}
