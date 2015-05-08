package tn.opendata.tainan311;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by newman on 5/8/15.
 */
public class GoogleSoftwareLicenseInfo extends Activity {
    @InjectView(R.id.license) TextView tv_license;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        ButterKnife.inject(this);
        tv_license.setText(GoogleApiAvailability.getInstance().getOpenSourceSoftwareLicenseInfo(this));
    }
}