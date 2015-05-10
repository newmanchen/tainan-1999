package tn.opendata.tainan311.tainan1999.api;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * returncode; //0表示成功，其它表示不成功
 * description; //錯誤說明【操作失敗才會顯示】
 * stacktrace; //錯誤Log【操作失敗才會顯示】
 * count; //結果筆數
 * records; //多筆查詢結果
 * record; //每筆資料
 * service_request_id; //案件編號
 * requested_datetime; //反映日期
 * status; //案件狀態
 * keyword; //案件描述
 * area; //行政區
 * service_name; //案件類型
 * agency; //業管單位
 * subproject; //案件事項
 * description; //案件內容
 * address_string; //地點
 * lat; //緯度
 * long; //經度
 * service_notice; //服務案件說明
 * updated_datetime; //結案日期
 * expected_datetime; //預計完成日期
 * pictures; //多筆處理前照片
 * picture; //每筆處理前照片
 * description; //照片描述
 * fileName; //檔案名稱
 * file; //檔案資料 (byte格式。)
 */

@Root(name = "root")
public class QueryResponse {
    @Element private int returncode; //; //0表示成功，其它表示不成功
    @Element(required=false)
    private String description; //錯誤說明【操作失敗才會顯示】
    @Element(required=false)
    private String stacktrace; //錯誤Log【操作失敗才會顯示】
    @Element private int count; //結果筆數
    @ElementList private List<Record> records;

    public int getCount() {
        return count;
    }

    public String getDescription() {
        return description;
    }

    public List<Record> getRecords() {
        return records;
    }

    public int getReturncode() {
        return returncode;
    }

    public String getStacktrace() {
        return stacktrace;
    }


}


