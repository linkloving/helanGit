package com.VitaBit.VitaBit.logic.UI.friend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.VitaBit.VitaBit.http.basic.NoHttpRuquestFactory;
import com.VitaBit.VitaBit.IntentFactory;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.logic.UI.personal.EntInfoActivity;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.linkloving.utils.CommonUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo.wang on 2016/3/17.
 */

public class SampleAdapyer extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final static String TAG = SampleAdapyer.class.getSimpleName();

    private LayoutInflater mInflater;

    private List<AttentionUser> list = new ArrayList<AttentionUser>();

    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;

    //上拉加载更多状态-默认为0
    private int load_more_status=0;

    private static final int TYPE_ITEM = 0;  //普通Item View
    private static final int TYPE_HEAD= 1;  //顶部HEAD

    Context context;

    public SampleAdapyer(Context context, List<AttentionUser> list) {
        this.context=context;
        this.list=list;
        this.mInflater=LayoutInflater.from(context);
    }


    @Override
    public int getItemViewType(int position) {
        MyLog.i("HEAD",">>>>>>>>>>>>>>>>>>>>>>>>>"+position);
        //第一个item设置为headView
        if (position==0) {
          if(!CommonUtils.isStringEmpty(MyApplication.getInstance(context).getLocalUserInfoProvider().getEntEntity().getEnt_id())){
                MyLog.i("HEAD",">>>>>>>>>>加入过群组>>>>>>>>>>>>>>>");
            return TYPE_HEAD;
            }else {
              return TYPE_ITEM;

          }


        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==TYPE_ITEM){
            MyLog.i(TAG,"RecyclerView.ViewHolder onCreateViewHolde");
            //进行判断显示类型，来创建返回不同的View
            View view=mInflater.inflate(com.VitaBit.VitaBit.R.layout.sampeadapteritem,parent,false);
            //这边可以做一些属性设置，甚至事件监听绑定
            //view.setBackgroundColor(Color.RED);
            ItemViewHolder itemViewHolder=new ItemViewHolder(view);
            return itemViewHolder;
        }

        else {

            MyLog.i("HEAD",">>>>>>>>>>加入过群组>>>>>>>>RecyclerView.ViewHolder onCreateViewHolde>>>>>>>");

            View foot_view=mInflater.inflate(com.VitaBit.VitaBit.R.layout.qunzunhead,parent,false);

            FootViewHolder footViewHolder=new FootViewHolder(foot_view);

            return footViewHolder;
        }
    }

    /**
     * 数据的绑定显示
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,  int position) {



        if(holder instanceof ItemViewHolder) {
            if(!CommonUtils.isStringEmpty(MyApplication.getInstance(context).getLocalUserInfoProvider().getEntEntity().getEnt_id())){
                position--;
            }
            final String userId=list.get(position).getAttention_user_id()+"";
            final String userAvatar =list.get(position).getUserAvatar();
            MyLog.i(TAG,"onBindViewHolde");
            final ItemViewHolder itemViewHolder= ((ItemViewHolder)holder);
            itemViewHolder.myLinearLayout.setOnClickListener(  new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳入个人信息页面
                    Intent intent=IntentFactory.create_FriendInfoActivity(context,userId,userAvatar);
                    context.startActivity(intent);
                }
            });
            //图像以后设置
            String url= NoHttpRuquestFactory.getUserAvatarDownloadURL(context,list.get(position).getAttention_user_id()+"",list.get(position).getUserAvatar(),true);
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                    .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                    .showImageOnFail(com.VitaBit.VitaBit.R.mipmap.default_avatar_m)//加载失败显示图片
                    .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                    .build();//构建完成

            ImageLoader.getInstance().displayImage(url, itemViewHolder.head, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageView  mhead=  (ImageView)view;
                    mhead.setImageBitmap(loadedImage);

                }
                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }
            });
            itemViewHolder.label.setText(list.get(position).getWhat_s_up());
            itemViewHolder.nickName.setText(list.get(position).getNickname());

            /*  if(list.get(position).getDistance() == 0 || list.get(position).getDistance().isEmpty() || list.get(position).getDistance().equals("0"))*/
            if(list.get(position).getDistance() == 0)
            {
                itemViewHolder.unitStep.setVisibility(View.GONE);
                itemViewHolder.steps.setVisibility(View.GONE);
            }
            else
            {
                itemViewHolder.unitStep.setVisibility(View.VISIBLE);
                itemViewHolder.steps.setVisibility(View.VISIBLE);
                itemViewHolder.steps.setText(list.get(position).getDistance()+"");
            }
        }
        else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder=(FootViewHolder)holder;
            if(!CommonUtils.isStringEmpty(MyApplication.getInstance(context).getLocalUserInfoProvider().getEntEntity().getEnt_id())){
                ((FootViewHolder) holder).name.setText(MyApplication.getInstance(context).getLocalUserInfoProvider().getEntEntity().getEnt_name());
                String imageUrl=NoHttpRuquestFactory.getEntAvatarDownloadURL(MyApplication.getInstance(context).getLocalUserInfoProvider().getEntEntity().getPortal_logo_file_name());
               //现在下载群组logo只做了下载成功的处理,如果下载失败就不显示出来,如果要做显示后期再加上下载失败,和取消的监听就好了
                ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        MyLog.i(TAG, imageUri);
                        ((FootViewHolder) holder).head.setImageBitmap(loadedImage);
                    }
                });
                ((FootViewHolder) holder).ent_linearlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, EntInfoActivity.class));
                    }
                });
            }
        }

    }


    @Override
    public int getItemCount() {

        if(!CommonUtils.isStringEmpty(MyApplication.getInstance(context).getLocalUserInfoProvider().getEntEntity().getEnt_id())){
            MyLog.i("HEAD",">>>>>>>>>>加入过群组>>>>>>>> return list.size()+1;>>>>>>>");
            return list.size()+1;
        }else {
            return list.size();
        }
    }



    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public  class ItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout myLinearLayout;
        public ImageView head;
        public TextView nickName;
        public TextView label;
        public TextView steps;
        public TextView unitStep;
        public ItemViewHolder(View view){
            super(view);
            myLinearLayout= (LinearLayout) view.findViewById(com.VitaBit.VitaBit.R.id.my_attention_layout);
            head = (ImageView) view.findViewById(com.VitaBit.VitaBit.R.id.user_head);
            label = (TextView) view.findViewById(com.VitaBit.VitaBit.R.id.concern_label);
            nickName = (TextView) view.findViewById(com.VitaBit.VitaBit.R.id.concern_nickname);
            steps = (TextView) view.findViewById(com.VitaBit.VitaBit.R.id.concern_steps);
            unitStep = (TextView) view.findViewById(com.VitaBit.VitaBit.R.id.concern_unit_step);
        }
    }
    /**
     * 头部布局
     */

    public static class FootViewHolder extends  RecyclerView.ViewHolder{
        public TextView name,what_up;
        RelativeLayout ent_linearlayout;
        public ImageView head;
        public FootViewHolder(View view) {
            super(view);
            ent_linearlayout= (RelativeLayout) view.findViewById(com.VitaBit.VitaBit.R.id.linearLayout_ent_follw);
            name=(TextView)view.findViewById(com.VitaBit.VitaBit.R.id.ent_name);
//            what_up=(TextView)view.findViewById(R.id.ent_whatsup);
            head= (ImageView) view.findViewById(com.VitaBit.VitaBit.R.id.ent_head);
        }
    }
    //添加数据
    public void addItem(List<AttentionUser> newDatas) {
        //mTitles.add(position, data);
        //notifyItemInserted(position);
        MyLog.i(TAG,"下拉刷新");
        newDatas.addAll(list);
        list.removeAll(list);
        list.addAll(newDatas);
        notifyDataSetChanged();
    }

   /* public void addMoreItem(List<AttentionUser> newDatas) {
        list.addAll(newDatas);
        notifyDataSetChanged();

    }*/

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
   /* public void changeMoreStatus(int status){

        load_more_status = status;

        MyLog.i(TAG,"加载更多");

    }
*/



}
