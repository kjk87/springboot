package kr.co.pplus.store.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class WalletSecureUtil {
    private static final Logger logger = LoggerFactory.getLogger(WalletSecureUtil.class);

    private static final String devSecretKey = "2r5u8x!A%D*G-KaPdSgVkYp3s6v9y$B?";
    private static final String devIvKey = "mZq4t7w!z$C&F)J@";

    private static final String prodSecretKey = "H@McQfTjWnZr4t7w!z%C*F-JaNdRgUkX";
    private static final String prodIvKey = "6w9z$C&F)J@NcRfU";



    public static String encrypt(String data, String storeType) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            String secretKey;
            String ivKey;
            if(storeType.equals("PROD")){
                secretKey = prodSecretKey;
                ivKey = prodIvKey;
            }else{
                secretKey = devSecretKey;
                ivKey = devIvKey;
            }

            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivKey.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal((data).getBytes(StandardCharsets.UTF_8));

            return new String(Base64.getEncoder().encode(encrypted));
        } catch (Exception e) {
            logger.error("input=" + data, e);
            return null;
        }
    }

    public static String decrypt(String data, String storeType) {

        if (StringUtils.isEmpty(data))
            return data;

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            String secretKey;
            String ivKey;
            if(storeType.equals("PROD")){
                secretKey = prodSecretKey;
                ivKey = prodIvKey;
            }else{
                secretKey = devSecretKey;
                ivKey = devIvKey;
            }

            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivKey.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = Base64.getDecoder().decode(data);

            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("input=" + data, e);
            return null;
        }
    }


}
