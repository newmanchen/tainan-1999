package tn.opendata.tainan311.tainan1999.api;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="picture")
public class AddPicture {
    @Element(data=true)
    private String description; //照片描述
    @Element(data=true)
    private String fileName; //檔案名稱
    @Element
    private String file; //檔案資料 (byte格式。)

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
}