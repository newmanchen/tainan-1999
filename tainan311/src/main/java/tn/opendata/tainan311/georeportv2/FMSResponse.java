package tn.opendata.tainan311.georeportv2;

import android.text.TextUtils;

/**
 * Created by vincent on 2014/6/11.
 */
public class FMSResponse {
    private String success;
    private FMSError errors;


    private class FMSError {
        private String category;
        private String photo;

        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return category;
        }

        public String getPhoto() {
            return photo;
        }
    }

    public FMSResponse() {
    }

    public boolean isSuccess() {
        return "1".equals(success);
    }

    public String getError() {
        return (errors != null)? errors.toString():"null";
    }

    @Override
    public String toString() {
        return (isSuccess())? "success":getError();
    }
}
