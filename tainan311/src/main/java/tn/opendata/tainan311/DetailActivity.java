package tn.opendata.tainan311;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.tainan1999.vo.QueryResponse;
import tn.opendata.tainan311.utils.LocationUtils;
import tn.opendata.tainan311.utils.LogUtils;

public class DetailActivity extends Activity {
    @InjectView(R.id.image) ImageView imageView;
    @InjectView(R.id.service_request_id) TextView service_request_id;
    @InjectView(R.id.area) TextView area;
    @InjectView(R.id.service_name) TextView service_name;
    @InjectView(R.id.subproject) TextView subproject;
    @InjectView(R.id.description) TextView description;
    @InjectView(R.id.request_date) TextView requestDate;
    @InjectView(R.id.ll_update_datetime) LinearLayout ll_update;
    @InjectView(R.id.updated_datetime) TextView updateDate;
    @InjectView(R.id.ll_expected_datetime) LinearLayout ll_expected;
    @InjectView(R.id.expected_datetime) TextView expectedData;
    @InjectView(R.id.agency) TextView agency;

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_KEY_REQUEST = "extra_key_request";
    private QueryResponse mRequest;

    protected ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions;
    private String mDataPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);

        mRequest = getIntent().getParcelableExtra(EXTRA_KEY_REQUEST);
        if (mRequest == null) {
            return;
        } else {
            mDataPath = getFilesDir().toString()+"/pic/";
            initImageLoader();
            updateActionBar();
            initViews();
        }
    }

    private void initImageLoader() {
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private void updateActionBar() {
        ActionBar ab = getActionBar();
        ab.setTitle(mRequest.getService_name());
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        // image
        File file = new File(mDataPath+mRequest.getService_request_id()+".jpg");
        if (file.exists()) {
            mImageLoader.loadImage(Uri.fromFile(file).toString(), mOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(loadedImage);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }
//        if (!TextUtils.isEmpty(mRequest.getMedia_url())) {
//            final ImageView imageView = EasyUtil.findView(this, R.id.image);
//            mImageLoader.loadImage(mRequest.getMedia_url(), mOptions
//                    , new ImageLoadingListener() {
//                @Override
//                public void onLoadingStarted(String imageUri, View view) {
//                }
//
//                @Override
//                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                }
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    imageView.setVisibility(View.VISIBLE);
//                    imageView.setImageBitmap(loadedImage);
//                }
//
//                @Override
//                public void onLoadingCancelled(String imageUri, View view) {
//                }
//            });
//        }
        area.setText(mRequest.getArea());
        service_request_id.setText(mRequest.getService_request_id());
        service_name.setText(mRequest.getService_name());
        subproject.setText(mRequest.getSubproject());
        description.setText(mRequest.getDescription_request());
        requestDate.setText(mRequest.getRequested_datetime());
//        LogUtils.d(TAG, "mRequest.getUpdated_datetime() is ", mRequest.getUpdated_datetime());
//        LogUtils.d(TAG, "mRequest.getExpected_datetime() is ", mRequest.getExpected_datetime());
        if (!TextUtils.isEmpty(mRequest.getUpdated_datetime()) && !mRequest.getUpdated_datetime().equalsIgnoreCase("null")) {
            ll_update.setVisibility(View.VISIBLE);
            updateDate.setText(mRequest.getUpdated_datetime());
        }
        if (!TextUtils.isEmpty(mRequest.getExpected_datetime()) && !mRequest.getExpected_datetime().equalsIgnoreCase("null")) {
            ll_expected.setVisibility(View.VISIBLE);
            expectedData.setText(mRequest.getExpected_datetime());
        }
        agency.setText(TainanConstant.AGENCY[Integer.valueOf(mRequest.getAgency())]);
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        final LatLng issueLocation;
        if (TextUtils.isEmpty(mRequest.getLatitude()) || mRequest.getLatitude().equals("0")
                || TextUtils.isEmpty(mRequest.getLongitude()) || mRequest.getLongitude().equals("0")) {
            issueLocation = LocationUtils.getLocationFromAddress(this, mRequest.getAddress_string());
        } else {
            issueLocation  = new LatLng(Double.valueOf(mRequest.getLatitude()), Double.valueOf(mRequest.getLongitude()));
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