package kr.co.pplus.store.pg.daou.auth.directCard_auto;

import kr.co.pplus.store.pg.daou.auth.common.Crypto;
import kr.co.pplus.store.pg.daou.auth.common.PayStruct;
import kr.co.pplus.store.pg.daou.util.CommonUtil;
import kr.co.pplus.store.pg.daou.util.File.FileManager;
import kr.co.pplus.store.pg.daou.util.network.SocketStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

public class DaouDirectCardAutoAPI {
    private final static Logger logger = LoggerFactory.getLogger(DaouDirectCardAutoAPI.class);
    private Socket Loc_socketClient = null;
    private PrintWriter Loc_printWriter = null;
    private BufferedReader Loc_bufferedReader = null;
    private InputStream Loc_inputStream = null;
    private StringTokenizer Loc_sToken = null;
    private FileManager fm = new FileManager();
    private String LocS_IP = null;
    private int LocI_PORT = 0;

    public DaouDirectCardAutoAPI() {
        this.LocS_IP = "127.0.0.1";
        this.LocI_PORT = 64003;
    }

    public DaouDirectCardAutoAPI(String InIP, int InPort) {
        this.LocS_IP = InIP;
        this.LocI_PORT = InPort;
    }

    public PayStruct directCardCancel(PayStruct sPay, String InLogPath) {
        sPay.PubSet_Function = "CANCEL_";
        return this.doPay(sPay, InLogPath);
    }

    public PayStruct directCardSugiChkBin(PayStruct sPay, String InLogPath) {
        sPay.PubSet_Function = "CHKBIN_";
        return this.doPay(sPay, InLogPath);
    }

    public PayStruct directCardSugiAutoKeygen(PayStruct sPay, String InLogPath) {
        sPay.PubSet_Function = "KEYGEN_";
        return this.doPay(sPay, InLogPath);
    }

    public PayStruct directCardSugiAutoBatch(PayStruct sPay, String InLogPath) {
        sPay.PubSet_Function = "MONBATA";
        return this.doPay(sPay, InLogPath);
    }

    public void doCreateAPILOG(PayStruct sPay, String InLogPath) {
        String CPDATA_LOG_PATH = InLogPath + "/directcardauto";
        String LS_FileName = CommonUtil.getDate("yyyyMMdd", CommonUtil.getDate());
        String LOG_Type = "[DIRECTCARDAUTOAPILOG]";
        String var6 = null;

        try {
            this.fm.SetAutoFile(CPDATA_LOG_PATH, LS_FileName);
            this.fm.println("-- " + LOG_Type + " Request Parmeter START (API) ---------------");
            var6 = "  상점ID  : " + this.MaxLength(sPay.PubSet_CPID, 20) + "|" + "  주문번호  : " + this.MaxLength(sPay.PubSet_OrderNo, 50) + "|" + "  상품구분  : " + this.MaxLength(sPay.PubSet_ProductType, 2) + "|" + "  과금유형  : " + this.MaxLength(sPay.PubSet_BillType, 2) + "|" + "  선택발행사코드  : " + this.MaxLength(sPay.PubSet_CardCode, 4) + "|" + "  과세비과세여부  : " + this.MaxLength(sPay.PubSet_TaxFreeCD, 2) + "|" + "  할부개월수  : " + this.MaxLength(sPay.PubSet_AllotMon, 2) + "|" + "  결제금액  : " + this.MaxLength(sPay.PubSet_Amount, 10) + "|" + "  고객IP  : " + this.MaxLength(sPay.PubSet_IPAddress, 40) + "|" + "  고객ID  :  " + this.MaxLength(sPay.PubSet_UserID, 30) + "|" + "  고객명  :  " + this.MaxLength(sPay.PubSet_UserName, 50) + "|" + "  상품코드  :  " + this.MaxLength(sPay.PubSet_ProductCode, 10) + "|" + "  상품명  :  " + this.MaxLength(sPay.PubSet_ProductName, 50) + "|" + "  고객전화번호  :  " + this.MaxLength(sPay.PubSet_telno1, 20) + "|" + "  고객휴대폰번호  :  " + this.MaxLength(sPay.PubSet_telno2, 20) + "|" + "  고객휴대폰번호  :  " + this.MaxLength(sPay.PubSet_OrderReserved, 1024) + "|" + "  예약항목1  :  " + this.MaxLength(sPay.PubSet_ReservedIndex1, 20) + "|" + "  예약항목2  :  " + this.MaxLength(sPay.PubSet_ReservedIndex2, 20) + "|" + "  예약항목  :  " + this.MaxLength(sPay.PubSet_ReservedString, 1024);
        } catch (Exception var11) {
            var11.printStackTrace();
        } finally {
            this.close();
            this.fm.FileDestory();
        }

    }

    public PayStruct doPay(PayStruct sPay, String InLogPath) {
        String LS_Header = null;
        String LS_AckHeader = null;
        String LS_Body = null;
        String LS_SendMessage = null;
        String LS_AckMessage = null;
        String LS_ReceMessage = null;
        String LS_FileName = CommonUtil.getDate("yyyyMMdd", CommonUtil.getDate());
        this.doCreateAPILOG(sPay, InLogPath);

        PayStruct var12;
        try {
            this.fm.SetAutoFile(InLogPath, LS_FileName);
            if (sPay.PubSet_Function.equals("CANCEL_")) {
                LS_Body = this.MaxLength(sPay.PubSet_DaouTrx, 20) + "|" + this.MaxLength(sPay.PubSet_Amount, 10) + "|" + this.MaxLength(sPay.PubSet_IPAddress, 40) + "|" + this.MaxLength(sPay.PubSet_CancelMemo, 50);
            } else if (sPay.PubSet_Function.equals("KEYGEN_")) {
                LS_Body = this.MaxLength(sPay.PubSet_PayMethod, 3) + "|" + this.MaxLength(sPay.PubSet_OrderNo, 50) + "|" + this.MaxLength(sPay.PubSet_ProductType, 2) + "|" + this.MaxLength(sPay.PubSet_BillType, 2) + "|" + this.MaxLength(sPay.PubSet_AutoMonths, 2) + "|" + this.MaxLength(sPay.PubSet_UserID, 30) + "|" + this.MaxLength(sPay.PubSet_ProductCode, 10) + "|" + this.MaxLength(sPay.PubSet_CardNo, 5120) + "|" + this.MaxLength(sPay.PubSet_ExpireDt, 5120) + "|" + this.MaxLength(sPay.PubSet_cardAuth, 256) + "|" + this.MaxLength(sPay.PubSet_CardPassword, 256);
            } else if (sPay.PubSet_Function.equals("MONBATA")) {
                LS_Body = this.MaxLength(sPay.PubSet_OrderNo, 50) + "|" + this.MaxLength(sPay.PubSet_ProductType, 2) + "|" + this.MaxLength(sPay.PubSet_BillType, 2) + "|" + this.MaxLength(sPay.PubSet_TaxFreeCD, 2) + "|" + this.MaxLength(sPay.PubSet_Amount, 10) + "|" + this.MaxLength(sPay.PubSet_AutoKey, 20) + "|" + this.MaxLength(sPay.PubSet_Quota, 2) + "|" + this.MaxLength(sPay.PubSet_UserID, 30) + "|" + this.MaxLength(sPay.PubSet_ProductCode, 10) + "|" + this.MaxLength(sPay.PubSet_Email, 100) + "|" + this.MaxLength(sPay.PubSet_UserName, 50) + "|" + this.MaxLength(sPay.PubSet_ProductName, 50) + "|" + this.MaxLength(sPay.PubSet_ReservedString, 100);
            }

            sPay.PubSet_OPCode = "D" + sPay.PubSet_Function;
            sPay.PubSet_Version = "1.0";
            if (sPay.PubSet_Key == null || sPay.PubSet_Key.length() < 1) {
                sPay.PubSet_Key = "fighting";
            }

            LS_Body = Crypto.Encrypt(sPay.PubSet_Key, LS_Body);
            LS_Header = sPay.PubSet_OPCode + "11" + sPay.PubSet_Version + this.SpaceInsert(sPay.PubSet_CPID, " ", 20, "L") + this.SpaceInsert(Integer.toString(LS_Body.getBytes("EUC-KR").length + 1), "0", 7, "R");
            LS_AckHeader = sPay.PubSet_OPCode + "13" + sPay.PubSet_Version + this.SpaceInsert(sPay.PubSet_CPID, " ", 20, "L") + this.SpaceInsert("1", "0", 7, "R");
            LS_SendMessage = LS_Header + LS_Body + '\n';
            LS_AckMessage = LS_AckHeader + '\n';
            logger.info("[INFO] SEND DATA -> " + LS_SendMessage);
            this.SendDataLocal(LS_SendMessage);
            LS_ReceMessage = this.ReceiveData();
//            byte[] utf8StringBuffer = LS_ReceMessage.getBytes(StandardCharsets.UTF_8);
//            String decodedFromUtf8 = new String(utf8StringBuffer, "EUC-KR");
            logger.info("[INFO] RECEIVE DATA -> " + LS_ReceMessage);
            this.SendACK(LS_AckMessage);
            logger.info("[INFO] SEND ACK -> " + LS_AckMessage);
            this.ResponseDataParse(LS_ReceMessage, sPay);
            var12 = sPay;
            return var12;
        } catch (Exception var15) {
            logger.error("[ERROR] DOPAY : " + var15.toString());
            System.out.println("[ERROR] DOPAY : " + var15.toString());
            sPay.PubGet_ErrorMessage = "예외사항-에러발생";
            sPay.PubGet_ResultCode = "9999";
            logger.info("[INFO] PubGet_ResultCode : " + sPay.PubGet_ResultCode);
            logger.info("[INFO] PubGet_ErrorMessage : " + sPay.PubGet_ErrorMessage);
            var12 = sPay;
        } finally {
            this.close();
            this.fm.FileDestory();
        }

        return var12;
    }

    private void SendDataLocal(String InFormat) throws Exception {
        this.Loc_socketClient = new Socket(this.LocS_IP, this.LocI_PORT);
        this.Loc_printWriter = new PrintWriter(this.Loc_socketClient.getOutputStream(), true);
        this.Loc_inputStream = this.Loc_socketClient.getInputStream();
        this.Loc_printWriter.write(InFormat);
        this.Loc_printWriter.flush();
    }

    private void SendACK(String InFormat) throws Exception {
        this.Loc_printWriter.write(InFormat);
        this.Loc_printWriter.flush();
    }

    private String ReceiveData() throws Exception {
        boolean LB_Ack = false;
        String LS_ResultMessage = null;
        SocketStream socketstream = new SocketStream();
        LS_ResultMessage = socketstream.read_data(this.Loc_inputStream);
        return LS_ResultMessage;
    }

    private void close() {
        try {
            if (this.Loc_socketClient != null) {
                this.Loc_socketClient.close();
            }
        } catch (Exception var3) {
        }

        try {
            this.Loc_inputStream.close();
        } catch (Exception var2) {
        }

        this.Loc_socketClient = null;
        this.Loc_inputStream = null;
        this.fm.println("[INFO] CLOSE : CLOSE END!!");
        this.fm.println("-----------------------------------------------------------------------");
    }

    private String MaxLength(String InString, int InLen) {
        if (InString == null) {
            return InString;
        } else {
            if (InString.length() > InLen) {
                InString.substring(0, InLen - 1);
            }

            return InString;
        }
    }

    private String SpaceInsert(String InString, String InSpace, int InLen, String InOrder) {
        try {
            if (InString.length() > InLen) {
                InString.substring(0, InLen - 1);
            } else if (InString.length() < InLen) {
                for(int m_StringLen = InString.length(); m_StringLen < InLen; ++m_StringLen) {
                    if (InOrder.equals("R")) {
                        InString = InSpace.concat(InString);
                    } else if (InOrder.equals("L")) {
                        InString = InString.concat(InSpace);
                    }
                }
            }
        } catch (Exception var6) {
            System.out.println("[ERROR] Util.SpaceInsert : " + var6.toString());
        }

        return InString;
    }

    private String GetNextToken() throws Exception {
        String LS_TokenValue = null;
        if (this.Loc_sToken.hasMoreTokens()) {
            if ((LS_TokenValue = this.Loc_sToken.nextToken()).equals("|")) {
                LS_TokenValue = "";
            } else if (this.Loc_sToken.hasMoreTokens()) {
                this.Loc_sToken.nextToken();
            }
        }

        return CommonUtil.checkNull(LS_TokenValue);
    }

    private void ResponseDataParse(String InMessage, PayStruct sPay) throws Exception {
        String LS_Body = null;
        sPay.PubGet_OPCode = InMessage.substring(0, 10).trim();
        sPay.PubGet_Version = InMessage.substring(10, 13).trim();
        sPay.PubGet_CPID = InMessage.substring(13, 33).trim();
        sPay.PubGet_Len = Integer.parseInt(InMessage.substring(33, 40).trim());
        sPay.PubGet_Function = sPay.PubGet_OPCode.substring(1, 8).trim();
        LS_Body = CommonUtil.bytesSubStr(InMessage, 40, sPay.PubGet_Len - 1);
        if (sPay.PubSet_Key == null || sPay.PubSet_Key.length() < 1) {
            sPay.PubSet_Key = "fighting";
        }

        LS_Body = Crypto.Decrypt(sPay.PubSet_Key, LS_Body);
        logger.debug("body : "+LS_Body);
        this.Loc_sToken = new StringTokenizer(LS_Body, "|", true);
        if (sPay.PubGet_Function.equals("CANCEL_")) {
            sPay.PubGet_ResultCode = this.GetNextToken();
            sPay.PubGet_ErrorMessage = this.GetNextToken();
            sPay.PubGet_DaouTrx = this.GetNextToken();
            sPay.PubGet_Amount = this.GetNextToken();
            sPay.PubGet_CancelDate = this.GetNextToken();
        } else if (sPay.PubGet_Function.equals("KEYGEN_")) {
            sPay.PubGet_ResultCode = this.GetNextToken();
            sPay.PubGet_ErrorMessage = this.GetNextToken();
            sPay.PubGet_AutoKey = this.GetNextToken();
            sPay.PubGet_CardCode = this.GetNextToken();
            sPay.PubGet_GenDate = this.GetNextToken();
        } else if (sPay.PubGet_Function.equals("MONBATA")) {
            sPay.PubGet_ResultCode = this.GetNextToken();
            sPay.PubGet_ErrorMessage = this.GetNextToken();
            sPay.PubGet_OrderNo = this.GetNextToken();
            sPay.PubGet_Amount = this.GetNextToken();
            sPay.PubGet_DaouTrx = this.GetNextToken();
            sPay.PubGet_AuthNO = this.GetNextToken();
            sPay.PubGet_NoIntFlag = this.GetNextToken();
            sPay.PubGet_Quota = this.GetNextToken();
            sPay.PubGet_AuthDate = this.GetNextToken();
            sPay.PubGet_CPName = this.GetNextToken();
            sPay.PubGet_CPUrl = this.GetNextToken();
            sPay.PubGet_CPTelNo = this.GetNextToken();
        }

    }
}
