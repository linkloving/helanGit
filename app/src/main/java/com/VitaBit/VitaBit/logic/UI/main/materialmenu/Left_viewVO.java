package com.VitaBit.VitaBit.logic.UI.main.materialmenu;

/**
 * Created by zkx on 2016/3/10.
 */
public class Left_viewVO {
    public static final int GOAL = 0;
    public static final int KEFU = 1;
    public static final int MORE = 2;

    public static final int LOGIN = 0;

    public static int[] menuIcon = {
            com.VitaBit.VitaBit.R.mipmap.ic_menu_goal,
            com.VitaBit.VitaBit.R.mipmap.ic_menu_kefu,
            com.VitaBit.VitaBit.R.mipmap.ic_menu_more
    };
    public static int[] menuText = {
            com.VitaBit.VitaBit.R.string.menu_Portal,
            com.VitaBit.VitaBit.R.string.service_center_title,
            com.VitaBit.VitaBit.R.string.general_more
    };

    public static int[] noLogin_menuIcon = {
            com.VitaBit.VitaBit.R.mipmap.ic_menu_vitabit,
            com.VitaBit.VitaBit.R.mipmap.ic_menu_kefu,
            com.VitaBit.VitaBit.R.mipmap.ic_menu_more
    };
    public static int[] noLogin_menuText = {
            com.VitaBit.VitaBit.R.string.menu_connect,
            com.VitaBit.VitaBit.R.string.service_center_title,
            com.VitaBit.VitaBit.R.string.general_more
    };
}
