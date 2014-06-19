package tn.opendata.tainan311.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Pair;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created by vincent on 2014/6/10.
 */
public final class ImageUtils {

    public static Optional<String> saveBitmap(Bitmap bitmap) {
        Optional<File> file = createImageFile();
        if ( file.isPresent() ) {
            FileOutputStream fout = null;
            try {
                File f = file.get();
                File parent = f.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                fout = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fout);
                fout.flush();

                return Optional.of(f.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                EasyUtil.close(fout);
            }
        }
        return Optional.absent();
    }

    public static Optional<Bitmap> getBitmapFromIntentData(Context context, Intent data) {
        Uri selectedImageUri = data.getData();
        if ( selectedImageUri == null || context == null ) {
            return Optional.absent();
        }
        final String[] column = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(data.getData(), column, null, null, null);
            if ( cursor != null && cursor.moveToNext() ) {
                final int columnIndex = cursor.getColumnIndex(column[0]);
                final String path = cursor.getString(columnIndex);
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 2;
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                return Optional.of(BitmapFactory.decodeFile(path, opt));
            }
        } finally {
            EasyUtil.close(cursor);
        }
        return Optional.absent();
    }

    public static Optional<File> createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            return Optional.of(File.createTempFile(path, ".jpg", storageDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.absent();
    }

    public static Bitmap cropCenterBitmap(Bitmap srcBmp) {
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }
}
