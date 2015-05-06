package tn.opendata.tainan311;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tn.opendata.tainan311.utils.AccountUtils;
import tn.opendata.tainan311.utils.Constant;
import tn.opendata.tainan311.utils.ProfileUtil;

public class ReportDetailFragment extends WizardFragment {
    //FIXME add mush have fields 
    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.email) TextView email;
    @InjectView(R.id.address) TextView address;
    @InjectView(R.id.detail) TextView detail;
    @InjectView(R.id.password) TextView password;
    @InjectView(R.id.service_name) Spinner serviceName;
    @InjectView(R.id.area) Spinner area;
    @InjectView(R.id.subproject) Spinner subProject;
    @InjectView(R.id.photo) ImageView photo;
    @InjectView(R.id.map_snapshot) ImageView mapview;

    private Handler mNonUiHandler;
    private boolean mNeedRegister = false;

    public ReportDetailFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
    * number.
    */
    public static ReportDetailFragment newInstance() {
        ReportDetailFragment fragment = new ReportDetailFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
        return fragment;
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
        ArrayAdapter<String> serviceNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, serviceNameArray);
        serviceName.setAdapter(serviceNameAdapter);
        serviceName.setOnItemSelectedListener(mOnItemSelectListener);
        String[] subProjectArray = getResources().getStringArray(R.array.subproject_pv);
        ArrayAdapter<String> subprojectAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, subProjectArray);
        subProject.setAdapter(subprojectAdapter);

        String[] areaArray = getResources().getStringArray(R.array.area);
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, areaArray);
        area.setAdapter(areaAdapter);

        mNonUiHandler.post(new Runnable() {
            @Override
            public void run() {
                loadPreference();
            }
        });
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
            ArrayAdapter<String> subprojectAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, subProjectArray);
            subProject.setAdapter(subprojectAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private void loadPreference() {
        Activity context = getActivity();
        if (context != null ) {
            SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);

            String account = prefs.getString(Constant.KEY_ACCOUNT_MAIL, "");
            final String name = prefs.getString(Constant.KEY_NAME, "");
            final String password = prefs.getString(Constant.KEY_PASSWORD, "");

            if ( TextUtils.isEmpty(account) ) {
                // first time...
                account = AccountUtils.getEmailAccount(context);
                mNeedRegister = true;
            } else {
                mNeedRegister = false;
            }

            final String mail = account;
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    email.setText(mail);
                    String nameValue = name;
                    if (TextUtils.isEmpty(name)) {
                        //nameValue = mail.substring(0, mail.indexOf("@"));
                        nameValue = ProfileUtil.getUserName(getActivity());
                    }
                    ReportDetailFragment.this.name.setText(nameValue);
                    String passValue = password;
                    if (TextUtils.isEmpty(password)) {
                        passValue = String.valueOf((int)(Math.random()*9000+1000));
                    }
                    ReportDetailFragment.this.password.setText(passValue);
                }
            });
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ( isVisibleToUser ) {
            Bundle data = getData();
            if (data.containsKey("photo")) {
                // put this to non ui thread
                String path = data.getString("photo");
                if (!TextUtils.isEmpty(path)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    photo.setImageBitmap(bitmap);
                    photo.setVisibility(View.VISIBLE);
                }
            } else {
                photo.setVisibility(View.GONE);
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
        //TODO area, subproject
        acc.putString("category", String.valueOf(serviceName.getSelectedItem()));
        final String name;
        final String email;
        final String password;
        final Context context = getActivity();

        name = this.name.getText().toString();

        if (TextUtils.isEmpty(name)) {
            this.name.setError(context.getString(R.string.must_have));
            throw new IllegalStateException();
        } else {
            acc.putString("name", name);
        }

        email = this.email.getText().toString();

        if (TextUtils.isEmpty(email)) {
            this.email.setError(context.getString(R.string.must_have));
            throw new IllegalStateException();
        } else {
            SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);
            String oldEmail = prefs.getString("email", "");
            if (!email.equals(oldEmail)) {
                mNeedRegister = true;
            }
            acc.putString("email", email);
        }

        String address = this.address.getText().toString();

        if ( TextUtils.isEmpty(address)) {
            this.address.setError(context.getString(R.string.must_have));
            throw new IllegalStateException();
        } else {
            acc.putString("title", address);
        }

        if ( detail.getText() != null ) {
            String detail = this.detail.getText().toString();
            if ( TextUtils.isEmpty(detail) ) {
                detail = context.getString(R.string.str_empty);
            }
            acc.putString("detail", detail);
        }
        if (this.password.getText() != null) {
            SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);
            password = this.password.getText().toString();
            String oldPassword = prefs.getString("password", "");
            if (!password.equals(oldPassword)) {
                mNeedRegister = true;
            }
            acc.putString("password", password);
        } else {
            password = "";
        }
        acc.putBoolean("register", mNeedRegister);

        mNonUiHandler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constant.KEY_NAME, name);
                editor.putString(Constant.KEY_ACCOUNT_MAIL, email);
                editor.putString(Constant.KEY_PASSWORD, password);
                editor.apply();
            }
        });
        return acc;
    }
}
