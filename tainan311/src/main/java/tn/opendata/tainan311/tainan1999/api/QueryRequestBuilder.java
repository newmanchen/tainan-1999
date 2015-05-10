package tn.opendata.tainan311.tainan1999.api;

public class QueryRequestBuilder {
    private String city_id = null;
    private String end_date = null;
    private String service_name = null;
    private String service_request_id = null;
    private String start_date = null;
    private String status = null;

    public QueryRequestBuilder setCityId(String city_id) {
        this.city_id = city_id;
        return this;
    }

    public QueryRequestBuilder setEndDate(String end_date) {
        this.end_date = end_date;
        return this;
    }

    public QueryRequestBuilder setServiceName(String service_name) {
        this.service_name = service_name;
        return this;
    }

    public QueryRequestBuilder setServiceRequestId(String service_request_id) {
        this.service_request_id = service_request_id;
        return this;
    }

    public QueryRequestBuilder setStartDate(String start_date) {
        this.start_date = start_date;
        return this;
    }

    public QueryRequestBuilder setStatus(String status) {
        this.status = status;
        return this;
    }

    public QueryRequest build() {
        return new QueryRequest(city_id, end_date, service_name, service_request_id, start_date, status);
    }
}