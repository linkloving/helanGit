package com.VitaBit.VitaBit.logic.UI.main.pay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.proltrol.dto.LLTradeRecord;
import com.example.android.bluetoothlegatt.proltrol.dto.LLXianJinCard;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;

import java.text.MessageFormat;
import java.util.LinkedList;

/**
 * Created by zkx on 2016/3/30.
 */
public class WalletAdapter extends RecyclerView.Adapter{
    private static final String TAG = WalletAdapter.class.getSimpleName();

    public static final int TYPE_QIANBAO = 98;
    public static final int TYPE_XIANJIN = 99;

    private int TYPE ;

    private Context mContext;

    private  LinkedList<LLTradeRecord> qianbaolist;
    private  LinkedList<LLXianJinCard> list_XJ;

    public WalletAdapter(Context context, Object obj ,int type) {
        this.mContext = context;
        this.TYPE = type;
        if(TYPE == TYPE_QIANBAO){
            this.qianbaolist = ((LinkedList<LLTradeRecord>) obj);
        }
        else if(TYPE == TYPE_XIANJIN){
            this.list_XJ = ((LinkedList<LLXianJinCard>) obj);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.VitaBit.VitaBit.R.layout.adapter_pay_list, null);
//        不知道为什么在xml设置的“android:layout_width="match_parent"”无效了，需要在这里重新设置
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MenuViewHolder holder = (MenuViewHolder) viewHolder;
        if(TYPE == TYPE_QIANBAO){
            LLTradeRecord qianbao = qianbaolist.get(position);
            String time = qianbao.getTradeTime().toString();
            String year = time.substring(0,4);
            String month = time.substring(4,6);
            String day = time.substring(6,8);
            String hour = time.substring(8,10);
            String min = time.substring(10,12);
            String sec = time.substring(12,14);
            holder.time.setText(MessageFormat.format(mContext.getString(com.VitaBit.VitaBit.R.string.menu_pay_time), year, month, day , hour , min ,sec));
            MyLog.e(TAG,"time========"+time);
            //充值
            if(qianbao.getTradeType().equals("in")){
                holder.money.setText("+ "+mContext.getResources().getString(com.VitaBit.VitaBit.R.string.menu_pay_yuan)+qianbao.getTradeAmount()+"");
                holder.money.setTextColor(mContext.getResources().getColor(com.VitaBit.VitaBit.R.color.text_light_green));
            }
            //消费
            else if(qianbao.getTradeType().equals("out")){
                holder.money.setText("- "+mContext.getResources().getString(com.VitaBit.VitaBit.R.string.menu_pay_yuan)+qianbao.getTradeAmount()+"");
                holder.money.setTextColor(mContext.getResources().getColor(com.VitaBit.VitaBit.R.color.yellow_title));
            }

        }else if(TYPE == TYPE_XIANJIN){
            LLXianJinCard xianjin = list_XJ.get(position);
            String time = "20"+xianjin.getData_3().toString()+xianjin.getTime_3().toString();
            String year = time.substring(0,4);
            String month = time.substring(4,6);
            String day = time.substring(6,8);

            String hour = time.substring(8,10);
            String min = time.substring(10,12);
            String sec = time.substring(12,14);

            holder.time.setText(year+mContext.getString(com.VitaBit.VitaBit.R.string.unit_year)+month+mContext.getString(com.VitaBit.VitaBit.R.string.unit_month)+day+mContext.getString(com.VitaBit.VitaBit.R.string.unit_day));

            if(xianjin.getTred_type_1().equals("in")){

                holder.money.setText("圈存 "+xianjin.getXianjinAmount_6());

            }else if(xianjin.getTred_type_1().equals("quanti")){

                holder.money.setText("圈提"+xianjin.getXianjinAmount_6());
            }
            else if(xianjin.getTred_type_1().equals("out")){

                holder.money.setText("消费"+xianjin.getXianjinAmount_6());
            }
        }

    }

    @Override
    public int getItemCount() {
        if(TYPE == TYPE_QIANBAO){
            return qianbaolist.size();
        }else if(TYPE == TYPE_XIANJIN){
            return list_XJ.size();
        }
        return 0;
    }

    class MenuViewHolder extends RecyclerView.ViewHolder{
        //时间
        public TextView time;
        //金额
        public TextView money;

        public MenuViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(com.VitaBit.VitaBit.R.id.time);
            money = (TextView) itemView.findViewById(com.VitaBit.VitaBit.R.id.money);
        }
    }
}
