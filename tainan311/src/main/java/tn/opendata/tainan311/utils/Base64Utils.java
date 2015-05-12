package tn.opendata.tainan311.utils;

import android.util.Base64;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by newman on 5/7/15.
 */
public class Base64Utils {
    private static final String TAG = Base64Utils.class.getSimpleName();
    public static String getBase64FileContent(File file) throws IOException {
        int len = 8192;
        byte[] byteBuffer = new byte[len];
        String dataBase64 = "";
        BufferedInputStream fileInput = null;
        try {
            ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(24576); //8192 * 3 arrange a large array to avoid expanding it frequently
            fileInput = new BufferedInputStream(new FileInputStream(file));
            int c = fileInput.read(byteBuffer);
            while (c > -1) {
                byteArrayBuffer.append(byteBuffer, 0, c);
                c = fileInput.read(byteBuffer);
            }
            // need import org.apache.commons.codec.binary.Base64
            // dataBase64 = new String(Base64.encodeBase64(byteArrayBuffer.toByteArray()));
            dataBase64 = new String(Base64.encodeToString(byteArrayBuffer.toByteArray(), Base64.DEFAULT));
        } finally {
            EasyUtil.close(fileInput);
        }
        return dataBase64;
    }

    public static void decodeBase64(String base64string, String path) {
        LogUtils.d(TAG, "decode, path is ", path);
        LogUtils.d(TAG, "decode, base64string is ", base64string, "  to  ", path);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(Base64.decode(base64string, Base64.DEFAULT));
        } catch (FileNotFoundException e) {
            LogUtils.w(TAG, e.getMessage(), e);
        } catch (IOException e) {
            LogUtils.w(TAG, e.getMessage(), e);
        } finally  {
            EasyUtil.close(fos);
        }
    }
}