package tn.opendata.tainan311.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;

public class LogUtils {
	private static String sApplicationName = "Tainan1999";
	private static boolean sLogFlagNormal = true;
	private static boolean sLogFlagCritical = true;

	public LogUtils() {
		Log.d(sApplicationName, "Enable normal log level: " + String.valueOf(sLogFlagNormal));
		Log.d(sApplicationName, "Enable critical log level: " + String.valueOf(sLogFlagCritical));
	}

	public static void critical(String tag, Object ...msgs) {
		if (sLogFlagCritical && msgs.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(getBracketTag(tag));
			for(Object msg : msgs) {
				sb.append(msg);
			}
			Log.d(sApplicationName, sb.toString());
		}
	}

	public static void v(String tag, Object ...msgs) {
		if (sLogFlagNormal && msgs.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(getBracketTag(tag));
			for(Object msg : msgs) {
				sb.append(msg);
			}
			Log.v(sApplicationName, sb.toString());
		}
	}

	public static void d(String tag, Object ...msgs) {
		if (sLogFlagNormal && msgs.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(getBracketTag(tag));
			for(Object msg : msgs) {
				sb.append(msg);
			}
			Log.d(sApplicationName, sb.toString());
		}
	}

	public static void i(String tag, Object ...msgs) {
		if (sLogFlagNormal && msgs.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(getBracketTag(tag));
			for(Object msg : msgs) {
				sb.append(msg);
			}
			Log.i(sApplicationName, sb.toString());
		}
	}

	public static void w(String tag, Object ...msgs) {
		if (sLogFlagNormal && msgs.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(getBracketTag(tag));
			for(Object msg : msgs) {
				sb.append(msg);
			}
			Log.w(sApplicationName, sb.toString());
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if(sLogFlagNormal) {
			Log.w(sApplicationName, getBracketTag(tag) + msg, tr);
		}
	}

	public static void e(String tag, String message) {
		if (sLogFlagNormal) {
			Log.e(sApplicationName, getBracketTag(tag) + message);
		}
	}

	public static void e(String tag, String message, Exception e) {
		if (sLogFlagNormal) {
			Log.e(sApplicationName, getBracketTag(tag) + message, e);
		}
	}

	public static void e(String tag, String prefix, String message, Exception e) {
		if (sLogFlagNormal) {
			Log.e(sApplicationName, getBracketTag(tag) + prefix);
			Log.e(sApplicationName, message, e);
		}
	}

	private static String getBracketTag(String tag) {
		return "<" + tag + "> ";
	}

	/**
	 * Output log to file
	 * @param context
	 * @param fileName file name
	 * @param msg log message
	 */
	public static void toFile(Context context, String fileName, String msg) {
		if (sLogFlagCritical) {
			if (!TextUtils.isEmpty(fileName) && !TextUtils.isEmpty(msg)) {
				String rootPath = context.getFilesDir().getAbsolutePath();
				FileWriter fw = null;
				try {
					fw = new FileWriter(rootPath + "/" + fileName);
					fw.write(msg);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					EasyUtil.close(fw);
				}
			}
		}
	}
}