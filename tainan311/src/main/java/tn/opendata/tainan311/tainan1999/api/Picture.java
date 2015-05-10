package tn.opendata.tainan311.tainan1999.api;

import android.os.Parcel;
import android.os.Parcelable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="Picture")
public class Picture implements Parcelable{
    @Element (required=false)
    private String description; //照片描述

    @Element private String fileName; //檔案名稱
    @Element private String file;//檔案資料 (byte格式。)


    public Picture(){}
    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }

    public String getFile() {
        return file;
    }


    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }
        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    private Picture(Parcel in) {
        description = in.readString();
        fileName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(description);
        parcel.writeString(fileName);
    }
}
