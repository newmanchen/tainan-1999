package tn.opendata.tainan311;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tn.opendata.tainan311.georeportv2.vo.Request;
import tn.opendata.tainan311.utils.EasyUtil;

public class DetailActivity extends Activity {
    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String EXTRA_KEY_REQUEST = "extra_key_request";
    private Request mRequest;

    protected ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions;
    private SimpleDateFormat mSimpleDateFormatTo;
    private SimpleDateFormat mSimpleDateFormatFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mRequest = getIntent().getParcelableExtra(EXTRA_KEY_REQUEST);
        if (mRequest == null) {
            return;
        } else {
            initImageLoader();
            updateActionBar();

            mSimpleDateFormatTo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", getResources().getConfiguration().locale);
            mSimpleDateFormatFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
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
        ab.setTitle(mRequest.getTitle());
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        if (!TextUtils.isEmpty(mRequest.getMedia_url())) {
            final ImageView imageView = EasyUtil.findView(this, R.id.image);
            mImageLoader.loadImage(mRequest.getMedia_url(), mOptions
                    , new ImageLoadingListener() {
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
        }
        TextView detail = EasyUtil.findView(this, R.id.detail);
        detail.setText(mRequest.getDetail());
        TextView serviceName = EasyUtil.findView(this, R.id.service_name);
        serviceName.setText(mRequest.getService_name());
        TextView requestDate = EasyUtil.findView(this, R.id.request_date);
        try {
            Date date = mSimpleDateFormatFrom.parse(removeFractionalSeconds(mRequest.getRequested_datetime()));
            requestDate.setText(mSimpleDateFormatTo.format(date));
        } catch (ParseException  e) {
            e.printStackTrace();
        }
        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        LatLng issueLocation = new LatLng(Double.valueOf(mRequest.getLat()), Double.valueOf(mRequest.getLon()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(issueLocation, 17));
        map.addMarker(new MarkerOptions().title(mRequest.getTitle()).position(issueLocation));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem redItem = menu.findItem(R.id.menu_red_open);
        MenuItem greenItem = menu.findItem(R.id.menu_green_close);
        if (mRequest != null) {
            if (Request.STATUS_CLOSE.equals(mRequest.getStatus())) {
                redItem.setVisible(false);
                greenItem.setVisible(true);
            } else if (Request.STATUS_OPEN.equals(mRequest.getStatus())) {
                redItem.setVisible(true);
                greenItem.setVisible(false);
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

    private String removeFractionalSeconds(String time) {
        // 2014-06-10T16:43:30.075028+08:00
        return time.replace(time.substring(19, 26), "");
    }
}