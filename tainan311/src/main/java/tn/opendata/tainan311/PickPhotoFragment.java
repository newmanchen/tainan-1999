package tn.opendata.tainan311;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.base.Optional;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import tn.opendata.tainan311.utils.ImageUtils;
import tn.opendata.tainan311.utils.LogUtils;

/**
 * To pick a photo or take a picture for adding to report
 *
 * Created by sam on 2014/6/11.
 */
public class PickPhotoFragment extends WizardFragment {
    private static final String TAG = PickPhotoFragment.class.getSimpleName();
    @InjectView(R.id.photo) ImageView mPhotoView;
//    @InjectView(R.id.album) ImageButton mPickPhoto;
//    @InjectView(R.id.camera) ImageButton mTakePhoto;

    private static final int REQUEST_CODE_IMAGE_PICKER = 0x1;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 0x2;
    private String mPhoto;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PickPhotoFragment newInstance() {
        return new PickPhotoFragment();
    }

    public PickPhotoFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setReady(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pick_photo, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == Activity.RESULT_OK ) {
            if ( requestCode == REQUEST_CODE_IMAGE_PICKER ) {
                Optional<Bitmap> bitmap = ImageUtils.getBitmapFromIntentData(getActivity(), data);
                if (bitmap.isPresent()) {
                    Bitmap bmp = bitmap.get();
                    mPhotoView.setImageBitmap(bmp); // scaled
                    int width = bmp.getWidth();
                    int height = (int) ((1280f/(float)width)*bmp.getHeight());
                    bmp = Bitmap.createScaledBitmap(bmp, 1280, height, false);
                    mPhoto = getImagePath(bmp);
                }
            } else if ( requestCode == REQUEST_CODE_IMAGE_CAPTURE ) {
                Bundle bundle = data.getExtras();
                Bitmap imageBitmap;
                if ( bundle != null && (imageBitmap = (Bitmap)bundle.get("data")) != null ) {
                    int width = imageBitmap.getWidth();
                    int height = (int) ((1280f/(float)width)*imageBitmap.getHeight());
                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 1280, height, false);
                    mPhotoView.setImageBitmap(imageBitmap);
                    mPhoto = getImagePath(imageBitmap);
                }
            }
        }
    }

    private String getImagePath(Bitmap bmp) {
        Optional<File> file = ImageUtils.saveBitmap(bmp);
        if (file.isPresent()) {
            checkBitmapSize(file.get());
            return file.get().getAbsolutePath();
        } else {
            LogUtils.d(TAG, "no image file");
            return null;
        }
    }

    private void checkBitmapSize(File bmp) {
        long bmpSize = bmp.length();
        LogUtils.d(TAG, "bmpSize@", bmp.getAbsoluteFile(), " is ", bmpSize);
        if (bmpSize > 10000000) {
            setReady(false);
            Activity act = getActivity();
            if (act != null) {
                Toast.makeText(act, R.string.less_than_one_mb, Toast.LENGTH_SHORT).show();
            }
        } else {
            setReady(true);
        }
    }

    @OnClick(R.id.album)
    public void onPickImageClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER);
    }

    @OnClick(R.id.camera)
    public void onTakePhotoClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Activity activity = getActivity();
        if (activity != null) {
            PackageManager pm = activity.getPackageManager();
            if (pm != null && intent.resolveActivity(pm) != null) {
                startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);
            } else {
                Toast.makeText(activity, "no camera app", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Bundle onNextClick(Bundle acc) {
        acc.putString(NewRequestIntentService.EXTRA_PHOTO, mPhoto);
        return acc;
    }
}