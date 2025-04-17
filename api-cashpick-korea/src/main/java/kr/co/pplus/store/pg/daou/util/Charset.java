package kr.co.pplus.store.pg.daou.util;

import java.io.UnsupportedEncodingException;

public class Charset {
    public String ToDB(String InStr) {
        return InStr;
    }

    public String FromWeb(String InStr) {
        try {
            String var2;
            if (InStr != null) {
                var2 = new String(InStr.getBytes("euc-kr"), "ISO-8859-1");
                return var2;
            } else {
                var2 = null;
                return var2;
            }
        } catch (UnsupportedEncodingException var4) {
            String var3 = "Encoding fail";
            return var3;
        }
    }
}
