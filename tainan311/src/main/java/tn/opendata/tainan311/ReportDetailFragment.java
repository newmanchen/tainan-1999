package tn.opendata.tainan311;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class ReportDetailFragment extends WizardFragment {

    private TextView name;
    private TextView email;
    private TextView title;
    private TextView detail;
    private Spinner category;
    private ImageView photo;

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_report_detail, container, false);
        name = (TextView)rootView.findViewById(R.id.name);
        name.setText(getDefaultNamePref(getActivity()));
        email = (TextView)rootView.findViewById(R.id.email);
        email.setText(getDefaultEmailPref(getActivity()));
        title = (TextView)rootView.findViewById(R.id.title);
        detail = (TextView)rootView.findViewById(R.id.detail);
        category = (Spinner)rootView.findViewById(R.id.category);
        ArrayList<String> spinArray = new ArrayList<String>();
        spinArray.add("市容整潔");
        spinArray.add("號誌與路燈故障");
        spinArray.add("路霸與騎樓佔用");
        spinArray.add("道路問題");
        spinArray.add("其他");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, spinArray);
        category.setAdapter(adapter);

        photo = (ImageView) rootView.findViewById(R.id.photo);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ( isVisibleToUser ) {
            Bundle data = getData();
            if (data.containsKey("photo")) {
                // put this to non ui thread
                Bitmap bitmap = BitmapFactory.decodeFile(data.getString("photo"));

                photo.setImageBitmap(bitmap);
                photo.setVisibility(View.VISIBLE);
            } else {
                photo.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public Bundle onNextClick(Bundle acc) {
        if ( name.getText() != null ) {
            acc.putString("name", name.getText().toString());
        }
        acc.putString("category", String.valueOf(category.getSelectedItem()));
        if ( email.getText() != null ) {
            acc.putString("email", email.getText().toString());
        }
        if ( title.getText() != null ) {
            acc.putString("title", title.getText().toString());
        }
        if ( detail.getText() != null ) {
            acc.putString("detail", detail.getText().toString());
        }

        return acc;
    }

    private static final String PREFS_NAME = "tn.opendata.tainan311_preferences";
    private static final String PREFS_KEY_DEFAULT_NAME = "reporter_name_preference";
    private static final String PREFS_KEY_DEFAULT_EMAIL = "reporter_email_preference";

    public static String getDefaultNamePref(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREFS_KEY_DEFAULT_NAME, "");
    }

    public static String getDefaultEmailPref(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREFS_KEY_DEFAULT_EMAIL, "");
    }
}