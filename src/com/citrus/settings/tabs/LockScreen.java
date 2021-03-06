/*
 * Copyright (C) 2016 Citrus-CAF Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.citrus.settings.tabs;

import android.content.Context;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.citrus.settings.utils.Utils;

import com.citrus.settings.preference.SystemSettingSwitchPreference;

public class LockScreen extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEYGUARD_TOGGLE_TORCH = "keyguard_toggle_torch";
    private static final String FINGERPRINT_VIB = "fingerprint_success_vib";
    private static final String FP_UNLOCK_KEYSTORE = "fp_unlock_keystore";
    private static final String CATEGORY_FP = "category_fp";

    private SwitchPreference mKeyguardTorch;
    private SwitchPreference mFpKeystore;
    private FingerprintManager mFingerprintManager;
    private SystemSettingSwitchPreference mFingerprintVib;

   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_tab);

        PreferenceScreen prefSet = getPreferenceScreen();

        ContentResolver resolver = getActivity().getContentResolver();

    mFingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
    mFpKeystore = (SwitchPreference) findPreference(FP_UNLOCK_KEYSTORE);
    final PreferenceCategory fpCat = (PreferenceCategory) prefSet.findPreference(CATEGORY_FP);
    if (!mFingerprintManager.isHardwareDetected()){
    prefSet.removePreference(fpCat);
   } else {
    mFpKeystore.setChecked((Settings.System.getInt(getContentResolver(),
            Settings.System.FP_UNLOCK_KEYSTORE, 0) == 1));
    mFpKeystore.setOnPreferenceChangeListener(this);
    }

    mKeyguardTorch = (SwitchPreference) findPreference(KEYGUARD_TOGGLE_TORCH);
    mKeyguardTorch.setOnPreferenceChangeListener(this);
    if (!Utils.deviceSupportsFlashLight(getActivity())) {
    prefSet.removePreference(mKeyguardTorch);
   } else {

    mKeyguardTorch.setChecked((Settings.System.getInt(resolver,
    Settings.System.KEYGUARD_TOGGLE_TORCH, 0) == 1));

    }
}
    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.CUSTOM_SQUASH;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
       if (preference == mKeyguardTorch) {
           boolean checked = ((SwitchPreference)preference).isChecked();
           Settings.System.putInt(getActivity().getContentResolver(),
                   Settings.System.KEYGUARD_TOGGLE_TORCH, checked ? 1:0);
         return true;
     } else if (preference == mFpKeystore) {
         boolean value = (Boolean) objValue;
         Settings.System.putInt(getActivity().getContentResolver(),
                  Settings.System.FP_UNLOCK_KEYSTORE, value ? 1 : 0);
         return true;
     }
        return false;
    }
}
