package tn.opendata.tainan311;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.http.cookie.Cookie;
import tn.opendata.tainan311.georeportv2.FMSResponse;
import tn.opendata.tainan311.georeportv2.GeoReportV2;

import java.util.List;

/**
 * Created by vincent on 2014/6/12.
 */
//TODO to send add request to tainan1999
public class NewRequestIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 100;

    private Bundle data = null;
    private Handler mHandler;

    public NewRequestIntentService() {
        super("NewRequest");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        data = intent.getBundleExtra("data");
        if (data == null ) {
            return;
        }
        // postRequest(data, null); //FIXME do nothing at this moment
    }

    private void postRequest(Bundle data, List<Cookie> cookies) {
        showNotification(getString(R.string.add_request));
        if (data != null) {
            final boolean needRegister = data.getBoolean("register");
            LatLng location = data.getParcelable("location");
            GeoReportV2.PostRequestBuilder builder = GeoReportV2.PostRequestBuilder.create(data.getString("category"),
                    location.latitude, location.longitude,
                    data.getString("title"),
                    data.getString("detail"),
                    data.getString("name"),
                    data.getString("email")
            );
            builder.password(needRegister, data.getString("password")).photo(data.getString("photo")).addCookie(cookies);

            ListenableFuture<FMSResponse> future = builder.build().execute();
            Futures.addCallback(future, new FutureCallback<FMSResponse>() {
                @Override
                public void onSuccess(FMSResponse result) {
                    String message;
                    if ( result.isSuccess() ) {
                        message = (needRegister) ? getString(R.string.confirm_mail):getString(R.string.success);
                    } else {
                        message = result.getError();
                    }
                    showNotification(message);
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