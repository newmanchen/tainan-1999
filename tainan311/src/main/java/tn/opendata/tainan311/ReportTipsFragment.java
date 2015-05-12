package tn.opendata.tainan311;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tn.opendata.tainan311.utils.PreferenceUtils;

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

    public ReportTipsFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setReady(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        boolean isIgnore = PreferenceUtils.getIgnorePref(getActivity());
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
            PreferenceUtils.setIgnoreTipPref(getActivity(), mCheck_ignore_tips.isChecked());
    }
}