package tn.opendata.tainan311.tn.opendata.tainan311.georeportv2.tn.opendata.tainan311.georeportv2.vo;

/**
 * Created by vincent on 2014/6/6.
 */
public class RequestResponse {
    private String service_request_id;
    private String token;
    private String service_notice;
    private String account_id;

    public RequestResponse() {

    }
    public String getServiceRequestId() {
        return service_request_id;
    }

    public String getToken() {
        return token;
    }

    public String getServiceNotice() {
        return service_notice;
    }

    public String getAccountId() {
        return account_id;
    }
}
