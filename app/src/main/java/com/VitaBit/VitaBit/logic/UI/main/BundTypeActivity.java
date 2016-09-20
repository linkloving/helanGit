package com.VitaBit.VitaBit.logic.UI.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.VitaBit.VitaBit.basic.toolbar.ToolBarActivity;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BundTypeActivity extends ToolBarActivity {
    private static final int TYPE_BAND = 0;
    private static final int TYPE_WATCH = 1;

    public static final String KEY_TYPE = "device_type";
    public static final String KEY_TYPE_WATCH = "watch";
    public static final String KEY_TYPE_BAND = "band";
    private TypeAdapter typeAdapeter;
    private ArrayList<TypeVo> typeList = new ArrayList<>();
    @InjectView(com.VitaBit.VitaBit.R.id.bundtype_recycler)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.VitaBit.VitaBit.R.layout.activity_bund_type); //之后去调用initView（）
        typeList.add(TYPE_BAND,new TypeVo(com.VitaBit.VitaBit.R.mipmap.bound_band_on, com.VitaBit.VitaBit.R.string.bound_link_band));
        typeList.add(TYPE_WATCH,new TypeVo(com.VitaBit.VitaBit.R.mipmap.bound_watch_on, com.VitaBit.VitaBit.R.string.bound_link_watch));
        ButterKnife.inject(this);
        typeAdapeter = new TypeAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(typeAdapeter);
    }

    @Override
    protected void getIntentforActivity() {

    }

    @Override
    protected void initView() {
        //default do nothing
        HideButtonRight(false);
        SetBarTitleText(getString(com.VitaBit.VitaBit.R.string.bound_list));
    }

    @Override
    protected void initListeners() {

    }

    private  class  TypeAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(com.VitaBit.VitaBit.R.layout.item_bundtype, null);
//          不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new MenuViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
            MenuViewHolder viewHolder = (MenuViewHolder) holder;
            viewHolder.image.setImageResource(typeList.get(position).getImgID());
            viewHolder.tv.setText(typeList.get(position).getTextID());
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(position == TYPE_BAND){
                        MyLog.i("绑定","点击了手环");
                        Intent intent=new Intent();
                        intent.putExtra(KEY_TYPE, KEY_TYPE_BAND);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else if(position == TYPE_WATCH){
                        MyLog.i("绑定","点击了手表");
                        Intent intent=new Intent();
                        intent.putExtra(KEY_TYPE, KEY_TYPE_WATCH);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return  typeList.size();
        }

        class MenuViewHolder extends RecyclerView.ViewHolder{
            public LinearLayout layout;
            //图片
            public ImageView image;
            //描述
            public TextView tv;
            public MenuViewHolder(View itemView) {
                super(itemView);
                layout = (LinearLayout) itemView.findViewById(com.VitaBit.VitaBit.R.id.layout);
                image = (ImageView) itemView.findViewById(com.VitaBit.VitaBit.R.id.bund_img);
                tv = (TextView) itemView.findViewById(com.VitaBit.VitaBit.R.id.bund_txt);
            }
        }
    }


    private class TypeVo{

        public TypeVo(int imgID, int textID) {
            this.imgID = imgID;
            this.textID = textID;
        }
        //自定义数据类型
        private int textID;

        private int imgID;

        public int getTextID() {
            return textID;
        }

        public void setTextID(int textID) {
            this.textID = textID;
        }

        public int getImgID() {
            return imgID;
        }

        public void setImgID(int imgID) {
            this.imgID = imgID;
        }
    }

}
