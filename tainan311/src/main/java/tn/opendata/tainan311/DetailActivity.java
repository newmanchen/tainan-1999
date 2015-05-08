package tn.opendata.tainan311;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.tainan1999.vo.QueryResponse;
import tn.opendata.tainan311.utils.LocationUtils;
import tn.opendata.tainan311.utils.LogUtils;

public class DetailActivity extends Activity {
    @InjectView(R.id.image) ImageView imageView;
    @InjectView(R.id.detail) TextView detail;
    @InjectView(R.id.service_name) TextView serviceName;
    @InjectView(R.id.request_date) TextView requestDate;
    @InjectView(R.id.request_area) TextView area;

    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_KEY_REQUEST = "extra_key_request";
    private QueryResponse mRequest;

    protected ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);

        mRequest = getIntent().getParcelableExtra(EXTRA_KEY_REQUEST);
        if (mRequest == null) {
            return;
        } else {
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
        ab.setTitle(mRequest.getSubproject());
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
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
        detail.setText(mRequest.getDescription_request());
        serviceName.setText(mRequest.getService_name());
        requestDate.setText(mRequest.getRequested_datetime());
        area.setText(mRequest.getArea());
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        final LatLng issueLocation;
        if (TextUtils.isEmpty(mRequest.getLatitude()) || mRequest.getLatitude().equals("0")
                || TextUtils.isEmpty(mRequest.getLongitude()) || mRequest.getLongitude().equals("0")) {
            issueLocation = LocationUtils.getLocationFromAddress(this, mRequest.getAddress_string());
        } else {
            issueLocation  = new LatLng(Double.valueOf(mRequest.getLatitude()), Double.valueOf(mRequest.getLongitude()));
        }
        LogUtils.d(TAG, "issueLocation = ", issueLocation);
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