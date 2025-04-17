package kr.co.pplus.store.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class DigestUtil {
	public static String encodeBase64(String str) {
		byte[] encodeByte = Base64.encodeBase64(str.getBytes());
		return new String(encodeByte);
	}

	public static String decodeBase64(String str) {
		byte[] decodeByte = Base64.decodeBase64(str.getBytes());
		return new String(decodeByte);
	}

	public static String encryptMD5(String str){
		String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			MD5 = NumberUtil.bytesToHexString(byteData);

		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
	}

	public static String encryptSHA1(String str) {
		String SHA1 = "";
		try {
			MessageDigest sh = MessageDigest.getInstance("SHA1");
			sh.update(str.getBytes("UTF-8"));
			byte[] digest = sh.digest();
			SHA1 = NumberUtil.bytesToHexString(digest);

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace(); 
			SHA1 = null;
		}

		return SHA1;
	}

	public static String encryptSHA256(String str){
		String SHA256 = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(str.getBytes("UTF-8")); 
			byte byteData[] = sh.digest();
			SHA256 = NumberUtil.bytesToHexString(byteData);

		}catch(NoSuchAlgorithmException | UnsupportedEncodingException e){
			e.printStackTrace(); 
			SHA256 = null; 
		}
		return SHA256;
	}

	public static String encryptSHA256Hmac(String deviceNumber) {
		return DigestUtil.encryptSHA256Hmac("^__^;*!!", deviceNumber);
	}

	public static String encryptSHA256Hmac(String secretKey, String deviceNumber) {

		String SHA256 = ""; 
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);

			byte[] sha256_result = sha256_HMAC.doFinal(deviceNumber.getBytes());
			SHA256 = NumberUtil.bytesToHexString(sha256_result);

		} catch (Exception e) {
			e.printStackTrace(); 
			SHA256 = null; 
		}
		return SHA256;
	}
	
	public static String encryptHMACMD5(String secretKey, String value) {
		String digest = "";
		try {
			Mac mac = Mac.getInstance("HmacMD5");
			SecretKeySpec secret_key = new SecretKeySpec((secretKey).getBytes("UTF-8"), "HmacMD5");
			mac.init(secret_key);
			
			byte[] bytes = mac.doFinal(value.getBytes("ASCII"));
			digest = NumberUtil.bytesToHexString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			digest = null;
		}
		return digest;
	}
}
