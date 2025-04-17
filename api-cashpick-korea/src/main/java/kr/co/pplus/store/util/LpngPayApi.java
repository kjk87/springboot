package kr.co.pplus.store.util;

import com.google.gson.Gson;
import kr.co.pplus.store.api.jpa.model.lpng.*;
import kr.co.pplus.store.api.jpa.model.udonge.UdongeCancelRequest;
import kr.co.pplus.store.api.jpa.model.udonge.UdongeRequest;
import kr.co.pplus.store.api.jpa.model.udonge.UdongeResponse;
import kr.co.pplus.store.api.util.AppUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LpngPayApi {

    private static final Logger logger = LoggerFactory.getLogger(LpngPayApi.class);

    private static String payUrl = "http://pay.lpng.co.kr/reqApproval.utf.ASP" ;

    private static String cancelUrl = "http://pay.lpng.co.kr/reqCancel.ASP" ;

    private static String shopcode = "170000410" ;

    private static String loginId = "town" ;

    private static String APPVERSION = "100" ;

    private static String SERVICECODE = "LPNG";

    private static String receiveType = "J" ;

    private static String receiveUrl  = ""  ;

    private static String NewpayUrl = "http://pay.lpng.co.kr/reqorder.app.utf.asp" ;

    private static String NewcancelUrl = "http://pay.lpng.co.kr/cancelorder.app.utf.asp" ;

    private static String checkUrl = "http://pay.lpng.co.kr/paychk.app.utf.asp" ;

    private static String udongeUrl = "http://api.udonge.co.kr/cms_trs.php" ;


    public static UdongeResponse callUdonge(UdongeRequest data) throws Exception{
        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("callUdonge Request: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(udongeUrl, data, false) ;
        HttpResponse res = client.execute(post) ;

        UdongeResponse udongeRes  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), UdongeResponse.class) ;
        client.close() ;
        logger.debug("callUdonge Response: " + gson.toJson(udongeRes)) ;
        return udongeRes ;
    }

    public static UdongeResponse callCancelUdonge(UdongeCancelRequest data) throws Exception{
        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("callUdonge Request: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(udongeUrl, data, false) ;
        HttpResponse res = client.execute(post) ;

        UdongeResponse udongeRes  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), UdongeResponse.class) ;
        client.close() ;
        logger.debug("callUdonge Response: " + gson.toJson(udongeRes)) ;
        return udongeRes ;
    }
    public static LpngResponseNew payRequestNew(LpngRequestNew data) throws Exception{

        data.setServicecode(SERVICECODE);
        if (StringUtils.isEmpty(data.getShopcode())) {
            data.setShopcode(shopcode);
        }

        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("LPNG_PAY Request: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(NewpayUrl, data, false) ;
        HttpResponse res = client.execute(post) ;

        LpngResponseNew lpngRes  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), LpngResponseNew.class) ;
        client.close() ;
        logger.debug("LPNG_PAY Response: " + gson.toJson(lpngRes)) ;
        return lpngRes ;
    }

    public static LpngResultResponse payCheck(LpngCheckRequest data) throws Exception{


        data.setServicecode(SERVICECODE);
        if (StringUtils.isEmpty(data.getShopcode())) {
            data.setShopcode(shopcode);
        }
        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("LPNG_PAY Request: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(checkUrl, data, true) ;
        HttpResponse res = client.execute(post) ;

        LpngResultResponse lpngRes  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), LpngResultResponse.class) ;
        client.close() ;
        logger.debug("LPNG_PAY Response: " + gson.toJson(lpngRes)) ;
        return lpngRes ;
    }



    public static LpngResponse payRequest(LpngRequest data) throws Exception{

        data.setAPPVERSION(APPVERSION);
        data.setLoginId(loginId);
        if (StringUtils.isEmpty(data.getShopcode())) {
            data.setShopcode(shopcode);
        }
        data.setSERVICECODE(SERVICECODE);
        data.setReceive_type(receiveType);
        data.setReceive_url(receiveUrl);
        if( data.getOrder_email() == null ) {
            data.setOrder_email("");
        }

        data.setComp_temp1("");
        data.setComp_temp2("");
        data.setComp_temp3("");
        data.setComp_temp4("");
        data.setComp_temp5("");
//        String orderId = "PRNumber-" + KeyGenerator.generateOrderNo()+ "-" + KeyGenerator.generateKey() ;
//        data.setComp_orderno(orderId);

        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("LPNG_PAY Request: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(payUrl, data, false) ;
        HttpResponse res = client.execute(post) ;
        LpngResponse lpngRes  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), LpngResponse.class) ;
        client.close() ;
        logger.debug("LPNG_PAY Response: " + gson.toJson(lpngRes)) ;
        return lpngRes ;
    }

    public static LpngCancelResponse cancelRequest(LpngCancelRequest data) throws Exception{

        data.setAPPVERSION(APPVERSION);
        data.setLoginId(loginId);
        if (StringUtils.isEmpty(data.getShopcode())) {
            data.setShopcode(shopcode);
        }
        data.setSERVICECODE(SERVICECODE);
//        data.setReceive_type(receiveType);
//        data.setReceive_url(receiveUrl);


        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("LPNG_CANCEL Request: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(cancelUrl, data, false) ;
        HttpResponse res = client.execute(post) ;

        LpngCancelResponse lpngRes  = gson.fromJson(AppUtil.convertMS949toUTF8(res.getEntity().getContent()), LpngCancelResponse.class) ;
        client.close() ;

        logger.debug("LPNG_CANCEL Request: " + gson.toJson(lpngRes)) ;
        return lpngRes ;
    }

    public static LpngCancelResponseNew cancelRequestNew(LpngCancelRequestNew data) throws Exception{

//        data.setAPPVERSION(APPVERSION);
//        data.setLoginId(loginId);
//        data.setShopcode(shopcode);
            data.setServicecode(SERVICECODE);
        if (StringUtils.isEmpty(data.getShopcode())) {
            data.setShopcode(shopcode);
        }
//        data.setReceive_type(receiveType);
//        data.setReceive_url(receiveUrl);


        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("LPNG_CANCEL Request: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(NewcancelUrl, data, false) ;
        HttpResponse res = client.execute(post) ;
        LpngCancelResponseNew lpngRes  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), LpngCancelResponseNew.class) ;
        client.close() ;

        logger.debug("LPNG_CANCEL Request: " + gson.toJson(lpngRes)) ;
        return lpngRes ;
    }


    public static void payTest() {

        try {

            LpngRequestNew reqData = new LpngRequestNew() ;

            reqData.setShopcode("170000410");
            reqData.setOrder_req_amt("1000");
            reqData.setOrder_goodsname("오렌지 쥬스");
            reqData.setComp_orderno("2002201745297501408855");
            reqData.setComp_memno("1000274") ;
            reqData.setOrder_name("홍길동") ;
            reqData.setOrder_hp("01012341234") ;
            reqData.setOrder_email("yhchoi@p-ple.com") ;


            Gson gson = new Gson() ;
            LpngResponseNew res = LpngPayApi.payRequestNew(reqData) ;
            System.out.println("res : " + gson.toJson(res)) ;

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancelTest() {

        try {

            LpngCancelRequestNew reqData = new LpngCancelRequestNew() ;
            reqData.setOrderno("200220000239");
            reqData.setShopcode("170000410");
            reqData.setOrder_req_amt("1000");

            Gson gson = new Gson() ;
            LpngCancelResponseNew res = LpngPayApi.cancelRequestNew(reqData) ;
            System.out.println("res : " + gson.toJson(res)) ;

/*
            reqData.setOrderNo("20191022144429142-3805-00");
            reqData.setCancelAmt("1800");
            reqData.setTranNo("2019101817C2078764");

            res = LpngPayApi.cancelRequest(reqData) ;
            System.out.println("res : " + gson.toJson(res)) ;

            reqData.setOrderNo("191018000162");
            reqData.setCancelAmt("1000");
            reqData.setTranNo("2019101817C2069100");

            res = LpngPayApi.cancelRequest(reqData) ;
            System.out.println("res : " + gson.toJson(res)) ;

            reqData.setOrderNo("191018000163");
            reqData.setCancelAmt("1000");
            reqData.setTranNo("2019101817C1069764");

            res = LpngPayApi.cancelRequest(reqData) ;
            System.out.println("res : " + gson.toJson(res)) ;
            */

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) {
        cancelTest();
    }

}
