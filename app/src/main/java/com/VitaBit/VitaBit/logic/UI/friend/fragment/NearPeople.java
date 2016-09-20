package com.VitaBit.VitaBit.logic.UI.friend.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.VitaBit.VitaBit.http.basic.CallServer;
import com.VitaBit.VitaBit.http.basic.HttpCallback;
import com.VitaBit.VitaBit.http.basic.NoHttpRuquestFactory;
import com.VitaBit.VitaBit.http.data.DataFromServer;
import com.VitaBit.VitaBit.logic.UI.friend.NearbyAdapter;
import com.VitaBit.VitaBit.utils.ToolKits;
import com.alibaba.fastjson.JSON;
import com.VitaBit.VitaBit.MyApplication;
import com.VitaBit.VitaBit.logic.UI.friend.UserSelected;
import com.VitaBit.VitaBit.utils.MyToast;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.yolanda.nohttp.rest.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by leo.wang on 2016/3/18.
 *
 * 附近的人
 */
public class NearPeople extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    //网络请求的what
    public final static int REQUEST_GET_MY_NEARPEOPLE=15;

    public final static String TAG = NearPeople.class.getSimpleName();

    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;

    LinearLayoutManager mLayoutManager;

    private NearbyAdapter adapter;

    ArrayList<UserSelected> list =new ArrayList<UserSelected>();

    int lastVisibleItem;

    private int pageIndex = 1;

    private int afterpage=0;

    private double longitude = 0;

    private double latitude = 0;

    private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MyLog.i(TAG,"initLocation()");


        return inflateAndSetupView(inflater, container, savedInstanceState, com.VitaBit.VitaBit.R.layout.fragment_my_attention);

    }

    private View inflateAndSetupView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState, int layoutResourceId) {
        View layout = inflater.inflate(layoutResourceId, container, false);

       mSwipeRefreshLayout= (SwipeRefreshLayout) layout.findViewById(com.VitaBit.VitaBit.R.id.swipe_refresh_widget);

        mRecyclerView= (RecyclerView) layout.findViewById(com.VitaBit.VitaBit.R.id.my_attention_recyclerView);

        // mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));

        mSwipeRefreshLayout.setColorSchemeResources(com.VitaBit.VitaBit.R.color.orange, com.VitaBit.VitaBit.R.color.black);

        //进入页面的时候加载进度条

        mSwipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mLayoutManager=new LinearLayoutManager(getContext());

        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);
        //添加分隔线

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return layout;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(this);//设置监听

        //mSwipeRefreshLayout.setRefreshing(false);

        mRecyclerView.addOnScrollListener(mOnScrollListener);

    }


    RecyclerView.OnScrollListener mOnScrollListener=new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (adapter == null) {
                return;
            }

            MyLog.i(TAG, "触发时候afterpage= " + afterpage + "pageIndex=" +pageIndex);


            if(afterpage==pageIndex){
                return;
            }

            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {

                MyLog.i(TAG, "加载更多newState= " + newState + "lastVisibleItem=" + lastVisibleItem + "adapter.getItemCount()=" + adapter.getItemCount());

                getNearPeople();

            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
        }

    };


    @Override
    public void onRefresh() {

        //重新请求
        pageIndex=1;
        afterpage=0;
        getNearPeople();
        //访问网络
    }

    private HttpCallback<String> httpCallback=new HttpCallback<String>(){

        @Override
        public void onSucceed(int what, Response<String> response) {
            DataFromServer dataFromServer =JSON.parseObject(response.get(), DataFromServer.class);
            String value = dataFromServer.getReturnValue().toString();
            MyLog.i(TAG, "value=" + value);
//            if (CommonUtils.isStringEmptyPrefer(value) && value instanceof String && ToolKits.isJSONNullObj(value)){
//                //f返回是空的且是第一次
//                if(pageIndex < 2)
//                    MyToast.showShort(getContext(), getString(R.string.relationship_search_empty));
//                afterpage--;
//                return;
//            }
            mSwipeRefreshLayout.setRefreshing(false);

            ArrayList<UserSelected> temp = (ArrayList<UserSelected>) JSON.parseArray(value, UserSelected.class);

            MyLog.i(TAG,"temp:"+temp.size());

            if(temp.size()<1)
            {
                if(pageIndex < 2){
                    MyToast.showShort(getContext(),getString(com.VitaBit.VitaBit.R.string.relationship_search_empty));
                    afterpage--;
                }

                else{

//                    MyToast.showShort(getContext(),"只有这么多了哦");

                    mRecyclerView.removeOnScrollListener(mOnScrollListener);//消除监听

                    return;

                }

            }

            if(pageIndex==1){
                //第一次获得数据
                MyLog.i("pageIndex="+pageIndex,"temp.size()="+temp.size());
                list=temp;
                MyLog.i(TAG, " list=" + list.size());
                adapter=new NearbyAdapter(getContext(),temp);
                mRecyclerView.setAdapter(adapter);
                //判断要不要隐藏foot
                pageIndex++;

            }
            else
            {

                //MyLog.i(TAG,"pageIndex="+pageIndex+"temp.size()="+temp.size());
                pageIndex++;


                MyLog.i(TAG,"加载后的pageIndex="+pageIndex);

                adapter.addMoreItem(temp);
            }

            if (temp.size()!=50){
                MyLog.i(TAG,"取消监听");
                mRecyclerView.removeOnScrollListener(mOnScrollListener);//消除监听
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, CharSequence message, int responseCode, long networkMillis) {

            mSwipeRefreshLayout.setRefreshing(false);

            MyLog.i(TAG, "onFailed");
            afterpage--;

        }

    };

    private void getNearPeople(){

        afterpage++;

        MyLog.i(TAG,"此时的pageIndex="+pageIndex+"afterpage="+afterpage);

        String userId = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id()+"";

        CallServer.getRequestInstance().add(getContext(), false, 10, NoHttpRuquestFactory.create_Near_People_Request(userId, longitude, latitude,pageIndex), httpCallback);

    }



}
