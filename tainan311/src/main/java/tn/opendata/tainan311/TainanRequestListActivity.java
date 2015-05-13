package tn.opendata.tainan311;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tn.opendata.tainan311.tainan1999.api.Picture;
import tn.opendata.tainan311.tainan1999.api.QueryRequest;
import tn.opendata.tainan311.tainan1999.api.Record;
import tn.opendata.tainan311.tainan1999.api.Tainan1999Service;
import tn.opendata.tainan311.tainan1999.util.TainanConstant;
import tn.opendata.tainan311.utils.LogUtils;

import static tn.opendata.tainan311.tainan1999.api.QueryRequest.Builder;
import static tn.opendata.tainan311.utils.EasyUtil.isNotEmpty;

/**
 * Created by newman on 5/5/15.
 */
public class TainanRequestListActivity extends ListActivity {
    private static final String TAG = TainanRequestListActivity.class.getSimpleName();
    @InjectView(R.id.normal_plus)
    AddFloatingActionButton addRequestButton;
    // View
    private LinearLayout mLoadingMoreItem;
    // Object
    private QueryRequestArrayAdapter mQueryRequestArrayAdapter;
    private SimpleDateFormat mSimpleDateFormat;
    // Value
    private boolean mLoadingMore;
    // Constant
    private RestAdapter restAdapter;
    private Calendar cal = Calendar.getInstance();
    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            //is the bottom item visible & not loading more already ? Load more !
            if((lastInScreen == totalItemCount) && !(mLoadingMore)){
                Record r = mQueryRequestArrayAdapter.getItem(mQueryRequestArrayAdapter.getCount()-1);
                String time = r.getRequested_datetime();
                try {
                    Date d = mSimpleDateFormat.parse(time);
                    cal.setTimeInMillis(d.getTime());
                    loadQueryRequest(false);
                } catch (ParseException e) {
                    LogUtils.w(TAG, e.getMessage(), e);
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tainan_query_list);
        ButterKnife.inject(this);
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", getResources().getConfiguration().locale);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(TainanConstant.TAINAN1999_URL)
                .setConverter(new SimpleXMLConverter())
                        //.setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        mLoadingMore = false;
        initActionBar();
        initViews();
        loadQueryRequest(true);
    }

    private void initActionBar() {
        ActionBar ab = getActionBar();
        ab.setTitle(R.string.request_list_title);
//        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        getListView().setDivider(null);
        mLoadingMoreItem = (LinearLayout) ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_loading_more, null);
        getListView().addFooterView(mLoadingMoreItem);

        mQueryRequestArrayAdapter = new QueryRequestArrayAdapter(TainanRequestListActivity.this, new ArrayList<Record>());
        setListAdapter(mQueryRequestArrayAdapter);
        getListView().setOnItemClickListener(mQueryRequestArrayAdapter);
        getListView().setOnScrollListener(mOnScrollListener);
    }

    @OnClick(R.id.normal_plus)
    public void clickAdd(View view) {
        startActivity(new Intent(TainanRequestListActivity.this, ReportActivity.class));
    }

    private void loadQueryRequest(boolean firstTimeQuery) {
        LogUtils.d(TAG, "loadQueryRequest firstTimeQuery:", firstTimeQuery);
        addLoadingMoreListItem();
        Date endDate = cal.getTime();
        if (firstTimeQuery) {
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 1);
        } else {
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }

        QueryRequest request = Builder.create()
                                      .setCityId(TainanConstant.CITY_ID)
                                      .setEndDate(mSimpleDateFormat.format(endDate))
                                      .setStartDate(mSimpleDateFormat.format(cal.getTime()))
                                      .build();

        // LogUtils.d(TAG, "builder.build() is ", builder());

        mLoadingMore = true;

        Tainan1999Service service = restAdapter.create(Tainan1999Service.class);
        service.queryReports(request)
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(queryResponse -> {
                   LogUtils.d(TAG, "callback onSuccess");
                   mLoadingMore = false;
                   removeLoadingMoreListItem();
                   if (queryResponse.getReturncode() == 0) { //success
                       List<Record> records = queryResponse.getRecords();
                       mQueryRequestArrayAdapter.addAll(records);
                   } else {
                       LogUtils.e(TAG, "error");
                       //TODO: error handle??
                   }

               }, err -> {
                   LogUtils.e(TAG, err.getMessage());
                   removeLoadingMoreListItem();
               });
    }

    private void addLoadingMoreListItem() {
        mLoadingMoreItem.setVisibility(View.VISIBLE);
    }

    private void removeLoadingMoreListItem() {
        mLoadingMoreItem.setVisibility(View.GONE);
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

            case R.id.menu_my_activity:
                startActivity(new Intent(this, MyActivity.class));
                break;

            case R.id.menu_license:
                startActivity(new Intent(this, GoogleSoftwareLicenseInfo.class));
                break;

            case R.id.menu_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        return true;
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
            holder.cover.setVisibility(View.INVISIBLE);
            Observable.from(r.getPictures())
                      .observeOn(Schedulers.io())
                      .filter(pic -> isNotEmpty(pic.getFile()) || isNotEmpty(pic.getFilePath()))
                      .doOnNext(pic -> pic.doPrepareImage(getContext()))
                      .toList()
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(pics -> {
                                  Picture pic = pics.get(0);
                                  Picasso.with(getContext())
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
                                         });
                              }, err -> LogUtils.e(TAG, err.getMessage())

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

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Intent i = new Intent(TainanRequestListActivity.this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_KEY_REQUEST, getItem(position));
            startActivity(i);
        }
    }

    public class ViewHolder {
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

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}