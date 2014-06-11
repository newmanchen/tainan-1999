package tn.opendata.tainan311;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.util.concurrent.ListenableFuture;
import tn.opendata.tainan311.georeportv2.GeoReportV2;
import tn.opendata.tainan311.georeportv2.vo.Request;
import tn.opendata.tainan311.utils.MainThreadExecutor;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by sam on 2014/6/11.
 */
public class PickMapFragment extends WizardFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private GoogleMap map;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PickMapFragment newInstance() {
        PickMapFragment fragment = new PickMapFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
        return fragment;
    }

    private PickMapFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(22.997144,120.212966),13); //台南火車站

        map.moveCamera(center);


        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(myLatLng)
                        .zoom(map.getMaxZoomLevel() - 3)
//                        .tilt(30)
                        .build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);

                map.setOnMyLocationChangeListener(null);  //once...
            }
        });


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -5);


        GeoReportV2.QueryRequestBuilder builder = GeoReportV2.QueryRequestBuilder.create()
                .open().build();

        final ListenableFuture<List<Request>> future = builder.execute();

        future.addListener(new Runnable() {
            @Override
            public void run() {
                Log.d("Sam","aaa");
                try {
                    for(Request r: future.get()){

                        //only display point with Location...
                        if(!TextUtils.isEmpty(r.getLat()) && !TextUtils.isEmpty(r.getLon())){
                            MarkerOptions markerOpt = new MarkerOptions();
                            markerOpt.position(new LatLng(Double.parseDouble(r.getLat()),Double.parseDouble(r.getLon())));
                            markerOpt.title(r.getService_code());
                            markerOpt.snippet(r.getDescription());
                            markerOpt.draggable(false);
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

        map.setMyLocationEnabled(true);
        setReady(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        return rootView;
    }


    @Override
    public Bundle onNextClick(Bundle acc) {
        acc.putParcelable("location", map.getCameraPosition().target);
        return acc;
    }
}
