package tn.opendata.tainan311.tainan1999.util;

/**
 * Created by newman on 5/6/15.
 */
public class TainanConstant {
    public static final String TAINAN1999_URL = "http://open1999.tainan.gov.tw:82";
    // city-id on tainan1999
    public static final String CITY_ID = "tainan.gov.tw";
    // service_name on tainan1999
    public static final String SERVICE_NAME_PARKING_VIOLATION = "違規停車";
    public static final String SERVICE_NAME_STREETLAMP_BREAKDOWN = "路燈故障";
    public static final String SERVICE_NAME_NOISE = "噪音舉發";
    public static final String SERVICE_NAME_ARCADE_OBSTACLE = "騎樓舉發";
    public static final String SERVICE_NAME_ROAD = "道路維修";
    public static final String SERVICE_NAME_TRANSPORTATION = "交通運輸";
    public static final String SERVICE_NAME_POLLUTION = "髒亂及汙染";
    public static final String SERVICE_NAME_PIPELINE = "民生管線";
    public static final String SERVICE_NAME_ANIMAL_RESCUE = "動物救援";

    //TODO fix as array
    // subproject on tainan1999
    // got this table by http://1999.tainan.gov.tw/%E9%A1%9E%E5%9E%8B%E5%8F%8A%E4%BA%8B%E9%A0%85%E7%9A%84%E9%97%9C%E8%81%AF.xls
    // by SERVICE_NAME_PARKING_VIOLATION(PV) : 違規停車
    public static final String[] SUBPROJECT_PV = new String[] { "違規停車" };
    // by SERVICE_NAME_STREETLAMP_BREAKDOWN (SB): 路燈故障
    public static final String[] SUBPROJECT_SB = new String[] { "9盞以下路燈故障", "10盞以上路燈故障" };
    // by SERVICE_NAME_NOISE(N) : 噪音舉發
    public static final String[] SUBPROJECT_N = new String[] { "場所連續噪音", "妨害安寧" };
    // by SERVICE_NAME_ARCADE_OBSTACLE(AO) : 騎樓舉發
    public static final String[] SUBPROJECT_AO = new String[] { "騎樓舉發" };
    // by SERVICE_NAME_ROAD(R) : 道路維修
    public static final String[] SUBPROJECT_R = new String[] { "路面坑洞", "寬頻管線、孔蓋損壞", "路面下陷、凹陷"
            , "路面掏空、塌陷", "路樹傾倒", "地下道積水", "人孔蓋或溝蓋聲響、鬆動", "人孔蓋凹陷坑洞" };
    // by SERVICE_NAME_TRANSPORTATION(T) : 交通運輸
    public static final String[] SUBPROJECT_T = new String[] { "號誌故障", "號誌秒差調整", "公車動態LED跑馬燈資訊顯示異常", "交通疏導", "佔用道路" };
    // by SERVICE_NAME_POLLUTION(AP) : 髒亂及污染
    public static final String[] SUBPROJECT_AP = new String[] { "空氣汙染", "其他汙染舉發", "環境髒亂", "道路散落物", "小廣告、旗幟"
            , "大型廢棄物清運", "市區道路路面油漬" };
    // by SERVICE_NAME_PIPELINE(P) : 民生管線
    public static final String[] SUBPROJECT_P_POWER = new String[] { "停電、漏電、電線掉落、孔蓋鬆動", "天然氣外洩", "瓦斯管溝修補、孔蓋鬆動"
            , "電信孔蓋鬆動、電信線路掉落、電信桿傾倒", "漏水、停水、消防栓漏水或損壞" };
    // by SERVICE_NAME_ANIMAL_RESCUE(AR) : 動物救援
    public static final String[] SUBPROJECT_AR_STRAY = new String[] { "遊蕩犬隻捕捉管制", "犬貓急難救援" };

    // status on tainana1999
    public static final String STATUS_NOT_TAKEN = "未處理";
    public static final String STATUS_IN_PROCESS = "處理中";
    public static final String STATUS_FINISH = "已完工";

    // agency
    public static final String[] AGENCY = new String[] { "臺南市政府" } ;
}