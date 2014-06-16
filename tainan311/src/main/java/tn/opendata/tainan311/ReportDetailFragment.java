package tn.opendata.tainan311;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.common.base.Optional;
import tn.opendata.tainan311.utils.AccountUtils;
import tn.opendata.tainan311.utils.Constant;
import tn.opendata.tainan311.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;


public class ReportDetailFragment extends WizardFragment {

    private TextView mName;
    private TextView mEmail;
    private TextView mTitle;
    private TextView mDetail;
    private TextView mPassword;
    private Spinner mCategory;
    private ImageView photo;
    private ImageView mapview;

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
        mName = (TextView)rootView.findViewById(R.id.name);
        mEmail = (TextView)rootView.findViewById(R.id.email);
        mTitle = (TextView)rootView.findViewById(R.id.title);
        mDetail = (TextView)rootView.findViewById(R.id.detail);
        mPassword = (TextView)rootView.findViewById(R.id.password);
        mCategory = (Spinner)rootView.findViewById(R.id.category);


        ArrayList<String> spinArray = new ArrayList<String>();
        spinArray.add("others");
        spinArray.add("市容整潔");
        spinArray.add("號誌與路燈故障");
        spinArray.add("路霸與騎樓佔用");
        spinArray.add("道路問題");
        spinArray.add("其他");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, spinArray);
        mCategory.setAdapter(adapter);

        photo = (ImageView) rootView.findViewById(R.id.photo);
        mapview = (ImageView) rootView.findViewById(R.id.map_snapshot);

        mNonUiHandler.post(new Runnable() {
            @Override
            public void run() {
                loadPreference();
            }
        });

        return rootView;
    }

    private void loadPreference() {
        Activity context = getActivity();


        if (context != null ) {
            SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);

            String account = prefs.getString(Constant.KEY_ACCOUNT, "");
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
                    mEmail.setText(mail);
                    String nameValue = name;
                    if (TextUtils.isEmpty(name)) {
                        nameValue = mail.substring(0, mail.indexOf("@"));
                    }
                    mName.setText(nameValue);
                    String passValue = password;
                    if (TextUtils.isEmpty(password)) {
                        passValue = String.valueOf((int)(Math.random()*9000+1000));
                    }
                    mPassword.setText(passValue);
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
        acc.putString("category", String.valueOf(mCategory.getSelectedItem()));
        final String name;
        final String email;
        final String password;
        final Context context = getActivity();
        if ( mName.getText() != null ) {
            name = mName.getText().toString();
            acc.putString("name", name);
        } else {
            name = "";
        }
        if ( mEmail.getText() != null ) {
            email = mEmail.getText().toString();
            acc.putString("email", email);
        } else {
            email = "";
        }
        if ( mTitle.getText() != null ) {
            acc.putString("title", mTitle.getText().toString());
        }
        if ( mDetail.getText() != null ) {
            acc.putString("detail", mDetail.getText().toString());
        }
        if (mPassword.getText() != null) {
            SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);
            password = mPassword.getText().toString();
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
                editor.putString(Constant.KEY_ACCOUNT, email);
                editor.putString(Constant.KEY_PASSWORD, password);
                editor.apply();
            }
        });


        return acc;
    }


}
