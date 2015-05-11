package tn.opendata.tainan311;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tn.opendata.tainan311.tainan1999.TainanReport1999;
import tn.opendata.tainan311.tainan1999.rpc.QueryRequest;
import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.tainan1999.vo.QueryResponse;
import tn.opendata.tainan311.utils.LogUtils;
import tn.opendata.tainan311.utils.PreferenceUtils;

/**
 * Created by newman on 5/8/15.
 */
public class MyActivity extends ListActivity {
    private static final String TAG = MyActivity.class.getSimpleName();
    // Object
    private QueryRequestArrayAdapter mQueryRequestArrayAdapter;
    protected ImageLoader mImageLoader = ImageLoader.getInstance();
    private DisplayImageOptions mOptions;
    // Value
    private String mDataPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        initActionBar();
        initEmptyView();
        initView();
        mDataPath = getFilesDir().toString()+"/pic/";

//        setRawRequestIdsForTest();
        loadQueryRequest();
    }

    private void initView() {
        getListView().setDivider(null);
    }

    private void initActionBar() {
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initEmptyView() {
        View view = getLayoutInflater().inflate(R.layout.empty_view, null);
        TextView tv = (TextView) view.findViewById(R.id.empty_string);
        tv.setText(R.string.text_no_my_activity);
        ((ViewGroup) getListView().getParent()).addView(view);
        getListView().setEmptyView(view);
    }

    private void showProgressOnActionBar(boolean show) {
        setProgressBarIndeterminateVisibility(show);
    }

    private void loadQueryRequest() {
        showProgressOnActionBar(true);
        Set<String> requestIds = PreferenceUtils.getMyRequestIds(this);
        String rids = TextUtils.join(",", requestIds);
        QueryRequest.Builder builder = QueryRequest.Builder.create();
        LogUtils.d(TAG, "rids are ", rids);
        builder.setServiceRequestId(rids);
        LogUtils.d(TAG, "builder.build() is ", builder.build());

        Futures.addCallback(TainanReport1999.executeQuery(this, builder.build())
                , new FutureCallback<List<QueryResponse>>() {
            @Override
            public void onSuccess(final List<QueryResponse> result) {
                LogUtils.d(TAG, "callback onSuccess");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null && result.size() > 0) {
                            LogUtils.d(TAG, "data count : ", result.size());
                            if (mQueryRequestArrayAdapter == null) {
                                mQueryRequestArrayAdapter = new QueryRequestArrayAdapter(MyActivity.this, result);
                                setListAdapter(mQueryRequestArrayAdapter);
                                getListView().setOnItemClickListener(mQueryRequestArrayAdapter);
                            } else {
                                mQueryRequestArrayAdapter.addAll(result);
                                mQueryRequestArrayAdapter.updateRequestList(result);
                            }
                        }
                        showProgressOnActionBar(false);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                // TODO
            }
        });
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
            // image
            File file = new File(mDataPath+r.getService_request_id()+".jpg");
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

            @InjectView(R.id.img)
            ImageView cover;
            @InjectView(R.id.subproject)
            TextView subproject;
            @InjectView(R.id.service_name) TextView service_name;
            @InjectView(R.id.datetime) TextView datetime;
            @InjectView(R.id.status) TextView status;
            @InjectView(R.id.area) TextView area;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent i = new Intent(MyActivity.this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_KEY_REQUEST, getItem(position));
            startActivity(i);
        }

        public void updateRequestList(List<QueryResponse> list) {
            mRequestList.addAll(list);
            mQueryRequestArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setRawRequestIdsForTest() {
        HashSet<String> t = new HashSet<String>();
        t.add("UN201505110183");
        t.add("UN201505110247");
        PreferenceUtils.setMyRequestIds(this, t);
    }
}