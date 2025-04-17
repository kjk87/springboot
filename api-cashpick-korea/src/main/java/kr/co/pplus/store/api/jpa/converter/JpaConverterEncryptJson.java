package kr.co.pplus.store.api.jpa.converter;

import au.com.xandar.crypto.AsymmetricCipher;
import au.com.xandar.crypto.CryptoPacket;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.pplus.store.api.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.*;
import java.util.Map;

@Converter(autoApply = true)
public class JpaConverterEncryptJson implements AttributeConverter<Object, byte[]> {

    private static final Logger logger = LoggerFactory.getLogger(JpaConverterEncryptJson.class);

    private static final String PUBLIC_KEY_BASE64 =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCKkC/b2fjIdS8atGeX/cOP8YWeEypeGnB1ICap\n" +
                    "dWajoUGGXWbl3410gZopmcXbN9imMmNNs7I9KsOKJj6b7PzP4/p74da55kaht0l63603iKandHAw\n" +
                    "7PeTWpHhJ3l12tAtvtiKppHqoY4IJDunOCHjB1fpmCkEX9JQ+a3vFl1/5wIDAQAB";

    private static final String PRIVATE_KEY_BASE64 =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIqQL9vZ+Mh1Lxq0Z5f9w4/xhZ4T\n" +
                    "Kl4acHUgJql1ZqOhQYZdZuXfjXSBmimZxds32KYyY02zsj0qw4omPpvs/M/j+nvh1rnmRqG3SXrf\n" +
                    "rTeIpqd0cDDs95NakeEneXXa0C2+2IqmkeqhjggkO6c4IeMHV+mYKQRf0lD5re8WXX/nAgMBAAEC\n" +
                    "gYAstefrfgMr07w2Vr4SqjyfRuTBpBeIs+lTseMnzQ0ogZEeJSddx2viiytOfyL74KJUxm+KlBBQ\n" +
                    "cmsUOdD8CVVt2VcH63naa835YEVojqdj3X05IZk72LbH7eoaDDr9gL3DCOs7BdCWCJyLv93AzaZJ\n" +
                    "zLpu3d7kKEvKlL8La/SvkQJBAM+ouGIRYDD89JbZgKUnCbyHSd3VFfKOluc5/Fn4CcT8vhbrQgmA\n" +
                    "pb8rTCs7TkC4Ya66u+zB+If5CkUOe5GkjmkCQQCq0cEk+wplQIKSc6v58+k+eORgL6ld0JdiKjgi\n" +
                    "dpEMHSmtM6et6Ukhi+CWZ/oEK6O20WhOWUHqzpOCi8PponHPAkA3293FW4ExjEnK7jUBt++RjB7d\n" +
                    "kj02Iw8Kofl0xhjyqT4E8kGwRq/PLblug6R4GmEEXGzCsibFhMMzckLhGY/JAkBy4yySYL23J9Iq\n" +
                    "Cd5K+H+RYuHGx4eT721Bur+SfkhD64FSWoGWeGaVR2y//CKtl2Q+20zaFTI+aL3ReYtEodsFAkEA\n" +
                    "ooGmtWsgxDSThLn2l+gYhfZLy+hrewTWc3rvfd59Vmdvw+06d4PFM6mlwE8SJPON2uFfaztwoOFy\n" +
                    "eoxJEeDeow==";

    private final AsymmetricCipher cipher = new AsymmetricCipher();


    private final static ObjectMapper objectMapper = new ObjectMapper();


    private byte[] encrypt(Map<String, String> map) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(map);
            out.flush();
            final byte[] yourBytes = bos.toByteArray();
            final CryptoPacket cryptoPacket = cipher.encrypt(yourBytes, PRIVATE_KEY_BASE64);
            out.close() ;
            bos.close() ;
            bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.writeObject(cryptoPacket);
            out.flush();
            final byte[] returnBytes = bos.toByteArray();
            return returnBytes ;
        } finally {
            try {
                bos.close();
            } catch (Exception ex) {
                // ignore close exception
            }
        }
    }

    private Map<String, String> decrypt(byte[] yourBytes) throws Exception {

        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            final CryptoPacket cryptoPacket  = (CryptoPacket)in.readObject();
            byte[] outputData = cipher.decrypt(cryptoPacket, PUBLIC_KEY_BASE64);
            in.close() ;
            bis.close() ;
            bis = new ByteArrayInputStream(outputData);
            in = new ObjectInputStream(bis);
            final Map<String, String> map  = (Map<String, String>)in.readObject();
            return map ;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }


    @Override
    public byte[] convertToDatabaseColumn(Object meta) {

        if (meta == null)
            return null;

        Map<String, String> map = (Map<String, String>)meta ;
        try {

            return encrypt( map ) ;
            /*
            PrivateKey privateKey = CryptoUtil.loadPrivateKey(); ;
            Set<Map.Entry<String,String>> set = map.entrySet() ;

            System.out.println(privateKey.toString());
            for(Map.Entry entry : set){
                String key = (String)entry.getKey() ;
                String value = (String)entry.getValue() ;
                System.out.println(key + ":" + value) ;
                //map.put(key, CryptoUtil.encryptWithRSA(privateKey, value)) ;
                map.put(key, encrypt(value)) ;
            }
            return objectMapper.writeValueAsString(map);
            */
        } catch (Exception ex) {
            logger.error(AppUtil.excetionToString(ex) + ":" + map.toString()) ;
            return null;
            // or throw an error
        }
    }

    @Override
    public Object convertToEntityAttribute(byte[] dbData) {

        if (dbData == null)
            return null;

        try {
            /*
            PrivateKey privateKey = CryptoUtil.loadPrivateKey();
            Map<String, String> map = (Map<String, String>)objectMapper.readValue(dbData, Object.class) ;
            Set<Map.Entry<String,String>> set = map.entrySet() ;
            for(Map.Entry entry : set){
                String key = (String)entry.getKey() ;
                String value = (String)entry.getValue() ;
                System.out.println(key + ":" + value) ;
                //map.put(key, CryptoUtil.decryptWithRSA(privateKey, value)) ;
                map.put(key, decrypt(value)) ;
            }
            return map ;
            */
            Map<String, String> map = decrypt(dbData) ;
            return map ;
        } catch (Exception ex) {
            logger.error(AppUtil.excetionToString(ex) + ":" + dbData);
            return null;
        }
    }

}