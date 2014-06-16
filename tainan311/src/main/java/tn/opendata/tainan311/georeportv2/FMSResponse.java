package tn.opendata.tainan311.georeportv2;

import android.text.TextUtils;

/**
 * Created by vincent on 2014/6/11.
 */
public class FMSResponse {
    private String success;
    private String report;
    //private FMSError errors;
    private String errors;


    private class FMSError {
        private String category;
        private String photo;
        private String password;

        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            if ( category != null ) {
                return category;
            }
            if ( photo != null ) {
                return photo;
            }
            if ( password != null ) {
                return password;
            }
            return "Unknown error";
        }

        public String getPhoto() {
            return photo;
        }

        public String getPassword() {
            return password;
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
