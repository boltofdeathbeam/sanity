package cri.sanity;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;


public abstract class PrefActivity extends PreferenceActivity
{	
	//---- inner classes

	public static abstract class Click implements OnPreferenceClickListener
	{
		protected Preference pref;
		public boolean onPreferenceClick(Preference pref) {
			this.pref = pref;
			return on();
		}
		public abstract boolean on();
	}

	public static abstract class Change implements OnPreferenceChangeListener
	{
		protected Preference pref;
		protected Object     value;
		public boolean onPreferenceChange(Preference pref, Object value) {
			this.pref  = pref;
			this.value = value;
			return on();
		}
		public abstract boolean on();
	}

	//---- Activity override

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		A.activity = this;
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart()
	{
		A.activity = this;
		super.onStart();
	}

	@Override
	public void onResume()
	{
		A.activity = this;
		super.onResume();
	}

	/*
	@Override
	public void onDestroy()
	{
		if(isRootActivity()) A.activity = null;
		super.onDestroy();
	}
	*/

	//---- general api

	//public boolean isRootActivity() { return !(getParent() instanceof PrefActivity); }
	public boolean isMainActivity() { return false; }

	//---- preference api

	public final Preference pref(String key) { return findPreference(key); }

	// enabledDep is true if and only if "key" preference is dependent also to global "enabled" key
	public final void updatePref(String key, boolean enabledDep) {
		final Preference         p   = findPreference(key);
		final CheckBoxPreference dep = (CheckBoxPreference)findPreference(p.getDependency());
		enabledDep = !enabledDep || A.isEnabled();
		p.setEnabled(enabledDep && (dep==null || (dep.isEnabled() && dep.isChecked())));
		if(     p instanceof CheckBoxPreference) ((CheckBoxPreference)p).setChecked(A.is(key));
		else if(p instanceof ListPreference    ) ((ListPreference)p).setValue(A.gets(key));
	}
	public final void updatePref(String key) { updatePref(key, true); }

	public final boolean is(String key)   { return ((CheckBoxPreference)pref(key)).isChecked(); }
	public final boolean is(Preference p) { return ((CheckBoxPreference)p        ).isChecked(); }

	public final void setChecked(String key, boolean checked) {
		((CheckBoxPreference)pref(key)).setChecked(checked);
	}
	public final void setChecked(Preference p, boolean checked) {
		((CheckBoxPreference)p).setChecked(checked);
	}
	public final void setEnabled(String key, boolean enabled) {
		pref(key).setEnabled(enabled);
	}
	public final void setEnabled(Preference p, boolean enabled) {
		p.setEnabled(enabled);
	}
	
	public final void on(String key, Click  listener)            { on(pref(key), listener);               }
	public final void on(String key, Change listener)            { on(pref(key), listener);               }
	public final void on(String key, Click click, Change change) { on(pref(key), click, change);          }
	public final void on(Preference p, Click  listener)          { p.setOnPreferenceClickListener (listener); }
	public final void on(Preference p, Change listener)          { p.setOnPreferenceChangeListener(listener); }
	public final void on(Preference p, Click click, Change change) {
		p.setOnPreferenceClickListener(click);
		p.setOnPreferenceChangeListener(change);
	}

}
