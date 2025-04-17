package kr.co.pplus.store.api.jpa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayBillKeyData;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayCancelData;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayLoginData;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayRes;
import kr.co.pplus.store.api.jpa.repository.ReappayInfoRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.RootService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ReapPayService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(ReapPayService.class);

    @Value("${STORE.REAPPAY.API_URL}")
    private String BASE_URL = "https://api-pg.reappay.net/api/";

    @Value("${STORE.REAPPAY.ID}")
    private String ID;

    @Value("${STORE.REAPPAY.BILL_KEY_ID}")
    private String BILL_KEY_ID;

    @Value("${STORE.REAPPAY.SECRET_KEY}")
    private String SECRET_KEY;

    @Value("${STORE.REAPPAY.PW}")
    private String PW;

    @Value("${STORE.REAPPAY.TEST_PAY}")
    private String TEST_PAY;

    @Value("${STORE.TYPE}")
    String storeType;

    @Autowired
    ReappayInfoRepository reappayInfoRepository;

    public ReappayInfo login(Long seqNo) {
        String url = BASE_URL + "v1/auth/login";

        CloseableHttpClient client = HttpClients.createDefault();

        try {
            Gson gson = new Gson();

            JsonObject params = new JsonObject();

            if (seqNo.equals(1L)) {
                params.addProperty("userId", ID);
            } else if (seqNo.equals(2L)) {
                params.addProperty("userId", BILL_KEY_ID);
            }

            params.addProperty("password", PW);

            StringEntity entity = new StringEntity(params.toString(), "utf-8");

            HttpPost post = AppUtil.getPost(url, entity);
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == 200) {
                ReapPayRes reapPayRes = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), ReapPayRes.class);
                logger.error("reapPayRes status : " + reapPayRes.getStatus());
                if (reapPayRes.getStatus() == 200) {

                    Type type = new TypeToken<ReapPayLoginData>() {
                    }.getType();
                    ReapPayLoginData content = gson.fromJson(gson.toJson(reapPayRes.getContent()), type);


                    ReappayInfo reappayInfo = new ReappayInfo();
                    reappayInfo.setSeqNo(seqNo);
                    reappayInfo.setAccessToken(content.getAccess_token());
                    reappayInfo.setExpiresIn(content.getExpires_in());
                    reappayInfo.setJti(content.getJti());
                    reappayInfo.setRefreshToken(content.getRefresh_token());
                    reappayInfo.setScop(content.getScope());
                    reappayInfo.setTokenType(content.getToken_type());
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.SECOND, reappayInfo.getExpiresIn());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String expireDate = sdf.format(calendar.getTime());
                    reappayInfo.setExpireDatetime(expireDate);

                    reappayInfo = reappayInfoRepository.save(reappayInfo);
                    return reappayInfo;
                }
            } else {
                logger.error("status code : " + res.getStatusLine().getStatusCode());
            }


        } catch (Exception e) {
            logger.error("login error : " + e.toString());

        }
        try {
            client.close();
        } catch (IOException e) {
            logger.error("client close error : " + e.toString());
        }

        return null;
    }

    public ReappayInfo getReapPayInfo(Long seqNo) {
        ReappayInfo reappayInfo = reappayInfoRepository.findBySeqNo(seqNo);
        if (reappayInfo == null) {
            reappayInfo = login(seqNo);
        } else {

            try {
                if (AppUtil.isEmpty(reappayInfo.getExpireDatetime())) {
                    reappayInfo = login(seqNo);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date expireDate = sdf.parse(reappayInfo.getExpireDatetime());

                    if (expireDate.before(new Date())) {
                        reappayInfo = login(seqNo);
                    }
                }
            } catch (ParseException e) {
                logger.error("expireDate : " + e.toString());
            }

        }

        return reappayInfo;
    }

    public ReapPayCancelData cancel(String tranSeq, Long seqNo) {

        ReappayInfo reappayInfo = getReapPayInfo(seqNo);

        String url = BASE_URL + "v1/pay/cancel";

        CloseableHttpClient client = HttpClients.createDefault();

        try {
            Gson gson = new Gson();

            JsonObject params = new JsonObject();
            params.addProperty("tranSeq", tranSeq);

            StringEntity entity = new StringEntity(params.toString(), "utf-8");
            HttpPost post = AppUtil.getPost(url, entity);
            post.setHeader("Authorization", "Bearer " + reappayInfo.getAccessToken());
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == 200) {
                ReapPayRes reapPayRes = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), ReapPayRes.class);
                if (reapPayRes.getStatus() == 200) {
                    String json = gson.toJson(reapPayRes.getContent());
                    logger.error("cancel result : " + json);

                    Type type = new TypeToken<ReapPayCancelData>() {
                    }.getType();
                    ReapPayCancelData content = gson.fromJson(json, type);

                    return content;
                }else{
                    logger.error("error result status: " + reapPayRes.getStatus());
                    logger.error("error result code: " + reapPayRes.getCode());
                    logger.error("error result message: " + reapPayRes.getMessage());
                    logger.error("error result : " + reapPayRes.getContent().toString());
                }
            }else{
                logger.error("error result getStatusLine : " + res.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;

    }

    public ReapPayBillKeyData billkeypayLuckyBol(LuckyBolPurchase luckyBolPurchase, String token, String installment) {
        long seqNo = 2L;

        ReappayInfo reappayInfo = getReapPayInfo(seqNo);

        String url = BASE_URL + "v1/pay/billkeypay";

        CloseableHttpClient client = HttpClients.createDefault();
        try {

            Gson gson = new Gson();

            JsonObject params = new JsonObject();
            params.addProperty("billingToken", token);
            if(luckyBolPurchase != null){
                params.addProperty("buyerTel", luckyBolPurchase.getBuyerTel());

                String title = luckyBolPurchase.getTitle();
                if(title.length() > 10){
                    title = title.substring(0, 10);
                }

                params.addProperty("goodsName", "럭키쇼핑 상품구매");
                params.addProperty("totAmt", String.valueOf(luckyBolPurchase.getEngagedPrice()));
                Float vat = (luckyBolPurchase.getEngagedPrice() * 0.1f);
                params.addProperty("vatAmt", String.valueOf(vat.intValue()));

            }

            params.addProperty("cardCate", "03");//인증구분(02:주민번호,03:휴대폰번호,04:사업자번호)
            params.addProperty("installment", installment);
            params.addProperty("interestFree", "0");//무이자할부 여부( 0 or 1 )
//            params.addProperty("payload", "");
            params.addProperty("productType", "0");
            params.addProperty("taxFlag", "00");//과세여부(과세:00, 비과세:01)
            params.addProperty("testPay", TEST_PAY);

            logger.info("params : " + params.toString());

            StringEntity entity = new StringEntity(params.toString(), "utf-8");
            HttpPost post = AppUtil.getPost(url, entity);
            post.setHeader("Authorization", "Bearer " + reappayInfo.getAccessToken());
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == 200) {
                ReapPayRes reapPayRes = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), ReapPayRes.class);
                if (reapPayRes.getStatus() == 200) {
                    String json = gson.toJson(reapPayRes.getContent());
                    logger.info("result : " + json);

                    Type type = new TypeToken<ReapPayBillKeyData>() {
                    }.getType();
                    ReapPayBillKeyData reapPayBillKeyData = gson.fromJson(json, type);

                    if (reapPayBillKeyData.getBillkeyrespCode().equals("0000")) {
                        return reapPayBillKeyData;
                    }

                } else {
//                        String json = gson.toJson(reapPayRes.getContent());
                    logger.info("error result : " + reapPayRes.getMessage());
                    if (reapPayRes.getContent() != null) {
                        String json = gson.toJson(reapPayRes.getContent());
                        logger.info("error result : " + json);
                    }
                }
            }

        }catch (Exception e){
            logger.error(e.toString());
        }
        return null;
    }

    public ReapPayBillKeyData billkeypay(Purchase purchase, LuckyBoxPurchase luckyBoxPurchase, LuckyBoxDeliveryPurchase luckyBoxDeliveryPurchase, LuckyPickPurchase luckyPickPurchase, LuckyPickDeliveryPurchase luckyPickDeliveryPurchase, String token, String installment) {

        long seqNo = 2L;

        ReappayInfo reappayInfo = getReapPayInfo(seqNo);

        String url = BASE_URL + "v1/pay/billkeypay";

        CloseableHttpClient client = HttpClients.createDefault();

        try {
            Gson gson = new Gson();

            JsonObject params = new JsonObject();
            params.addProperty("billingToken", token);
            if(purchase != null){
                params.addProperty("buyerTel", purchase.getBuyerTel());

                String title = purchase.getTitle();
                if(title.length() > 10){
                    title = title.substring(0, 10);
                }

                params.addProperty("goodsName", title);
                params.addProperty("totAmt", String.valueOf(purchase.getPgPrice()));
                Float vat = (purchase.getPgPrice() * 0.1f);
                params.addProperty("vatAmt", String.valueOf(vat.intValue()));

            }else if (luckyBoxPurchase != null) {
                params.addProperty("buyerTel", luckyBoxPurchase.getMember().getMobileNumber().replace("luckyball##", ""));
                params.addProperty("goodsName", luckyBoxPurchase.getTitle());
                params.addProperty("totAmt", String.valueOf(luckyBoxPurchase.getPgPrice().intValue()));
                Float vat = (luckyBoxPurchase.getPgPrice() * 0.1f);
                params.addProperty("vatAmt", String.valueOf(vat.intValue()));

            }else if(luckyBoxDeliveryPurchase != null){
                params.addProperty("buyerTel", luckyBoxDeliveryPurchase.getMember().getMobileNumber().replace("luckyball##", ""));
                params.addProperty("goodsName", luckyBoxDeliveryPurchase.getLuckyBoxPurchaseItem().getLuckyBoxTitle());
                params.addProperty("totAmt", String.valueOf(luckyBoxDeliveryPurchase.getPgPrice()));
                Float vat = (luckyBoxDeliveryPurchase.getPgPrice() * 0.1f);
                params.addProperty("vatAmt", String.valueOf(vat.intValue()));
            }else if (luckyPickPurchase != null) {
                params.addProperty("buyerTel", luckyPickPurchase.getMember().getMobileNumber().replace("luckyball##", ""));
                params.addProperty("goodsName", luckyPickPurchase.getTitle());
                params.addProperty("totAmt", String.valueOf(luckyPickPurchase.getPrice().intValue()));
                Float vat = (luckyPickPurchase.getPrice() * 0.1f);
                params.addProperty("vatAmt", String.valueOf(vat.intValue()));
            }else if(luckyPickDeliveryPurchase != null){
                params.addProperty("buyerTel", luckyPickDeliveryPurchase.getMember().getMobileNumber().replace("luckyball##", ""));
                params.addProperty("goodsName", luckyPickDeliveryPurchase.getLuckyPickPurchaseItem().getLuckyPickTitle());
                params.addProperty("totAmt", String.valueOf(luckyPickDeliveryPurchase.getPgPrice().intValue()));
                Float vat = (luckyPickDeliveryPurchase.getPgPrice() * 0.1f);
                params.addProperty("vatAmt", String.valueOf(vat.intValue()));
            }

            params.addProperty("cardCate", "03");//인증구분(02:주민번호,03:휴대폰번호,04:사업자번호)
            params.addProperty("installment", installment);
            params.addProperty("interestFree", "0");//무이자할부 여부( 0 or 1 )
//            params.addProperty("payload", "");
            params.addProperty("productType", "0");
            params.addProperty("taxFlag", "00");//과세여부(과세:00, 비과세:01)
            params.addProperty("testPay", TEST_PAY);

            logger.info("params : " + params.toString());

            StringEntity entity = new StringEntity(params.toString(), "utf-8");
            HttpPost post = AppUtil.getPost(url, entity);
            post.setHeader("Authorization", "Bearer " + reappayInfo.getAccessToken());
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == 200) {
                ReapPayRes reapPayRes = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), ReapPayRes.class);
                if (reapPayRes.getStatus() == 200) {
                    String json = gson.toJson(reapPayRes.getContent());
                    logger.info("result : " + json);

                    Type type = new TypeToken<ReapPayBillKeyData>() {
                    }.getType();
                    ReapPayBillKeyData reapPayBillKeyData = gson.fromJson(json, type);

                    if (reapPayBillKeyData.getBillkeyrespCode().equals("0000")) {
                        return reapPayBillKeyData;
                    }

                } else {
//                        String json = gson.toJson(reapPayRes.getContent());
                    logger.info("error result : " + reapPayRes.getMessage());
                    if (reapPayRes.getContent() != null) {
                        String json = gson.toJson(reapPayRes.getContent());
                        logger.info("error result : " + json);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;

    }


    public ReapPayBillKeyData billkeyregist(Map<String, String> data) {

        try {

            long seqNo = 2L;

            ReappayInfo reappayInfo = getReapPayInfo(seqNo);

            // 32 byte 로그인정보의 userId(예: testid) + api (예: billkeyregist) = "testidbillkeyregist" + "부족한자리수 0으로 채움"

//            String SECRET_KEY = "roots37"+"billkeyregist"+"000000000000";
            // 16 byte 로그인정보의 jti = "b4ed0b24-6d56-4c4d-ab0c-3eb423f05af1"
            String iv = reappayInfo.getJti().substring(0, 16);

//            HashMap<String, String> data = new HashMap<>();
//            data.put("cardNo", "5361421009862251"); // 카드번호
//            data.put("expireYy", "27");          // 유효기간의 년
//            data.put("expireMm", "09");        // 유효기간의 월
//            data.put("passwd", "08");            // 비밀번호 앞2
//            data.put("birthday", "870926");    // yyMMdd 생년월일 또는 사업자번호 숫자만
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));

            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(data);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            String encryptString = Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));
            logger.debug("encryptString = {}", encryptString);

            String url = BASE_URL + "v1/pay/billkeyregist";

            CloseableHttpClient client = HttpClients.createDefault();

            try {
                Gson gson = new Gson();

                JsonObject params = new JsonObject();
                params.addProperty("encryptData", encryptString);
                params.addProperty("encryptYn", "Y");
//                params.addProperty("payload", "");

                StringEntity entity = new StringEntity(params.toString(), "utf-8");
                HttpPost post = AppUtil.getPost(url, entity);
                post.setHeader("Authorization", "Bearer " + reappayInfo.getAccessToken());
                HttpResponse res = client.execute(post);
                if (res.getStatusLine().getStatusCode() == 200) {
                    ReapPayRes reapPayRes = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), ReapPayRes.class);
                    if (reapPayRes.getStatus() == 200) {
                        String json = gson.toJson(reapPayRes.getContent());
                        logger.error("result : " + json);

                        Type type = new TypeToken<ReapPayBillKeyData>() {
                        }.getType();
                        ReapPayBillKeyData billRegData = gson.fromJson(json, type);
//                        if (billRegData.getBillkeyrespCode().equals("0000")) {
//
//                        }
                        return billRegData;
                    } else {
//                        String json = gson.toJson(reapPayRes.getContent());
                        logger.error("error result : " + reapPayRes.getMessage());
                        if (reapPayRes.getContent() != null) {
                            String json = gson.toJson(reapPayRes.getContent());
                            logger.error("error result : " + json);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return null;

    }

    public boolean billkeyCancel(String token) {

        try {

            long seqNo = 2L;

            ReappayInfo reappayInfo = getReapPayInfo(seqNo);


            String url = BASE_URL + "v1/pay/billkeycancel";

            CloseableHttpClient client = HttpClients.createDefault();

            try {
                Gson gson = new Gson();

                JsonObject params = new JsonObject();
                params.addProperty("billingToken", token);
//                params.addProperty("payload", "");

                StringEntity entity = new StringEntity(params.toString(), "utf-8");
                HttpPost post = AppUtil.getPost(url, entity);
                post.setHeader("Authorization", "Bearer " + reappayInfo.getAccessToken());
                HttpResponse res = client.execute(post);
                if (res.getStatusLine().getStatusCode() == 200) {
                    ReapPayRes reapPayRes = gson.fromJson(new InputStreamReader(res.getEntity().getContent(), StandardCharsets.UTF_8), ReapPayRes.class);
                    if (reapPayRes.getStatus() == 200) {
                        String json = gson.toJson(reapPayRes.getContent());
                        logger.error("result : " + json);

                        Type type = new TypeToken<ReapPayBillKeyData>() {
                        }.getType();
                        ReapPayBillKeyData billKeyData = gson.fromJson(json, type);
                        if (billKeyData.getBillkeyrespCode().equals("0000")) {
                            return true;
                        }
                    } else {
//                        String json = gson.toJson(reapPayRes.getContent());
                        logger.error("error result : " + reapPayRes.getMessage());
                        if (reapPayRes.getContent() != null) {
                            String json = gson.toJson(reapPayRes.getContent());
                            logger.error("error result : " + json);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.toString());
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return false;

    }

    public static void main(String[] argv) {

        try {

            String cardData = "{\"cardNo\":\"5376920001888803\",\"expireYy\":\"27\",\"expireMm\":\"05\",\"passwd\":\"86\",\"birthday\":\"861214\"}";

            Map<String, String> map = new HashMap<>();
            Gson gson = new Gson();
            map = (Map<String, String>) gson.fromJson(cardData, map.getClass());
//            map.put("timestamp", String.valueOf(System.currentTimeMillis()));
            ObjectMapper objectMapper = new ObjectMapper();
            String content = objectMapper.writeValueAsString(map);
            System.out.println(content) ;

            String iv = "HNSFQENdj04B61O38Q13JBLXFjw".substring(0, 16);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec("roots37billkeybillkeyregist00000".getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            String encryptString = Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));

            System.out.println(encryptString) ;

        }catch (Exception e){

        }


    }

}
