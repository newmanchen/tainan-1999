package tn.opendata.tainan311.tainan1999.util;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

import tn.opendata.tainan311.tainan1999.rpc.QueryRequest;

/**
 * Created by newman on 5/5/15.
 */
public class TainanRequestXmlUtils {

    public static String getXML(List<BasicNameValuePair> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        sb.append(surround(QueryRequest.TAG_ROOT, false));
        for(BasicNameValuePair nv : list) {
            String name = nv.getName();
            sb.append(surround(name, false));
//            sb.append(nv.getValue());
            sb.append(surroundCDATA(nv.getValue()));
            sb.append(surround(name, true));
        }
        sb.append(surround(QueryRequest.TAG_ROOT, true));
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