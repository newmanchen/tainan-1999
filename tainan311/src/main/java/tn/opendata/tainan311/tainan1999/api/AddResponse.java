package tn.opendata.tainan311.tainan1999.api;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by newman on 5/13/15.
 */
@Root(name="root")
public class AddResponse {
    @Element(required=false)
    private int returncode; // 0表示成功，其它表示不成功
    @Element(required=false)
    private String description; // 錯誤說明【操作失敗才會顯示】
    @Element(required=false)
    private String stacktrace; // 錯誤Log【操作失敗才會顯示】
    @Element(required=false)
    private String token; // token
    @Element(required=false)
    private String service_request_id; // 案件編號
    @Element(required=false)
    private String service_notice; // 服務案件說明
    @Element(required=false)
    private String count; // 結果筆數

    public String getToken() {
        return token;
    }

    public String getService_request_id() {
        return service_request_id;
    }

    public String getService_notice() {
        return service_notice;
    }

    public int getReturncode() {
        return returncode;
    }

    public String getDescription() {
        return description;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public String getCount() {
        return count;
    }
}