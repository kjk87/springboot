package kr.co.pplus.store.pg.daou.util.network;

import java.io.PrintWriter;

public class SocketUtil {
    private static char ACK = 6;
    private static char ENQ = 5;
    private static char NAK = 21;

    public SocketUtil() {
    }

    public void SendDataLocal(PrintWriter InPWOut, String InMessage) {
        try {
            System.out.println(String.valueOf(String.valueOf((new StringBuffer("Send Message:'")).append(InMessage).append("'"))));
            InPWOut.write(InMessage);
            InPWOut.flush();
        } catch (Exception var8) {
            System.out.println("[ERROR] APIManager.SendDataLocal : ".concat(String.valueOf(String.valueOf(var8.toString()))));
        } finally {
            ;
        }

    }

    public void SendACK(PrintWriter InPWOut) {
        try {
            InPWOut.write(ACK);
            InPWOut.flush();
        } catch (Exception var7) {
            System.out.println("[ERROR] SocketUtil.SendACK : ".concat(String.valueOf(String.valueOf(var7.toString()))));
        } finally {
            ;
        }

    }

    public void SendENQ(PrintWriter InPWOut) {
        try {
            InPWOut.write(ENQ);
            InPWOut.flush();
        } catch (Exception var7) {
            System.out.println("[ERROR] SocketUtil.SendENQ : ".concat(String.valueOf(String.valueOf(var7.toString()))));
        } finally {
            ;
        }

    }
}
