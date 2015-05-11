package tn.opendata.tainan311.tainan1999.rpc;

import android.content.Context;
import android.util.Xml;

import com.google.common.collect.Lists;

import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.tainan1999.util.TainanRequestXmlUtils;
import tn.opendata.tainan311.tainan1999.vo.QueryResponse;
import tn.opendata.tainan311.utils.Base64Utils;
import tn.opendata.tainan311.utils.EasyUtil;
import tn.opendata.tainan311.utils.LogUtils;

/**
 * Created by newman on 5/4/15.
 */
public class QueryRequest extends BaseRequest {
    private static final String TAG = QueryRequest.class.getSimpleName();
    //-------------------------------------Response------------------------------------------------------
    private static final String returncode = "returncode"; // 0表示成功，其它表示不成功
    private static final String description_fail = "description"; // 錯誤說明【操作失敗才會顯示】
    private static final String stacktrace = "stacktrace"; // 錯誤Log【操作失敗才會顯示】
    private static final String count = "count"; // 結果筆數
    private static final String records = "records"; // 多筆查詢結果
    private static final String record = "record"; // 每筆資料
    private static final String service_request_id = "service_request_id"; // 案件編號
    private static final String requested_datetime = "requested_datetime"; // 反映日期
    private static final String status = "status"; // 案件狀態
    private static final String keyword = "keyword"; // 案件描述
    private static final String area = "area"; // 行政區
    private static final String service_name = "service_name"; // 案件類型
    private static final String agency = "agency"; // 業管單位
    private static final String subproject = "subproject"; // 案件事項
    private static final String description_request = "description"; // 案件內容
    private static final String address_string = "address_string"; // 地點
    private static final String latitude = "lat"; // 緯度
    private static final String longitude = "long"; // 經度
    private static final String service_notice = "service_notice"; // 服務案件說明
    private static final String updated_datetime = "updated_datetime"; // 結案日期
    private static final String expected_datetime = "expected_datetime"; // 預計完成日期
    private static final String pictures = "Pictures"; // 多筆處理前照片
    private static final String picture = "Picture"; // 每筆處理前照片
    private static final String description_pic = "description"; // 照片描述
    private static final String fileName = "fileName"; // 檔案名稱
    private static final String file = "file"; // 檔案資料

    private static List readResponse(Context context, XmlPullParser parser) throws XmlPullParserException, IOException {
        List responses = Lists.newArrayList();
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
            } else if (name.equals(records)) {
                responses = readRecords(parser, context);
            } else {
                skip(parser);
            }
        }
        LogUtils.d(TAG, "responses size=", responses.size());
        return responses;
    }

    /**
     * read records in "records" tag
     * @param parser
     * @param context
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static List<QueryResponse> readRecords(XmlPullParser parser, Context context) throws XmlPullParserException, IOException {
        List<QueryResponse> qrrList = Lists.newArrayList();
        parser.require(XmlPullParser.START_TAG, ns, records);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(record)) {
                qrrList.add(readRecord(parser, context));
            } else {
                skip(parser);
            }
        }
        return qrrList;
    }

    /**
     * read records in "record" tag
     * @param parser
     * @return return a record as a QueryResponse
     * @throws XmlPullParserException
     * @throws IOException
     */
    private static QueryResponse readRecord(XmlPullParser parser, Context context) throws XmlPullParserException, IOException {
        QueryResponse qrr = new QueryResponse();
        parser.require(XmlPullParser.START_TAG, ns, record);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(service_request_id)) {
                qrr.setService_request_id(readData(parser, service_request_id));
            } else if (name.equals(requested_datetime)) {
                qrr.setRequested_datetime(readData(parser, requested_datetime));
            } else if (name.equals(status)) {
                qrr.setStatus(readData(parser, status));
            } else if (name.equals(keyword)) {
                qrr.setKeyword(readData(parser, keyword));
            } else if (name.equals(area)) {
                qrr.setArea(readData(parser, area));
            } else if (name.equals(service_name)) {
                qrr.setService_name(readData(parser, service_name));
            } else if (name.equals(agency)) {
                qrr.setAgency(readData(parser, agency));
            } else if (name.equals(subproject)) {
                qrr.setSubproject(readData(parser, subproject));
            } else if (name.equals(description_request)) {
                qrr.setDescription_request(readData(parser, description_request));
            } else if (name.equals(address_string)) {
                qrr.setAddress_string(readData(parser, address_string));
            } else if (name.equals(latitude)) {
                qrr.setLatitude(readData(parser, latitude));
            } else if (name.equals(longitude)) {
                qrr.setLongitude(readData(parser, longitude));
            } else if (name.equals(service_notice)) {
                qrr.setService_notice(readData(parser, service_notice));
            } else if (name.equals(updated_datetime)) {
                qrr.setUpdated_datetime(readData(parser, updated_datetime));
            } else if (name.equals(expected_datetime)) {
                qrr.setExpected_datetime(readData(parser, expected_datetime));
            } else if (name.equals(pictures)) {
                qrr.setPictures(readPictures(parser, qrr.getService_request_id(), context));
            } else {
                LogUtils.d(TAG, "readRecord::skip this tag : ", name);
                skip(parser);
            }
        }
        return qrr;
    }

    private static List<QueryResponse.Picture> readPictures(XmlPullParser parser, String requestId, Context context) throws IOException, XmlPullParserException {
        List<QueryResponse.Picture> pics = Lists.newArrayList();
        parser.require(XmlPullParser.START_TAG, ns, pictures);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(picture)) {
                pics.add(readPicture(parser, requestId, context));
            } else {
                skip(parser);
            }
        }
        return pics;
    }

    private static QueryResponse.Picture readPicture(XmlPullParser parser, String requestId, Context context) throws IOException, XmlPullParserException {
        QueryResponse.Picture pic = new QueryResponse.Picture();
        parser.require(XmlPullParser.START_TAG, ns, picture);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(description_pic)) {
                pic.setDescription_pic(readData(parser, description_pic));
            } else if (name.equals(fileName)) {
                pic.setFileName(readData(parser, fileName));
            } else if (name.equals(file)) {
                String dataPath = context.getFilesDir().toString() + "/pic/";
                File folder = new File(dataPath);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                dataPath += requestId + ".jpg";
                Base64Utils.decodeBase64(readData(parser, file), dataPath);
                //TODO FIXME there are most 3 pics from server
            } else {
                LogUtils.d(TAG, "readPicture::skip this tag : ", name);
                skip(parser);
            }
        }
        return pic;
    }

    /**
     *
     * @param in input stream from query result
     * @return a list of query result as a vo
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static List<QueryResponse> onResponse(Context context, InputStream in) throws XmlPullParserException, IOException{
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readResponse(context, parser);
        } finally {
            EasyUtil.close(in);
        }
    }

    //-------------------------------------Request------------------------------------------------------
    public static final String TAG_ROOT = "root";
    private static final String TAG_CITY_ID = "city_id"; // 城市識別碼
    private static final String TAG_SERVICE_REQUEST_ID = "service_request_id"; // 案件編號, 多筆以逗號(,)分隔
    private static final String TAG_SERVICE_NAME = "service_name";  // 案件類型, 多筆以逗號(,)分隔
    private static final String TAG_START_DATE = "start_date";   // 反映開始時間
    private static final String TAG_END_DATE = "end_date";  // 反映結束時間
    private static final String TAG_STATUS = "status";  // 案件狀態

    public static class Builder {
        private List<BasicNameValuePair> list = Lists.newArrayList();

        private Builder() {
            list = Lists.newArrayList();
            list.add(new BasicNameValuePair(TAG_CITY_ID, TainanConstant.CITY_ID)); //add by default
        }

        public static Builder create() { return new Builder();
        }

        /**
         *
         * @param cityId
         * @return Builder
         */
        public Builder setCityId(String cityId) {
            list.add(new BasicNameValuePair(TAG_CITY_ID, cityId));
            return this;
        }

        /**
         *
         * @param serviceRequestId for multiple input, user "," to separate eg. UN201505020008, UN201505020020
         * @return Builder
         */
        public Builder setServiceRequestId(String serviceRequestId) {
            list.add(new BasicNameValuePair(TAG_SERVICE_REQUEST_ID, serviceRequestId));
            return this;
        }

        /**
         *
         * @param serviceName for multiple input, user "," to separate eg. 違規停車,路燈故障,噪音舉發
         * @return Builder
         * @see TainanConstant
         */
        public Builder setServiceName(String serviceName) {
            list.add(new BasicNameValuePair(TAG_SERVICE_NAME, serviceName));
            return this;
        }

        /**
         *
         * @param startTime date time format yyyy-MM-dd HH:mm:ss
         * @return Builder
         */
        public Builder setStartTime(String startTime) {
            list.add(new BasicNameValuePair(TAG_START_DATE, startTime));
            return this;
        }

        /**
         *
         * @param endTime date time format yyyy-MM-dd HH:mm:ss
         * @return Builder
         */
        public Builder setEndTime(String endTime) {
            list.add(new BasicNameValuePair(TAG_END_DATE, endTime));
            return this;
        }

        /**
         *
         * @param status 處理中 or 已完成
         * @return Builder
         * @see TainanConstant
         */
        public Builder setStatus(String status) {
            list.add(new BasicNameValuePair(TAG_STATUS, status));
            return this;
        }

        public String build() {
            return TainanRequestXmlUtils.getXML(list);
        }
    }
}