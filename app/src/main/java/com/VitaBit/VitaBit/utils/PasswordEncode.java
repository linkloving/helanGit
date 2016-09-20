/**
 * PasswordEncode.java
 * @author Jason Lu
 * @date 2016-6-27
 * @version 1.0
 */
package com.VitaBit.VitaBit.utils;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 类说明：密码的加密与解密
 * 
 * @author 作者: LiuJunGuang
 * @version 创建时间：2011-5-19 下午11:07:09
 */
public class PasswordEncode
{
	public final static String MD5 = "MD5";
	public final static String NONE = "NONE";
	public final static String SHA_256 = "SHA-256";
	public final static String SHA_512 = "SHA-512";
	public final static String SHA_384 = "SHA-384";

	public final static String client_id = "B2FADC98-631F-441D-BD80-4C30F8C4488D";//固定
	public final static String client_secret = "575116B2-833A-405B-8773-CD4E99A87AA4";//固定
	public final static String private_key = "06354AA5-FC2B-4174-B015-F3B57FEAD6D0";//固定

	/**
	 * 加密文件算法
	 *
	 * @param filename
	 *            需要加密的文件名
	 * @param algorithm
	 *            加密算法名
	 */
	public static void digestFile(String filename, String algorithm)
	{
		byte[] b = new byte[1024 * 4];
		int len = 0;
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance(algorithm);
			fis = new FileInputStream(filename);
			while ((len = fis.read(b)) != -1)
			{
				md.update(b, 0, len);
			}
			byte[] digest = md.digest();
			StringBuffer fileNameBuffer = new StringBuffer(128).append(filename).append(".").append(algorithm);
			fos = new FileOutputStream(fileNameBuffer.toString());
			OutputStream encodedStream = new Base64OutputStream(fos);
			encodedStream.write(digest);
			encodedStream.flush();
			encodedStream.close();
		}
		catch (Exception e)
		{
			System.out.println("Error computing Digest: " + e);
		}
		finally
		{
			try
			{
				if (fis != null)
					fis.close();
			}
			catch (Exception ignored)
			{
			}
			try
			{
				if (fos != null)
					fos.close();
			}
			catch (Exception ignored)
			{
			}
		}
	}

	/**
	 * 加密密码算法
	 * 
	 * @param password
	 *            需要加密的原始密码
	 * @param alg
	 *            加密算法名称
	 * @return 加密后的密码
	 * @throws NoSuchAlgorithmException
	 *             当加密算法不可用时抛出此异常
	 */
	public static String digestString(String password, String alg) throws NoSuchAlgorithmException
	{
		String newPass;
		if (alg == null || MD5.equals(alg))
		{
			newPass = DigestUtils.md5Hex(password);
		}
		else if (NONE.equals(alg))
		{
			newPass = password;
		}
		else if (SHA_256.equals(alg))
		{
			newPass = DigestUtils.sha256Hex(password);
		}
		else if (SHA_384.equals(alg))
		{
			newPass = DigestUtils.sha384Hex(password);
		}
		else if (SHA_512.equals(alg))
		{
			newPass = DigestUtils.sha512Hex(password);
		}
		else
		{
			newPass = DigestUtils.shaHex(password);
		}
		return newPass;
	}

	/**
	 * 加密密码算法，默认的加密算法是MD5
	 * 
	 * @param password
	 *            未加密的密码
	 * @return String 加密后的密码
	 */
	public static String digestPassword(String password)
	{
		try
		{
			if (password != null && !"".equals(password))
			{
				return digestString(password, MD5);
			}
			else
				return null;
		}
		catch (NoSuchAlgorithmException nsae)
		{
			throw new RuntimeException("Security error: " + nsae);
		}
	}

	/**
	 * 判断密码是不是相等，默认的加密算法是MD5
	 * 
	 * @param beforePwd
	 *            要判断的密码
	 * @param afterPwd
	 *            加密后的数据库密码
	 * @return Boolean true 密码相等
	 */
	public static boolean isPasswordEnable(String beforePwd, String afterPwd)
	{
		if (beforePwd != null && !"".equals(beforePwd))
		{
			String password = digestPassword(beforePwd);
			return afterPwd.equals(password);
		}
		else
			return false;
	}
	
	public static String digestSignature(String code, String client_id, String client_secret)
	{
		String str = code + client_id + client_secret;
		String signature = "";
		try {
			signature = digestString(str, PasswordEncode.SHA_512);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return signature;
	}


//	public static void main(String[] args) throws NoSuchAlgorithmException
//	{
//
//		String code = "1787";
//		String userId = "111";
//
//		System.out.println(PasswordEncode.digestSignature(code, client_id, client_secret));
//	}
}