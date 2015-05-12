package tn.opendata.tainan311.tainan1999.api;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import tn.opendata.tainan311.utils.Base64Utils;
import tn.opendata.tainan311.utils.LogUtils;

import java.io.File;

@Root(name="Picture")
public class Picture implements Parcelable{
    @Element (required=false)
    private String description; //照片描述

    @Element private String fileName; //檔案名稱
    @Element private String file;//檔案資料 (byte格式。)

    private String filePath;

    public Picture(){}
    public String getFileName() {
        return fileName;
    }

    public String getFilePath(){
        return filePath;
    }

    public String getDescription() {
        return description;
    }

    public String getFile() {
        return file;
    }

    public void doPrepareImage(Context context){
        if(TextUtils.isEmpty(file)){
            return;
        }
        String dataPath = context.getFilesDir().toString() + "/pic/";
        File folder = new File(dataPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        dataPath += System.currentTimeMillis() + ".jpg";
        Base64Utils.decodeBase64(file, dataPath);
        filePath = dataPath;
        file = null; //reduce memory usage
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
        filePath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(description);
        parcel.writeString(fileName);
        parcel.writeString(filePath);
    }

}
