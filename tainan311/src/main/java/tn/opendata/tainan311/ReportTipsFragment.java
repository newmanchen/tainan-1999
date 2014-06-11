package tn.opendata.tainan311;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sam on 2014/6/11.
 */
public class ReportTipsFragment extends WizardFragment {

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

        //FIXME: only for test..
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                setReady(true);
            }
        }, 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tips, container, false);
        return rootView;
    }

    @Override
    public Bundle onNextClick(Bundle acc) {
        return acc;
    }
}

