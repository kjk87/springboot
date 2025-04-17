package kr.co.pplus.store.api.jpa.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.AlreadyLimitException;
import kr.co.pplus.store.exception.InvalidGoodsException;
import kr.co.pplus.store.exception.LackCostException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.DateUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class GiftishowService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(GiftishowService.class);


    private static final String AUTH_CODE = "REAL2ad47b7032b344dca92461eb49b76f35";
    private static final String AUTH_TOKEN = "GWZ/YuR8abJY5v7BG2HuIg==";

    private static final String GIFTISHOW_BASE_URL = "https://bizapi.giftishow.com/bizApi/";
    private static final String GIFTISHOW_URL = GIFTISHOW_BASE_URL + "goods";
    private static final String GIFTISHOW_SEND_URL = GIFTISHOW_BASE_URL + "send";
    private static final String GIFTISHOW_CANCEL_URL = GIFTISHOW_BASE_URL + "cancel";
    private static final String GIFTISHOW_COUPON_URL = GIFTISHOW_BASE_URL + "coupons";
    private static final String GIFTISHOW_RESEND_URL = GIFTISHOW_BASE_URL + "resend";

    @Value("${STORE.TYPE}")
    String storeType ;

    @Autowired
    GiftishowRepository giftishowRepository;

    @Autowired
    GiftishowBuyRepository giftishowBuyRepository;

    @Autowired
    GiftishowBuyDetailRepository giftishowBuyDetailRepository;

    @Autowired
    GiftishowTargetRepository giftishowTargetRepository;

    @Autowired
    GiftishowCategoryRepository giftishowCategoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PointService pointService;

    @Autowired
    MobileCategoryRepository mobileCategoryRepository;

    @Autowired
    MobileBrandRepository mobileBrandRepository;

    @Autowired
    BolService bolService;

    public static void main(String argv[]) {
//        send("G00000590926", "캍쵸먹자", "01038007428");

//        cancel("pplus_201207154438470279");
//        check("pplus_201207102443170245");
    }

    public List<MobileCategory> getMobileCategoryList(){
        return mobileCategoryRepository.findAllByStatusOrderByArrayDesc("active");
    }

    public List<MobileBrand> getMobileBrandList(Long categorySeqNo){
        return mobileBrandRepository.findAllByCategorySeqNoAndStatusOrderByArrayDesc(categorySeqNo, "active");
    }

    public Giftishow getGiftishow(Long seqNo){
        return giftishowRepository.findBySeqNo(seqNo);
    }

    public String getTrId(){
        return "pplus_"+getRandomId();
    }

    @Transactional(transactionManager = "jpaTransactionManager", isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int buy(User user, GiftishowBuy giftishowBuy, Boolean isBol) throws ResultCodeException{

        String dateStr = AppUtil.localDatetimeNowString() ;

        Giftishow giftishow = giftishowRepository.findBySeqNo(giftishowBuy.getGiftishowSeqNo());

        List<GiftishowTarget> targetList = giftishowBuy.getTargetList();

        if(giftishow.getRmIdBuyCntFlagCd().equals("MONTH")){

            String strDate = DateUtil.getDateString(DateUtil.DATE_FORMAT, Calendar.getInstance().getTime());
            String startDate = strDate + " 00:00:00";
            Integer buyCount = giftishowBuyRepository.countByMemberSeqNoAndGiftishowSeqNoAndStatusAndRegDatetimeGreaterThanEqual(user.getNo(), giftishow.getSeqNo(), "success", startDate);
            if((buyCount + targetList.size()) > 5){
                throw new AlreadyLimitException();
            }
        }

        Integer price = giftishow.getRealPrice()*targetList.size();

        Member member = memberRepository.findBySeqNo(user.getNo());

        if(isBol){
            if (price > member.getBol()) {
                throw new LackCostException("point lack");
            }
        }else{
            if (price > member.getPoint()) {
                throw new LackCostException("point lack");
            }
        }

        giftishowBuy.setUnitPrice(giftishow.getRealPrice());
        giftishowBuy.setStatus("pending");
        giftishowBuy.setPrice(price);
        giftishowBuy.setTotalCount(targetList.size());
        giftishowBuy.setRegDatetime(dateStr);
        giftishowBuy = giftishowBuyRepository.saveAndFlush(giftishowBuy);

        if(isBol){
            kr.co.pplus.store.api.jpa.model.BolHistory bolHistory = new kr.co.pplus.store.api.jpa.model.BolHistory();
            bolHistory.setAmount(price.floatValue());
            bolHistory.setMemberSeqNo(user.getNo());
            bolHistory.setSubject(giftishow.getGoodsName());
            bolHistory.setPrimaryType("decrease");
            bolHistory.setSecondaryType("buyGiftishow");
            bolHistory.setTargetType("member");
            bolHistory.setTargetSeqNo(user.getNo());
            bolHistory.setHistoryProp(new HashMap<String, Object>());
            bolHistory.getHistoryProp().put("사용유형", giftishow.getGoodsName() + " 구매");
            bolService.decreaseBol(user.getNo(), bolHistory);
        }else{
            PointHistory pointHistory = new PointHistory();
            pointHistory.setMemberSeqNo(user.getNo());
            pointHistory.setType("used");
            pointHistory.setPoint(price.floatValue());
            pointHistory.setSubject(giftishow.getGoodsName());
            pointService.updatePoint(user.getNo(), pointHistory);
        }



        boolean success = true;

        for(GiftishowTarget giftishowTarget : targetList){
            giftishowTarget.setMobileNumber(giftishowTarget.getMobileNumber().replace("luckyball##", "").replace("biz##", ""));

            String msg = "";
            if(AppUtil.isEmpty(giftishowBuy.getMsg())){
                msg = giftishow.getGoodsName();
            }else{
                msg = giftishowBuy.getMsg();
            }
            String trId = getTrId();
            JsonObject resultObject = send(giftishow.getGoodsCode(), trId, "", msg, giftishowTarget.getMobileNumber().replace("luckyball##", "").replace("biz##", ""));

            if(resultObject != null){
                String orderNo = resultObject.get("orderNo").getAsString();
                giftishowTarget.setGiftishowBuySeqNo(giftishowBuy.getSeqNo());
                giftishowTarget.setOrderNo(orderNo);
                giftishowTarget.setTrId(trId);
                giftishowTarget.setRegDatetime(dateStr);
                giftishowTarget = giftishowTargetRepository.saveAndFlush(giftishowTarget);
            }else{
                success = false;
                if(isBol){
                    kr.co.pplus.store.api.jpa.model.BolHistory bolHistory = new kr.co.pplus.store.api.jpa.model.BolHistory();
                    bolHistory.setAmount(giftishowBuy.getUnitPrice().floatValue());
                    bolHistory.setMemberSeqNo(user.getNo());
                    bolHistory.setSubject("모바일상품권 구매 실패");
                    bolHistory.setPrimaryType("increase");
                    bolHistory.setSecondaryType("refundGiftishow");
                    bolHistory.setTargetType("member");
                    bolHistory.setTargetSeqNo(user.getNo());
                    bolHistory.setHistoryProp(new HashMap<String, Object>());
                    bolHistory.getHistoryProp().put("적립유형", "모바일상품권 구매 실패");
                    bolService.increaseBol(user.getNo(), bolHistory);

                }else{
                    PointHistory pointHistory = new PointHistory();
                    pointHistory.setMemberSeqNo(user.getNo());
                    pointHistory.setType("charge");
                    pointHistory.setPoint(giftishowBuy.getUnitPrice().floatValue());
                    pointHistory.setSubject("모바일상품권 구매 실패");
                    pointService.updatePoint(user.getNo(), pointHistory);
                }

            }
        }
        if(success){
            giftishowBuy.setStatus("success");
            giftishowBuy = giftishowBuyRepository.saveAndFlush(giftishowBuy);
            return Const.E_SUCCESS;
        }else{
            giftishowBuy.setStatus("fail");
            giftishowBuy = giftishowBuyRepository.saveAndFlush(giftishowBuy);
            return Const.E_INVALID_BUY;
        }
    }

    public JsonObject send(String goodsCode, String trId, String title, String msg, String mobileNumber){


        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("api_code", "0204"));
        nameValuePairList.add(new BasicNameValuePair("custom_auth_code", AUTH_CODE));
        nameValuePairList.add(new BasicNameValuePair("custom_auth_token", AUTH_TOKEN));
        nameValuePairList.add(new BasicNameValuePair("dev_yn", "N"));
        nameValuePairList.add(new BasicNameValuePair("goods_code", goodsCode));
        nameValuePairList.add(new BasicNameValuePair("order_no", getRandomId()));
        if(AppUtil.isEmpty(title)){
            nameValuePairList.add(new BasicNameValuePair("mms_title", "모바일 상품권 구매"));
        }else{
            nameValuePairList.add(new BasicNameValuePair("mms_title", title));
        }

        nameValuePairList.add(new BasicNameValuePair("mms_msg", msg));
        nameValuePairList.add(new BasicNameValuePair("callback_no", "0263151234"));
        nameValuePairList.add(new BasicNameValuePair("phone_no", mobileNumber));
        nameValuePairList.add(new BasicNameValuePair("tr_id", trId));
        nameValuePairList.add(new BasicNameValuePair("rev_info_yn", "N"));
        nameValuePairList.add(new BasicNameValuePair("template_id", "202012030072337"));
        nameValuePairList.add(new BasicNameValuePair("banner_id", "202012030074048"));
        nameValuePairList.add(new BasicNameValuePair("user_id", "partner@p-ple.com"));
        nameValuePairList.add(new BasicNameValuePair("gubun", "N"));
        String paramsStr = URLEncodedUtils.format(nameValuePairList, "utf-8");
        logger.debug("giftishow send params : "+paramsStr);

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = AppUtil.getPost(GIFTISHOW_SEND_URL, nameValuePairList);
            HttpResponse res = client.execute(post);
            Gson gson = new Gson();
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            logger.debug("giftishow send result : "+str);
            JsonObject jsonObj = gson.fromJson (str, JsonElement.class).getAsJsonObject();
            String code = jsonObj.get("code").getAsString();
            if(code.equals("0000")){
                logger.debug("trid : "+trId);
                JsonObject resultObject = jsonObj.getAsJsonObject("result").getAsJsonObject("result");
                return resultObject;
            }else{
                return null;
            }
        }catch (Exception e){
            //취소 api처리
            logger.error("giftishow send error : "+e.toString());
            cancel(trId);
            return null;
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int resendGiftishow(Long giftshowBuySeqNo) throws ResultCodeException{
        List<GiftishowTarget> targetList = giftishowTargetRepository.findAllByGiftishowBuySeqNo(giftshowBuySeqNo);
        for(GiftishowTarget target : targetList){
            resend(target.getTrId());
        }

        return Const.E_SUCCESS;
    }

    public String check(String trId){
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("api_code", "0201"));
        nameValuePairList.add(new BasicNameValuePair("custom_auth_code", AUTH_CODE));
        nameValuePairList.add(new BasicNameValuePair("custom_auth_token", AUTH_TOKEN));
        nameValuePairList.add(new BasicNameValuePair("dev_yn", "N"));
        nameValuePairList.add(new BasicNameValuePair("tr_id", trId));

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = AppUtil.getPost(GIFTISHOW_COUPON_URL, nameValuePairList);
            HttpResponse res = client.execute(post);
            Gson gson = new Gson();
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            JsonObject jsonObj = gson.fromJson (str, JsonElement.class).getAsJsonObject();
            String code = jsonObj.get("code").getAsString();
            if(code.equals("0000")){
                logger.debug("code : "+code);
                String statusCode = jsonObj.getAsJsonArray("result").get(0).getAsJsonObject().getAsJsonArray("couponInfoList").get(0).getAsJsonObject().get("pinStatusCd").getAsString();
                logger.debug("statusCode : "+statusCode);//01 만 정상
                return statusCode;
            }else{
                return null;
            }
        }catch (Exception e){
        }
        return null;
    }

    public boolean cancel(String trId){
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("api_code", "0202"));
        nameValuePairList.add(new BasicNameValuePair("custom_auth_code", AUTH_CODE));
        nameValuePairList.add(new BasicNameValuePair("custom_auth_token", AUTH_TOKEN));
        nameValuePairList.add(new BasicNameValuePair("dev_yn", "N"));
        nameValuePairList.add(new BasicNameValuePair("tr_id", trId));
        nameValuePairList.add(new BasicNameValuePair("user_id", "partner@p-ple.com"));

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = AppUtil.getPost(GIFTISHOW_CANCEL_URL, nameValuePairList);
            HttpResponse res = client.execute(post);
            Gson gson = new Gson();
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            JsonObject jsonObj = gson.fromJson (str, JsonElement.class).getAsJsonObject();
            String code = jsonObj.get("code").getAsString();
            if(code.equals("0000")){
                logger.debug("code : "+code);
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            //취소 api처리
        }
        return false;
    }

    public boolean resend(String trId){
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("api_code", "0203"));
        nameValuePairList.add(new BasicNameValuePair("custom_auth_code", AUTH_CODE));
        nameValuePairList.add(new BasicNameValuePair("custom_auth_token", AUTH_TOKEN));
        nameValuePairList.add(new BasicNameValuePair("dev_yn", "N"));
        nameValuePairList.add(new BasicNameValuePair("tr_id", trId));
        nameValuePairList.add(new BasicNameValuePair("sms_flag", "N"));
        nameValuePairList.add(new BasicNameValuePair("user_id", "partner@p-ple.com"));

        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = AppUtil.getPost(GIFTISHOW_RESEND_URL, nameValuePairList);
            HttpResponse res = client.execute(post);
            Gson gson = new Gson();
            String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            JsonObject jsonObj = gson.fromJson (str, JsonElement.class).getAsJsonObject();
            String code = jsonObj.get("code").getAsString();
            if(code.equals("0000")){
                logger.debug("code : "+code);
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            //취소 api처리
        }
        return false;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void syncGoodsList() throws ResultCodeException {

        try {

            List<Giftishow> beforeList = giftishowRepository.findAll();
            List<Giftishow> afterList = new ArrayList<>();

            List<String> codeList = new ArrayList<>();
            codeList.add("G00000182233");
            codeList.add("G00000165804");
            codeList.add("G00000182525");
            codeList.add("G00000182524");

            int i = 0;
            do {
                i++;
                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(new BasicNameValuePair("api_code", "0101"));
                nameValuePairList.add(new BasicNameValuePair("custom_auth_code", AUTH_CODE));
                nameValuePairList.add(new BasicNameValuePair("custom_auth_token", AUTH_TOKEN));
                nameValuePairList.add(new BasicNameValuePair("dev_yn", "N"));
                nameValuePairList.add(new BasicNameValuePair("start", String.valueOf(i)));
                nameValuePairList.add(new BasicNameValuePair("size", "500"));


                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost post = AppUtil.getPost(GIFTISHOW_URL, nameValuePairList);
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(60*1000)
                        .setConnectTimeout(60*1000)
                        .setConnectionRequestTimeout(60*1000)
                        .build();
                post.setConfig(requestConfig);
                HttpResponse res = client.execute(post);
                String str = IOUtils.toString(res.getEntity().getContent(), "UTF-8");
                Gson gson = new Gson();
                JsonObject jsonObj = gson.fromJson (str, JsonElement.class).getAsJsonObject();
                String code = jsonObj.get("code").getAsString();
                if(code.equals("0000")){
                    JsonObject resultObject = jsonObj.getAsJsonObject("result");
                    String listNumb = resultObject.get("listNum").getAsString();
                    JsonArray jsonArray = resultObject.getAsJsonArray("goodsList");

                    logger.debug("jsonArray size : "+jsonArray.size());
                    for(int j = 0; j < jsonArray.size(); j++){
                        JsonElement jsonElement = jsonArray.get(j);
                        Giftishow giftishow = gson.fromJson(jsonElement.getAsJsonObject(), Giftishow.class);
                        if(giftishow.getGoodsTypeNm().equals("배송상품")){
                            giftishow.setSale(false);
                        }else{
                            giftishow.setSale(true);
                        }

                        giftishow.setPriority(1);

//                        if(giftishow.getGoodsCode().equals("G00000182233") || giftishow.getGoodsCode().equals("G00000165804") || giftishow.getGoodsCode().equals("G00000182525") || giftishow.getGoodsCode().equals("G00000182524")){
                        Float price = giftishow.getRealPrice()*1.4f;
                        giftishow.setRealPrice(price.intValue());

                        afterList.add(giftishow);
                    }

                    logger.debug("size : "+afterList.size());
                    if(afterList.size() >= Integer.valueOf(listNumb)){
                        break;
                    }


                }else {
                    break;
                }
            }while (true);

            if(beforeList == null || beforeList.isEmpty()){
                giftishowRepository.saveAll(afterList);
            }else{

                for(Giftishow before :  beforeList){
                    before.setSale(false);
                }
                for(Giftishow giftishow : afterList){
                    boolean insert = true;
                    for(int j = 0; j < beforeList.size(); j++){
                        if(beforeList.get(j).getGoodsCode().equals(giftishow.getGoodsCode())){
                            insert = false;
                            giftishow.setSeqNo(beforeList.get(j).getSeqNo());
                            if(giftishow.getGoodsTypeNm().equals("배송상품")){
                                giftishow.setSale(false);
                            }else{
                                giftishow.setSale(true);
                            }

                            giftishow.setPriority(beforeList.get(j).getPriority());
                            giftishow.setGiftishowCategorySeqNo(beforeList.get(j).getGiftishowCategorySeqNo());
                            giftishow.setBrandSeqNo(beforeList.get(j).getBrandSeqNo());
                            beforeList.set(j, giftishow);
                            break;
                        }
                    }

                    if(insert){
                        giftishow.setSale(true);
                        giftishowRepository.save(giftishow);
                    }

                }

                giftishowRepository.saveAll(beforeList);
            }

        }catch (Exception e){
            logger.error(e.toString());
            throw new InvalidGoodsException(e.toString());
        }

    }

    public Page<Giftishow> getGoodsList(Long categorySeqNo, Pageable pageable){
        if(categorySeqNo == null){
            return giftishowRepository.findAllBySaleAndGoodsStateCdAndDiscountRateGreaterThanEqualOrderByPriorityDescSeqNoDesc(true, "SALE", 6f, pageable);
        }else{
            return giftishowRepository.findAllByGiftishowCategorySeqNoAndSaleAndGoodsStateCdAndDiscountRateGreaterThanEqualOrderByPriorityDescSeqNoDesc(categorySeqNo, true, "SALE", 6f, pageable);
        }
    }

    public Page<Giftishow> getGoodsListByBrand(Long brandSeqNo, Pageable pageable){
        return giftishowRepository.findAllByBrandSeqNoAndSaleAndGoodsStateCdOrderByPriorityDescSeqNoDesc(brandSeqNo, true, "SALE", pageable);

    }

    public Page<Giftishow> getGoodsListByBrandAndMinPrice(Long brandSeqNo, Integer minPrice, Pageable pageable){
        return giftishowRepository.findAllByBrandSeqNoAndSaleAndGoodsStateCdAndRealPriceGreaterThanEqualOrderByPriorityDescSeqNoDesc(brandSeqNo, true, "SALE", minPrice, pageable);

    }

    public Page<GiftishowBuyDetail> getGfitishowBuyList(Long memberSeqNo, Pageable pageable){
        return giftishowBuyDetailRepository.findAllByMemberSeqNoAndStatus(memberSeqNo, "success", pageable);
    }

    public Integer getGiftishowBuyCount(Long memberSeqNo){
        return giftishowBuyDetailRepository.countByMemberSeqNoAndStatus(memberSeqNo, "success");
    }

    public GiftishowBuyDetail getGiftishowBuyDetailBySeqNo(Long seqNo){
        return giftishowBuyDetailRepository.findBySeqNo(seqNo);
    }

    public List<GiftishowCategory> getGifitishowCategoryList(){
        return giftishowCategoryRepository.findAllByStatusOrderByPriorityDesc("active");
    }

    private static String getRandomId(){
        try {
            SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");
            int number = secureRandomGenerator.nextInt(900000) + 100000;

            Date now = new Date();
            SimpleDateFormat transFormat = new SimpleDateFormat("yyMMddHHmmss");
            String to = transFormat.format(now);

            String orderId = to + number;
            return orderId;
        }catch (Exception e){

        }
        return null;
    }
}
