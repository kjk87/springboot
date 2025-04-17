package kr.co.pplus.store.util;

import com.google.gson.Gson;
import kr.co.pplus.store.api.jpa.model.ftlink.*;
import kr.co.pplus.store.api.util.AppUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FTLinkPayApi {

    private static final Logger logger = LoggerFactory.getLogger(FTLinkPayApi.class);

    private static String payUrl = "http://pay.ftlink.co.kr/reqApproval.qr.ASP" ;

    private static String cancelUrl = "http://pay.ftlink.co.kr/reqCancel.ASP" ;

    private static String payDecide = "http://pay.ftlink.co.kr/company/deliok.pplus.asp" ;

    private static String join = "http://pay.ftlink.co.kr/company/shop_add.pplus.asp";

    private static String checkId = "http://pay.ftlink.co.kr/company/shop_chkid.pplus.asp";

    private static String checkMobile = "http://pay.ftlink.co.kr/company/shop_chkphone.pplus.asp";

    private static String cancelNotiUrl = "http://pay.ftlink.co.kr/payalert/pplus/noti_cert.cancel.asp";

    private static String shopcode = "190000109" ;

    private static String loginId = "test02" ;

    private static String APPVERSION = "100" ;

    private static String SERVICECODE = "LINK";

    public static Boolean checkId(String id) throws Exception{
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("compcode", SERVICECODE));
        nameValuePairList.add(new BasicNameValuePair("id", id));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPost(checkId, nameValuePairList) ;
        HttpResponse res = client.execute(post) ;
        Gson gson = new Gson() ;
        FTLinkPayCommonResponse ftLinkPayCommonResponse  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), FTLinkPayCommonResponse.class) ;
        client.close() ;
        logger.debug("checkId: " + gson.toJson(ftLinkPayCommonResponse)) ;
        if(ftLinkPayCommonResponse.getErrcode().equals("00") || ftLinkPayCommonResponse.getErrcode().equals("0000")){
            return true;
        }else{
            return false;
        }

    }

    public static Boolean checkMobile(String mobile) throws Exception{
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("compcode", SERVICECODE));
        nameValuePairList.add(new BasicNameValuePair("mobile", mobile));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPost(checkMobile, nameValuePairList) ;
        HttpResponse res = client.execute(post) ;
        Gson gson = new Gson() ;
        FTLinkPayCommonResponse ftLinkPayCommonResponse  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), FTLinkPayCommonResponse.class) ;
        client.close() ;
        logger.debug("checkMobile: " + gson.toJson(ftLinkPayCommonResponse)) ;
        if(ftLinkPayCommonResponse.getErrcode().equals("00") || ftLinkPayCommonResponse.getErrcode().equals("0000")){
            return true;
        }else{
            return false;
        }
    }

    public static FTLinkPayCommonResponse add(FTLinkAddRequest data) throws Exception{
        data.setCompcode(SERVICECODE);
        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("FTLinkPayDecideRequest: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(join, data, false) ;
        HttpResponse res = client.execute(post) ;
        FTLinkPayCommonResponse fTLinkPayCommonResponse  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), FTLinkPayCommonResponse.class) ;
        client.close() ;
        logger.debug("add: " + gson.toJson(fTLinkPayCommonResponse)) ;
        return fTLinkPayCommonResponse ;
    }

    public static FTLinkPayCommonResponse payDecideRequest(FTLinkPayDecideRequest data) throws Exception{
        data.setCompcode(SERVICECODE);
        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("FTLinkPayDecideRequest: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(payDecide, data, false) ;
        HttpResponse res = client.execute(post) ;
        FTLinkPayCommonResponse fTLinkPayCommonResponse  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), FTLinkPayCommonResponse.class) ;
        client.close() ;
        logger.debug("payDecideRequest: " + gson.toJson(fTLinkPayCommonResponse)) ;
        return fTLinkPayCommonResponse ;
    }

    public static FTLinkPayResponse payRequest(FTLinkPayRequest data) throws Exception{

        data.setDuptest("1");
        data.setAppversion(APPVERSION);
        data.setServicecode(SERVICECODE);

//        data.setLoginId(loginId);
//        data.setShopcode(shopcode);

//        if (StringUtils.isEmpty(data.getShopcode())) {
//            data.setShopcode(shopcode);
//            data.setLoginId(loginId);
//        }

        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("FTLinkPayRequest: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(payUrl, data, false) ;
        HttpResponse res = client.execute(post) ;
        FTLinkPayResponse ftLinkPayResponse  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), FTLinkPayResponse.class) ;
        client.close() ;
        logger.debug("FTLinkPayResponse: " + gson.toJson(ftLinkPayResponse)) ;
        return ftLinkPayResponse ;
    }

    public static FTLinkCancelResponse cancelNotiRequest(FTLinkCancelNotiRequest data) throws Exception{
        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("FTLinkCancelNotiRequest: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParamsEucKr(cancelNotiUrl, data) ;
        HttpResponse res = client.execute(post) ;

        FTLinkCancelResponse ftLinkCancelResponse  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), FTLinkCancelResponse.class) ;
        client.close() ;

        logger.debug("FTLinkCancelResponse: " + gson.toJson(ftLinkCancelResponse)) ;
        return ftLinkCancelResponse ;
    }

    public static FTLinkCancelResponse cancelRequest(FTLinkCancelRequest data) throws Exception{

        data.setAPPVERSION(APPVERSION);
//        if (StringUtils.isEmpty(data.getShopcode())) {
//            data.setShopcode(shopcode);
//            data.setLoginId(loginId);
//        }
        data.setSERVICECODE(SERVICECODE);


        Gson gson = new Gson() ;
        String jsonStr = gson.toJson(data) ;
        logger.debug("FTLinkCancelRequest: " + jsonStr) ;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = AppUtil.getPostWithParams(cancelUrl, data, false) ;
        HttpResponse res = client.execute(post) ;

        FTLinkCancelResponse ftLinkCancelResponse  = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), FTLinkCancelResponse.class) ;
        client.close() ;

        logger.debug("FTLinkCancelResponse: " + gson.toJson(ftLinkCancelResponse)) ;
        return ftLinkCancelResponse ;
    }


    public static void main(String argv[]) {
//        cancelTest();
    }

}
