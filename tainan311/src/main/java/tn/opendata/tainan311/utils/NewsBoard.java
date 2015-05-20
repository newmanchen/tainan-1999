package tn.opendata.tainan311.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by Vincentnien on 15/5/18.
 */
public class NewsBoard {
    private static final String TAG = NewsBoard.class.getSimpleName();
    private static final String SHARD_PREF = "update_message";

    public enum CheckMethod {
        LAST_MODIFIED,
        GITHUB_ETAG,
        MD5
    }

    private WeakReference<Activity> activity;
    private MaterialDialog mMaterialDialog;

    private String url;
    private int negativeId = -1;
    private int positiveId = -1;
    private int titleId = -1;

    private View.OnClickListener negativeClickListener = null;
    private View.OnClickListener positiveClickListener = null;
    private boolean negativeDefaultAction = false;
    private boolean positiveDefaultAction = false;

    private CheckMethod method = CheckMethod.LAST_MODIFIED;

    private static class NewsMessage {
        boolean isNew;
        String message;
        String data;

        private NewsMessage() {
            isNew = false;
        }

        public static NewsMessage empty() {
            return new NewsMessage();
        }

        public static NewsMessage create(boolean isNew, String message, String lastModified) {
            return new NewsMessage(isNew, message, lastModified);
        }

        private NewsMessage(boolean isNew, String message, String data) {
            this.isNew = isNew;
            this.message = message;
            this.data = data;
        }
    }

    private NewsBoard(Activity activity) {
        this.activity = new WeakReference<>(activity);
    }

    public NewsBoard withUrl(String url) {
        this.url = url;
        return this;
    }

    public NewsBoard addPositiveButton(int id, View.OnClickListener listener) {
        positiveId = id;
        positiveClickListener = listener;
        return this;
    }

    public NewsBoard addPositiveButton(int id) {
        positiveId = id;
        positiveDefaultAction = true;
        return this;
    }

    public NewsBoard addNegativeButton(int id, View.OnClickListener listener) {
        negativeId = id;
        negativeClickListener = listener;
        return this;
    }

    public NewsBoard addNegativeButton(int id) {
        negativeId = id;
        negativeDefaultAction = true;
        return this;
    }

    public NewsBoard checkMethod(CheckMethod method) {
        this.method = method;
        return this;
    }

    public NewsBoard titleId(int titleId) {
        this.titleId = titleId;
        return this;
    }

    public MaterialDialog run() throws IOException {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url cannot be empty.");
        }

        final NewsMessage msg = readMessage();
        if ( msg.isNew ) {
            // update last modified time
            SharedPreferences pref = activity.get().getSharedPreferences(SHARD_PREF, Context.MODE_PRIVATE);
            pref.edit().putString("lastModified", msg.data).apply();

            if (!isEmpty(msg.message)) {
                activity.get().runOnUiThread(() -> {
                    mMaterialDialog = new MaterialDialog(activity.get());
                    mMaterialDialog.setMessage(msg.message);
                    if (titleId != -1) {
                        mMaterialDialog.setTitle(titleId);
                    }
                    if (negativeId != -1) {
                        if (negativeDefaultAction) {
                            mMaterialDialog.setNegativeButton(negativeId, v -> {
                                mMaterialDialog.dismiss();
                            });
                        } else {
                            mMaterialDialog.setNegativeButton(negativeId, negativeClickListener);
                        }
                    }
                    if (positiveId != -1) {
                        if (positiveDefaultAction) {
                            mMaterialDialog.setPositiveButton(positiveId, v -> {
                                mMaterialDialog.dismiss();
                            });
                        } else {
                            mMaterialDialog.setPositiveButton(positiveId, positiveClickListener);
                        }
                    }
                    mMaterialDialog.show();
                });
            } else {
                LogUtils.d(TAG, "no news - empty news");
            }
            return mMaterialDialog;
        } else {
            LogUtils.d(TAG, "no news - the same key");
            return mMaterialDialog;
        }
    }

    private NewsMessage readMessage() {
        try {
            Activity ref = activity.get();
            SharedPreferences pref = ref.getSharedPreferences(SHARD_PREF, Context.MODE_PRIVATE);
            String lastdataInPref = pref.getString("lastModified", "");

            URL urlObj = new URL(url);
            URLConnection connection = urlObj.openConnection();
            String fromHeader = null;
            if (method == CheckMethod.LAST_MODIFIED) {
                fromHeader = "Last-Modified";
            } else if ( method == CheckMethod.GITHUB_ETAG ) {
                fromHeader = "ETag";
            } else {
                // not supported for now
            }

            if ( fromHeader != null ) {
                String data = connection.getHeaderField(fromHeader);
                if (!lastdataInPref.equals(data)) {
                    LogUtils.w(TAG, "" + data);
                    // only support text
                    StringBuilder sb = new StringBuilder();
                    BufferedReader in = null;
                    try {
                        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                            sb.append(inputLine).append("\n");
                    } finally {
                        EasyUtil.close(in);
                    }
                    LogUtils.w(TAG, "message:" + sb.toString());
                    return NewsMessage.create(true, sb.toString(), data);
                }
            }
        } catch(IOException e) {
            LogUtils.e(TAG, e.toString(), e);
        }
        return NewsMessage.empty();
    }

    public static NewsBoard create(Activity activity) {
        return new NewsBoard(activity);
    }

    private boolean isEmpty(String message) {
        message = message.replace("\n", "");
        return TextUtils.isEmpty(message);
    }
}