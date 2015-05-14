package tn.opendata.tainan311;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tn.opendata.tainan311.utils.Constant;
import tn.opendata.tainan311.utils.LocationUtils;
import tn.opendata.tainan311.utils.LogUtils;
import tn.opendata.tainan311.utils.ProfileUtil;

public class ReportDetailFragment extends WizardFragment {
    @InjectView(R.id.area) Spinner spinner_area;
    @InjectView(R.id.service_name) Spinner spinner_serviceName;
    @InjectView(R.id.subproject) Spinner spinner_subProject;
    @InjectView(R.id.description) TextView textview_description;
    @InjectView(R.id.address) TextView textview_address;
    @InjectView(R.id.name) TextView textview_name;
    @InjectView(R.id.phone) TextView textview_phone;
    @InjectView(R.id.email) TextView textview_email;
    @InjectView(R.id.photo) ImageView imageview_photo;
//    @InjectView(R.id.map_snapshot) ImageView imageview_mapview;

    private Handler mNonUiHandler;
    private static final String TAG = ReportDetailFragment.class.getSimpleName();

    public ReportDetailFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
    * number.
    */
    public static ReportDetailFragment newInstance() {
        return new ReportDetailFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread ht = new HandlerThread("nonUi");
        ht.start();
        mNonUiHandler = new Handler(ht.getLooper());
    }

    @Override
    public void onDestroy() {
        mNonUiHandler.getLooper().quit();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_detail, container, false);
        ButterKnife.inject(this, rootView);

        //TODO: should query from Server (service_code)
        String[] serviceNameArray = getResources().getStringArray(R.array.service_name);
        ArrayAdapter<String> serviceNameAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, serviceNameArray);
        spinner_serviceName.setAdapter(serviceNameAdapter);
        spinner_serviceName.setOnItemSelectedListener(mOnItemSelectListener);
        String[] subProjectArray = getResources().getStringArray(R.array.subproject_pv);
        ArrayAdapter<String> subprojectAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, subProjectArray);
        spinner_subProject.setAdapter(subprojectAdapter);

        String[] areaArray = getResources().getStringArray(R.array.area);
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, areaArray);
        spinner_area.setAdapter(areaAdapter);

        mNonUiHandler.post(() -> loadPreSetupFields());
        return rootView;
    }

    private AdapterView.OnItemSelectedListener mOnItemSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            int arrayResId = R.array.subproject_pv; // default and case 0
            switch (position) {
                case 1:
                    arrayResId = R.array.subproject_sb;
                    break;

                case 2:
                    arrayResId = R.array.subproject_n;
                    break;

                case 3:
                    arrayResId = R.array.subproject_ao;
                    break;

                case 4:
                    arrayResId = R.array.subproject_r;
                    break;

                case 5:
                    arrayResId = R.array.subproject_t;
                    break;

                case 6:
                    arrayResId = R.array.subproject_ap;
                    break;

                case 7:
                    arrayResId = R.array.subproject_p;
                    break;

                case 8:
                    arrayResId = R.array.subproject_ar;
                    break;
            }
            String[] subProjectArray = getResources().getStringArray(arrayResId);
            ArrayAdapter<String> subprojectAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, subProjectArray);
            spinner_subProject.setAdapter(subprojectAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private void loadPreSetupFields() {
        Activity context = getActivity();
        if (context != null ) {
            SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);

            String account = prefs.getString(SettingActivity.PREFS_KEY_DEFAULT_EMAIL, "");
            final String name = prefs.getString(SettingActivity.PREFS_KEY_DEFAULT_NAME, "");
            String tempPhone = prefs.getString(SettingActivity.PREFS_KEY_DEFAULT_PHONE, "");

            if (TextUtils.isEmpty(account) ) {
                // first time...
                account = ProfileUtil.getEmailAccount(context);
            }

            if (TextUtils.isEmpty(tempPhone)) {
                // first time
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                tempPhone = tm.getLine1Number();
            }
            LogUtils.d(TAG, "tempPhone, ", tempPhone);
            final String mail = account;
            final String phone = tempPhone;
            context.runOnUiThread(() -> {
                textview_email.setText(mail);
                String nameValue = name;
                if (TextUtils.isEmpty(name)) {
                    nameValue = ProfileUtil.getUserName(getActivity());
                }
                textview_name.setText(nameValue);
                textview_phone.setText(phone);
            });

            Bundle b = getData();
            LatLng location = b.getParcelable(NewRequestIntentService.EXTRA_LOCATION);
            final Address address = LocationUtils.getFromLocationName(context, location, null);
            LogUtils.d(TAG, "address is ", address.getAddressLine(0));
            context.runOnUiThread(() -> textview_address.setText(address.getAddressLine(0)));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ( isVisibleToUser ) {
            Bundle data = getData();
            if (data.containsKey(NewRequestIntentService.EXTRA_PHOTO)) {
                // put this to non ui thread
                String path = data.getString(NewRequestIntentService.EXTRA_PHOTO);
                if (!TextUtils.isEmpty(path)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageview_photo.setImageBitmap(bitmap);
                    imageview_photo.setVisibility(View.VISIBLE);
                }
            } else {
                imageview_photo.setVisibility(View.GONE);
            }
            /*
            if (data.containsKey("map_photo")) {
                Bitmap bitmap = BitmapFactory.decodeFile(data.getString("map_photo"));
                mapview.setImageBitmap(bitmap);
            }
            */
        }
    }

    @Override
    public Bundle onNextClick(Bundle acc) {
        boolean throwsException = false;
        acc.putString(NewRequestIntentService.EXTRA_AREA, String.valueOf(spinner_area.getSelectedItem()));
        acc.putString(NewRequestIntentService.EXTRA_SERVICE_NAME, String.valueOf(spinner_serviceName.getSelectedItem()));
        acc.putString(NewRequestIntentService.EXTRA_SUBPROJECT, String.valueOf(spinner_subProject.getSelectedItem()));

        final Context context = getActivity();
        String string_must_have = context.getString(R.string.must_have);
        // check description
        final String description = this.textview_description.getText().toString();
        if (TextUtils.isEmpty(description)) {
            this.textview_description.setError(string_must_have);
            throwsException = true;
        } else {
            acc.putString(NewRequestIntentService.EXTRA_DESCRIPTION, description);
        }
        // check address
        final String address = this.textview_address.getText().toString();
        if (TextUtils.isEmpty(address)) {
            this.textview_address.setError(string_must_have);
            throwsException = true;
        } else {
            acc.putString(NewRequestIntentService.EXTRA_ADDRESS, address);
        }
        // check name
        final String name = this.textview_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            this.textview_name.setError(string_must_have);
            throwsException = true;
        } else {
            acc.putString(NewRequestIntentService.EXTRA_NAME, name);
        }
        // check phone
        final String phone = this.textview_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            this.textview_phone.setError(string_must_have);
            throwsException = true;
        } else {
            acc.putString(NewRequestIntentService.EXTRA_PHONE, phone);
        }

        // check here to make sure that all the mush have fields are ready
        if (throwsException) {
            throw new IllegalStateException("Some fields are empty");
        }

        // email is optional field
        final String email = this.textview_email.getText().toString();
        acc.putString(NewRequestIntentService.EXTRA_EMAIL, email);

        mNonUiHandler.post(() -> {
            SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(SettingActivity.PREFS_KEY_DEFAULT_NAME, name);
            editor.putString(SettingActivity.PREFS_KEY_DEFAULT_PHONE, phone);
            editor.putString(SettingActivity.PREFS_KEY_DEFAULT_EMAIL, email);
            editor.apply();
        });
        return acc;
    }
}