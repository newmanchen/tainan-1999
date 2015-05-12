package tn.opendata.tainan311.tainan1999;

import android.os.Environment;

import com.google.common.io.Closer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import tn.opendata.tainan311.tainan1999.rpc.AddRequest;
import tn.opendata.tainan311.tainan1999.vo.AddResponse;
import tn.opendata.tainan311.utils.EasyUtil;
import tn.opendata.tainan311.utils.LogUtils;

import static tn.opendata.tainan311.utils.EasyUtil.close;

/**
 * Created by newman on 5/4/15.
 */
public class TainanReport1999 {
    private static final String TAG = TainanReport1999.class.getSimpleName();
    private static final String QUERY_REQUEST_URL = "http://open1999.tainan.gov.tw:82/ServiceRequestsQuery.aspx";
    private static final String ADD_REQUEST_URL = "http://open1999.tainan.gov.tw:82/ServiceRequestAdd.aspx";
    private static final String TESTING_POST_URL = "http://posttestserver.com/post.php?dir=newman";

    private static final ListeningExecutorService executor = MoreExecutors
            .listeningDecorator(Executors.newCachedThreadPool());


    public static ListenableFuture<AddResponse> executeAdd(final String entity) {
        return executor.submit(new Callable<AddResponse>() {
            @Override
            public AddResponse call() throws Exception {
                return addRequestResponse(new URL(ADD_REQUEST_URL), entity);
            }
        });
    }

    private static AddResponse addRequestResponse(URL url, String entityXML) {
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
            return AddRequest.onResponse(closer.register(response.getEntity().getContent()));
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

    public static ListenableFuture<AddResponse> executeAddToTest(final String entity) {
        return executor.submit(new Callable<AddResponse>() {
            @Override
            public AddResponse call() throws Exception {
                return addRequestResponseFromTest(new URL(TESTING_POST_URL), entity);
            }
        });
    }

    private static AddResponse addRequestResponseFromTest(URL url, String entityXML) {
        Closer closer = Closer.create();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url.toURI());
            post.setEntity(new StringEntity(entityXML));
            post.setHeader("Content-type", "application/xml");
            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            if (status != 200 && status != 201) {
                //TODO enhancement : error handling
                throw new RuntimeException("Failed : HTTP error code : " + status);
            }
            saveToFile(response.getEntity().getContent());
//            return AddRequest.onResponse(closer.register(response.getEntity().getContent()));
            return new AddResponse();
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

    private static void saveToFile(InputStream is) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path = path + "Log.xml";

        FileOutputStream stream = null;
        BufferedInputStream br = null;
        try {
            stream = new FileOutputStream(path);
            br = new BufferedInputStream(is);

            byte[] sByte = new byte[8192];
            int c;
            while ((c = br.read(sByte)) > 0) {
                stream.write(sByte, 0, c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            EasyUtil.close(stream);
            EasyUtil.close(br);
        }
    }
}