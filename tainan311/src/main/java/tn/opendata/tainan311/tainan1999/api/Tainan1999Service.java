package tn.opendata.tainan311.tainan1999.api;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;


public interface Tainan1999Service {
    @POST("/ServiceRequestsQuery.aspx")
    Observable<QueryResponse> queryReports(@Body QueryRequest request);
}

