package tn.opendata.tainan311;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tn.opendata.tainan311.utils.Constant;

/**
 * Created by sam on 2014/6/11.
 */
public class ReportTipsFragment extends WizardFragment {
    @InjectView(R.id.checkbox_ignore_tips) CheckBox mCheck_ignore_tips;
    private static final String ARG_SECTION_NUMBER = "section_number";
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ReportTipsFragment newInstance() {
        ReportTipsFragment fragment = new ReportTipsFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
        return fragment;
    }

    private ReportTipsFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setReady(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        boolean isIgnore = ReportTipsFragment.getIgnorePref(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_tips, container, false);
        ButterKnife.inject(this, rootView);
        mCheck_ignore_tips.setChecked(isIgnore);
        return rootView;
    }

    @Override
    public Bundle onNextClick(Bundle acc) {
        return acc;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mCheck_ignore_tips !=null)
            setIgnoreTipPref(getActivity(),mCheck_ignore_tips.isChecked());
    }

    public static boolean getIgnorePref(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constant.PREF_NAME, 0);
        return !prefs.getBoolean(Constant.KEY_SHOW_TIPS, true); // default show tip
    }

    private void setIgnoreTipPref(Context context,boolean ignore){
        SharedPreferences.Editor prefs = context.getSharedPreferences(Constant.PREF_NAME, 0).edit();
        prefs.putBoolean(Constant.KEY_SHOW_TIPS, !ignore);
        prefs.commit();
    }
}