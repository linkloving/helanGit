package com.linkloving.helan.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/7/20.
 */
public class EncrypSHA {
    public final static String Clientid= "B2FADC98-631F-441D-BD80-4C30F8C4488D";
    public final static String ClientSecret= "575116B2-833A-405B-8773-CD4E99A87AA4";
    public final static String private_key = "06354AA5-FC2B-4174-B015-F3B57FEAD6D0";//固定
    /**
     * TODO(description of this method)
     * @param info
     * @author丶贰九 2015-4-29 下午5:12:17
     * @since v1.0
     */

    //byte字节转换成16进制的字符串MD5Utils.hexString
    public byte[] eccrypt(String info, String shaType) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance(shaType);
        byte[] srcBytes = info.getBytes();
        // 使用srcBytes更新摘要
        sha.update(srcBytes);
        // 完成哈希计算，得到result
        byte[] resultBytes = sha.digest();
        return resultBytes;
    }

    public byte[] eccryptSHA1(String info) throws NoSuchAlgorithmException {
        return eccrypt(info, "SHA1");
    }

    public byte[] eccryptSHA256(String info) throws NoSuchAlgorithmException {
        return eccrypt(info, "SHA-256");
    }

    public byte[] eccryptSHA384(String info) throws NoSuchAlgorithmException {
        return eccrypt(info, "SHA-384");
    }

    public byte[] eccryptSHA512(String info) throws NoSuchAlgorithmException {
        return eccrypt(info, "SHA-512");
    }

    public static String SHA512(String info){
        EncrypSHA sha = new EncrypSHA();
        String sha1= null;
        try {
            sha1 = sha.hexString(sha.eccryptSHA512(info));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sha1;
    }


    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(SHA512("111"+ Clientid+private_key));
    }

    public static String hexString(byte[] bytes){
        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            int val = ((int) bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
