package tn.opendata.tainan311.tn.opendata.tainan311.georeportv2;

import android.text.TextUtils;
import android.util.Log;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closer;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import tn.opendata.tainan311.tn.opendata.tainan311.georeportv2.tn.opendata.tainan311.georeportv2.vo.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ListeningExecutorService;

import static tn.opendata.tainan311.tn.opendata.tainan311.utils.EasyUtil.close;

/**
 * Created by vincent on 2014/6/6.
 */
public class GeoReportV2 {

    private static final String prefix = "http://fixmystreet.tw/open311/v2/";
    private static final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    private static final String PATH_SERVICE_LIST = "services.json";
    private static final String PATH_SERVICE_DEFINITION = "services/%s.json";
    private static final String PATH_REQUESTS = "requests.json";
    private static final String PATH_SERVICE_REQUEST_ID_BY_TOKEN = "requests/%s.json";
    private static final String PATH_SERVICE_BY_REQUEST_ID = "requests/%s.json";

    private static final String DEFAULT_JURISDICTION_ID = "tainan.fixmystreet.tw";

    private GeoReportV2() {
    }

    public static ListenableFuture<List<Service>> getServiceList() {
        return getServiceList(DEFAULT_JURISDICTION_ID);
    }

    public static ListenableFuture<List<Service>> getServiceList(final String id) {
        return executor.submit(new Callable<List<Service>>(){

            @Override
            public List<Service> call() throws Exception {
                String url = prefix + PATH_SERVICE_LIST;
                if (!TextUtils.isEmpty(id)) {
                    Map<String, Object> data = AttributeBuilder.newBuilder().put("jurisdiction_id", id).build();
                    url += "?" + encodeGetParameters(convert(data), HTTP.UTF_8);
                }
                return getServiceListResponse(new URL(url));
            }
        });
    }

    // fix my street has different format...
    @Deprecated
    public static ListenableFuture<ServiceDefinition> getServiceDefinition(final String id, final String service_code) {
        return executor.submit(new Callable<ServiceDefinition>() {
            @Override
            public ServiceDefinition call() throws Exception {
                if ( TextUtils.isEmpty(service_code) ) {
                    throw new RuntimeException("service_code must not be empty.");
                }
                String url = prefix + String.format(PATH_SERVICE_DEFINITION, service_code);
                if (!TextUtils.isEmpty(id)) {
                    Map<String, Object> data = AttributeBuilder.newBuilder().put("jurisdiction_id", id).build();
                    url += "?" + encodeGetParameters(convert(data), HTTP.UTF_8);
                }
                return getServiceDefinitionResponse(new URL(url));
            }
        });
    }

    // fix my street has different format...
    @Deprecated
    public static ListenableFuture<ServiceDefinition> getServiceDefinition(String service_code) {
        return getServiceDefinition(DEFAULT_JURISDICTION_ID, service_code);
    }


    public static ListenableFuture<RequestId> getRequestIdByToken(final String jurisdiction_id, final String token) {
        return executor.submit(new Callable<RequestId>() {
            @Override
            public RequestId call() throws Exception {
                if (TextUtils.isEmpty(token)) {
                    throw new RuntimeException("token must not be empty.");
                }
                String url = prefix + String.format(PATH_SERVICE_REQUEST_ID_BY_TOKEN, token);
                if (!TextUtils.isEmpty(jurisdiction_id)) {
                    Map<String, Object> data = AttributeBuilder.newBuilder().put("jurisdiction_id", jurisdiction_id).build();
                    url += "?" + encodeGetParameters(convert(data), HTTP.UTF_8);
                }
                return getRequestIdResponse(new URL(url));
            }
        });
    }

    public static ListenableFuture<RequestId> getRequestIdByToken(String token) {
        return getRequestIdByToken(DEFAULT_JURISDICTION_ID, token);
    }

    public static ListenableFuture<Service> getServiceByRequestId(final String jurisdiction_id, final String requestId) {
        return executor.submit(new Callable<Service>() {
            @Override
            public Service call() throws Exception {
                if (TextUtils.isEmpty(requestId)) {
                    throw new RuntimeException("token must not be empty.");
                }
                String url = prefix + String.format(PATH_SERVICE_BY_REQUEST_ID, requestId);
                if (!TextUtils.isEmpty(jurisdiction_id)) {
                    Map<String, Object> data = AttributeBuilder.newBuilder().put("jurisdiction_id", jurisdiction_id).build();
                    url += "?" + new UrlEncodedFormEntity(convert(data), HTTP.UTF_8);
                }
                return getServiceByRequestIdResponse(new URL(url));
            }
        });
    }

    public static ListenableFuture<Service> getServiceByRequestId(String requestId) {
        return getServiceByRequestId(DEFAULT_JURISDICTION_ID, requestId);
    }

    private static <K, V> RequestResponse getPostResponse(URL url, Map<K, V> postData) {
        Closer closer = Closer.create();
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url.toURI());

            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            if ( status != 200 && status != 201 ) {
                throw new RuntimeException("Failed : HTTP error code : " + status);
            }

            BufferedReader br = closer.register(new BufferedReader(new InputStreamReader(response.getEntity().getContent())));
            Gson gson = new Gson();
            Type type = new TypeToken<RequestResponse>(){}.getType();

            return gson.fromJson(br, type);
        } catch (Throwable e) {
            try {
                closer.rethrow(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            close(closer);
        }
    }

    // will return null if error occurred

    private static Service getServiceByRequestIdResponse(URL url) {
        Closer closer = Closer.create();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            int status = urlConnection.getResponseCode();

            if ( status != 200 && status != 201 ) {
                throw new RuntimeException("Failed : HTTP error code : " + status);
            }

            BufferedReader br = closer.register(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())));
            Gson gson = new Gson();
            Type type = new TypeToken<Service>(){}.getType();

            return gson.fromJson(br, type);
        } catch (Throwable e) {
            try {
                closer.rethrow(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            close(closer);
        }
    }

    private static RequestId getRequestIdResponse(URL url) {
        Closer closer = Closer.create();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            int status = urlConnection.getResponseCode();

            if ( status != 200 && status != 201 ) {
                throw new RuntimeException("Failed : HTTP error code : " + status);
            }

            BufferedReader br = closer.register(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())));
            Gson gson = new Gson();
            Type type = new TypeToken<RequestId>(){}.getType();

            return gson.fromJson(br, type);
        } catch (Throwable e) {
            try {
                closer.rethrow(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            close(closer);
        }
    }

    private static ServiceDefinition getServiceDefinitionResponse(URL url) {
        Closer closer = Closer.create();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            int status = urlConnection.getResponseCode();

            if ( status != 200 && status != 201 ) {
                throw new RuntimeException("Failed : HTTP error code : " + status);
            }

            BufferedReader br = closer.register(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())));
            Gson gson = new Gson();
            Type type = new TypeToken<ServiceDefinition>(){}.getType();

            return gson.fromJson(br, type);
        } catch (Throwable e) {
            try {
                Log.e("Vincent", e.toString());
                closer.rethrow(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            close(closer);
        }
    }

    private static List<Service> getServiceListResponse(URL url) {
        Closer closer = Closer.create();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            int status = urlConnection.getResponseCode();

            if ( status != 200 && status != 201 ) {
                throw new RuntimeException("Failed : HTTP error code : " + status);
            }

            String data = Json311.fromFixMyStreet(closer.register(urlConnection.getInputStream()));

            Gson gson = new Gson();
            Type type = new TypeToken<List<Service>>() {
            }.getType();

            return gson.fromJson(data, type);
        } catch (Throwable e) {
            try {
                closer.rethrow(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            close(closer);
        }
    }

    private static List<Request> getRequestListResponse(URL url) {
        Closer closer = Closer.create();
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            int status = urlConnection.getResponseCode();

            if ( status != 200 && status != 201 ) {
                throw new RuntimeException("Failed : HTTP error code : " + status);
            }

            String data = Json311.fromFixMyStreet(closer.register(urlConnection.getInputStream()));
            Gson gson = new Gson();
            Type type = new TypeToken<List<Request>>(){}.getType();

            return gson.fromJson(data, type);
        } catch (Throwable e) {
            Log.e("Vincent", e.toString());
            try {
                closer.rethrow(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return Lists.newArrayList();
        } finally {
            close(closer);
        }
    }

    private static class AttributeBuilder {
        private Map<String, Object> attribute;
        AttributeBuilder() {
            attribute = Maps.newHashMap();
        }

        public static AttributeBuilder newBuilder() {
            return new AttributeBuilder();
        }

        public <T> AttributeBuilder put(String key, T obj) {
            attribute.put(key, obj);
            return this;
        }

        public Map<String, Object> build() {
            return Collections.unmodifiableMap(attribute);
        }
    }

    private static List<NameValuePair> convert(Map<String,Object> args){
        List<NameValuePair> params = Lists.newArrayList();
        for(String k:args.keySet()){
            params.add(new BasicNameValuePair(k, String.valueOf(args.get(k))));
        }

        return params;
    }

    private static String encodeGetParameters(List<NameValuePair> list, String charset) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for(NameValuePair pair :list) {
            sb.append(pair.getName());
            sb.append("=");
            sb.append(URLEncoder.encode(pair.getValue(), charset));
            sb.append("&");
        }
        return sb.toString();
    }

    public static class QueryRequestBuilder {
        private AttributeBuilder data;
        private List<String> requestIdList;

        private QueryRequestBuilder(String jurisdiction_id) {
            requestIdList = Lists.newArrayList();
            data = AttributeBuilder.newBuilder();
            data.put("jurisdiction_id", jurisdiction_id);
        }

        private QueryRequestBuilder() {
            this(DEFAULT_JURISDICTION_ID);
        }

        public static QueryRequestBuilder newBuilder(String jurisdiction_id) {
            return new QueryRequestBuilder(jurisdiction_id);
        }

        public static QueryRequestBuilder newBuilder() {
            return new QueryRequestBuilder();
        }

        public QueryRequestBuilder requestId(String request_id) {
            requestIdList.add(request_id);
            return this;
        }

        public QueryRequestBuilder serviceCode(String service_code) {
            data.put("service_code", service_code);
            return this;
        }

        public QueryRequestBuilder startDate(Date start) {
            data.put("start_date", start.toString());
            return this;
        }

        public QueryRequestBuilder endDate(Date end) {
            data.put("end_date", end.toString());
            return this;
        }

        public QueryRequestBuilder open() {
            data.put("status", "open");
            return this;
        }

        public QueryRequestBuilder closed() {
            data.put("status", "closed");
            return this;
        }

        public ListenableFuture<List<Request>> build() {
            if ( requestIdList.size() > 0 ) {
                String requestList = TextUtils.join(",", requestIdList);
                data.put("service_request_id", requestList);
            }
            return executor.submit(new Callable<List<Request>>() {
                @Override
                public List<Request> call() throws Exception {
                    Map<String, Object> map = data.build();
                    String path = prefix + PATH_REQUESTS + "?" + encodeGetParameters(convert(map), HTTP.UTF_8);
                    return getRequestListResponse(new URL(path));
                }
            });
        }

    }

    public static class PostRequestBuilder {
        // required
        private AttributeBuilder data;

        private PostRequestBuilder(String jurisdiction_id, String service_code) {
            data = AttributeBuilder.newBuilder();
            data.put("jurisdiction_id", jurisdiction_id).put("service_code", service_code);
        }

        private PostRequestBuilder(String jurisdiction_id, String service_code, long lat, long lon) {
            this(jurisdiction_id, service_code);
            location(lat, lon);
        }

        private PostRequestBuilder(String jurisdiction_id, String service_code, String address_string) {
            this(jurisdiction_id, service_code);
            location(address_string);
        }

        private PostRequestBuilder(String jurisdiction_id, String service_code, int location_id) {
            this(jurisdiction_id, service_code);
            location(location_id);
        }

        public PostRequestBuilder location(long lat, long lon) {
            data.put("lat", lat).put("long", lon);
            return this;
        }

        public PostRequestBuilder location(String address_string) {
            data.put("address_string",address_string);
            return this;
        }

        public PostRequestBuilder location(int location_id) {
            data.put("location_id",location_id);
            return this;
        }

        public PostRequestBuilder email(String email) {
            data.put("email", email);
            return this;
        }

        public PostRequestBuilder deviceId(String device_id) {
            data.put("device_id",device_id);
            return this;
        }

        public PostRequestBuilder accountId(String account_id) {
            data.put("account_id",account_id);
            return this;
        }

        public PostRequestBuilder firstName(String first_name) {
            data.put("first_name", first_name);
            return this;
        }

        public PostRequestBuilder lastName(String last_name) {
            data.put("last_name", last_name);
            return this;
        }

        public PostRequestBuilder description(String description) {
            data.put("description", description);
            return this;
        }

        public PostRequestBuilder mediaUrl(String media_url) {
            data.put("media_url", media_url);
            return this;
        }

        public PostRequestBuilder phone(String phone) {
            data.put("phone", phone);
            return this;
        }

        public ListenableFuture<RequestResponse> build() {
            return executor.submit(new Callable<RequestResponse>(){

                @Override
                public RequestResponse call() throws Exception {
                    return getPostResponse(new URL(prefix + PATH_REQUESTS), data.build());
                }
            });

        }

        public static PostRequestBuilder newBuilder(String jurisdiction_id, String service_code, long lat, long lon) {
            return new PostRequestBuilder(jurisdiction_id, service_code, lat, lon);
        }

        public static PostRequestBuilder newBuilder(String service_code, long lat, long lon) {
            return new PostRequestBuilder(DEFAULT_JURISDICTION_ID, service_code, lat, lon);
        }

        public static PostRequestBuilder newBuilder(String service_code, String address_string) {
            return new PostRequestBuilder(DEFAULT_JURISDICTION_ID, service_code, address_string);
        }

        public static PostRequestBuilder newBuilder(String service_code, int address_id) {
            return new PostRequestBuilder(DEFAULT_JURISDICTION_ID, service_code, address_id);
        }

    }

}
