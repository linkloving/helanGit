package com.VitaBit.VitaBit.http;

import android.content.Context;

import com.VitaBit.VitaBit.CommParams;
import com.VitaBit.VitaBit.http.basic.MyJsonRequest;
import com.VitaBit.VitaBit.http.data.ActionConst;
import com.VitaBit.VitaBit.http.data.DataFromClientNew;
import com.VitaBit.VitaBit.http.data.JobDispatchConst;
import com.VitaBit.VitaBit.logic.dto.SportRecordUploadDTO;
import com.VitaBit.VitaBit.prefrences.devicebean.VitBitData;
import com.VitaBit.VitaBit.utils.EncrypSHA;
import com.VitaBit.VitaBit.utils.LanguageHelper;
import com.VitaBit.VitaBit.utils.PasswordEncode;
import com.VitaBit.VitaBit.utils.logUtils.MyLog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.linkloving.band.dto.SportRecord;
import com.VitaBit.VitaBit.MyApplication;
import com.linkloving.utils.TimeUtil;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/22.
 */
public class HttpHelper {
     public final static String url_new= CommParams.SERVER_CONTROLLER_URL_NEW;//服务器的根地址（新）
    public final static String client_id = "B2FADC98-631F-441D-BD80-4C30F8C4488D";//固定
    public final static String client_secret = "575116B2-833A-405B-8773-CD4E99A87AA4";//固定
    public final static String private_key = "06354AA5-FC2B-4174-B015-F3B57FEAD6D0";//固定

    public final static String Private_Policy = "https://support.vitabit.software/hc/en-us/sections/201758879-Policies-Guidelines";//固定
    public final static String Visit_Portal = "https://app.vitabit.software";
    public final static String faq = "https://support.vitabit.software";

    /**
     * 提交卡号到服务端接口参数
     */
    public static Request<String> createUpCardNumberRequest(String userId, String cardnumber) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", userId);
        newData.put("card_number", cardnumber);

        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_7);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }

    /**
     * 提交deviceId到服务端接口参数
     */
    public static Request<String> createUpDeviceIdRequest(String user_id, String last_sync_device_id,String last_sync_device_id2) {

        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", user_id);
        newData.put("last_sync_device_id", last_sync_device_id);
        newData.put("last_sync_device_id2", last_sync_device_id2);

        MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_8);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        httpsRequest.setRequestBody(JSON.toJSONString(dataFromClientNew).getBytes());
        return httpsRequest;
    }

     /**
      *  提交运动数据到服务器
      */
     public static Request<String> sportDataSubmitServer(SportRecordUploadDTO sportRecordUploadDTO) {
         MyJsonRequest httpsRequest = new MyJsonRequest(url_new);
         DataFromClientNew dataFromClient = new DataFromClientNew();
         dataFromClient.setActionId(ActionConst.ACTION_101);
         dataFromClient.setJobDispatchId(JobDispatchConst.REPORT_BASE);
         dataFromClient.setData(JSON.toJSONString(sportRecordUploadDTO));
         httpsRequest.setRequestBody(JSON.toJSONString(dataFromClient).getBytes());
         return httpsRequest;
     }

     /**
      *  提交运动数据到服务器 JSON
      */
     public static String sportDataSubmitServerJSON(SportRecordUploadDTO sportRecordUploadDTO) {
         DataFromClientNew dataFromClient = new DataFromClientNew();
         dataFromClient.setActionId(ActionConst.ACTION_101);
         dataFromClient.setJobDispatchId(JobDispatchConst.REPORT_BASE);
         dataFromClient.setData(JSON.toJSONString(sportRecordUploadDTO));
         return JSON.toJSONString(dataFromClient);
     }

    /**
     * 生成云同步
     */
    public static DataFromClientNew GenerateCloudSyncParams(String userId,int pageIndex) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", userId);
        newData.put("pageNum", pageIndex+"");
        DataFromClientNew dataFromClientNew = new DataFromClientNew();
        dataFromClientNew.setActionId(ActionConst.ACTION_100);
        dataFromClientNew.setJobDispatchId(JobDispatchConst.REPORT_BASE);
        dataFromClientNew.setData(JSON.toJSONString(newData));
        return dataFromClientNew;
    }




     /**
      * 生成常见问题等请求
      */
     public static String creatTermUrl(Context context,int type) {
         String termUrl="";
         if(MyApplication.getInstance(context).getLocalUserInfoProvider().getDeviceEntity().getDevice_type() > 0 ){
             termUrl = "https://linkloving.com/linkloving_server_2.0/web/service?" +
                     "page="+type+
                     "&device="+MyApplication.getInstance(context).getLocalUserInfoProvider().getDeviceEntity().getDevice_type()+"&app=1&chan=1&" +
                     "isEn="+!LanguageHelper.isChinese_SimplifiedChinese();

         }else{
             termUrl = "https://linkloving.com/linkloving_server_2.0/web/service?" +
                     "page="+type+
                     "&device="+1+"&app=1&chan=1&" +
                     "isEn="+!LanguageHelper.isChinese_SimplifiedChinese();
         }
         return termUrl;
     }



    /**
     * 获取code
     */
    public static Request<String> getCode(String userId) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("user_id", userId);
        newData.put("client_id", PasswordEncode.client_id);
        newData.put("signature", EncrypSHA.SHA512(userId+client_id+private_key));
        MyLog.e("http", "====================="+EncrypSHA.SHA512(userId+client_id+private_key)); //这后面去做处理连接Vitable的操作
        MyJsonRequest httpsRequest = new MyJsonRequest(CommParams.SERVER_CONTROLLER_URL_HELAN +"code");
        httpsRequest.setRequestBody(JSON.toJSONString(newData));
        return httpsRequest;
    }

    /**
     * 获取token
     */
    public static Request<String> getToken(String code) {
        Map<String, String> newData = new HashMap<String, String>();
        newData.put("code", code);
//        newData.put("client_id", PasswordEncode.client_id);
        newData.put("client_id", "8C9BEFAE-E735-4ADE-85D9-A22D1056F927");
//        newData.put("signature", EncrypSHA.SHA512(code+client_id+client_secret));
        newData.put("signature", EncrypSHA.SHA512(code+"8C9BEFAE-E735-4ADE-85D9-A22D1056F927"+"908FACEF-136B-4354-AC7C-586D623FE151"));

        MyJsonRequest httpsRequest = new MyJsonRequest(CommParams.SERVER_CONTROLLER_URL_HELAN+"authorize");
        httpsRequest.setRequestBody(JSON.toJSONString(newData));
        return httpsRequest;
    }

    /**
     * 获取getProfile
     */
    public static Request<String> getProfile(String token) {
        MyLog.e("getProfile",token);
        MyJsonRequest httpsRequest = new MyJsonRequest(CommParams.SERVER_CONTROLLER_URL_HELAN+"profile", RequestMethod.GET);
        httpsRequest.addHeader("Authorization",token);
        return httpsRequest;
    }
//
//    /**
//     * 获取getProfile
//     */
//    public static Request<String> updataSportDate(String mac, List<SportRecord> sportRecords) {
//        Map<String, String> newData = new HashMap<String, String>();
//        newData.put("device_id", mac);
//        newData.put("duration", duration+"");
//        newData.put("metric", "person.posture.sitting");
//        newData.put("start_time", time);
//        newData.put("value", time);
//        MyJsonRequest httpsRequest = new MyJsonRequest(CommParams.SERVER_CONTROLLER_URL_HELAN+"measurement");
//        httpsRequest.setRequestBody(JSON.toJSONString(newData).getBytes());
//        return httpsRequest;
//    }

    /**
     *  提交运动数据到服务器
     */
//    public static String sport2Server(String mac, List<SportRecord> sportRecords) {
//        List<VitBitData> vitBitDatas = new ArrayList<>();
//        for(int i = 0;i<sportRecords.size();i++){
//            VitBitData vitBitData = new VitBitData();
//            vitBitData.setDevice_id(mac);
//            vitBitData.setDuration(Integer.parseInt(sportRecords.get(i).getDuration()));
//
//            if(sportRecords.get(i).getState()=="112"){
//                vitBitData.setMetric("person.posture.sitting");
//            }else if(sportRecords.get(i).getState()=="113"){
//                vitBitData.setMetric("person.posture.standing");
//            }else if(sportRecords.get(i).getState()=="1" ||sportRecords.get(i).getState()=="2"  ){
//                vitBitData.setMetric("person.activity.walking");
//            }
//            vitBitData.setStart_time(sportRecords.get(i).getStart_time());
//            vitBitData.setValue(Integer.parseInt(sportRecords.get(i).getDuration()));
//            vitBitDatas.add(vitBitData);
//        }
//        return JSON.toJSONString(vitBitDatas);
//    }

    /**
     * 获取getProfile
     */
    public static Request<String> updataSportDate(String mac, List<SportRecord> sportRecords,String token) {
        List<VitBitData> vitBitDatas = new ArrayList<>();
        for(int i = 0;i<sportRecords.size();i++){
            if(sportRecords.get(i).getState().equals("1") || sportRecords.get(i).getState().equals("2") )
            {
                if(Integer.parseInt(sportRecords.get(i).getDuration())<=0){
                    continue;
                }
                VitBitData vitBitData = new VitBitData();
                vitBitData.setDevice_id(mac);
                vitBitData.setDuration(Integer.parseInt(sportRecords.get(i).getDuration()));
                vitBitData.setMetric("person.activity.walking");
                vitBitData.setStart_time(format(sportRecords.get(i).getStart_time()));
                vitBitData.setValue(Integer.parseInt(sportRecords.get(i).getDuration()));
                MyLog.e("【NEW离线数据同步】","vitBitData.toString()："+vitBitData.toString());
                vitBitDatas.add(vitBitData);


                VitBitData vitBitData1 = new VitBitData();
                vitBitData1.setDevice_id(mac);
                vitBitData1.setDuration(Integer.parseInt(sportRecords.get(i).getDuration()));
                vitBitData1.setMetric("person.steps");
                vitBitData1.setStart_time(format(sportRecords.get(i).getStart_time()));
                vitBitData1.setValue(Integer.parseInt(sportRecords.get(i).getStep()));
                MyLog.e("【NEW离线数据同步】","vitBitData.toString()："+vitBitData1.toString());
                vitBitDatas.add(vitBitData1);
            }
            else
            {
                if(sportRecords.get(i).getState().equals("7")){
                    continue;
                }
                if(Integer.parseInt(sportRecords.get(i).getDuration())<=0){
                    continue;
                }
                VitBitData vitBitData = new VitBitData();
                vitBitData.setDevice_id(mac);
                vitBitData.setDuration(Integer.parseInt(sportRecords.get(i).getDuration()));
                if(sportRecords.get(i).getState().equals("112")){
                    vitBitData.setMetric("person.posture.sitting");
                }else if(sportRecords.get(i).getState().equals("113")){
                    vitBitData.setMetric("person.posture.standing");
                }else if(sportRecords.get(i).getState().equals("0")){
                    vitBitData.setMetric("person.activeness.sedentary");
                }
                vitBitData.setStart_time(format(sportRecords.get(i).getStart_time()));
                vitBitData.setValue(Integer.parseInt(sportRecords.get(i).getDuration()));
                MyLog.e("【NEW离线数据同步】","vitBitData.toString()："+vitBitData.toString());
                vitBitDatas.add(vitBitData);
            }
        }
        MyJsonRequest httpsRequest = new MyJsonRequest(CommParams.SERVER_CONTROLLER_URL_HELAN+"measurement");
        httpsRequest.addHeader("Authorization",token);
        MyLog.e("【NEW离线数据同步】","data："+JSON.toJSONString(vitBitDatas, SerializerFeature.DisableCircularReferenceDetect));
        httpsRequest.setRequestBody(JSON.toJSONString(vitBitDatas, SerializerFeature.DisableCircularReferenceDetect).getBytes());
        return httpsRequest;
    }

    //将起始时间加上s
    private static String format(String date){
        Date d = TimeUtil.stringToDate(date,"yyyy-MM-dd HH:mm:ss");
        String dateStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(d);
        Date newdate = TimeUtil.stringToDate(dateStr,"yyyy-MM-dd'T'HH:mm:ssZ");
        return dateStr;
    }
}
