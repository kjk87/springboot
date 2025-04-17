package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.helper.VerificationHelper;
import kr.co.pplus.store.mvc.bean.VerificationHelperManager;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.SysTemplate;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.type.model.Verification;
import kr.co.pplus.store.type.model.code.TemplateVariable;
import kr.co.pplus.store.util.DateUtil;
import kr.co.pplus.store.util.FTLinkPayApi;
import kr.co.pplus.store.util.StoreUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class VerificationService extends RootService {

    @Autowired
    UserService userSvc;

    @Autowired
    TemplateService templateSvc;

    @Autowired
    VerificationHelperManager helperManager;

    @Value("${STORE.VERIFICATION}")
    private Boolean VERIFICATION = false;


    @Value("${STORE.CERT_NUM_LENGTH}")
    private int CERT_NUM_LENGTH = 6;

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer request(Verification verification) throws Exception {
        if (!verification.getMedia().equals("external")
                && (
                (verification.getMedia().equals("sms") && StringUtils.isEmpty(verification.getMobile()))
                        || (verification.getMedia().equals("ars") && StringUtils.isEmpty(verification.getMobile()))
                        || (verification.getMedia().equals("email") && StringUtils.isEmpty(verification.getEmail()))
                        || (!verification.getMedia().equals("ars") && verification.getType().equals("findPassword") && StringUtils.isEmpty(verification.getLoginId()))
        )
        ) {
            return Const.E_INVALID_ARG;
        }

        if (verification.getMedia().equals("sms") && !StoreUtil.isCellPhoneNumber(verification.getMobile())) {
            return Const.E_INVALID_ARG;
        }


        boolean test = false;
        boolean testNum = false;
        if (verification.getMedia().equals("sms") && verification.getMobile().startsWith("0169999"))
            testNum = true;

        verification.setMobile(StoreUtil.getValidatePhoneNumber(verification.getMobile()));

        verification.setRegDate(DateUtil.getCurrentDate());
        if (!verification.getMedia().equals("external")) {
            if (!verification.getMedia().equals("ars") && !(verification.getMedia().equals("sms") && verification.getType().equals("changeMobile"))) {
                User user = null;
                boolean ftlinkExist = false;
                if (verification.getMedia().equals("sms")) {
                    if (StringUtils.isEmpty(verification.getAppType())) {
                        verification.setAppType("pplus");
                    }
                    String mobile = verification.getMobile();
                    if (!verification.getAppType().equals("pplus") && !mobile.startsWith(verification.getAppType() + "##")) {
                        mobile = verification.getAppType() + "##" + mobile;
                    }
                    user = userSvc.getUserByMobile(mobile);

                    if (user == null) {
                        if (verification.getAppType().equals("biz")) {
                            ftlinkExist = !FTLinkPayApi.checkMobile(verification.getMobile().replace(verification.getAppType() + "##", ""));
                        }

                    }

                } else if (verification.getMedia().equals("email")) {
                    user = userSvc.getUserByEmail(verification.getEmail());
                }

                if ((user != null || ftlinkExist) && (verification.getType().equals("join") || verification.getType().equals("profile"))) {
                    if (user != null) {
                        throw new AlreadyExistsException("user no", user.getNo());
                    }
                    throw new AlreadyExistsException();
                } else if (user == null && !verification.getType().equals("join") && !verification.getType().equals("profile")) {
                    throw new NotFoundTargetException("mobile", "not found");
                }

            }

            verification.setNumber("111111");
            if (VERIFICATION && test == false && testNum == false) {
                verification.setNumber(StoreUtil.generateRandomNumber(CERT_NUM_LENGTH));
            }

            verification.setToken(StoreUtil.generateVerificationToken(verification.getNumber()));

            if (!test)
                send(verification);
        }

        sqlSession.insert("Verification.insert", verification);

        return Const.E_SUCCESS;
    }

    public Integer confirm(Verification verification) throws ResultCodeException {
        if (StringUtils.isEmpty(verification.getToken()) || StringUtils.isEmpty(verification.getNumber()))
            throw new InvalidArgumentException();

        Verification dbVal = sqlSession.selectOne("Verification.get", verification.getToken());

        if (dbVal == null)
            throw new NotFoundTargetException();


        if (!verification.getNumber().equals(dbVal.getNumber()))
            throw new NotMatchedVerificationException();

        //type이 levelup이고 external 이고 loginId가 있는 경우 해당 사용자의 인증 레벨을 11로 높인다.
        if (dbVal.getMedia().equals("external")
                && !StringUtils.isEmpty(verification.getLoginId())
                && verification.getType() != null
                && verification.getType().equals("levelup")) {

            if (StringUtils.isEmpty(verification.getAppType())) {
                verification.setAppType("pplus");
            }

            if (!verification.getAppType().equals("pplus") && !verification.getLoginId().startsWith(verification.getAppType() + "##")) {
                verification.setLoginId(verification.getAppType() + "##" + verification.getLoginId());
            }

            User user = userSvc.getUserByLoginId(verification.getLoginId());
            if (user == null)
                throw new NotFoundTargetException("loginId", "not found user");

            if (user.getCertificationLevel() < (short) 11) {
                user.setCertificationLevel((short) 11);
                userSvc.updateCertificationLevel(user);
            }
        }

        return Const.E_SUCCESS;
    }

    public Verification get(Verification verification) {
        return sqlSession.selectOne("Verification.get", verification.getToken());
    }

    public User getUserByVerification(Verification verification) throws ResultCodeException {
        Verification saved = get(verification);
        if (saved == null)
            throw new NotFoundTargetException("verificaiton", "not found");

        if (!verification.getNumber().equals(saved.getNumber()))
            throw new NotMatchedVerificationException();

        User user = null;
        if (saved.getMedia().equals("sms") || saved.getMedia().equals("external")) {
            user = userSvc.getUserByMobile(saved.getMobile());
        } else if (saved.getMedia().equals("email")) {
            user = userSvc.getUserByEmail(saved.getEmail());
        }
        return user;
    }

    private void send(Verification verification) throws Exception {
        VerificationHelper helper = helperManager.get(verification.getMedia());
        String target = null;
        SysTemplate template = null;
        if (verification.getMedia().equals("sms") || verification.getMedia().equals("email")) {
            StringBuilder buf = new StringBuilder("KOR");
            buf.append(helper.getTemplateCode()).append(StoreUtil.getTemplateSubCode(verification.getType()));

            Map<String, String> variableMap = new HashMap<String, String>();
            variableMap.put(TemplateVariable.VERIFICATION_NUMBER.getVariable(), verification.getNumber());
            variableMap.put(TemplateVariable.CREATE_TIME.getVariable(), DateUtil.getDate(DateUtil.DEFAULT_FORMAT, verification.getRegDate()));

            template = templateSvc.get(buf.toString(), variableMap);

            String contents = null;
            if (verification.getAppType().equals("pplus")) {
                contents = "[오리마켓] " + template.getContents();
            } else if (verification.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                contents = "[캐시픽] " + template.getContents();
            } else if (verification.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                contents = "[럭키픽] " + template.getContents();
            } else if (verification.getAppType().equals(Const.APP_TYPE_BIZ)) {
                contents = "[PR넘버] " + template.getContents();
            } else {
                contents = template.getContents();
            }

            template.setContents(contents);

            if (verification.getMedia().equals("sms"))
                target = verification.getMobile();
            else if (verification.getMedia().equals("email"))
                target = verification.getEmail();
        } else if (verification.getMedia().equals("ars")) {
            target = verification.getMobile();
            template = new SysTemplate();
            template.setCode(verification.getNumber());
        }

        if (target != null && template != null)
            helper.getSender().send(target, template);
    }
}
