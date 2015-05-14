package tn.opendata.tainan311;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import com.google.common.collect.Sets;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tn.opendata.tainan311.tainan1999.api.QueryRequest;
import tn.opendata.tainan311.tainan1999.api.Record;
import tn.opendata.tainan311.tainan1999.api.Tainan1999Service;
import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.utils.LogUtils;
import tn.opendata.tainan311.utils.PreferenceUtils;

import static tn.opendata.tainan311.utils.EasyUtil.isNotEmpty;

/**
 * This is an activity for reports added by user own
 *
 * Created by newman on 5/8/15.
 */
public class MyActivity extends ListActivity {
    private static final String TAG = MyActivity.class.getSimpleName();
    // Object
    private QueryRequestArrayAdapter mQueryRequestArrayAdapter;
    private RestAdapter restAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // setRawRequestIdsForTest(); // for testing

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(TainanConstant.TAINAN1999_URL)
                .setConverter(new SimpleXMLConverter())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();


        initActionBar();
        initEmptyView();
        initView();
        loadQueryRequest();
    }

    private void initView() {
        getListView().setDivider(null);
    }

    private void initActionBar() {
        ActionBar ab = getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
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
        if (requestIds != null && !requestIds.isEmpty()) {
            String rids = TextUtils.join(",", requestIds);
            QueryRequest.Builder builder = QueryRequest.Builder.create();
            LogUtils.d(TAG, "rids are ", rids);
            builder.setServiceRequestId(rids);
            LogUtils.d(TAG, "builder.build() is ", builder.build());
            builder.setCityId(TainanConstant.CITY_ID);

            Tainan1999Service service = restAdapter.create(Tainan1999Service.class);
            service.queryReports(builder.build())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(queryResponse -> {
                       LogUtils.d(TAG, "callback onSuccess");
                       if (queryResponse.getReturncode() == 0) { //success
                           List<Record> records = queryResponse.getRecords();
                           if (isNotEmpty(records)) {
                               LogUtils.d(TAG, "data count : ", records.size());
                               if (mQueryRequestArrayAdapter == null) {
                                   mQueryRequestArrayAdapter = new QueryRequestArrayAdapter(MyActivity.this, records);
                                   setListAdapter(mQueryRequestArrayAdapter);
                                   getListView().setOnItemClickListener(mQueryRequestArrayAdapter);
                               } else {
                                   mQueryRequestArrayAdapter.addAll(records);
                                   mQueryRequestArrayAdapter.updateRequestList(records);
                               }
                           }
                       } else {
                           LogUtils.e(TAG, "error");
                           //TODO: error handle??
                       }
                       showProgressOnActionBar(false);
                   }, err -> {
                       showProgressOnActionBar(false);
                       LogUtils.e(TAG, err.getMessage());
                   });
        } else {
            showProgressOnActionBar(false);
            LogUtils.d(TAG, "requestIds is null or empty");
        }
    }

    public class QueryRequestArrayAdapter extends ArrayAdapter<Record> implements AdapterView.OnItemClickListener {
        private final LayoutInflater mInflater;
        private final int mResource;
        private final List<Record> mRequestList = Lists.newArrayList();
        private QueryRequestArrayAdapter(Context context, List<Record> objects) {
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
            Record r = getItem(position);
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            // image
            holder.cover.setVisibility(View.GONE);
            Observable.from(r.getPictures())
                      .observeOn(Schedulers.io())
                      .filter(pic -> !TextUtils.isEmpty(pic.getFile()) || pic.getFilePath() != null)
                      .doOnNext(pic -> pic.doPrepareImage(getContext()))
                    .first() //We only need 1 pic
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pic -> Picasso.with(getContext())
                           .load(new File(pic.getFilePath()))
                           .fit()
                           .centerCrop()
                           .into(holder.cover, new Callback() {
                               @Override
                               public void onSuccess() {
                                   holder.cover.setVisibility(View.VISIBLE);
                               }

                               @Override
                               public void onError() {
                                   LogUtils.w(TAG, "onError");
                               }
                           }), err -> LogUtils.e(TAG, err.getMessage())

                    );
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
            @InjectView(R.id.service_name)
            TextView service_name;
            @InjectView(R.id.datetime)
            TextView datetime;
            @InjectView(R.id.status)
            TextView status;
            @InjectView(R.id.area)
            TextView area;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent i = new Intent(MyActivity.this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_KEY_REQUEST, getItem(position));
            startActivity(i);
        }

        public void updateRequestList(List<Record> list) {
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

    @SuppressWarnings("unused")
    private void setRawRequestIdsForTest() {
        HashSet<String> t = Sets.newHashSet();
        t.add("UN201505110183");
        t.add("UN201505110247");
        PreferenceUtils.setMyRequestIds(this, t);
    }
}