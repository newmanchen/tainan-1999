package tn.opendata.tainan311.tainan1999;

import com.google.common.io.Closer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import tn.opendata.tainan311.tainan1999.rpc.QueryRequest;
import tn.opendata.tainan311.tainan1999.vo.QueryResponse;
import tn.opendata.tainan311.utils.LogUtils;

import static tn.opendata.tainan311.utils.EasyUtil.close;

/**
 * Created by newman on 5/4/15.
 */
public class TainanReport1999 {
    private static final String TAG = TainanReport1999.class.getSimpleName();
    private static final String QUERY_REQUEST_URL = "http://open1999.tainan.gov.tw:82/ServiceRequestsQuery.aspx";
    private static final String ADD_REQUEST_URL = "http://open1999.tainan.gov.tw:82/ServiceRequestAdd.aspx";

    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public static ListenableFuture<List<QueryResponse>> executor(final String entity) {
        return executor.submit(new Callable<List<QueryResponse>> () {
            @Override
            public List<QueryResponse> call() throws Exception {
                return queryRequestResponse(new URL(QUERY_REQUEST_URL), entity);
            }
        });
    }

    private static List<QueryResponse> queryRequestResponse(URL url, String entityXML) {
        Closer closer = Closer.create();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url.toURI());
            post.setEntity(new StringEntity(entityXML));

            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            if (status != 200 && status != 201) {
                //TODO enhancement : error handling
                throw new RuntimeException("Failed : HTTP error code : " + status);
            }
//            saveToFile(response.getEntity().getContent());
            return QueryRequest.onResponse(closer.register(response.getEntity().getContent()));
        } catch (Throwable e) {
            LogUtils.w(TAG, e.getMessage(), e);
            try {
                closer.rethrow(e);
            } catch (IOException e1) {
                LogUtils.w(TAG, e1.getMessage(), e1);
            }
            return null;
        } finally {
            close(closer);
        }
    }

    //TODO add request
}