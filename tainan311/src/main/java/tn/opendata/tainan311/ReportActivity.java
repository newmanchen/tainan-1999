package tn.opendata.tainan311;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

import com.balysv.materialripple.MaterialRippleLayout;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import butterknife.InjectView;
import tn.opendata.tainan311.utils.FixedSpeedScroller;
import tn.opendata.tainan311.utils.LogUtils;

public class ReportActivity extends Activity implements WizardFragment.FlowController {
    @InjectView(R.id.ripple_previous) MaterialRippleLayout ripplePrevious;
    @InjectView(R.id.previous) Button previousButton;
    @InjectView(R.id.ripple_next) MaterialRippleLayout rippleNext;
    @InjectView(R.id.next) Button nextButton;
    @InjectView(R.id.ripple_done) MaterialRippleLayout rippleDone;
    @InjectView(R.id.done) Button doneButton;
    @InjectView(R.id.pager) ViewPager mViewPager;

    private static final String TAG = ReportActivity.class.getSimpleName();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private final Bundle data = new Bundle(); //TODO: handle config change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.inject(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        if (ReportTipsFragment.getIgnorePref(this)){
            //skip tips fragment.
            mViewPager.setCurrentItem(1);
        }

        mViewPager.setPageTransformer(true,new ZoomOutPageTransformer());

        //scroll speed
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext(),
                    new AccelerateInterpolator());
            field.set(mViewPager, scroller);
            scroller.setmDuration(600);
        } catch (Exception e) {
            LogUtils.w(TAG, e.getMessage(), e);
        }

        resetButtonState();
    }

    public void onClick(View v){
        if(v.getId() == R.id.next) {
            String tag = makeFragmentName(R.id.pager,mViewPager.getCurrentItem());
            WizardFragment f = (WizardFragment)getFragmentManager().findFragmentByTag(tag);
            data.putAll(f.onNextClick((Bundle)data.clone()));

            int nextIndex = mViewPager.getCurrentItem()+1;
            mViewPager.setCurrentItem(nextIndex, true);
        } else if(v.getId() == R.id.previous) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1,true);
        } else {
            String tag = makeFragmentName(R.id.pager,mViewPager.getCurrentItem());
            WizardFragment f = (WizardFragment)getFragmentManager().findFragmentByTag(tag);

            try {
                data.putAll(f.onNextClick((Bundle) data.clone()));
                showConfirmDialog();
//                createNewRequest();
//                finish();
            } catch(IllegalStateException e) {
               LogUtils.w(TAG, e.getMessage(), e);
            }
        }
        resetButtonState();
    }

    @Override
    public void onBackPressed() {
        boolean first = mViewPager.getCurrentItem() == 0;
        boolean ifIgnoreTips = (ReportTipsFragment.getIgnorePref(this) == true && mViewPager.getCurrentItem() == 1);

        if(first || ifIgnoreTips){
          super.onBackPressed();
        }else{
            previousButton.performClick();
        }
    }

    private void showConfirmDialog() {
        ConfirmDialog cd = new ConfirmDialog();
        Bundle b = new Bundle();
        b.putParcelable(EXTRA_BUNDLE, data);
        cd.setArguments(b);
        cd.show(getFragmentManager(), TAG_DIALOG_CONFIRM);
    }

    private void createNewRequest() {
        Intent intent = new Intent(ReportActivity.this, NewRequestIntentService.class);
        intent.putExtra(NewRequestIntentService.EXTRA_DATA, data);
        startService(intent);
    }

    private void resetButtonState(){
        boolean last = (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() -1);
        boolean first = mViewPager.getCurrentItem() == 0;

        if (first){
            nextButton.setText(R.string.next_to_report);
        } else if (mViewPager.getCurrentItem() == 1){
            nextButton.setText(R.string.next);
            previousButton.setText(R.string.previous_tips);
        } else {
            nextButton.setText(R.string.next);
            previousButton.setText(R.string.previous);
        }

        rippleDone.setVisibility(last ? View.VISIBLE : View.GONE);
        doneButton.setVisibility(last ? View.VISIBLE : View.GONE);
        rippleNext.setVisibility(last ? View.GONE : View.VISIBLE);
        nextButton.setVisibility(last ? View.GONE : View.VISIBLE);
        ripplePrevious.setVisibility(first ? View.GONE : View.VISIBLE);
        previousButton.setVisibility(first ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setNextEnabled(boolean enabled) {
        doneButton.setEnabled(true);
        nextButton.setEnabled(true);
    }

    @Override
    public Bundle getData() {
        return data;
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
                case 1:
                    return PickMapFragment.newInstance();
                case 2:
                    return PickPhotoFragment.newInstance();
                case 3:
                    return ReportDetailFragment.newInstance();
                //TODO: ... add new here...
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;    //TODO: ... don't forget it...
        }
    }

    private static String makeFragmentName(int viewId, int index){
        return "android:switcher:" + viewId + ":" + index;
    }

    private static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.7f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    private static final String TAG_DIALOG_CONFIRM = "TAG_DIALOG_CONFIRM";
    private static final String EXTRA_BUNDLE = "extra_bundle";
    public static class ConfirmDialog extends DialogFragment {
        Context context;
        Bundle sentData;

        public ConfirmDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle bundle) {
            Bundle bb = getArguments();
            final Context context = getActivity();
            sentData = (Bundle) bb.get(EXTRA_BUNDLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.text_confirm)
            .setMessage(R.string.text_confirm_message)
            .setPositiveButton(R.string.text_button_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Activity act = getActivity();
                    if (act != null) {
                        Intent intent = new Intent(context, NewRequestIntentService.class);
                        intent.putExtra(NewRequestIntentService.EXTRA_DATA, sentData);
                        act.startService(intent);
                    }
                }
            })
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            return builder.create();
        }
    }
}
