package com.appsrox.remindme;

import java.util.List;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.text.TextUtils;

import com.appsrox.common.BasePreferenceActivity;
import com.appsrox.common.BasePreferenceChangeListener;

public class SettingsActivity extends BasePreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static final BasePreferenceChangeListener sListener = new BasePreferenceChangeListener() {

		@Override
		protected boolean updatePreference(Preference preference, String value) {
			if (RemindMe.SNOOZE_TIME.equals(preference.getKey())){
				EditTextPreference editPreference = (EditTextPreference) preference;
				if (TextUtils.isEmpty(value)) 
					editPreference.setText(RemindMe.DEFAULT_SNOOZE_TIME);

				int val = Integer.parseInt(value);
				editPreference.setSummary(val + " minute" + (val>1 ? "s":""));
				return true;
			}
			
			return super.updatePreference(preference, value);
		}
	};
	
	@Override
	protected void addPreferences() {
		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);

		// Add 'notifications' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_notifications);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_notification);
	}
	
	@Override
	protected void bindPreferences() {
		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.TIME_OPTION));
		sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.DATE_RANGE));
		sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.DATE_FORMAT));
		sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.RINGTONE_PREF));
		sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.SNOOZE_TIME));
	}
	
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void loadHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.pref_headers, target);
	}
	
	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.TIME_OPTION));
			sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.DATE_RANGE));
			sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.DATE_FORMAT));
		}
	}

	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.RINGTONE_PREF));
			sListener.bindPreferenceSummaryToValue(findPreference(RemindMe.SNOOZE_TIME));
		}
	}	
	
}
