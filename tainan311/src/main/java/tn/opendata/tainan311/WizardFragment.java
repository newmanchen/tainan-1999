package tn.opendata.tainan311;

import android.app.Activity;
import android.app.Fragment;

/**
 * Created by sam on 2014/6/11.
 */
public class WizardFragment extends Fragment{
    private boolean mReady;
    private FlowController conroller = DummyController;

    public boolean isReady() {
        return mReady;
    }

    public void setReady(boolean mReady) {
        this.mReady = mReady;
        conroller.setNextEnabled(true);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof  FlowController){
            conroller =  (FlowController) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        conroller = DummyController;
    }


    public static  interface FlowController{
        public void setNextEnabled(boolean enabled);

    }

    protected  static FlowController DummyController = new FlowController(){

        @Override
        public void setNextEnabled(boolean visible) {}
    };
}
