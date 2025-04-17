package kr.co.pplus.store.util;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.codec.binary.Base64;

public class KeyGenerator {
    private static Base64 BASE64 = new Base64(true);

    public static String generateMobileAuth(){
        Random r = new Random() ;
        return String.format("%07d", r.nextInt(10000000)) ;
    }

    public static String generateOrderNo(){
        Date now = new Date() ;
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String to = transFormat.format(now);
        Random random = new Random(now.getTime()) ;
        return to + String.format("%4d", random.nextInt()%10000) ;
    }

    /*
     *  randomUUID 를  Base64  코드 형식으로 자동 생성
     * @return  uuid 스트링
     */
    public static String generateKey(){
        UUID uuid = UUID.randomUUID();
        byte[] uuidArray = KeyGenerator.toByteArray(uuid);
        byte[] encodedArray = BASE64.encode(uuidArray);
        String returnValue = new String(encodedArray);
        returnValue = StringUtils.removeEnd(returnValue, "\r\n");
        return returnValue;
    }

    /*
     *  입력된 아규먼트 문장을  sha256  암호로 변경
     * @param 	str  입력 문장
     * @return  sha256 암호로 변환된 스트링
     */
    public static String sha256(String str){
        String SHA = "";
        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuffer sb = new StringBuffer();
            for(int i = 0 ; i < byteData.length ; i++){
                sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();

        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }

    /*
     *  입력된 아규먼트 문장을  sha256  암호로 변경(SEED 포함)
     * @param 	str  입력 문장
     * @return  sha256 암호로 변환된 스트링
     */
    public static String  password(String str){
        final String SEED = "_P_R_N_u_m_b_e_r_" ;
        return sha256(SEED+str) ;
    }

    /*
     *  randomUUID 를  sha256   암호 코드 형식으로 자동 생성
     * @return   sha256  암호 스트링
     */
    public static String generateSha256() {
        return sha256(UUID.randomUUID().toString()) ;
    }

    /*
     *  BASE64  코드를 원래의  random UUID  스트링으로 변환
     * @return   random UUID  스트링
     */
    public static String convertUUID(String key){
        byte[] encodedArray = BASE64.encode(key.getBytes());
        String returnValue = new String(encodedArray);
        returnValue = StringUtils.removeEnd(returnValue, "\r\n");
        return returnValue;
    }

    /*
     *  BASE64  코드를 원래의  random UUID 객체로 변환
     * @return   random UUID  객체
     */
    public static UUID convertKey(String key){
        UUID returnValue = null;
        if(StringUtils.isNotBlank(key)){
            // Convert base64 string to a byte array
            byte[] decodedArray = BASE64.decode(key);
            returnValue = KeyGenerator.fromByteArray(decodedArray);
        }
        return returnValue;
    }

    /*
     *  UUID 객체를  byte  어레이로 변환
     * @param 	uuid UUID  객체
     * @return   변환된  byte  어레이
     */
    private static byte[] toByteArray(UUID uuid) {
        byte[] byteArray = new byte[(Long.SIZE / Byte.SIZE) * 2];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        LongBuffer longBuffer = buffer.asLongBuffer();
        longBuffer.put(new long[] { uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() });
        return byteArray;
    }

    /*
     *  byte  어레이를  UUID  객체로 변환
     * @param 	 bytes  변환할  byte  어레이
     * @return   변환된   UUID 객체
     */
    private static UUID fromByteArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        LongBuffer longBuffer = buffer.asLongBuffer();
        return new UUID(longBuffer.get(0), longBuffer.get(1));
    }


    public static void main(String[] argv) {

        System.out.println(KeyGenerator.generateKey()) ;
    }

}
