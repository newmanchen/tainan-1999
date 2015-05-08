package tn.opendata.tainan311.tainan1999.rpc;

import android.util.Xml;

import com.google.common.collect.Lists;

import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.tainan1999.util.TainanRequestXmlUtils;
import tn.opendata.tainan311.tainan1999.vo.AddResponse;
import tn.opendata.tainan311.utils.EasyUtil;
import tn.opendata.tainan311.utils.LogUtils;

/**
 * Created by newman on 5/4/15.
 */
public class AddRequest extends BaseRequest {
    private static final String TAG = AddRequest.class.getSimpleName();
    //-------------------------------------Response------------------------------------------------------
    private static final String returncode = "returncode"; // 0表示成功，其它表示不成功
    private static final String description_fail = "description"; // 錯誤說明【操作失敗才會顯示】
    private static final String stacktrace = "stacktrace"; // 錯誤Log【操作失敗才會顯示】
    private static final String token = "token"; // token
    private static final String count = "count"; // 結果筆數
    private static final String service_request_id = "service_request_id"; // 案件編號
    private static final String service_notice = "service_notice"; // 服務案件說明

    private static AddResponse readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
        AddResponse add = new AddResponse();

        parser.require(XmlPullParser.START_TAG, ns, TAG_ROOT);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the record tag
            if (name.equals(returncode)) {
                String returnCode = readData(parser, returncode);  // TODO error handling, 0 is ok; otherwise, has error
                LogUtils.d(TAG, "return code : ", returnCode);
            } else if (name.equals(count)) {
                String recordCount = readData(parser, count);
                LogUtils.d(TAG, "record count : ", recordCount);
            } else if (name.equals(description_fail)) {
                String failDescription = readData(parser, description_fail);
                LogUtils.d(TAG, "failDescription : ", failDescription);
            } else if (name.equals(stacktrace)) {
                String stackTrace = readData(parser, stacktrace);
                LogUtils.d(TAG, "stackTrace : ", stackTrace);
            } else if (name.equals(token)) {
                add.setToken(readData(parser, token));
            } else if (name.equals(service_request_id)) {
                add.setService_request_id(readData(parser, service_request_id));
            } else if (name.equals(service_notice)) {
                add.setService_notice(readData(parser, service_notice));
            } else {
                skip(parser);
            }
        }
        return add;
    }

    /**
     *
     * @param in input stream from query result
     * @return a list of query result as a vo
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static AddResponse onResponse(InputStream in) throws XmlPullParserException, IOException{
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readResponse(parser);
        } finally {
            EasyUtil.close(in);
        }
    }

    //-------------------------------------Request------------------------------------------------------
    private static final String TAG_CITY_ID = "city_id"; // 城市識別碼
    private static final String TAG_AREA = "area"; // 行政區
    private static final String TAG_ADDRESS_STRING = "address_string"; // 地點
    private static final String TAG_LATI = "lat"; // 緯度(WGS84)
    private static final String TAG_LONG = "long"; // 經度(WGS84)
    private static final String TAG_EMAIL = "email"; // E-MAIL
    private static final String TAG_DEVICE_ID = "device_id"; // 僅用於行動裝置
    private static final String TAG_NAME = "name"; // 姓名
    private static final String TAG_PHONE = "phone"; // 電話
    private static final String TAG_SERVICE_NAME = "service_name";  // 案件類型
    private static final String TAG_SUBPROJECT = "subproject"; // 案件事項
    private static final String TAG_DESCRIPTION_REQUEST = "description"; // 案件內容
    public static final String TAG_PICTURES = "pictures"; // 上傳照片
    public static final String TAG_PICTURE = "picture"; // 照片
    private static final String TAG_DESCRIPTION_PIC = "description"; // 照片描述
    private static final String TAG_FILENAME = "fileName"; // 檔案名稱, 需含副檔名且檔案類型限制JPG
    public static final String TAG_FILE = "file"; // 檔案資料

    /**
     * city_id、area、address_string、name、phone、service_name、subproject、description 必填
     */
    public static class Builder {
        private List<BasicNameValuePair> list = Lists.newArrayList();
        private List<List<BasicNameValuePair>> pics = Lists.newArrayList();

        private Builder() {
            list = Lists.newArrayList();
            list.add(new BasicNameValuePair(TAG_CITY_ID, TainanConstant.CITY_ID)); //add by default
            pics = Lists.newArrayList();
        }

        public static Builder create() {
            return new Builder();
        }

        /**
         * MUST HAVE
         * @param cityId
         * @return Builder
         */
        public Builder setCityId(String cityId) {
            list.add(new BasicNameValuePair(TAG_CITY_ID, cityId));
            return this;
        }

        /**
         * MUST HAVE
         * @param area area of the reported issue, eg. 東區
         * @return
         */
        public Builder setArea(String area) {
            list.add(new BasicNameValuePair(TAG_AREA, area));
            return this;
        }

        /**
         * MUST HAVE
         * @param address
         * @return
         */
        public Builder setAddressString(String address) {
            list.add(new BasicNameValuePair(TAG_ADDRESS_STRING, address));
            return this;
        }

        /**
         * MUST HAVE
         * @param name name of reporter
         * @return
         */
        public Builder setName(String name) {
            list.add(new BasicNameValuePair(TAG_NAME, name));
            return this;
        }

        /**
         * MUST HAVE
         * @param phone phone of reporter
         * @return
         */
        public Builder setPhone(String phone) {
            list.add(new BasicNameValuePair(TAG_PHONE, phone));
            return this;
        }

        /**
         * MUST HAVE
         * @param serviceName
         * @return Builder
         * @see TainanConstant
         */
        public Builder setServiceName(String serviceName) {
            list.add(new BasicNameValuePair(TAG_SERVICE_NAME, serviceName));
            return this;
        }

        /**
         * MUST HAVE
         * @param subProject sub-project of service_name
         * @return
         * @see TainanConstant
         */
        public Builder setSubProject(String subProject) {
            list.add(new BasicNameValuePair(TAG_SUBPROJECT, subProject));
            return this;
        }

        /**
         * MUST HAVE
         * @param descriptionRequest description of the request
         * @return
         */
        public Builder setDescription(String descriptionRequest) {
            list.add(new BasicNameValuePair(TAG_DESCRIPTION_REQUEST, descriptionRequest));
            return this;
        }

        /**
         * this is automatically added from mobile
         * @param latitude
         * @return
         */
        public Builder setLatitude(String latitude) {
            list.add(new BasicNameValuePair(TAG_LATI, latitude));
            return this;
        }

        /**
         * this is automatically added from mobile
         * @param longitude
         * @return
         */
        public Builder setLongitude(String longitude) {
            list.add(new BasicNameValuePair(TAG_LONG, longitude));
            return this;
        }

        /**
         * this is OPTIONAL field
         * @param email email of reporter
         * @return
         */
        public Builder setEmail(String email) {
            list.add(new BasicNameValuePair(TAG_EMAIL, email));
            return this;
        }

        /**
         * this is OPTIONAL field, currently the device id would not be submit due to sensitive data
         * @param deviceId maybe use android.
         * @return
         * @see android.os.Build.MODEL
         */
        public Builder setDeviceId(String deviceId) {
            list.add(new BasicNameValuePair(TAG_DEVICE_ID, deviceId));
            return this;
        }

        /**
         * this is OPTIONAL field, upload limit with 3 pics/3 MB in total
         * @param pic picture with binary file/filename/description , binary file needs to encode with base64
         * @return
         * @see tn.opendata.tainan311.tainan1999.rpc.AddRequest.Picture
         */
        public Builder setPicture(Picture pic) {
            List<BasicNameValuePair> picNVP = Lists.newArrayList();
            picNVP.add(new BasicNameValuePair(TAG_DESCRIPTION_PIC, pic.getDescription_pic()));
            picNVP.add(new BasicNameValuePair(TAG_FILE, pic.getFile()));
            picNVP.add(new BasicNameValuePair(TAG_FILENAME, pic.getFileName()));
            pics.add(picNVP);
            return this;
        }

        public String build() {
            return TainanRequestXmlUtils.getAddRequestXML(list, pics);
        }
    }

    public static class Picture {
        private String description_pic; // 照片描述
        private String fileName; // 檔案名稱, file extension is limited to .JPG
        private String file = "file"; // 檔案資料, encoded with base64

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

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }
    }
}
