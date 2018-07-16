package com.yhy.drawtracedemo.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yhy.drawtracedemo.R;

public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //getPreferenceManager().setSharedPreferencesName("mysetting");


        //偏好设置更改监听
        Preference.OnPreferenceChangeListener pfChangeListener = new Preference.OnPreferenceChangeListener() {

            /**
             * @param preference The changed Preference.
             * @param newValue   The new value of the Preference.
             * @return True to update the state of the Preference with the new value.
             */
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(String.valueOf(newValue));
                return true;
            }
        };

        //当偏好值改变的时候改变Summary
        final EditTextPreference editTextPrefMap = (EditTextPreference) getPreferenceManager()
                .findPreference(getString(R.string.map_location));
        final EditTextPreference editTextPrefServer = (EditTextPreference) getPreferenceManager()
                .findPreference(getString(R.string.server_host));
        final EditTextPreference editTextPrefSpeed = (EditTextPreference) getPreferenceManager()
                .findPreference(getString(R.string.over_speed));
        editTextPrefMap.setOnPreferenceChangeListener(pfChangeListener);
        editTextPrefServer.setOnPreferenceChangeListener(pfChangeListener);
        editTextPrefSpeed.setOnPreferenceChangeListener(pfChangeListener);

        //设置默认地图包位置
        String defaultMapLocation = Environment.getExternalStorageDirectory().getPath() + "/osmdroid/map.sqlite";
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userSetMapLocation = prefs.getString(getString(R.string.map_location),"");
        String userSetServerHost = prefs.getString(getString(R.string.server_host),"");
        String userSetSpeed = "90";
        try {
            userSetSpeed = prefs.getString(getString(R.string.over_speed), "90");
        }
        catch (Exception e)
        {}
       // Toast.makeText(getActivity(), ""+checkbox, Toast.LENGTH_SHORT).show();

        if(editTextPrefMap.getText() == null)
        {
            editTextPrefMap.setText(defaultMapLocation);
            editTextPrefMap.setSummary(defaultMapLocation);
        }
        else
        {
            editTextPrefMap.setSummary(userSetMapLocation);
        }

        //设置默认服务器地址
        if(editTextPrefServer.getText() == null)
        {
            editTextPrefServer.setText("http://192.168.43.1/");
            editTextPrefServer.setSummary("http://192.168.43.1/");
        }
        else
        {
            editTextPrefServer.setSummary(userSetServerHost);
        }

        //设置默认超速监测速度
        if(editTextPrefSpeed.getText() == null)
        {
            editTextPrefSpeed.setText("90");
            editTextPrefSpeed.setSummary("90");
        }
        else
        {
            editTextPrefSpeed.setSummary(userSetSpeed);
        }
    }
}