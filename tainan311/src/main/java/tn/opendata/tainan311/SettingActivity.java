package tn.opendata.tainan311;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.view.MenuItem;

/**
 * Created by newman on 6/13/14.
 */
public class SettingActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new SettingFragment()).commit();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static final String PREFS_KEY_DEFAULT_NAME = "reporter_name_preference";
    public static final String PREFS_KEY_DEFAULT_PHONE = "reporter_phone_preference";
    public static final String PREFS_KEY_DEFAULT_EMAIL = "reporter_email_preference";

    public static class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_setting);

            EditTextPreference pName = (EditTextPreference) findPreference(PREFS_KEY_DEFAULT_NAME);
            setSummary(pName);
            pName.setOnPreferenceChangeListener(this);
            EditTextPreference pPhone = (EditTextPreference) findPreference(PREFS_KEY_DEFAULT_PHONE);
            setSummary(pPhone);
            pPhone.setOnPreferenceChangeListener(this);
            EditTextPreference pEmail = (EditTextPreference) findPreference(PREFS_KEY_DEFAULT_EMAIL);
            setSummary(pEmail);
            pEmail.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (PREFS_KEY_DEFAULT_NAME.equals(preference.getKey())
                    || PREFS_KEY_DEFAULT_EMAIL.equals(preference.getKey())
                    || PREFS_KEY_DEFAULT_PHONE.equals(preference.getKey())) {
                setSummary(preference, newValue);
            }
            return true;
        }

        private void setSummary(Preference p, Object summary) {
            if (p instanceof EditTextPreference) {
                String sum = String.valueOf(summary);
                if (!TextUtils.isEmpty(sum)) {
                    p.setSummary(sum);
                } else {
                    p.setSummary(getString(R.string.text_no_default));
                }
            }
        }

        private void setSummary(Preference p) {
            if (p instanceof EditTextPreference) {
                String summary = ((EditTextPreference) p).getText();
                if (!TextUtils.isEmpty(summary)) {
                    p.setSummary(summary);
                } else {
                    p.setSummary(getString(R.string.text_no_default));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
