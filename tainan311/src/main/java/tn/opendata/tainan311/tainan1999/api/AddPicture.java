package tn.opendata.tainan311.tainan1999.api;

import android.content.Context;
import android.text.TextUtils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.File;

import tn.opendata.tainan311.utils.Base64Utils;

@Element(name="picture")
public class AddPicture {
    @Element(required=false, data=true)
    private String description; //照片描述
    @Element(data=true)
    private String fileName; //檔案名稱
    @Element
    private String file; //檔案資料 (byte格式。)
    private String filePath;

    public AddPicture(){}
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFilePath(){
        return filePath;
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
}