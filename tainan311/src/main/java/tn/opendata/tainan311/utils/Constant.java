package tn.opendata.tainan311.utils;

/**
 * Created by vincent on 2014/6/13.
 */
public class Constant {
    public static final String PREF_NAME = "tn.opendata.tainan311_preferences";
    public static final String KEY_SHOW_TIPS = "show_tips_preference";
    public static final String KEY_MY_REQUEST_ID = "my_request_id";

    /**
     * TODO
     * 1 hr for every query interval in millisecond
     */
    public static final int QUERY_INTERVAL = 60 * 60 * 1000;

    /**
     * TODO
     * a week as a default query scope
     * a customized value by user in setting
     */
    public static final int QUERY_SCOPE = 7 * 24 * 60 * 60 * 1000;
}
