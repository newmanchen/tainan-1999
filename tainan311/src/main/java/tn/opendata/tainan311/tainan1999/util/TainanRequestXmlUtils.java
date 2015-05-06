package tn.opendata.tainan311.tainan1999.util;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

import tn.opendata.tainan311.tainan1999.rpc.AddRequest;
import tn.opendata.tainan311.tainan1999.rpc.BaseRequest;
import tn.opendata.tainan311.tainan1999.vo.AddResponse;

/**
 * Created by newman on 5/5/15.
 */
public class TainanRequestXmlUtils {

    public static String getXML(List<BasicNameValuePair> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        sb.append(surround(BaseRequest.TAG_ROOT, false));
        for(BasicNameValuePair nv : list) {
            String name = nv.getName();
            sb.append(surround(name, false));
//            sb.append(nv.getValue());
            sb.append(surroundCDATA(nv.getValue()));
            sb.append(surround(name, true));
        }
        sb.append(surround(BaseRequest.TAG_ROOT, true));
        return sb.toString();
    }

    public static String getAddRequestXML(List<BasicNameValuePair> list, List<List<BasicNameValuePair>> pics) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        sb.append(surround(BaseRequest.TAG_ROOT, false));
        for(BasicNameValuePair nv : list) {
            String name = nv.getName();
            sb.append(surround(name, false));
            sb.append(surroundCDATA(nv.getValue()));
            sb.append(surround(name, true));
        }
        if (!pics.isEmpty()) {
            sb.append(surround(AddRequest.TAG_PICTURES, false));
            for (List<BasicNameValuePair> pic : pics) {
                sb.append(surround(AddRequest.TAG_PICTURE, false));
                for(BasicNameValuePair nvp : pic) {
                    String name = nvp.getName();
                    sb.append(surround(name, false));
                    if (AddRequest.TAG_FILE.equals(name)) {
                        sb.append(nvp.getValue());
                    } else {
                        sb.append(surroundCDATA(nvp.getValue()));
                    }
                    sb.append(surround(name, true));
                }
                sb.append(surround(AddRequest.TAG_PICTURE, false));
            }
            sb.append(surround(AddRequest.TAG_PICTURES, true));
        }
        sb.append(surround(BaseRequest.TAG_ROOT, true));
        return sb.toString();
    }

    private static String surround(String tag, boolean end){
        StringBuilder result = new StringBuilder();
        result.append("<").append(end?"/":"").append(tag).append(">");
        return result.toString();
    }

    /**
     *
     * @param value
     * @return <![CDATA[value]]>
     */
    private static String surroundCDATA(String value){
        StringBuilder result = new StringBuilder();
        result.append("<![CDATA[").append(value).append("]]>");
        return result.toString();
    }
}