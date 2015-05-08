package tn.opendata.tainan311.tainan1999.rpc;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import tn.opendata.tainan311.utils.LogUtils;

/**
 * Created by newman on 5/6/15.
 */
public class BaseRequest {
    public static final String TAG_ROOT = "root";
    protected static final String ns = null;

    /**
     *  Common reader for the tag that depth has only 1
     *  @param parser
     *  @param tag
     */
    protected static String readData (XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String data = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return data;
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * skip unused tag
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    protected static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
