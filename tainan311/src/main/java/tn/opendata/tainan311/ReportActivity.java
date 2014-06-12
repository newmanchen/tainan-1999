package tn.opendata.tainan311;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.maps.MapFragment;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Locale;

import static tn.opendata.tainan311.utils.EasyUtil.NOT_IMPLELENT;
import static tn.opendata.tainan311.utils.EasyUtil.findView;


public class ReportActivity extends Activity implements WizardFragment.FlowController {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Button nextButton;
    private Button doneButton;
    private Button previousButton;
    private final Bundle data = new Bundle(); //TODO: handle config change
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        previousButton = findView(this, R.id.previous);
        nextButton = findView(this, R.id.next);
        doneButton = findView(this, R.id.done);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (ReportTipsFragment.getIgnorePref(this)){
            //skip tips fragment.
            mViewPager.setCurrentItem(1);
        }

        resetButtonState();
    }

    public void onClick(View v){
        if(v.getId() == R.id.next){
            String tag = makeFragmentName(R.id.pager,mViewPager.getCurrentItem());
            WizardFragment f = (WizardFragment)getFragmentManager().findFragmentByTag(tag);
            data.putAll(f.onNextClick((Bundle)data.clone()));

            int nextIndex = mViewPager.getCurrentItem()+1;
            mViewPager.setCurrentItem(nextIndex,true);

        }else if(v.getId() == R.id.previous){
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1,true);
        }else{
            String tag = makeFragmentName(R.id.pager,mViewPager.getCurrentItem());
            WizardFragment f = (WizardFragment)getFragmentManager().findFragmentByTag(tag);
            data.putAll(f.onNextClick((Bundle)data.clone()));
            finish();
        }
        resetButtonState();
    }

    private void resetButtonState(){
        boolean last = (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() -1);
        boolean first = mViewPager.getCurrentItem() == 0;


        doneButton.setVisibility(last ? View.VISIBLE : View.GONE);
        nextButton.setVisibility(last ? View.GONE : View.VISIBLE);
        previousButton.setVisibility(first ? View.GONE : View.VISIBLE);

//        doneButton.setEnabled(false);
//        nextButton.setEnabled(false);
    }

    @Override
    public void setNextEnabled(boolean enabled) {
        doneButton.setEnabled(true);
        nextButton.setEnabled(true);
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return ReportTipsFragment.newInstance();
                case 3:
                    return PickMapFragment.newInstance();
                case 1:
                    return PickPhotoFragment.newInstance();
                case 2:
                    return ReportDetailFragment.newInstance();
                //TODO: ... add new here...

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;    //TODO: ... don't forget it...
        }


    }


    private static String makeFragmentName(int viewId, int index){
        return "android:switcher:" + viewId + ":" + index;
    }

}
