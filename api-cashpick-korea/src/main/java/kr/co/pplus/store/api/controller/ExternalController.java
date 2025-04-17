package kr.co.pplus.store.api.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.jpa.model.PointClickReward;
import kr.co.pplus.store.api.jpa.model.SmaadReward;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.ExternalService;
import kr.co.pplus.store.mvc.service.UserService;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.RejectRequest;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.util.DigestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ExternalController extends RootController {
    private final static Logger logger = LoggerFactory.getLogger(ExternalController.class);


    @Autowired
    ExternalService svc;

    @Autowired
    UserService userSvc;

    @Value("${STORE.ADPOPCORN_AOS_HASHKEY}")
    private String ADPOPCORN_AOS_HASHKEY;

    @Value("${STORE.ADPOPCORN_AOS_LUCKYBOL_HASHKEY}")
    private String ADPOPCORN_AOS_LUCKYBOL_HASHKEY;

    @Value("${STORE.ADPOPCORN_IOS_HASHKEY}")
    private String ADPOPCORN_IOS_HASHKEY;

    @Value("${STORE.ADPOPCORN_IOS_LUCKYBOL_HASHKEY}")
    private String ADPOPCORN_IOS_LUCKYBOL_HASHKEY;

    @Value("${STORE.ADSYNC_AOS_SERVICE_ID}")
    private String ADSYNC_AOS_SERVICE_ID;

    @Value("${STORE.ADSYNC_AOS_AUTH_KEY}")
    private String ADSYNC_AOS_AUTH_KEY;

    @Value("${STORE.ADSYNC_IOS_SERVICE_ID}")
    private String ADSYNC_IOS_SERVICE_ID;

    @Value("${STORE.ADSYNC_IOS_AUTH_KEY}")
    private String ADSYNC_IOS_AUTH_KEY;

    private String TNK_AOS_APP_KEY = "140bc4d0f93c803908ef2c5e9803c896";
    private String TNK_IOS_APP_KEY = "3b27987f9d1c2115db67d9bd4c920911";

    private String IVE_AOS_APP_KEY = "E7OzIiFPAD";
    private String IVE_IOS_APP_KEY = "E7OzIiFPAD";

    @SkipSessionCheck
    @RequestMapping(produces = "text/plain;charset=UTF-8", value = baseUri + "/external/registRejectNumber/**")
    @ResponseBody
    public String registRejectNumber(RejectRequest req) {
        Integer ret = svc.registRejectNumber(req);
        return ret.equals(Const.E_SUCCESS) ? "Success" : "Failure";
    }

    @SkipSessionCheck
    @RequestMapping(produces = "text/plain;charset=UTF-8", value = baseUri + "/external/callbackSendMsg/{msgNo}/{mobile}/**")
    @ResponseBody
    public String callbackSendMsg(@PathVariable("msgNo") String msgNo, @PathVariable("mobile") String mobile, String data) {
        return svc.callbackSendMsg(Long.parseLong(msgNo), mobile, data);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/external/flex/reward/**")
    @ResponseBody
    public Map<String, Object> flexReward(FlexReward reward) {

        Map<String, Object> result = new HashMap<String, Object>();

        if (userSvc.existsUser(Long.valueOf(reward.getUserkey()))) {
            if (!svc.existsFlexReward(reward.getFlexcode())) {
                return svc.rewardFlex(reward);
            } else {
                logger.warn("Duplicate Reward Key: " + reward.getFlexcode());
                result.put("return_code", "error3");
            }
        } else {
            logger.warn("User Not Found: userNo=" + reward.getFlexcode());
            result.put("return_code", "error6");
        }
        return result;
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/external/adSync/{platform}/reward/**")
    @ResponseBody
    public Map<String, Object> adSyncReward(@PathVariable("platform") String platform, AdSyncReward reward) {
        String authKey = null;
        if (platform.equals("aos")) {
            authKey = ADSYNC_AOS_AUTH_KEY;
        } else if (platform.equals("ios")) {
            authKey = ADSYNC_IOS_AUTH_KEY;
        }

        Map<String, Object> result = new HashMap<String, Object>();
        if (AppUtil.isEmpty(reward.getPartner()) || AppUtil.isEmpty(reward.getCust_id()) || AppUtil.isEmpty(reward.getAd_no()) || reward.getPoint() == null || reward.getPoint() == 0 || AppUtil.isEmpty(reward.getAd_title()) || AppUtil.isEmpty(reward.getValid_key())) {
            result.put("Result", false);
            result.put("ResultCode", 4);
            result.put("ResultMsg", "파라미터 오류");
            return result;
        }


        String signedValue = DigestUtil.encryptMD5(authKey + reward.getCust_id() + reward.getSeq_id());
        if (!reward.getValid_key().equals(signedValue)) {
            result.put("Result", false);
            result.put("ResultCode", 2);
            result.put("ResultMsg", "유효성 확인 key 오류");
            return result;
        }

        if (userSvc.existsUser(Long.valueOf(reward.getCust_id()))) {
            if (!svc.existsAdSyncReward(reward.getSeq_id())) {
                return svc.rewardAdSync(reward);
            } else {
                logger.warn("Duplicate Reward Key: " + reward.getSeq_id());
                result.put("Result", false);
                result.put("ResultCode", 3);
                result.put("ResultMsg", "중복 지급  오류");
            }
        } else {
            logger.warn("User Not Found: userNo=" + reward.getSeq_id());
            result.put("Result", false);
            result.put("ResultCode", 6);
            result.put("ResultMsg", "invalid user");
        }
        return result;
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/external/tnk/{platform}/reward/**")
    @ResponseBody
    public Map<String, Object> tnkReward(@PathVariable("platform") String platform, TNKReward reward) {

        String appKey = null;
        if (platform.equals("aos")) {
            appKey = TNK_AOS_APP_KEY;
        } else if (platform.equals("ios")) {
            appKey = TNK_IOS_APP_KEY;
        }

        Map<String, Object> result = new HashMap<String, Object>();


        String signedValue = DigestUtil.encryptMD5(appKey + reward.getMd_user_nm() + reward.getSeq_id());

        if (!reward.getMd_chk().equals(signedValue)) {
            logger.error("유효성 확인 key 오류");
            result.put("return_code", "error");
            return result;
        }

        if (userSvc.existsUser(Long.valueOf(reward.getMd_user_nm()))) {
            if (!svc.existsTNKReward(reward.getSeq_id())) {
                return svc.rewardTNK(reward);
            } else {
                logger.error("Duplicate Reward Key: " + reward.getSeq_id());
                result.put("return_code", "error");
            }
        } else {
            logger.error("User Not Found: userNo=" + reward.getSeq_id());
            result.put("return_code", "error");
        }
        return result;
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/external/pointClick/reward/**")
    @ResponseBody
    public void rewardPointClick(HttpServletRequest request) {
        PointClickReward reward = new PointClickReward();
        reward.setTransactionKey(request.getParameter("transaction_key"));
        reward.setPlacementUid(request.getParameter("placement_uid"));
        reward.setAdKey(request.getParameter("ad_key"));
        reward.setAdName(request.getParameter("ad_name"));
        reward.setAdProfit(Float.valueOf(request.getParameter("ad_profit")));
        reward.setAdCurrency(request.getParameter("ad_currency"));
        reward.setPoint(Float.valueOf(request.getParameter("point")));
        reward.setDeviceIfa(request.getParameter("device_ifa"));
        reward.setPickerUid(request.getParameter("picker_uid"));


        svc.rewardPointClick(reward);
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/external/smaad/reward/**")
    @ResponseBody
    public void rewardSmaad(HttpServletRequest request) {
        try {
            logger.error("smaad query" + request.getQueryString());
        }catch (Exception e){
            logger.error(e.toString());
        }

        SmaadReward reward = new SmaadReward();
        reward.setUser(request.getParameter("user"));
        reward.setAdid(request.getParameter("adid"));
        reward.setTitle(request.getParameter("title"));
        reward.setOrdersId(request.getParameter("orders_id"));
        reward.setInstallId(request.getParameter("install_id"));
        reward.setPay(Integer.valueOf(request.getParameter("pay")));
        reward.setUserPay(Integer.valueOf(request.getParameter("user_pay")));
        reward.setUserPay2(Integer.valueOf(request.getParameter("user_pay2")));
        reward.setTimeUtc(request.getParameter("time_utc"));
        reward.setTimeJst(request.getParameter("time_jst"));
        reward.setApproved(Integer.valueOf(request.getParameter("approved")));
        reward.setCourseId(request.getParameter("course_id"));
        reward.setNetworkZoneId(request.getParameter("network_zone_id"));
        reward.setAmount(Integer.valueOf(request.getParameter("amount")));

        svc.rewardSmaadReward(reward);

    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/external/buzvil/reward/**")
    @ResponseBody
    public Map<String, Object> buzvilReward(BuzvilReward reward) {

        Map<String, Object> result = new HashMap<String, Object>();

        if (userSvc.existsUser(Long.valueOf(reward.getUser_id()))) {
            if (!svc.existsBuzvil(reward.getTransaction_id())) {
                return svc.rewardBuzvil(reward);
            } else {
                logger.error("Duplicate Reward Key: " + reward.getTransaction_id());
                result.put("result", "fail");
            }
        } else {
            logger.error("User Not Found: userNo=" + reward.getTransaction_id());
            result.put("result", "fail");
        }
        return result;
    }

    @SkipSessionCheck
    @PostMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/external/pincrux/reward/**")
    @ResponseBody
    public Map<String, Object> pincruxReward(PincruxReward reward) {

        Map<String, Object> result = new HashMap<String, Object>();

        if (reward.getAppkey() == null || reward.getPubkey() == null || AppUtil.isEmpty(reward.getUsrkey()) || AppUtil.isEmpty(reward.getTransid()) || reward.getCoin() == null || reward.getCoin() == 0) {
            result.put("code", "01");
            return result;
        }
        if (userSvc.existsUser(Long.valueOf(reward.getUsrkey()))) {
            if (!svc.existsPincruxReward(reward.getTransid())) {
                return svc.rewardPincrux(reward);
            } else {
                logger.error("Duplicate Reward Key: " + reward.getTransid());
                result.put("code", "11");
            }
        } else {
            logger.error("User Not Found: userNo=" + reward.getUsrkey());
            result.put("code", "05");
        }
        return result;
    }

    @SkipSessionCheck
    @RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri + "/external/adpop/{platform}/reward/**")
    @ResponseBody
    public Map<String, Object> adpop_reward(@PathVariable("platform") String platform, AdpopcornReward reward) {

        String hashKey = null;
        if (platform.equals("aos")) {
            hashKey = ADPOPCORN_AOS_HASHKEY;
        } else if (platform.equals("ios")) {
            hashKey = ADPOPCORN_IOS_HASHKEY;
        } else if (platform.equals("luckybolaos")) {
            hashKey = ADPOPCORN_AOS_LUCKYBOL_HASHKEY;
        } else if (platform.equals("luckybolios")) {
            hashKey = ADPOPCORN_IOS_LUCKYBOL_HASHKEY;
        }


        Map<String, Object> result = new HashMap<String, Object>();
        StringBuffer buf = new StringBuffer();
        buf.append(reward.getUsn()).append(reward.getReward_key()).append(reward.getQuantity())
                .append(reward.getCampaign_key());

        String signedValue = DigestUtil.encryptHMACMD5(hashKey, buf.toString());

        if (!reward.getSigned_value().equals(signedValue)) {
            logger.warn("Received Signed Value=" + reward.getSigned_value() + ", Calc Signed Value=" + buf.toString());
            result.put("Result", false);
            result.put("ResultCode", 1100);
            result.put("ResultMsg", "invalid signed value");
        } else {
            if (userSvc.existsUser(reward.getUsn())) {
                if (!svc.existsAdpopcornReward(reward.getReward_key())) {
                    return svc.reward(reward);
                } else {
                    logger.warn("Duplicate Reward Key: " + reward.getReward_key());
                    result.put("Result", false);
                    result.put("ResultCode", 3100);
                    result.put("ResultMsg", "duplicate transaction");
                }
            } else {
                logger.warn("User Not Found: userNo=" + reward.getUsn());
                result.put("Result", false);
                result.put("ResultCode", 3200);
                result.put("ResultMsg", "invalid user");
            }

        }
        return result;
    }
}
