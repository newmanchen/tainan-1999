package tn.opendata.tainan311;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import org.apache.http.cookie.Cookie;

import java.io.File;
import java.io.IOException;
import java.util.List;

import tn.opendata.tainan311.tainan1999.TainanReport1999;
import tn.opendata.tainan311.tainan1999.rpc.AddRequest;
import tn.opendata.tainan311.tainan1999.vo.AddResponse;
import tn.opendata.tainan311.utils.Base64Utils;
import tn.opendata.tainan311.utils.LogUtils;

/**
 * Created by vincent on 2014/6/12.
 */
//TODO to send add request to tainan1999
public class NewRequestIntentService extends IntentService {
    private static final String TAG = NewRequestIntentService.class.getSimpleName();

    public static final String EXTRA_DATA = "data";
    public static final String EXTRA_AREA = "area";
    public static final String EXTRA_SERVICE_NAME = "service_name";
    public static final String EXTRA_SUBPROJECT = "subproject";
    public static final String EXTRA_DESCRIPTION = "description";
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_PHOTO = "photo";

    private Bundle data = null;
    private Handler mHandler;

    private String GOOGLE_MAP_LINK = "https://www.google.com.tw/maps/@%s,%s,17z";
    public NewRequestIntentService() {
        super("NewRequest");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler(getMainLooper());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        data = intent.getBundleExtra(EXTRA_DATA);
        if (data == null ) {
            return;
        }
        postRequest(data, null);
    }

    private void postRequest(Bundle data, List<Cookie> cookies) {
        showNotification(getString(R.string.add_request));
        if (data != null) {
            AddRequest.Builder builder = AddRequest.Builder.create();
            builder.setArea(data.getString(EXTRA_AREA));
            builder.setServiceName(data.getString(EXTRA_SERVICE_NAME));
            builder.setSubProject(data.getString(EXTRA_SUBPROJECT));
            LatLng location = data.getParcelable(EXTRA_LOCATION);
            String lat = String.valueOf(location.latitude);
            builder.setLatitude(lat);
            String lng = String.valueOf(location.longitude);
            builder.setLongitude(lng);
            StringBuilder sb = new StringBuilder(data.getString(EXTRA_DESCRIPTION));
            sb.append("             Google Map : ").append(String.format(GOOGLE_MAP_LINK, lat, lng));
            sb.append("             sent from ").append(getString(R.string.app_name));
            LogUtils.d(TAG, "description :: ", sb.toString());
            builder.setDescription(sb.toString());
            builder.setAddressString(data.getString(EXTRA_ADDRESS));
            builder.setName(data.getString(EXTRA_NAME));
            builder.setPhone(data.getString(EXTRA_PHONE));
            builder.setEmail(data.getString(EXTRA_EMAIL));
            if (data.containsKey(NewRequestIntentService.EXTRA_PHOTO)) {
                // put this to non ui thread
                String path = data.getString(NewRequestIntentService.EXTRA_PHOTO);
                if (!TextUtils.isEmpty(path)) {
                    try {
                        AddRequest.Picture pic = new AddRequest.Picture();
                        File picFile = new File(path);
                        LogUtils.d(TAG, "pic file size is ", picFile.length());
                        pic.setFileName(picFile.getName());
                        pic.setFile(Base64Utils.getBase64FileContent(picFile));
                        builder.setPicture(pic);
                    } catch (IOException e) {
                        LogUtils.w(TAG, e.getMessage(), e);
                    }
                } else {
                    LogUtils.d(TAG, "path is empty");
                }
            }
            Futures.addCallback(TainanReport1999.executeAdd(builder.build())
                    , new FutureCallback<AddResponse>() {
                @Override
                public void onSuccess(AddResponse result) {
                    LogUtils.d(TAG, "onSuccess");
                    if (result != null) {
                        LogUtils.d(TAG, "add response :: token = ", result.getToken());
                        LogUtils.d(TAG, "add response :: service_notice = ", result.getService_notice());
                        LogUtils.d(TAG, "add response :: service_request_id = ", result.getService_request_id());

                        //TODO save service_request_id to preference for MyReport
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    showNotification(getString(R.string.add_failed) + t.getMessage());
                }
            });
        } else {
            showNotification(getString(R.string.no_data));
        }
    }

    private void showNotification(final String message) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewRequestIntentService.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}