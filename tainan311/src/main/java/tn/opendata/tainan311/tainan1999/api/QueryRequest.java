package tn.opendata.tainan311.tainan1999.api;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.collect.Lists;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * 1.service_request_id、service_name、start_date、end_date及status擇一查詢。
 *
 * 2.service_request_id:條件最多1000筆。
 *
 * 3.service_name:條件最多1000筆，可查詢條件【違規停車、路燈故障、噪音舉發、騎樓舉發、道路維修、交通運輸、髒亂及汙染、民生管線、動物救援】。
 *
 * 4.status:可查詢條件【處理中、已完成】。
 */

@Root(name="root")
public class QueryRequest {
    @Element
    private String city_id; // 城市識別碼
    @Element(required=false)
    private String service_request_id; // 案件編號, 多筆以逗號(,)分隔
    @Element(required=false)
    private String service_name;  // 案件類型, 多筆以逗號(,)分隔
    @Element
    private String start_date;   // 反映開始時間
    @Element
    private String end_date;  // 反映結束時間
    @Element(required=false)
    private String status;  // 案件狀態

    QueryRequest(String city_id, String end_date, String service_name, String service_request_id, String start_date, String status) {
        this.city_id = city_id;
        this.end_date = end_date;
        this.service_name = service_name;
        this.service_request_id = service_request_id;
        this.start_date = start_date;
        this.status = status;
    }

    public String getCity_id() {
        return city_id;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getService_name() {
        return service_name;
    }

    public String getService_request_id() {
        return service_request_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getStatus() {
        return status;
    }
}