package tn.opendata.tainan311;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.common.base.Optional;
import tn.opendata.tainan311.georeportv2.GeoReportV2;
import tn.opendata.tainan311.utils.ImageUtils;

import java.io.File;

/**
 * Created by sam on 2014/6/11.
 */
public class PickPhotoFragment extends WizardFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final int REQUEST_CODE_IMAGE_PICKER = 0x1;
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 0x2;

    private String mPhoto;
    private ImageView mPhotoView;

    private ImageButton mPickPhoto;
    private ImageButton mTakePhoto;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PickPhotoFragment newInstance() {
        PickPhotoFragment fragment = new PickPhotoFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
        return fragment;
    }

    private PickPhotoFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // user can skip pick photo, so we always set this flag = true
        setReady(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pick_photo, container, false);
        if ( rootView != null ) {
            mPhotoView = (ImageView) rootView.findViewById(R.id.photo);
            mPickPhoto = (ImageButton) rootView.findViewById(R.id.album);
            mTakePhoto = (ImageButton) rootView.findViewById(R.id.camera);

            mPickPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPickImageClick(view);
                }
            });

            mTakePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTakePhotoClick(view);
                }
            });
        }

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == Activity.RESULT_OK ) {

            if ( requestCode == REQUEST_CODE_IMAGE_PICKER ) {
                Uri uri = data.getData();

                Optional<Bitmap> bitmap = ImageUtils.getBitmapFromIntentData(getActivity(), data);
                if (bitmap.isPresent()) {
                    Bitmap bmp = bitmap.get();
                    mPhotoView.setImageBitmap(bmp); //ImageUtils.cropCenterBitmap()
                    mPhoto = getImagePath(bmp);
                }
            } else if ( requestCode == REQUEST_CODE_IMAGE_CAPTURE ) {
                Bundle bundle = data.getExtras();
                Bitmap imageBitmap = null;
                if ( bundle != null && (imageBitmap = (Bitmap)bundle.get("data")) != null ) {
                    mPhotoView.setImageBitmap(ImageUtils.cropCenterBitmap(imageBitmap));
                    mPhoto = getImagePath(imageBitmap);
                }
            }
        }
    }

    private String getImagePath(Bitmap bmp) {
        Optional<String> path = ImageUtils.saveBitmap(bmp);
        if ( path.isPresent() ) {
            return path.get();
        }
        return null;
    }

    public void onPickImageClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER);
    }

    public void onTakePhotoClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Activity activity = getActivity();
        if (activity != null) {
            PackageManager pm = activity.getPackageManager();
            if (pm != null && intent.resolveActivity(pm) != null) {
                /*
                Optional<File> file = ImageUtils.createImageFile();
                if (file.isPresent()) {
                    //forTest = file.get();
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file.get()));

                }
                */
                startActivityForResult(intent, REQUEST_CODE_IMAGE_CAPTURE);

            } else {
                Toast.makeText(activity, "no camera app", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public Bundle onNextClick(Bundle acc) {
        acc.putString("photo", mPhoto);
        return acc;
    }
}

