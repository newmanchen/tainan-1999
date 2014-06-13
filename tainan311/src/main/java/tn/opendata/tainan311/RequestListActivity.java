package tn.opendata.tainan311;


import android.app.ActionBar;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tn.opendata.tainan311.georeportv2.GeoReportV2;
import tn.opendata.tainan311.georeportv2.vo.Request;
import tn.opendata.tainan311.utils.EasyUtil;
import tn.opendata.tainan311.utils.MainThreadExecutor;


public class RequestListActivity extends ListActivity {
    private static final String TAG = RequestListActivity.class.getSimpleName();

    protected ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions;
    private SimpleDateFormat mSimpleDateFormatTo;
    private SimpleDateFormat mSimpleDateFormatFrom;
    private RequestListArrayAdapter mRequestListArrayAdapter;
    private LinearLayout mLoadingMoreItem;
    private static final int MAX_REQUEST = 10; // request 10 issues at a time
    // NOTICE :: if size of max request is less than size of one page, weird thing would happen, do not ask just set larger
    private boolean mHasNext;
    private boolean mLoadingMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSimpleDateFormatTo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", getResources().getConfiguration().locale);
        //2014-06-10T16:43:30.075028+08:00
        mSimpleDateFormatFrom = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");

        mHasNext = true;
        mLoadingMore = false;
        initImageLoader();
        initActionBar();
        initViews();
        loadRequest(null);
        getListView().setDivider(null);
    }

    private void initImageLoader() {
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
        mOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private void initActionBar() {
        ActionBar ab = getActionBar();
        ab.setTitle(R.string.request_list_title);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        mLoadingMoreItem = (LinearLayout) ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_loading_more, null);
        getListView().addFooterView(mLoadingMoreItem);
    }

    private void loadRequest(String endDateTime) {
        if (mHasNext) {
            addLoadingMoreListItem();
            GeoReportV2.QueryRequestBuilder builder = GeoReportV2.QueryRequestBuilder.create();
            builder.max_requests(MAX_REQUEST);
            if (!TextUtils.isEmpty(endDateTime)) {
                builder.endDate(endDateTime);
            }
//            Log.d(TAG, "newman::url to fetch data :: " + builder.toString());
            mLoadingMore = true;
            Futures.addCallback(builder.execute()
                    , new FutureCallback<List<Request>>() {
                @Override
                public void onSuccess(List<Request> result) {
                    mLoadingMore = false;
                    removeLoadingMoreListItem();
                    Log.d(TAG, "callback onSuccess");
                    if (result != null && result.size() > 0) {
                        if (result.size() < MAX_REQUEST) {
                            mHasNext = false;
                        }

                        if (mRequestListArrayAdapter == null) {
                            mRequestListArrayAdapter = new RequestListArrayAdapter(RequestListActivity.this, result);
                            setListAdapter(mRequestListArrayAdapter);
                            getListView().setOnItemClickListener(mRequestListArrayAdapter);
                            getListView().setOnScrollListener(mOnScrollListener);
                        } else {
                            mRequestListArrayAdapter.addAll(result);
                            mRequestListArrayAdapter.updateReqeustList(result);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    mLoadingMore = false;
                    removeLoadingMoreListItem();
                    Log.d(TAG, "callback onFail");
                    t.printStackTrace();
                    //TODO
                    Toast.makeText(RequestListActivity.this, "沒有網路或抓取失敗", Toast.LENGTH_LONG).show();
                }
            }, new MainThreadExecutor());
        } else {
            Log.d(TAG, "NO MORE DATA");
        }
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
                Request r = mRequestListArrayAdapter.getItem(mRequestListArrayAdapter.getCount()-1);
                loadRequest(r.getRequested_datetime());
            }
        }
    };

    private void addLoadingMoreListItem() {
        mLoadingMoreItem.setVisibility(View.VISIBLE);
    }

    private void removeLoadingMoreListItem() {
        mLoadingMoreItem.setVisibility(View.GONE);
    }

    private class RequestListArrayAdapter extends ArrayAdapter<Request> implements AdapterView.OnItemClickListener {
        private final LayoutInflater mInflater;
        private final int mResource;
        private final List<Request> mRequestList = Lists.newArrayList();
        private RequestListArrayAdapter(Context context, List<Request> objects) {
            super(context, R.layout.list_item_request, objects);
            mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            mResource = R.layout.list_item_request;
            mRequestList.addAll(objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = mInflater.inflate(mResource, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.cover = EasyUtil.findView(view, R.id.img);
                holder.title = EasyUtil.findView(view, R.id.title);
                holder.service_name = EasyUtil.findView(view, R.id.service_name);
                holder.datetime = EasyUtil.findView(view, R.id.datetime);
                holder.status = EasyUtil.findView(view, R.id.status);
                view.setTag(holder);
            } else {
                view = convertView;
            }
            Request r = getItem(position);
            final ViewHolder holder = (ViewHolder) view.getTag();
            if (!TextUtils.isEmpty(r.getMedia_url())) {
                String url = r.getMedia_url();
                String smallImageUrl = url.replace(".full.", ".fp."); // 90*60
                mImageLoader.loadImage(smallImageUrl, mOptions, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.cover.setVisibility(View.VISIBLE);
                        holder.cover.setImageBitmap(loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            } else {
                holder.cover.setVisibility(View.GONE);
            }
            holder.title.setText(r.getTitle());
            holder.service_name.setText(r.getService_name());
            try {
                Date date = mSimpleDateFormatFrom.parse(r.getRequested_datetime());
                holder.datetime.setText(mSimpleDateFormatTo.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (Request.STATUS_CLOSE.equals(r.getStatus())) {
                holder.status.setText(R.string.status_closed);
                holder.status.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                holder.status.setVisibility(View.VISIBLE);
            } else if (Request.STATUS_OPEN.equals(r.getStatus())) {
                holder.status.setText(R.string.status_open);
                holder.status.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                holder.status.setVisibility(View.VISIBLE);
            } else {
                holder.status.setVisibility(View.GONE);
            }
            return view;
        }

        private class ViewHolder {
            ImageView cover;
            TextView title;
            TextView service_name;
            TextView datetime;
            TextView status;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent i = new Intent(RequestListActivity.this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_KEY_REQUEST, (Request) getItem(position));
            startActivity(i);
        }

        public void updateReqeustList(List<Request> list) {
            mRequestList.addAll(list);
            mRequestListArrayAdapter.notifyDataSetChanged();
        }
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