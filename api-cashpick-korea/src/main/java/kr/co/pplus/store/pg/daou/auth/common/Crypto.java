package kr.co.pplus.store.pg.daou.auth.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class Crypto {
    public static String md5Encrypt(String InStr) throws Exception {
        StringBuffer sb = new StringBuffer();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(InStr.getBytes());
        byte[] md5Bytes = md5.digest();

        for(int i = 0; i < md5Bytes.length; ++i) {
            String md5Char = Integer.toHexString(255 & (char)md5Bytes[i]);
            if (md5Char.length() < 2) {
                md5Char = "0" + md5Char;
            }

            sb.append(md5Char);
        }

        return new String(Base64.getEncoder().encode(sb.toString().getBytes()));
    }

    public static String sha256(String InStr) throws Exception {
        StringBuffer sb = new StringBuffer();
        MessageDigest md5 = MessageDigest.getInstance("SHA-256");
        md5.update(InStr.getBytes());
        byte[] md5Bytes = md5.digest();

        for(int i = 0; i < md5Bytes.length; ++i) {
            String md5Char = Integer.toHexString(255 & (char)md5Bytes[i]);
            if (md5Char.length() < 2) {
                md5Char = "0" + md5Char;
            }

            sb.append(md5Char);
        }

        return sb.toString();
    }

    public static String sha256Encrypt(String InStr) throws Exception {
        StringBuffer sb = new StringBuffer();
        MessageDigest md5 = MessageDigest.getInstance("SHA-256");
        md5.update(InStr.getBytes());
        byte[] md5Bytes = md5.digest();

        for(int i = 0; i < md5Bytes.length; ++i) {
            String md5Char = Integer.toHexString(255 & (char)md5Bytes[i]);
            if (md5Char.length() < 2) {
                md5Char = "0" + md5Char;
            }

            sb.append(md5Char);
        }

        return new String(Base64.getEncoder().encode(sb.toString().getBytes()));
    }

    public static String sha512Encrypt(String InStr) throws Exception {
        StringBuffer sb = new StringBuffer();
        MessageDigest md5 = MessageDigest.getInstance("SHA-512");
        md5.update(InStr.getBytes());
        byte[] md5Bytes = md5.digest();

        for(int i = 0; i < md5Bytes.length; ++i) {
            String md5Char = Integer.toHexString(255 & (char)md5Bytes[i]);
            if (md5Char.length() < 2) {
                md5Char = "0" + md5Char;
            }

            sb.append(md5Char);
        }

        return sb.toString();
    }

    public static byte[] hexToByteArray(String hex) {
        if (hex != null && hex.length() != 0) {
            byte[] ba = new byte[hex.length() / 2];

            for(int i = 0; i < ba.length; ++i) {
                ba[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
            }

            return ba;
        } else {
            return null;
        }
    }

    public static String byteArrayToHex(byte[] ba) {
        if (ba != null && ba.length != 0) {
            StringBuffer sb = new StringBuffer(ba.length * 2);

            for(int x = 0; x < ba.length; ++x) {
                String hexNumber = "0" + Integer.toHexString(255 & ba[x]);
                sb.append(hexNumber.substring(hexNumber.length() - 2));
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    public static String convert_to_ksc(String str) {
        String result = null;

        try {
            byte[] kscBytes = str.getBytes("8859_1");
            result = new String(kscBytes, "KSC5601");
        } catch (Exception var3) {
        }

        return result;
    }

    public static String Encrypt(String InKey, String InValue) throws Exception {
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Encrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, key, ivSpec);
            byte[] LB_EncryptValue = cipher.doFinal(InValue.getBytes("EUC-KR"));
            LS_Encrypt = byteArrayToHex(LB_EncryptValue);
            return LS_Encrypt;
        }
    }

    public static String Decrypt(String InKey, String InValue) throws Exception {
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Decrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, key, ivSpec);
            byte[] LB_Value = hexToByteArray(InValue);
            byte[] LB_DecryptValue = cipher.doFinal(LB_Value);
            LS_Decrypt = new String(LB_DecryptValue, "euc-kr");
            return LS_Decrypt;
        }
    }

    public static String DaouEncrypt2(String InKey, String InValue) throws Exception {
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Encrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, key, ivSpec);
            byte[] LB_EncryptValue = cipher.doFinal(InValue.getBytes());
            LS_Encrypt = byteArrayToHex(LB_EncryptValue);
            return LS_Encrypt;
        }
    }

    public static String DaouDecrypt2(String InKey, String InValue) throws Exception {
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Decrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, key, ivSpec);
            byte[] LB_Value = hexToByteArray(InValue);
            byte[] LB_DecryptValue = cipher.doFinal(LB_Value);
            LS_Decrypt = new String(LB_DecryptValue, "euc-kr");
            return LS_Decrypt;
        }
    }

    public static String DaouEncrypt(String InKey, String InValue) throws Exception {
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            InValue = EnCode(InValue);
            String LS_Encrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, key, ivSpec);
            byte[] LB_EncryptValue = cipher.doFinal(InValue.getBytes());
            LS_Encrypt = byteArrayToHex(LB_EncryptValue);
            return LS_Encrypt;
        }
    }

    public static String DaouDecrypt(String InKey, String InValue) throws Exception {
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Decrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, key, ivSpec);
            byte[] LB_Value = hexToByteArray(InValue);
            byte[] LB_DecryptValue = cipher.doFinal(LB_Value);
            LS_Decrypt = new String(LB_DecryptValue);
            InValue = DeCode(LS_Decrypt);
            return InValue;
        }
    }

    public static String EnCode(String param) {
        StringBuffer sb = new StringBuffer();
        if (param == null) {
            sb.append("");
        } else if (param.length() > 0) {
            for(int i = 0; i < param.length(); ++i) {
                String len = "" + param.charAt(i);
                sb.append(len.length());
                sb.append(param.charAt(i));
            }
        }

        return sb.toString();
    }

    public static String DeCode(String param) {
        StringBuffer sb = new StringBuffer();
        int pos = 0;
        boolean flg = true;
        if (param != null) {
            if (param.length() > 1) {
                while(flg) {
                    String sLen = param.substring(pos++, pos);
                    boolean var5 = false;

                    int nLen;
                    try {
                        nLen = Integer.parseInt(sLen);
                    } catch (Exception var7) {
                        nLen = 0;
                    }

                    String code = "";
                    if (pos + nLen > param.length()) {
                        code = param.substring(pos);
                    } else {
                        code = param.substring(pos, pos + nLen);
                    }

                    pos += nLen;
                    sb.append((char)Integer.parseInt(code));
                    if (pos >= param.length()) {
                        flg = false;
                    }
                }
            }
        } else {
            param = "";
        }

        return sb.toString();
    }

    public static String DaouHash(String InStr1, String InStr2, String InStr3, String InStr4, String InStr5) throws Exception {
        StringBuffer sb = new StringBuffer();
        String InStr = InStr1 + InStr2 + InStr3 + InStr4 + InStr5;
        MessageDigest md5 = MessageDigest.getInstance("SHA-256");
        md5.update(InStr.getBytes());
        byte[] md5Bytes = md5.digest();

        for(int i = 0; i < md5Bytes.length; ++i) {
            String md5Char = Integer.toHexString(255 & (char)md5Bytes[i]);
            if (md5Char.length() < 2) {
                md5Char = "0" + md5Char;
            }

            sb.append(md5Char);
        }

        return sb.toString();
    }

    public static String CertifyProc_1(String InValue, String cpid, char type, char keyOrder) throws Exception {
        String InKey = "";
        InKey = getKeyValue(cpid, type, keyOrder);
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Encrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, key, ivSpec);
            byte[] LB_EncryptValue = cipher.doFinal(InValue.getBytes());
            LS_Encrypt = byteArrayToHex(LB_EncryptValue);
            return LS_Encrypt;
        }
    }

    public static String CertifyProc_25(String InValue, String cpid, char type, char keyOrder) throws Exception {
        String InKey = "";
        InKey = getKeyValue(cpid, type, keyOrder);
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Decrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, key, ivSpec);
            byte[] LB_Value = hexToByteArray(InValue);
            byte[] LB_DecryptValue = cipher.doFinal(LB_Value);
            LS_Decrypt = new String(LB_DecryptValue, "euc-kr");
            return LS_Decrypt;
        }
    }

    public static String CertifyProc_2(String InValue) throws Exception {
        String InKey = "CERT!@#$";
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Encrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, key, ivSpec);
            byte[] LB_EncryptValue = cipher.doFinal(InValue.getBytes());
            LS_Encrypt = byteArrayToHex(LB_EncryptValue);
            return LS_Encrypt;
        }
    }

    public static String CertifyProc_23(String InValue) throws Exception {
        String InKey = "CERT!@#$";
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Decrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, key, ivSpec);
            byte[] LB_Value = hexToByteArray(InValue);
            byte[] LB_DecryptValue = cipher.doFinal(LB_Value);
            LS_Decrypt = new String(LB_DecryptValue, "euc-kr");
            return LS_Decrypt;
        }
    }

    public static String CertifyProc_3(String InValue) throws Exception {
        String InKey = "SAFE$#@!";
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Encrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, key, ivSpec);
            byte[] LB_EncryptValue = cipher.doFinal(InValue.getBytes());
            LS_Encrypt = byteArrayToHex(LB_EncryptValue);
            return LS_Encrypt;
        }
    }

    public static String CertifyProc_24(String InValue) throws Exception {
        String InKey = "SAFE$#@!";
        if (InValue == null) {
            return null;
        } else if (InKey == null) {
            return InValue;
        } else {
            String LS_Decrypt = null;
            InKey = byteArrayToHex(InKey.getBytes());
            byte[] keyString = hexToByteArray("6b6364736f667421" + InKey);
            byte[] iv = hexToByteArray("6b6364736f6674216669676874696e67");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(keyString, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, key, ivSpec);
            byte[] LB_Value = hexToByteArray(InValue);
            byte[] LB_DecryptValue = cipher.doFinal(LB_Value);
            LS_Decrypt = new String(LB_DecryptValue, "euc-kr");
            return LS_Decrypt;
        }
    }

    public static String getKeyValue(String cpid, char type, char keyOrder) throws Exception {
        String keyValue = "";
        String[] keyOrderValue = new String[]{"Jm", "jH"};
        if (cpid.length() == 8) {
            if (keyOrder == '1') {
                keyValue = cpid.substring(4, 5) + cpid.substring(7, 8) + keyOrderValue[0].substring(0, 1) + cpid.substring(1, 2) + type + cpid.substring(6, 7) + cpid.substring(5, 6) + keyOrderValue[0].substring(1, 2);
            } else if (keyOrder == '2') {
                keyValue = keyOrderValue[1].substring(0, 1) + cpid.substring(6, 7) + cpid.substring(7, 8) + cpid.substring(4, 5) + cpid.substring(2, 3) + type + keyOrderValue[1].substring(1, 2) + cpid.substring(5, 6);
            } else {
                keyValue = cpid;
            }

            return keyValue;
        } else {
            throw new Exception("ERROR - keyValueCHECK");
        }
    }

    public static void main(String[] args) throws Exception {
    }
}
