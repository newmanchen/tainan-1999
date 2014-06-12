package tn.opendata.tainan311;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by vincent on 2014/6/12.
 */
public class NewRequestIntentService extends IntentService {

    public NewRequestIntentService() {
        super("NewRequest");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle data = intent.getBundleExtra("data");


    }
}
