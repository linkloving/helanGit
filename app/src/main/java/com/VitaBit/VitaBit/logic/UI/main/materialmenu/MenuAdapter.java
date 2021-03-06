package com.VitaBit.VitaBit.logic.UI.main.materialmenu;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zkx on 2016/3/7.
 * 未连接Vitbit的UI
 */
public class MenuAdapter extends CommonAdapter<MenuVO> {
    public class ViewHolder {
        public LinearLayout layout;
        public TextView itemName;
        public ImageView itemImg;
        public TextView unread;
    }

    private ViewHolder holder;

    private Context mContext;

    public MenuAdapter(Context context, List<MenuVO> list) {
        super(context, list);
        mContext = context;
    }

    @Override
    protected View noConvertView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(com.VitaBit.VitaBit.R.layout.list_item_menu, parent, false);
        holder = new ViewHolder();
        holder.itemImg = (ImageView) convertView.findViewById(com.VitaBit.VitaBit.R.id.fragment_item_img);
        holder.itemName = (TextView) convertView.findViewById(com.VitaBit.VitaBit.R.id.fragment_item_text);
        holder.unread = (TextView) convertView.findViewById(com.VitaBit.VitaBit.R.id.fragment_item_unread_text);
        holder.layout = (LinearLayout) convertView.findViewById(com.VitaBit.VitaBit.R.id.Layout);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    protected View hasConvertView(int position, View convertView,ViewGroup parent) {
        holder = (ViewHolder) convertView.getTag();
        return convertView;
    }

    @Override
    protected View initConvertView(final int position, final View convertView, ViewGroup parent) {
        holder.itemImg.setBackgroundResource(list.get(position).getImgID());
        holder.itemName.setText(list.get(position).getTextID());
        holder.itemName.setTextColor(Color.WHITE);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (position){
                    case Left_viewVO.GOAL:

                        break;
                    case Left_viewVO.KEFU:

                        break;
                    case Left_viewVO.MORE:

                        break;
                    default :
                }
            }
        });
        return convertView;
    }


}

