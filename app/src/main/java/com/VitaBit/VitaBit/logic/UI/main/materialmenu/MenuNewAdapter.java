package com.VitaBit.VitaBit.logic.UI.main.materialmenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VitaBit.VitaBit.IntentFactory;
import com.VitaBit.VitaBit.R;
import com.VitaBit.VitaBit.http.HttpHelper;
import com.VitaBit.VitaBit.logic.UI.customerservice.serviceItem.Feedback;
import com.VitaBit.VitaBit.logic.dto.UserEntity;

import java.util.List;

/**
 * Created by zkx on 2016/3/11.
 */
public class MenuNewAdapter extends RecyclerView.Adapter {
    private static final String TAG = MenuNewAdapter.class.getSimpleName();
    public  static int JUMP_FRIEND_TAG=1;
    UserEntity userEntity;
    private Context mContext;
    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
    }

    private List<MenuVO> list;

    public MenuNewAdapter(Context context, List<MenuVO> list) {
        this.mContext = context;
        this.list = list;
    }

    public void setList(List<MenuVO> list){
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_menu, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MenuViewHolder holder = (MenuViewHolder) viewHolder;
        holder.itemImg.setImageResource(list.get(position).getImgID());
        holder.itemName.setText(list.get(position).getTextID());
        holder.itemName.setTextColor(Color.WHITE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //还可以添加长点击事件
    class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public LinearLayout layout;
        public TextView itemName;
        public ImageView itemImg;
        public TextView unread;

        public MenuViewHolder(View convertView) {
            super(convertView);
            itemImg = (ImageView) convertView.findViewById(R.id.fragment_item_img);
            itemName = (TextView) convertView.findViewById(R.id.fragment_item_text);
            unread = (TextView) convertView.findViewById(R.id.fragment_item_unread_text);
            layout = (LinearLayout) convertView.findViewById(R.id.Layout);
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(this.getPosition());
                switch (this.getPosition()){

                    case 0: //只负责进入目标
                        mContext.startActivity(IntentFactory.createCommonWebActivityIntent((Activity) mContext, HttpHelper.Visit_Portal));
                        break;

                    case Left_viewVO.KEFU:
                        IntentFactory.start_CustomerService_ActivityIntent((Activity) mContext, Feedback.PAGE_INDEX_ONE);
                        break;

                    case Left_viewVO.MORE:
                        //        跳转到更多界面
                        mContext.startActivity(IntentFactory.start_MoreActivityIntent((Activity) mContext));
                        break;
                    default :
                }
            }
        }

    }
}
