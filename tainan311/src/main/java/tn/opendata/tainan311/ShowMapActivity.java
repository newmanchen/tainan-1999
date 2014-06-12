package tn.opendata.tainan311;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
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


public class ShowMapActivity extends Activity {

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setBuildingsEnabled(true);

        //TODO:switch
        //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(22.997144, 120.212966), 13); //台南火車站

        map.moveCamera(center);


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

        //TODO: limit to 90 days
        GeoReportV2.QueryRequestBuilder builder = GeoReportV2.QueryRequestBuilder.create().build();

        final ListenableFuture<List<Request>> future = builder.execute();

        future.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    for(Request r: future.get()){

                        //only display point with Location...
                        if(!TextUtils.isEmpty(r.getLat()) && !TextUtils.isEmpty(r.getLon())){
                            MarkerOptions markerOpt = new MarkerOptions();
                            markerOpt.position(new LatLng(Double.parseDouble(r.getLat()),Double.parseDouble(r.getLon())));
                            markerOpt.title(r.getService_code());
                            markerOpt.snippet(r.getDescription());
                            markerOpt.draggable(false);

                            float color = "closed".equals(r.getStatus()) ? BitmapDescriptorFactory.HUE_GREEN :BitmapDescriptorFactory.HUE_RED;


                            markerOpt.icon(BitmapDescriptorFactory.defaultMarker(color));
                            map.addMarker(markerOpt);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        }, MainThreadExecutor.build());

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
