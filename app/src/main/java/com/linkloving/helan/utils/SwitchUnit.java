package com.linkloving.helan.utils;

import android.content.Context;

import com.linkloving.helan.prefrences.PreferencesToolkits;

/**
 * Created by DC on 2016/4/9.
 */
public class SwitchUnit {
    //单位转换入口  传进的参数
    public static int getLocalUnit(Context context) {
        int unit = PreferencesToolkits.getLocalSettingUnitInfo(context);
        return unit;

    }
}