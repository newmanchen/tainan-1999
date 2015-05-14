package tn.opendata.tainan311.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;

/**
 * Utility for getting address from lat/long or reverse
 *
 * Created by newman on 5/8/15.
 */
public class LocationUtils {
    private static final String TAG = LocationUtils.class.getSimpleName();

    public static LatLng getLocationFromAddress(Context context, String strAddress){
        LogUtils.d(TAG, "query address = ", strAddress);
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng lat = new LatLng(0d, 0d);
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return lat;
            } else if (address.isEmpty()) {
                return lat;
            }
            Address location = address.get(0);
            lat = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            LogUtils.w(TAG, e.getMessage(), e);
        }
        LogUtils.d(TAG, "result latlng = ", lat);
        return lat;
    }

    /**
     * should be called by background thread
     *
     * @param context context
     * @param location lat and long
     * @param address address string
     * @return the best address of the LatLng
     */
    public static Address getFromLocationName(Context context, LatLng location, String address) {
        List<Address> resultAddress = Lists.newArrayList();
        double precession = 0.05;

        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(context);
            double lat = .0;
            double lng = .0;
            if ( location != null ) {
                lat = location.latitude;
                lng = location.longitude;
            }
            try {
                if (address == null) {// query by lat/lon
                    resultAddress = geocoder.getFromLocation(lat, lng, 5);
                } else {
                    if (lat != 0 && lng != 0) {// query by lat/long and Address
                        resultAddress = geocoder.getFromLocationName(
                                address, 5, lat - precession, lng
                                        - precession, lat + precession, lng
                                        + precession);
                    } else {// request by address
                        resultAddress = geocoder.getFromLocationName(
                                address, 5);
                    }
                }
            } catch (IOException e) {
                LogUtils.e(TAG, e.getMessage(), e);
            }
        }
        return resultAddress.isEmpty() ? null : resultAddress.get(0);
    }
}
