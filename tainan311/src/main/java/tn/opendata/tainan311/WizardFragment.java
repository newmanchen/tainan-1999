package tn.opendata.tainan311;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by sam on 2014/6/11.
 */
public abstract class WizardFragment extends Fragment{
    private boolean mReady;
    private FlowController controller = DummyController;

    public boolean isReady() {
        return mReady;
    }

    public void setReady(boolean mReady) {
        this.mReady = mReady;
        controller.setNextEnabled(mReady);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof  FlowController){
            controller =  (FlowController) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        controller = DummyController;
    }

    protected Bundle getData() {
        return controller.getData();
    }

    public abstract Bundle onNextClick(Bundle acc);

    public interface FlowController{
        void setNextEnabled(boolean enabled);

        Bundle getData();
    }

    protected  static FlowController DummyController = new FlowController(){

        @Override
        public void setNextEnabled(boolean visible) {}

        @Override
        public Bundle getData() {
            return new Bundle();
        }


    };
}
