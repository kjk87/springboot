package kr.co.pplus.store.pg.daou.util.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SocketStream {
    public String read_line(InputStream In_InStream) {
        try {
            ByteArrayOutputStream L_byteArrayOS = new ByteArrayOutputStream();
            boolean LB_Eof = false;

            int LI_Read;
            do {
                LI_Read = In_InStream.read();
                if (LI_Read == -1) {
                    LB_Eof = true;
                    break;
                }

                if (LI_Read != 13 && LI_Read != 10) {
                    L_byteArrayOS.write((byte)LI_Read);
                }
            } while(LI_Read != 13);

            L_byteArrayOS.flush();
            String LS_Data;
            if (LB_Eof && L_byteArrayOS.size() == 0) {
                LS_Data = null;
                return LS_Data;
            } else {
                LS_Data = new String(L_byteArrayOS.toString());
                L_byteArrayOS.close();
                L_byteArrayOS = null;
                return LS_Data;
            }
        } catch (Exception var6) {
            System.out.println("[ERROR] InputStream.read() ".concat(String.valueOf(String.valueOf(var6.toString()))));
            Object var3 = null;
            return (String)var3;
        }
    }

    public String read_data(InputStream In_InStream, int In_Length) {
        try {
            ByteArrayOutputStream L_byteArrayOS = new ByteArrayOutputStream();
            int LI_Count = 0;
            byte[] LB_Buf = new byte[2048];

            while(LI_Count < In_Length) {
                int LI_Read = In_InStream.read(LB_Buf, 0, In_Length - LI_Count < 2048 ? In_Length - LI_Count : 2048);
                if (LI_Read > 0) {
                    LI_Count += LI_Read;
                    L_byteArrayOS.write(LB_Buf, 0, LI_Read);
                } else if (LI_Read == -1) {
                    break;
                }
            }

            L_byteArrayOS.flush();
            String var9 = L_byteArrayOS.toString();
            return var9;
        } catch (IOException var7) {
            System.out.println("[ERROR] InputStream has returned an unexpected EOF".concat(String.valueOf(String.valueOf(var7.toString()))));
            Object var4 = null;
            return (String)var4;
        }
    }

    public String read_data(InputStream In_InStream) {
        ByteArrayOutputStream L_byteArrayOS = null;

        Object var4;
        try {
            L_byteArrayOS = new ByteArrayOutputStream();
            int LI_Count = 0;
            byte[] LB_Buf = new byte[6044];

            while(true) {
                int LI_Read = In_InStream.read(LB_Buf);
                System.out.println("READ:::::::".concat(String.valueOf(String.valueOf(LI_Read))));
                if (LI_Read > 0) {
                    int var10000 = LI_Count + LI_Read;
                    L_byteArrayOS.write(LB_Buf, 0, LI_Read);
                } else if (LI_Read != -1) {
                    continue;
                }

                L_byteArrayOS.flush();
                String var18 = L_byteArrayOS.toString();
                return var18;
            }
        } catch (IOException var15) {
            System.out.println("[ERROR] InputStream has returned an unexpected EOF".concat(String.valueOf(String.valueOf(var15.toString()))));
            var4 = null;
        } finally {
            try {
                L_byteArrayOS.close();
                L_byteArrayOS = null;
            } catch (Exception var14) {
            }

        }

        return (String)var4;
    }
}
