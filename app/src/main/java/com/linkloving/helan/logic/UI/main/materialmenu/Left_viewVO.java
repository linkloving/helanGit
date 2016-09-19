package com.linkloving.helan.logic.UI.main.materialmenu;

import com.linkloving.helan.R;

/**
 * Created by zkx on 2016/3/10.
 */
public class Left_viewVO {
    public static final int GOAL = 0;
    public static final int KEFU = 1;
    public static final int MORE = 2;

    public static final int LOGIN = 0;

    public static int[] menuIcon = {
            R.mipmap.ic_menu_goal,
            R.mipmap.ic_menu_kefu,
            R.mipmap.ic_menu_more
    };
    public static int[] menuText = {
            R.string.menu_Portal,
            R.string.service_center_title,
            R.string.general_more
    };

    public static int[] noLogin_menuIcon = {
            R.mipmap.ic_menu_vitabit,
            R.mipmap.ic_menu_kefu,
            R.mipmap.ic_menu_more
    };
    public static int[] noLogin_menuText = {
            R.string.menu_connect,
            R.string.service_center_title,
            R.string.general_more
    };
}
