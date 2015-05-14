package tn.opendata.tainan311;

import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import tn.opendata.tainan311.utils.LogUtils;

/**
 * To pick a location on map to retrieve the lat/long to server
 *
 * Created by sam on 2014/6/11.
 */
public class PickMapFragment extends WizardFragment implements View.OnClickListener{
    private static final String TAG = PickMapFragment.class.getSimpleName();
    private GoogleMap map;
    private Handler handler = new Handler();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PickMapFragment newInstance() {
        return new PickMapFragment();
    }

    public PickMapFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        map = getMapFragment().getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(22.997144, 120.212966), 13); //台南火車站

        map.moveCamera(center);
        map.setOnMyLocationChangeListener(location -> {
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLatLng)
                    .zoom(map.getMaxZoomLevel() - 3)
//                        .tilt(30)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, null);
            map.setOnMyLocationChangeListener(null);
        });
        map.setMyLocationEnabled(true);
        setReady(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        rootView.findViewById(R.id.btn_map_type).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();

        //workaround for nested MapFragment
        handler.postAtTime(() -> {
            FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
            ft.remove(getMapFragment());
            ft.commit();
        }, "removemap", 300) ;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //workaround for nested MapFragment
        handler.removeCallbacksAndMessages("removemap");
    }

    private MapFragment getMapFragment() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            LogUtils.d(TAG, "using getFragmentManager");
            return (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        } else {
            LogUtils.d(TAG, "using getChildFragmentManager");
            return (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        }
    }

    @Override
    public Bundle onNextClick(Bundle acc) {
        /*
        Optional<File> of = ImageUtils.createImageFile();
        if ( of.isPresent()) {
            final File file = of.get();
            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                private Bitmap bitmap;

                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    bitmap = snapshot;
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            map.snapshot(callback);
            acc.putString("map_photo", file.getAbsolutePath());
        }
        */
        acc.putParcelable(NewRequestIntentService.EXTRA_LOCATION, map.getCameraPosition().target);

        return acc;
    }

    @Override
    public void onClick(View v) {
        if (map.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else {
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }
}