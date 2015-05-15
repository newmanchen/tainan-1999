package tn.opendata.tainan311;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tn.opendata.tainan311.tainan1999.api.Picture;
import tn.opendata.tainan311.tainan1999.api.Record;
import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.utils.LocationUtils;
import tn.opendata.tainan311.utils.LogUtils;

import static tn.opendata.tainan311.utils.EasyUtil.isNotEmpty;

public class DetailActivity extends Activity {
    public static final String EXTRA_KEY_REQUEST = "extra_key_request";
    private static final String TAG = DetailActivity.class.getSimpleName();
    @InjectView(R.id.image1)
    ImageView imageView1;
    @InjectView(R.id.image2)
    ImageView imageView2;
    @InjectView(R.id.image3)
    ImageView imageView3;
    @InjectView(R.id.service_request_id)
    TextView service_request_id;
    @InjectView(R.id.area)
    TextView area;
    @InjectView(R.id.service_name)
    TextView service_name;
    @InjectView(R.id.subproject)
    TextView subproject;
    @InjectView(R.id.description)
    TextView description;
    @InjectView(R.id.request_date)
    TextView requestDate;
    @InjectView(R.id.ll_update_datetime)
    LinearLayout ll_update;
    @InjectView(R.id.updated_datetime)
    TextView updateDate;
    @InjectView(R.id.ll_expected_datetime)
    LinearLayout ll_expected;
    @InjectView(R.id.expected_datetime)
    TextView expectedData;
    @InjectView(R.id.agency)
    TextView agency;
    private Record mRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);

        mRequest = getIntent().getParcelableExtra(EXTRA_KEY_REQUEST);
        if (mRequest == null) {
            throw new IllegalStateException("Request should not be null!!!");
        } else {
            updateActionBar();
            initViews();
        }
    }


    private void updateActionBar() {
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setTitle(mRequest.getService_name());
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        ImageView[] images = new ImageView[]{imageView1,imageView2,imageView3};
        int upbound = Math.min(mRequest.getPictures().size(),images.length);
        for(int i=0;i< upbound;i++){

            Picture pic = mRequest.getPictures().get(i);

            if(isNotEmpty(pic.getFilePath())){
                Picasso.with(this)
                       .load(new File(pic.getFilePath()))
                       .fit()
                       .centerCrop()
                       .into(images[i]);
                images[i].setVisibility(View.VISIBLE);
            }

        }

        area.setText(mRequest.getArea());
        service_request_id.setText(mRequest.getService_request_id());
        service_name.setText(mRequest.getService_name());
        subproject.setText(mRequest.getSubproject());
        description.setText(mRequest.getDescription());
        requestDate.setText(mRequest.getRequested_datetime());

        if (isNotEmpty(mRequest.getUpdated_datetime()) && !mRequest.getUpdated_datetime().equalsIgnoreCase("null")) {
            ll_update.setVisibility(View.VISIBLE);
            updateDate.setText(mRequest.getUpdated_datetime());
        }
        if (isNotEmpty(mRequest.getExpected_datetime()) && !mRequest.getExpected_datetime().equalsIgnoreCase("null")) {
            ll_expected.setVisibility(View.VISIBLE);
            expectedData.setText(mRequest.getExpected_datetime());
        }
        if (mRequest.getAgency() != null && mRequest.getAgency().equals("0")) {
            agency.setText(TainanConstant.AGENCY[Integer.valueOf(mRequest.getAgency())]);
        } else {
            agency.setText(mRequest.getAgency());
        }
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setZoomControlsEnabled(true);
        final LatLng issueLocation;
        if (mRequest.getLat() == 0 || mRequest.getLng() == 0) {
            issueLocation = LocationUtils.getLocationFromAddress(this, mRequest.getAddress_string());
        } else {
            issueLocation = new LatLng(mRequest.getLat(), mRequest.getLng());
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(issueLocation, 17));
        map.addMarker(new MarkerOptions().title(mRequest.getSubproject()).position(issueLocation));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem notTakenItem = menu.findItem(R.id.menu_red_not_taken);
        MenuItem finishedItem = menu.findItem(R.id.menu_green_finished);
        MenuItem inProgressed = menu.findItem(R.id.menu_orange_inprogress);
        LogUtils.d(TAG, "status is ", mRequest.getStatus());
        if (mRequest != null) {
            if (TainanConstant.STATUS_FINISH.equals(mRequest.getStatus())) {
                notTakenItem.setVisible(false);
                finishedItem.setVisible(true);
                inProgressed.setVisible(false);
            } else if (TainanConstant.STATUS_IN_PROCESS.equals(mRequest.getStatus())) {
                notTakenItem.setVisible(false);
                finishedItem.setVisible(false);
                inProgressed.setVisible(true);
            } else if (TainanConstant.STATUS_NOT_TAKEN.equals(mRequest.getStatus())) {
                notTakenItem.setVisible(true);
                finishedItem.setVisible(false);
                inProgressed.setVisible(false);
            } else {
                LogUtils.d(TAG, "status is not as expected");
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}