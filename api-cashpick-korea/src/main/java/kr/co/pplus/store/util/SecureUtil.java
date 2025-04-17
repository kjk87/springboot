package kr.co.pplus.store.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class SecureUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecureUtil.class);

    private final static byte[] keyBytes = {-20, -96, -100, -20, -99, -76, -19, -120, -84, -20, -105, -108, -20, -89, -79, -19};

    private final static IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);

    private final static SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

    /**
     * @param loginId
     * @param password
     * @return
     * @Description        : 비밀번호 단방향 암호화 메소드 (loginId도 반드시 필요)
     * @Author            : angelkum
     * @Date                : 오후 2:21:18
     */
    public static String encryptPassword(String loginId, String password) {
        return DigestUtil.encryptSHA256Hmac(loginId + "xwspqj)(#", password + "eptigvy@&^(");
    }

    /**
     * @param mobileNumber
     * @return
     * @Description        : 메소드이름에서 알 수 있듯이 핸드폰번호 전용 암호화 메소드
     * @Author            : angelkum
     * @Date                : 오후 2:22:58
     */
    public static String encryptMobileNumber(String mobileNumber) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal((mobileNumber + ".vnkwpq#^&").getBytes("UTF-8"));
            return new String(Base64.getEncoder().encode(encrypted));
        } catch (Exception e) {
            logger.error("input=" + mobileNumber, e);
            return null;
        }
    }

    /**
     * @param mobileNumber
     * @return
     * @Description        : 메소드이름에서 알 수 있듯이 핸드폰번호 전용 복호화 메소드
     * @Author            : angelkum
     * @Date                : 오후 2:23:23
     */
    public static String decryptMobileNumber(String mobileNumber) {

        if (StringUtils.isEmpty(mobileNumber))
            return mobileNumber;

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            //logger.debug("decryptMobileNumber : " +  mobileNumber) ;
            byte[] encrypted = Base64.getDecoder().decode(mobileNumber);
            //logger.debug("encryptedMobileNumber.length : " +  encrypted.length) ;
            String decrypted = new String(cipher.doFinal(encrypted), "UTF-8");

            return decrypted.replace(".vnkwpq#^&", "");
        } catch (Exception e) {
            logger.error("input=" + mobileNumber, e);
            return null;
        }
    }

    public static String urlDecoder(String data) {

        return new String(Base64.getUrlDecoder().decode(data));
    }

    public static String decryptBase64(String base64) {
        byte[] byteArray = org.apache.commons.codec.binary.Base64.decodeBase64(base64.getBytes());

        // Print the decoded array
        // System.out.println(Arrays.toString(byteArray));

        // Print the decoded string
        String decodedString = new String(byteArray);

        return decodedString;
    }

    public static String encryptBase64(String utf8) {
        byte[] byteArray = org.apache.commons.codec.binary.Base64.encodeBase64(utf8.getBytes());

        // Print the decoded array
        // System.out.println(Arrays.toString(byteArray));

        // Print the decoded string
        String encodedString = new String(byteArray);

        return encodedString;
    }

    public static String encryptTest(String test) {
        try {

            IvParameterSpec ivParameterSpec = new IvParameterSpec("zsefvgyjmko@$^1+".getBytes());

            SecretKeySpec secretKeySpec = new SecretKeySpec("plmjygvcdwa!#%&(".getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal((test + ".vnkwpq#^&").getBytes("UTF-8"));
            return new String(Base64.getEncoder().encode(encrypted));
        } catch (Exception e) {
            logger.error("input=" + test, e);
            return null;
        }
    }

    public static String decryptTest(String test) {

        if (StringUtils.isEmpty(test))
            return test;

        try {

            IvParameterSpec ivParameterSpec = new IvParameterSpec("zsefvgyjmko@$^1+".getBytes());

            SecretKeySpec secretKeySpec = new SecretKeySpec("plmjygvcdwa!#%&(".getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            //logger.debug("decryptMobileNumber : " +  mobileNumber) ;
            byte[] encrypted = Base64.getDecoder().decode(test);
            //logger.debug("encryptedMobileNumber.length : " +  encrypted.length) ;
            String decrypted = new String(cipher.doFinal(encrypted), "UTF-8");

            return decrypted.replace(".vnkwpq#^&", "");
        } catch (Exception e) {
            logger.error("input=" + test, e);
            return null;
        }
    }

    public static void main(String[] argv) {
//        String loginId = "luckyball" + "##" + "qkrwlgns8080@naver.com";
//        System.out.println(loginId.replace("luckyball" + "##", ""));
//        String password = encryptPassword(loginId.replace("luckyball" + "##", ""), "naver-luckyball##qkrwlgns8080@naver.com");
//        System.out.println(password);
//
//        password = encryptPassword("qkrwlgns8080@naver.com", "F2+URdGV/1iyruB3mPBHFqyhklhQT1FTmreSO5G3KH0rA1Cl9/5s8h2AjOdt4JAfOduPRqYiEiDapCwDEPoYSA==");
//        System.out.println(password);

        System.out.println(encryptMobileNumber("luckyball##01081271638"));
    }
}
