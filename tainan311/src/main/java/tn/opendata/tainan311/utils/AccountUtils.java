package tn.opendata.tainan311.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * Created by vincent on 2014/6/13.
 */
public class AccountUtils {

    public static String getEmailAccount(Context context) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        AccountManager am = AccountManager.get(context);
        if ( am != null ) {
            Account[] accounts = am.getAccountsByType("com.google");
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    return account.name;
                }
            }
        }
        return "";
    }
}
