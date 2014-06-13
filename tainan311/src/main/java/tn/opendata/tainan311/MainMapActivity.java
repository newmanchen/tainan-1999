package tn.opendata.tainan311;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Map;

import tn.opendata.tainan311.georeportv2.GeoReportV2;
import tn.opendata.tainan311.georeportv2.vo.Request;
import tn.opendata.tainan311.utils.MainThreadExecutor;

import static tn.opendata.tainan311.utils.EasyUtil.findView;


public class MainMapActivity extends FragmentActivity implements ListView.OnItemClickListener,DrawerLayout.DrawerListener, GoogleMap.OnInfoWindowClickListener {

    private CharSequence mTitle;
    private GoogleMap map;
    private DrawerLayout mDrawerLayout;
    private ImageView drawerButton;
    private Map<Marker,Request> requestMap = Maps.newConcurrentMap();
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        progress = findView(this,R.id.progress);

        String[] drawer_text = getResources().getStringArray(R.array.drawer_text);

        drawerButton = findView(this,R.id.drawer_icon);
        mDrawerLayout = findView(this, R.id.drawer_layout);
        View mDrawerView = findView(this, R.id.navigation_drawer);
        ListView mDrawerList = findView(this, R.id.navigation_list);


        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_item, drawer_text));
        mDrawerList.setOnItemClickListener(this);
        mDrawerLayout.setDrawerListener(this);


        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setBuildingsEnabled(true);

        map.setOnInfoWindowClickListener(this);


        //TODO:switch
        //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(22.997144, 120.212966), 13); //台南火車站
        map.moveCamera(center);


        animateToMyLocation();

        showProblems();
    }

    //from xml
    public void onDrawerClick(View view){
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    //from xml
    public void onReportClick(View view){
        startActivity(new Intent(this, ReportActivity.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        requestMap.clear();
    }

    private void showProblems(){
        progress.setVisibility(View.VISIBLE);
        //TODO: limit to 90 days
        GeoReportV2.QueryRequestBuilder builder = GeoReportV2.QueryRequestBuilder.create().build();
        final ListenableFuture<List<Request>> future = builder.execute();
        future.addListener(new Runnable() {
            @Override
            public void run() {
                Map<Marker,Request> localMap = Maps.newConcurrentMap();
                try {
                    for (Request r : future.get()) {
                        //only display point with Location...
                        if (!TextUtils.isEmpty(r.getLat()) && !TextUtils.isEmpty(r.getLon())) {
                            MarkerOptions markerOpt = new MarkerOptions();
                            markerOpt.position(new LatLng(Double.parseDouble(r.getLat()), Double.parseDouble(r.getLon())));
                            markerOpt.title(r.getService_code());
                            markerOpt.snippet(r.getDescription());
                            markerOpt.draggable(false);

                            float color = "closed".equals(r.getStatus()) ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED;


                            markerOpt.icon(BitmapDescriptorFactory.defaultMarker(color));
                            Marker m = map.addMarker(markerOpt);
                            localMap.put(m, r);
                        }
                        requestMap = localMap;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    progress.setVisibility(View.GONE);
                }
            }
        }, MainThreadExecutor.build());
    }

    private void animateToMyLocation(){
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(myLatLng)
                        .zoom(map.getCameraPosition().zoom)
                        .build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

                map.setOnMyLocationChangeListener(null);  //once...
            }
        });

        map.setMyLocationEnabled(true);
    }




    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position){   //R.arrays.drawer_text
            case 0:
                startActivity(new Intent(this,RequestListActivity.class));
                break;
            case 1:
                startActivity(new Intent(this,ReportActivity.class));
                break;
            case 2:
                startActivity(new Intent(this,DetailActivity.class));
                break;
            case 3:
                startActivity(new Intent(this, SettingActivity.class));
        }

//        mDrawerLayout.closeDrawer(mDrawerView);

    }



    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailActivity.EXTRA_KEY_REQUEST, requestMap.get(marker));
        startActivity(i);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
          drawerButton.setVisibility(View.GONE);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        drawerButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

}