package tn.opendata.tainan311;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import tn.opendata.tainan311.tainan1999.TainanReport1999;
import tn.opendata.tainan311.tainan1999.rpc.QueryRequest;
import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.tainan1999.vo.QueryResponse;
import tn.opendata.tainan311.utils.LogUtils;

/**
 * Created by newman on 5/5/15.
 */
public class TainanRequestListActivity extends ListActivity {
    @InjectView(R.id.normal_plus) AddFloatingActionButton addRequestButton;
    private static final String TAG = TainanRequestListActivity.class.getSimpleName();
    // View
    private LinearLayout mLoadingMoreItem;
    // Object
    private QueryRequestArrayAdapter mQueryRequestArrayAdapter;
    protected ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions;
    private SimpleDateFormat mSimpleDateFormat;
    // Value
    private boolean mLoadingMore;
    // Constant

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tainan_query_list);
        ButterKnife.inject(this);
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", getResources().getConfiguration().locale);

        mLoadingMore = false;
        initImageLoader();
        initActionBar();
        initViews();
        loadQueryRequest(true);
    }

    private void initActionBar() {
        ActionBar ab = getActionBar();
        ab.setTitle(R.string.request_list_title);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        getListView().setDivider(null);
        mLoadingMoreItem = (LinearLayout) ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_loading_more, null);
        getListView().addFooterView(mLoadingMoreItem);
    }

    @OnClick(R.id.normal_plus)
    public void clickAdd(View view) {
        startActivity(new Intent(TainanRequestListActivity.this, ReportActivity.class));
    }

    private Calendar cal = Calendar.getInstance();
    private void loadQueryRequest(boolean firstTimeQuery) {
        LogUtils.d(TAG, "loadQueryRequest firstTimeQuery:", firstTimeQuery);
        addLoadingMoreListItem();

        QueryRequest.Builder builder = QueryRequest.Builder.create();
        builder.setEndTime(mSimpleDateFormat.format(cal.getTime()));
        if (firstTimeQuery) {
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 1);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        builder.setStartTime(mSimpleDateFormat.format(cal.getTime()));
        LogUtils.d(TAG, "builder.build() is ", builder.build());

        mLoadingMore = true;
        Futures.addCallback(TainanReport1999.executeQuery(builder.build())
                , new FutureCallback<List<QueryResponse>>() {
            @Override
            public void onSuccess(final List<QueryResponse> result) {
                LogUtils.d(TAG, "callback onSuccess");
                mLoadingMore = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        removeLoadingMoreListItem();
                        if (result != null && result.size() > 0) {
                            LogUtils.d(TAG, "data count : ", result.size());
                            if (mQueryRequestArrayAdapter == null) {
                                mQueryRequestArrayAdapter = new QueryRequestArrayAdapter(TainanRequestListActivity.this, result);
                                setListAdapter(mQueryRequestArrayAdapter);
                                getListView().setOnItemClickListener(mQueryRequestArrayAdapter);
                                getListView().setOnScrollListener(mOnScrollListener);
                            } else {
                                mQueryRequestArrayAdapter.addAll(result);
                                mQueryRequestArrayAdapter.updateRequestList(result);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO
            }
        });
    }

    private void initImageLoader() {
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private void addLoadingMoreListItem() {
        mLoadingMoreItem.setVisibility(View.VISIBLE);
    }

    private void removeLoadingMoreListItem() {
        mLoadingMoreItem.setVisibility(View.GONE);
    }

    public class QueryRequestArrayAdapter extends ArrayAdapter<QueryResponse> implements AdapterView.OnItemClickListener {
        private final LayoutInflater mInflater;
        private final int mResource;
        private final List<QueryResponse> mRequestList = Lists.newArrayList();
        private QueryRequestArrayAdapter(Context context, List<QueryResponse> objects) {
            super(context, R.layout.list_item_request, objects);
            mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            mResource = R.layout.list_item_request;
            mRequestList.addAll(objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(mResource, parent, false);
                ViewHolder holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            QueryResponse r = getItem(position);
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            //TODO due to the api design is not well and put this task as to-do
            // image
//            if (!TextUtils.isEmpty(r.getMedia_url())) {
//                String url = r.getMedia_url();
//                String smallImageUrl = url.replace(".full.", ".fp."); // 90*60
//                mImageLoader.loadImage(smallImageUrl, mOptions, new ImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        holder.cover.setVisibility(View.VISIBLE);
//                        holder.cover.setImageBitmap(loadedImage);
//                    }
//
//                    @Override
//                    public void onLoadingCancelled(String imageUri, View view) {
//
//                    }
//                });
//            } else {
//                holder.cover.setVisibility(View.GONE);
//            }
            // service name
            holder.service_name.setText(r.getService_name());
            // subproject
            holder.subproject.setText(r.getSubproject());
            // area
            holder.area.setText(r.getArea());
            // status
            if (TainanConstant.STATUS_FINISH.equals(r.getStatus())) {
                holder.status.setText(R.string.status_finished);
                holder.status.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                holder.status.setVisibility(View.VISIBLE);
            } else if (TainanConstant.STATUS_IN_PROCESS.equals(r.getStatus())) {
                holder.status.setText(R.string.status_inprogress);
                holder.status.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                holder.status.setVisibility(View.VISIBLE);
            } else if (TainanConstant.STATUS_NOT_TAKEN.equals(r.getStatus())) {
                holder.status.setText(R.string.status_not_taken);
                holder.status.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                holder.status.setVisibility(View.VISIBLE);
            } else {
                holder.status.setVisibility(View.GONE);
            }
            // requested date and time
            holder.datetime.setText(r.getRequested_datetime());
            return convertView;
        }

        public class ViewHolder {
            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }

            @InjectView(R.id.img) ImageView cover;
            @InjectView(R.id.subproject) TextView subproject;
            @InjectView(R.id.service_name) TextView service_name;
            @InjectView(R.id.datetime) TextView datetime;
            @InjectView(R.id.status) TextView status;
            @InjectView(R.id.area) TextView area;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent i = new Intent(TainanRequestListActivity.this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_KEY_REQUEST, getItem(position));
            startActivity(i);
        }

        public void updateRequestList(List<QueryResponse> list) {
            mRequestList.addAll(list);
            mQueryRequestArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_show_map:
                startActivity(new Intent(TainanRequestListActivity.this, MainMapActivity.class));
                break;

            case R.id.menu_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        return true;
    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            //is the bottom item visible & not loading more already ? Load more !
            if((lastInScreen == totalItemCount) && !(mLoadingMore)){
                //TODO send a another request
                loadQueryRequest(false);
            }
        }
    };
}