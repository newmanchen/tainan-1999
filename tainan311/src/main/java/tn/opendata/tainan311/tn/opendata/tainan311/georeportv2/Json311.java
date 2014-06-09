package tn.opendata.tainan311.tn.opendata.tainan311.georeportv2;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vincent on 2014/6/9.
 */
public class Json311 {

    public static String fromFixMyStreet(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String read;

        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        String rawData = sb.toString();
        try {
            JSONArray data = getJSONArrayLevel(new JSONObject(rawData), 2);
            String jsonString = data.toString();
            // need refine... can't parse if content is {"service_request_id":[2]}
            Pattern pattern = Pattern.compile(":\\[[^{](.+?)[^}]\\]");
            Matcher matcher = pattern.matcher(jsonString);
            StringBuffer buf = new StringBuffer();
            while(matcher.find()) {
                matcher.appendReplacement(buf, matcher.group(0).replaceAll("(\\[|\\])", ""));
            }
            matcher.appendTail(buf);
            return buf.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private static JSONArray getJSONArrayLevel(JSONObject obj, int level) throws JSONException {
        Iterator<?> iter = obj.keys();
        if(iter.hasNext()) {
            JSONArray array = obj.getJSONArray((String)iter.next());
            return (level > 1)?
                getJSONArrayLevel(array.getJSONObject(0), level-1) : array;
        }
        return null;
    }
}
