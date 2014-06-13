package tn.opendata.tainan311;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import tn.opendata.tainan311.georeportv2.FMSResponse;
import tn.opendata.tainan311.georeportv2.GeoReportV2;

/**
 * Created by vincent on 2014/6/12.
 */
public class NewRequestIntentService extends IntentService {

    private static final int NOTIFICATION_ID = 100;

    public NewRequestIntentService() {
        super("NewRequest");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle data = intent.getBundleExtra("data");

        if ( data != null ) {
            LatLng location = data.getParcelable("location");
            GeoReportV2.PostRequestBuilder builder = GeoReportV2.PostRequestBuilder.create(data.getString("category"),
                    location.latitude, location.longitude,
                    data.getString("title"),
                    data.getString("detail"),
                    data.getString("name"),
                    data.getString("email")
            );
            builder.password(data.getString("password")).photo(data.getString("photo"));

            ListenableFuture<FMSResponse> future = builder.build().execute();
            Futures.addCallback(future, new FutureCallback<FMSResponse>() {
                @Override
                public void onSuccess(FMSResponse result) {
                    //showNotification(R.string.success);
                }

                @Override
                public void onFailure(Throwable t) {
                    //showNotification(R.string.failed);
                }
            });
        } else {
            //showNotification(R.string.no_data);
        }
    }

    private void showNotification(int message) {
        Notification notification = new Notification();
        notification.tickerText = getString(message);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, notification);
    }
}
