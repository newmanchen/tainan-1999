package tn.opendata.tainan311;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import rx.android.schedulers.AndroidSchedulers;
import tn.opendata.tainan311.tainan1999.api.AddPicture;
import tn.opendata.tainan311.tainan1999.api.AddRequest;
import tn.opendata.tainan311.tainan1999.api.Tainan1999Service;
import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.utils.Base64Utils;
import tn.opendata.tainan311.utils.LogUtils;
import tn.opendata.tainan311.utils.PreferenceUtils;

/**
 * To add a request to server
 *
 * Created by vincent on 2014/6/12.
 */
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

    String GOOGLE_MAP_LINK = "https://www.google.com.tw/maps/@%s,%s,18z";

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
        if (data == null) {
            return;
        }
        postRequest(data);
    }

    private void postRequest(Bundle data) {
        showNotification(getString(R.string.add_request));
        if (data != null) {
            AddRequest.Builder builder = AddRequest.Builder.create();
            builder.setCity_id(TainanConstant.CITY_ID);
            builder.setArea(data.getString(EXTRA_AREA));
            builder.setService_name(data.getString(EXTRA_SERVICE_NAME));
            builder.setSubproject(data.getString(EXTRA_SUBPROJECT));
            LatLng location = data.getParcelable(EXTRA_LOCATION);
            String lat = String.valueOf(location.latitude);
            builder.setLat(lat);
            String lng = String.valueOf(location.longitude);
            builder.setLng(lng);
            builder.setDescription(getWatermarkDescription(lat, lng));
            builder.setAddress_string(data.getString(EXTRA_ADDRESS));
            builder.setName(data.getString(EXTRA_NAME));
            builder.setPhone(data.getString(EXTRA_PHONE));
            builder.setEmail(data.getString(EXTRA_EMAIL));
            if (data.containsKey(NewRequestIntentService.EXTRA_PHOTO)) {
                // put this to non ui thread
                String path = data.getString(NewRequestIntentService.EXTRA_PHOTO);
                if (!TextUtils.isEmpty(path)) {
                    try {
                        AddPicture pic = new AddPicture();
                        File picFile = new File(path);
                        LogUtils.d(TAG, "pic file size is ", picFile.length());
                        pic.setFileName(picFile.getName());
                        pic.setFile(Base64Utils.getBase64FileContent(picFile));

                        //TODO 3 pictures
                        List<AddPicture> pics = Lists.newArrayList();
                        pics.add(pic);

                        builder.setPictures(pics);
                    } catch (IOException e) {
                        LogUtils.w(TAG, e.getMessage(), e);
                    }
                } else {
                    LogUtils.d(TAG, "photo path is empty");
                }
            }

            RestAdapter rest = new RestAdapter.Builder()
//                    .setEndpoint(TainanConstant.POST_TEST_SERVER_URL)
                    .setEndpoint(TainanConstant.TAINAN1999_URL)
                    .setConverter(new SimpleXMLConverter())
                    .build();

            Tainan1999Service service = rest.create(Tainan1999Service.class);
            service.addReports(builder.build())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addResponse -> {
                        LogUtils.d(TAG, "onSuccess");
                        if (addResponse != null) {
                            if (addResponse.getReturncode() == 0) {
                                LogUtils.d(TAG, "add response :: token = ", addResponse.getToken());
                                LogUtils.d(TAG, "add response :: service_notice = ", addResponse.getService_notice());
                                LogUtils.d(TAG, "add response :: service_request_id = ", addResponse.getService_request_id());

                                Set<String> temp = PreferenceUtils.getMyRequestIds(NewRequestIntentService.this);
                                HashSet<String> requestIds = Sets.newHashSet();
                                if (temp != null) {
                                    requestIds.addAll(temp);
                                }
                                requestIds.add(addResponse.getService_request_id());
                                PreferenceUtils.setMyRequestIds(NewRequestIntentService.this, requestIds);
                            } else {
                                // TODO error handling
                                LogUtils.d(TAG, "add response :: returncode = ", addResponse.getReturncode());
                                LogUtils.d(TAG, "add response :: description = ", addResponse.getDescription());
                                LogUtils.d(TAG, "add response :: stacktrace = ", addResponse.getStacktrace());
                            }
                        }
                    }, err -> LogUtils.w(TAG, err.getMessage(), err));
        } else {
            showNotification(getString(R.string.no_data));
        }
    }

    private void showNotification(final String message) {
        mHandler.post(() -> Toast.makeText(NewRequestIntentService.this, message, Toast.LENGTH_LONG).show());
    }

    private String getWatermarkDescription(String lat, String lng) {
        StringBuilder sb = new StringBuilder(data.getString(EXTRA_DESCRIPTION));
        sb.append("\nGoogle Map : ").append(String.format(GOOGLE_MAP_LINK, lat, lng));
        sb.append("\nsent from ").append(getString(R.string.app_name));
        LogUtils.d(TAG, "description :: ", sb.toString());
        return sb.toString();
    }
}