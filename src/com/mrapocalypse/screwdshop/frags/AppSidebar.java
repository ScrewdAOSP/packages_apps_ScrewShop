package com.mrapocalypse.screwdshop.frags;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.mrapocalypse.screwdshop.prefs.CustomSeekBarPreference;
import com.android.internal.logging.MetricsProto.MetricsEvent;

public class AppSidebar extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = "AppSideBar";

    private static final String KEY_ENABLED = "sidebar_enable";
    private static final String KEY_TRANSPARENCY = "sidebar_transparency";
    private static final String KEY_SETUP_ITEMS = "sidebar_setup_items";
    private static final String KEY_POSITION = "sidebar_position";
    private static final String KEY_HIDE_LABELS = "sidebar_hide_labels";
    private static final String KEY_TRIGGER_WIDTH = "trigger_width";
    private static final String KEY_TRIGGER_TOP = "trigger_top";
    private static final String KEY_TRIGGER_BOTTOM = "trigger_bottom";
    private static final String KEY_HIDE_TIMEOUT = "app_sidebar_hide_timeout";

    private SwitchPreference mEnabledPref;
    private CustomSeekBarPreference mTransparencyPref;
    private ListPreference mPositionPref;
    private SwitchPreference mHideLabelsPref;
    private CustomSeekBarPreference mTriggerWidthPref;
    private CustomSeekBarPreference mTriggerTopPref;
    private CustomSeekBarPreference mTriggerBottomPref;
    private CustomSeekBarPreference mHideTimeoutPref;

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.SCREWD;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.app_sidebar);

        mEnabledPref = (SwitchPreference) findPreference(KEY_ENABLED);
        mEnabledPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_ENABLED, 0) == 1));
        mEnabledPref.setOnPreferenceChangeListener(this);

        mHideLabelsPref = (SwitchPreference) findPreference(KEY_HIDE_LABELS);
        mHideLabelsPref.setChecked((Settings.System.getInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_DISABLE_LABELS, 0) == 1));

        PreferenceScreen prefSet = getPreferenceScreen();
        mPositionPref = (ListPreference) prefSet.findPreference(KEY_POSITION);
        mPositionPref.setOnPreferenceChangeListener(this);
        int position = Settings.System.getInt(getContentResolver(), Settings.System.APP_SIDEBAR_POSITION, 0);
        mPositionPref.setValue(String.valueOf(position));
        updatePositionSummary(position);

        mTransparencyPref = (CustomSeekBarPreference) findPreference(KEY_TRANSPARENCY);
        mTransparencyPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_TRANSPARENCY, 0));
        mTransparencyPref.setOnPreferenceChangeListener(this);

        mTriggerWidthPref = (CustomSeekBarPreference) findPreference(KEY_TRIGGER_WIDTH);
        mTriggerWidthPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_TRIGGER_WIDTH, 20));
        mTriggerWidthPref.setOnPreferenceChangeListener(this);

        mTriggerTopPref = (CustomSeekBarPreference) findPreference(KEY_TRIGGER_TOP);
        mTriggerTopPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_TRIGGER_TOP, 0));
        mTriggerTopPref.setOnPreferenceChangeListener(this);

        mTriggerBottomPref = (CustomSeekBarPreference) findPreference(KEY_TRIGGER_BOTTOM);
        mTriggerBottomPref.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_TRIGGER_HEIGHT, 100));
        mTriggerBottomPref.setOnPreferenceChangeListener(this);

        mHideTimeoutPref = (CustomSeekBarPreference) findPreference(KEY_HIDE_TIMEOUT);
        mHideTimeoutPref.setValue(Settings.System.getInt(getContentResolver(),
            Settings.System.APP_SIDEBAR_HIDE_TIMEOUT, 3000));
        mHideTimeoutPref.setOnPreferenceChangeListener(this);

        findPreference(KEY_SETUP_ITEMS).setOnPreferenceClickListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mTransparencyPref) {
            int transparency = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.APP_SIDEBAR_TRANSPARENCY, transparency);
            return true;
        } else if (preference == mTriggerWidthPref) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.APP_SIDEBAR_TRIGGER_WIDTH, width);
            return true;
        } else if (preference == mTriggerTopPref) {
            int top = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.APP_SIDEBAR_TRIGGER_TOP, top);
            return true;
        } else if (preference == mTriggerBottomPref) {
            int bottom = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.APP_SIDEBAR_TRIGGER_HEIGHT, bottom);
            return true;
        } else if (preference == mPositionPref) {
            int position = Integer.valueOf((String) newValue);
            updatePositionSummary(position);
            return true;
        } else if (preference == mEnabledPref) {
            boolean value = ((Boolean)newValue).booleanValue();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.APP_SIDEBAR_ENABLED,
                    value ? 1 : 0);
            return true;
        } else if (preference == mHideTimeoutPref) {
            int timeout = ((Integer)newValue).intValue();
            Settings.System.putInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_HIDE_TIMEOUT, timeout);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        boolean value;

        if (preference == mHideLabelsPref) {
            value = mHideLabelsPref.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.APP_SIDEBAR_DISABLE_LABELS,
                    value ? 1 : 0);
        } else {
            return super.onPreferenceTreeClick(preference);
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference.getKey().equals(KEY_SETUP_ITEMS)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName("com.android.systemui",
                    "com.android.systemui.statusbar.sidebar.SidebarConfigurationActivity"));
            getActivity().startActivity(intent);
            return true;
        }
        return false;
    }

    private void updatePositionSummary(int value) {
        mPositionPref.setSummary(mPositionPref.getEntries()[mPositionPref.findIndexOfValue("" + value)]);
        Settings.System.putInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_POSITION, value);
    }

    @Override
    public void onPause() {
        super.onPause();
        Settings.System.putInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_SHOW_TRIGGER, 0);
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        Settings.System.putInt(getContentResolver(),
                Settings.System.APP_SIDEBAR_SHOW_TRIGGER, 1);
    }
}
