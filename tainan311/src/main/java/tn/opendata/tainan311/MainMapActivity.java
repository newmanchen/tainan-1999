package tn.opendata.tainan311;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.util.concurrent.ListenableFuture;
import tn.opendata.tainan311.georeportv2.GeoReportV2;
import tn.opendata.tainan311.georeportv2.vo.Request;
import tn.opendata.tainan311.utils.MainThreadExecutor;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static tn.opendata.tainan311.utils.EasyUtil.findView;


public class MainMapActivity extends FragmentActivity implements ListView.OnItemClickListener,DrawerLayout.DrawerListener {

    private CharSequence mTitle;
    private GoogleMap map;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView drawerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        drawerButton = findView(this,R.id.drawer_icon);
        mDrawerLayout = findView(this, R.id.drawer_layout);
        mDrawerList = findView(this, R.id.navigation_drawer);


        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new String[]{
                "全部回報","我的回報","關於我們"}));
        mDrawerList.setOnItemClickListener(this);
        mDrawerLayout.setDrawerListener(this);


        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setBuildingsEnabled(true);

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


    private void showProblems(){
        //TODO: limit to 90 days
        GeoReportV2.QueryRequestBuilder builder = GeoReportV2.QueryRequestBuilder.create().build();
        final ListenableFuture<List<Request>> future = builder.execute();

        future.addListener(new Runnable() {
            @Override
            public void run() {
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
                            map.addMarker(markerOpt);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        switch(position){
            case 0:
                startActivity(new Intent(this,RequestListActivity.class));
                break;
            case 1:
                startActivity(new Intent(this,MyRequestActivity.class));
                break;
            case 2:
                startActivity(new Intent(this,DetailActivity.class));
                break;
        }

        mDrawerLayout.closeDrawer(mDrawerList);

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