package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.api.jpa.model.Lottery;
import kr.co.pplus.store.api.jpa.model.Lotto;
import kr.co.pplus.store.api.jpa.model.NotificationBox;
import kr.co.pplus.store.api.jpa.model.Partnership;
import kr.co.pplus.store.api.jpa.repository.LottoRepository;
import kr.co.pplus.store.api.jpa.service.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.JoinUser;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import kr.co.pplus.store.type.model.code.Platform;
import kr.co.pplus.store.type.model.code.SalesType;
import kr.co.pplus.store.type.model.code.StoreType;
import kr.co.pplus.store.type.model.code.TalkRecvBound;
import kr.co.pplus.store.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@Transactional(transactionManager = "transactionManager")
public class AuthService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final static String[] RECOMMEND_CHARS = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
            , "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"
            , "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
            , "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"
            , "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    };

//	@Autowired
//    AuthDao dao;

    @Autowired
    UserService userSvc;

    @Autowired
    PageService pageSvc;

    @Autowired
    NumberService numSvc;

    @Autowired
    CustomerService custSvc;

    @Autowired
    PlusService plusSvc;

    @Autowired
    CommonService commonSvc;

    @Autowired
    CashBolService cashBolSvc;

    @Autowired
    VerificationService verificationSvc;

    @Autowired
    ContactService contactSvc;

    @Autowired
    MsgService msgSvc;

    @Autowired
    QueueService queueSvc;

    @Autowired
    ArticleService articleSvc;

    @Autowired
    LottoRepository lottoRepository;

    @Autowired
    AgentService agentSvc;

    @Autowired
    PartnerShipService partnerShipService;

    @Autowired
    NotificationBoxService notificationBoxService;

    @Autowired
    BolService bolService;

    @Autowired
    PointService pointService;

    @Autowired
    BuffWalletService buffWalletService;

    @Autowired
    LotteryService lotteryService;


    @Value("${STORE.TERMS_BASEURL}")
    private String TERMS_BASEURL = "";


    @Value("${STORE.TERMS_EXTENSION}")
    private String TERMS_EXTENSION = "";

    @Value("${STORE.VERIFICATION}")
    private Boolean VERIFICATION = false;

    @Value("${STORE.CERT_NUM_LENGTH}")
    private int CERT_NUM_LENGTH = 6;

    public AppVersion getAppVersion(AppVersion appVersion) throws ResultCodeException {
        AppVersion saved = sqlSession.selectOne("Auth.getAppVersion", appVersion);
        if (saved == null)
            throw new NotFoundTargetException("appversion", "not found");
        return saved;
    }

    public Integer existsUser(User user) throws ResultCodeException {
        if (user == null)
            throw new InvalidArgumentException("user", "not found");


        if (StringUtils.isEmpty(user.getAppType())) {
            user.setAppType("pplus");
        }

        boolean exists = false;
        if (!StringUtils.isEmpty(user.getLoginId())) {

            if (!user.getAppType().equals("pplus") && !user.getLoginId().startsWith(user.getAppType() + "##")) {
                user.setLoginId(user.getAppType() + "##" + user.getLoginId());
            }

            exists = userSvc.existsUserByLoginId(user.getLoginId());

            if (!exists) {
                if (user.getAppType().equals("biz")) {
                    try {
                        exists = !FTLinkPayApi.checkId(user.getLoginId().replace(user.getAppType() + "##", ""));
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }
                }


            }

        } else if (!StringUtils.isEmpty(user.getNickname())) {
            exists = userSvc.existsUserByNickname(user.getNickname(), user.getAppType());
        } else if (!StringUtils.isEmpty(user.getMobile())) {
            if (!user.getAppType().equals("pplus") && !user.getMobile().startsWith(user.getAppType() + "##")) {
                user.setMobile(user.getAppType() + "##" + user.getMobile());
            }

            exists = userSvc.existsUserByMobile(user.getMobile());


            if (!exists) {
                if (user.getAppType().equals("biz")) {
                    try {
                        exists = !FTLinkPayApi.checkMobile(user.getMobile().replace(user.getAppType() + "##", ""));
                    } catch (Exception e) {
                        logger.error(e.toString());
                    }
                }

            }

        } else if (!StringUtils.isEmpty(user.getEmail())) {
            exists = userSvc.existsUserByEmail(user.getEmail());
        }


        if (!exists)
            throw new NotMatchUserException();

//		if( RedisUtil.getInstance().getOpsHash("pplus-login-id", user.getLoginId()) == null ) {
//			RedisUtil.getInstance().putOpsHash("pplus-login-id", user.getLoginId(), "1") ;
//		}
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Integer updateUserAccount(User user, String loginId, String password, String accountType) throws ResultCodeException {

        if (!user.getAccountType().equals("kakao")) {
            throw new NotPermissionException();
        }

        if (!loginId.startsWith(user.getAppType() + "##")) {
            user.setLoginId(user.getAppType() + "##" + loginId);
        }

        user.setPassword(SecureUtil.decryptMobileNumber(password));

        user.setAccountType(accountType);

        if (user.getAccountType().equals("pplus")) {
            if (!user.getLoginId().replace(user.getAppType() + "##", "").matches("^[a-zA-Z0-9]{4,20}$"))
                throw new NotAllowCharacterException("loginId", "alphabet/number/4~20");
        }

        if (userSvc.existsUserByLoginId(user.getLoginId())) {
            throw new DuplicateLoginIdException();
        }

        user.setPassword(SecureUtil.encryptPassword(user.getLoginId().replace(user.getAppType() + "##", ""), user.getPassword()));

        sqlSession.update("Auth.updateAccount", user);

        user.setPassword(null);
        RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "loginId", user.getLoginId(), "1");

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Integer updateUserAccountForAdmin(Long memberSeqNo, String loginId, String password) throws ResultCodeException {

        User user = userSvc.getUser(memberSeqNo);

//        if (!user.getAccountType().equals("kakao")) {
//            throw new NotPermissionException();
//        }

        if (!loginId.startsWith(user.getAppType() + "##")) {
            user.setLoginId(user.getAppType() + "##" + loginId);
        }

        user.setPassword(password);

        user.setAccountType("pplus");

        if (!user.getLoginId().replace(user.getAppType() + "##", "").matches("^[a-zA-Z0-9]{4,20}$"))
            throw new NotAllowCharacterException("loginId", "alphabet/number/4~20");

        if (userSvc.existsUserByLoginId(user.getLoginId())) {
            throw new DuplicateLoginIdException();
        }

        user.setPassword(SecureUtil.encryptPassword(user.getLoginId().replace(user.getAppType() + "##", ""), user.getPassword()));

        sqlSession.update("Auth.updateAccount", user);

        user.setPassword(null);
        RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "loginId", user.getLoginId(), "1");

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Integer joinWithVerification(JoinUser user, Boolean withVerification) throws ResultCodeException {

        if (user.getAccountType().equals("kakao")) {
            throw new NotPermissionException();
        }

        if (user.getCountry() == null) {
            user.setCountry(new Country());
            user.getCountry().setNo((long) 1);
        }

        if (StringUtils.isEmpty(user.getAppType())) {
            user.setAppType("pplus");
        }

        if (user.getIsVirtual() == null) {
            user.setIsVirtual(false);
        }

        CountryConfig config = commonSvc.getCountryConfig(user.getCountry());

        if (user.getAccountType() == null || user.getAccountType().equals("pplus")) {
            if (!user.getLoginId().replace(user.getAppType() + "##", "").matches("^[a-zA-Z0-9]{4,20}$"))
                throw new NotAllowCharacterException("loginId", "alphabet/number/4~20");
        }


        if (withVerification && (user.getVerification() == null || user.getVerification().getToken() == null))
            throw new InvalidArgumentException("verification", "need token");

        if (StringUtils.isEmpty(user.getNickname()) && StringUtils.isEmpty(user.getName())) {
            user.setName("nonamed");
        }

        user.setMobile(StoreUtil.getValidatePhoneNumber(user.getMobile().replace(user.getAppType() + "##", "")));

        if (user.getEncrypted() != null && user.getEncrypted()) {
            user.setPassword(SecureUtil.decryptMobileNumber(user.getPassword()));
        }

        String ftLinkLoginId = user.getLoginId();
        String ftLinkPassword = user.getPassword();
        String ftLinkMobile = user.getMobile();

        if (!user.getAccountType().equals("pplus")) {
            String nickname = user.getLoginId().split("@")[0];
            user.setNickname(nickname);
        } else {
            user.setNickname(user.getLoginId());
        }

        if (!user.getAppType().equals("pplus")) {
            if (!user.getLoginId().startsWith(user.getAppType() + "##")) {
                user.setLoginId(user.getAppType() + "##" + user.getLoginId());
            }

            if (!user.getMobile().startsWith(user.getAppType() + "##")) {
                user.setMobile(user.getAppType() + "##" + user.getMobile());
            }

        }

        if (!user.getAccountType().equals("pplus")) {
            user.setPassword(user.getAccountType() + "-" + user.getLoginId());
        }

        user.setPassword(SecureUtil.encryptPassword(user.getLoginId().replace(user.getAppType() + "##", ""), user.getPassword()));
        //throw new InvalidArgumentException("name", "need name or nickname");

        Verification v = null;
        if (withVerification) {
            v = verificationSvc.get(user.getVerification());

            if (StringUtils.isNotEmpty(v.getName())) {
                user.setName(v.getName());
            }
            logger.info("verification = { number : " + v.getNumber() + ", mobile : " + v.getMobile() + " }");
            logger.info("user.verification.number : " + user.getVerification().getNumber() + ", user.mobile : " + user.getMobile());
            if (!v.getNumber().equals(user.getVerification().getNumber())
                    || !v.getMobile().equals(user.getMobile().replace(user.getAppType() + "##", "")))
                throw new NotMatchedVerificationException();
        } else {
            user.setUseStatus("normal");
            v = new Verification();
            v.setMedia("sms");
            v.setMobile(user.getMobile().replace(user.getAppType() + "##", ""));
            v.setRegDate(DateUtil.getCurrentDate());
            if (user.getUseStatus().equals("normal")) {
                v.setMedia("sms");
                v.setNumber("111111");
                if (VERIFICATION) {
                    v.setNumber(StoreUtil.generateRandomNumber(CERT_NUM_LENGTH));
                }
                v.setToken(StoreUtil.generateVerificationToken(v.getNumber()));
                sqlSession.insert("Verification.insert", v);
                user.setVerification(v);
            }
        }
        if (!StringUtils.isEmpty(user.getRecommendationCode()) && user.getPage() != null && user.getPage().getDistributorAgentCode() != null) {
            Agent agent = new Agent();
            agent.setCode(user.getRecommendationCode());
            agent = this.getAgentByCode(agent);
            if (agent == null)
                throw new NotFoundTargetException("recommender Agent", "not found");
        } else if (!StringUtils.isEmpty(user.getRecommendationCode())) {

            Agent agent = new Agent();
            agent.setCode(user.getRecommendationCode());
            agent = this.getAgentByCode(agent);
            if (agent != null) {
                user.setAgentCode(agent.getCode());
            } else {
                User recommender = userSvc.getUserByRecommendKey(user.getRecommendationCode());
                if (recommender == null) {
                    throw new NotFoundTargetException("recommender", "not found");
                }

                if (recommender.getAppType().equals("biz")) {
                    user.setRecommendType("page");
                } else {
                    user.setRecommendType("user");
                }

            }
        }


        if (userSvc.existsUserByLoginId(user.getLoginId())) {
            throw new DuplicateLoginIdException();
        }

        if (userSvc.existsUserByMobile(user.getMobile()))
            throw new DuplicateMobileException();

        List<Terms> compulsoryList = null;
        if (StringUtils.isEmpty(user.getAppType())) {
            ParamMap map = new ParamMap();
            map.put("app", user.getApp());
            map.put("url", TERMS_BASEURL);
            map.put("ext", TERMS_EXTENSION);
            compulsoryList = sqlSession.selectList("Auth.getActiveCompulsoryTermsAll", map);
        } else {
            ParamMap map = new ParamMap();
            map.put("appType", user.getAppType());
            map.put("url", TERMS_BASEURL);
            map.put("ext", TERMS_EXTENSION);
            compulsoryList = sqlSession.selectList("Auth.getActiveCompulsoryTermsAllByAppType", map);
        }

        if (compulsoryList != null && compulsoryList.size() > 0) {
            if (user.getTermsList() == null || user.getTermsList().size() == 0)
                throw new NeedAgreeCompulsoryTerms();

            for (Terms terms : compulsoryList) {
                if (!existsTerms(terms, user.getTermsList()))
                    throw new NeedAgreeCompulsoryTerms();
            }
        }

        if (withVerification) {
            user.setMemberType("general");
            user.setUseStatus("normal");
            user.setRestrictionStatus("none");
            if (v.getMedia().equals("external") || (user.getNumber() != null && !StringUtils.isEmpty(user.getNumber().getNumber())))
                user.setCertificationLevel((short) 11);
            else if (v.getMedia().equals("sms") || v.getMedia().equals("email"))
                user.setCertificationLevel((short) 5);
            else
                user.setCertificationLevel((short) 1);
        } else {
            user.setMemberType("general");
            user.setUseStatus("normal");
            user.setRestrictionStatus("none");
            user.setCertificationLevel((short) 5);
//            if (user.getUseStatus().equals("normal")) {
//                user.setCertificationLevel((short) 11);
//            } else { // pending
//                user.setCertificationLevel((short) 1);
//            }
        }
        if (user.getIsVirtual()) {
            user.setMemberType("virtual");
        }

        user.setRecommendKey(getNewRecommendKey());
        if (StringUtils.isEmpty(user.getName()))
            user.setName(user.getNickname());

        if (user.getProperties() == null)
            user.setProperties(new HashMap<String, Object>());

        if (!StringUtils.isEmpty(user.getRecommendationCode()))
            user.getProperties().put("needRecommendMsg", true);

        // mgk_add : 추가 board[member] [
        BulletinBoard board = new BulletinBoard();
        board.setType("member");
        board.setName(user.getLoginId());
        Integer ret = articleSvc.insertBoard(board);
        user.setBoardSeqNo(board.getNo());
        user.setPlusPush(true);
        // mgk_add ]


        int effected = sqlSession.insert("Auth.join", user);
        if (effected > 0) {
            RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "loginId", user.getLoginId(), "1");
        }
        logger.debug("join user: {}", user.getNo());

        MemberKakao memberKakao = sqlSession.selectOne("Auth.getKakaoUserByMobile", user.getMobile());
        if(memberKakao != null){
            if (memberKakao.getPoint() > 0) {
                PointHistory history = new PointHistory();
                history.setMemberSeqNo(user.getNo());
                history.setType("charge");
                history.setPoint(memberKakao.getPoint().floatValue());
                history.setSubject("기존 포인트 복원");
                history.setHistoryProp(new HashMap<String, Object>());
                history.getHistoryProp().put("지급처", "캐시픽 운영팀");
                List<PointHistory> pointHistoryList = new ArrayList<>();
                pointHistoryList.add(history);
                cashBolSvc.increasePointList(pointHistoryList);

                sqlSession.update("Auth.kakaoUserChanged", memberKakao.getSeqNo());

                user.setIsKakao(true);
            }
        }else{
            user.setIsKakao(false);
        }


        if (user.getTermsList() != null && user.getTermsList().size() > 0) {
            ParamMap params = new ParamMap("userNo", user.getNo());
            for (Terms terms : user.getTermsList()) {
                params.put("termsNo", terms.getNo());
                sqlSession.insert("Auth.agreeTerms", params);
            }
        }

        if (withVerification && v != null && v.getMedia().equals("external") && !StringUtils.isEmpty(v.getMobile())) {
            //본인 인증 한 번호를 SMS 발신 번호로 저장한다.
            userSvc.insertUserAuthedNumber(user, v.getMobile());
        }

        PlusGroup plusGroup = new PlusGroup();
        plusGroup.setName("ALL");
        plusGroup.setPriority(100);
        plusGroup.setDefaultGroup(true);

        plusSvc.insertGroup(user, plusGroup);

        if (user.getIsVirtual()) {
            BolHistory history = new BolHistory();
            history.setAmount(100000f);
            history.setUser(user);
            history.setSubject("회원 가입 적립");
            history.setPrimaryType("increase");
            history.setSecondaryType("joinMember");
            history.setTargetType("member");
            history.setTarget(user);
            history.setProperties(new HashMap<String, Object>());
            history.getProperties().put("지급처", "운영팀");
            cashBolSvc.increaseBol(user, history);
        }

        if (user.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
            //        가입자에게 BOL 지급한다.
            if (withVerification && config.getProperties() != null && config.getProperties().containsKey("joinBol")) {
                Float joinBol = ((Integer) config.getProperties().get("joinBol")).floatValue();
                if (joinBol > 0) {
                    BolHistory history = new BolHistory();
                    history.setAmount(joinBol);
                    history.setUser(user);
                    history.setSubject("회원 가입 적립");
                    history.setPrimaryType("increase");
                    history.setSecondaryType("joinMember");
                    history.setTargetType("member");
                    history.setTarget(user);
                    history.setProperties(new HashMap<String, Object>());
                    history.getProperties().put("지급처", "캐시픽 운영팀");
                    cashBolSvc.increaseBol(user, history);
                }
            }

            if (withVerification && config.getProperties() != null && config.getProperties().containsKey("joinPoint")) {
                Float joinPoint = ((Integer) config.getProperties().get("joinPoint")).floatValue();
                if (joinPoint > 0) {
                    PointHistory history = new PointHistory();
                    history.setMemberSeqNo(user.getNo());
                    history.setType("charge");
                    history.setPoint(joinPoint);
                    history.setSubject("회원 가입 적립");
                    history.setHistoryProp(new HashMap<String, Object>());
                    history.getHistoryProp().put("지급처", "캐시픽 운영팀");
                    List<PointHistory> pointHistoryList = new ArrayList<>();
                    pointHistoryList.add(history);
                    cashBolSvc.increasePointList(pointHistoryList);
                }
            }
        } else if (user.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
            //        가입자에게 BOL 지급한다.
            if (withVerification && config.getProperties() != null && config.getProperties().containsKey("joinLuckyPickBol")) {
                Float joinBol = ((Integer) config.getProperties().get("joinLuckyPickBol")).floatValue();
                if (joinBol > 0) {
                    BolHistory history = new BolHistory();
                    history.setAmount(joinBol);
                    history.setUser(user);
                    history.setSubject("회원 가입 적립");
                    history.setPrimaryType("increase");
                    history.setSecondaryType("joinMember");
                    history.setTargetType("member");
                    history.setTarget(user);
                    history.setProperties(new HashMap<String, Object>());
                    history.getProperties().put("지급처", "럭키픽 운영팀");
                    cashBolSvc.increaseBol(user, history);
                }
            }

            if (withVerification && config.getProperties() != null && config.getProperties().containsKey("joinLuckyPickPoint")) {
                Float joinPoint = ((Integer) config.getProperties().get("joinLuckyPickPoint")).floatValue();
                if (joinPoint > 0) {
                    PointHistory history = new PointHistory();
                    history.setMemberSeqNo(user.getNo());
                    history.setType("charge");
                    history.setPoint(joinPoint);
                    history.setSubject("회원 가입 적립");
                    history.setHistoryProp(new HashMap<String, Object>());
                    history.getHistoryProp().put("지급처", "럭키픽 운영팀");
                    List<PointHistory> pointHistoryList = new ArrayList<>();
                    pointHistoryList.add(history);
                    cashBolSvc.increasePointList(pointHistoryList);
                }
            }
        } else if (user.getAppType().equals("pplus")) {
            if (withVerification && config.getProperties() != null && config.getProperties().containsKey("joinPplusBol")) {
                Float joinBol = ((Integer) config.getProperties().get("joinPplusBol")).floatValue();
                if (joinBol > 0) {
                    BolHistory history = new BolHistory();
                    history.setAmount(joinBol);
                    history.setUser(user);
                    history.setSubject("회원 가입 적립");
                    history.setPrimaryType("increase");
                    history.setSecondaryType("joinMember");
                    history.setTargetType("member");
                    history.setTarget(user);
                    history.setProperties(new HashMap<String, Object>());
                    history.getProperties().put("지급처", "오리마켓 운영팀");
                    cashBolSvc.increaseBol(user, history);
                }
            }
        }


        effected = sqlSession.update("Auth.loginSuccessProc", user);
        user.setPassword(null);
        user.setTermsList(null);


        //사용자의 전화번호를 연락처에 가지고 있는 회원과 친구 관계를 맺는다.
        contactSvc.remappingFriendByUser(user, null);

        if (user.getNumber() != null && !StringUtils.isEmpty(user.getNumber().getNumber())) {
            //기본 페이지 생성
            Page page = user.getPage();
            if (page == null) {
                page = new Page();
            }

            if (page.getAgent() == null) {
                Agent agent = sqlSession.selectOne("Auth.getBasicAgent");
                page.setAgent(agent);
                Partnership partnership = partnerShipService.getPartnerShip(agent.getPartnershipCode());
                page.setPoint(partnership.getBenefit());
            }

            if (page.getUser() == null) {
                page.setUser(new User(user.getNo()));
                user.setPage(page);
            }


            // pending 상태는 아직 홍보샵의 정보를 직접 입력하지 않은 상태이다. 이 상태인 경우 로그인은 성공하지만 existsDevice, registDevice는 실패한다.
            // 홍보샵의 정보를 직접 입력한 후에 normal 상태로 변경한다.
            if (StringUtils.isEmpty(page.getStatus())) {
                page.setStatus("peding");
            }


            if (StringUtils.isEmpty(page.getType())) {
                page.setType("shop");
            }


            if (StringUtils.isEmpty(page.getName())) {
                if (!StringUtils.isEmpty(user.getNickname()))
                    page.setName(user.getNickname() + "님의 페이지");
                else
                    page.setName(user.getName() + "님의 페이지");
            }

            if (StringUtils.isEmpty(page.getOpenBound())) {
                page.setOpenBound("everybody");
            }


            page.setTodayViewCount((long) 0);
            page.setTotalViewCount((long) 0);

            if (page.getBlind() == null)
                page.setBlind(false);

            if (StringUtils.isEmpty(page.getTalkRecvBound()))
                page.setTalkRecvBound("everybody");

            if (page.getCooperation() == null) {
                page.setCooperation(new Cooperation());
                page.getCooperation().setNo((long) 1);
            }
            page.setVirtualPage(false);

            pageSvc.insert(page, ftLinkLoginId, ftLinkPassword, ftLinkMobile);

            if (user.getNumber().getReserved() == null)
                user.getNumber().setReserved(false);

            if (user.getNumber().getDeleted() == null)
                user.getNumber().setDeleted(false);

            // 가상 번호 매핑
//			numSvc.allocateVirtualNumberToPage(user, user.getPage(), user.getNumber(), StoreUtil.getDefaultAllocateNumberDuration());

            //고객 기본 그룹 생성
            CustomerGroup group = new CustomerGroup();
            group.setDefaultGroup(true);
            group.setName("ALL");
            group.setPriority(100);
            group.setPage(page);
            custSvc.insertGroup(group);

            //custSvc.updateTargetsByUser(user);

            user.setPage(pageSvc.getPageByUser(user));
        }


        user.setNumber(null);
        return Const.E_SUCCESS;
    }


    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Integer join(JoinUser user) throws ResultCodeException {
        return joinWithVerification(user, true);
    }


    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public Integer levelup(JoinUser user) throws ResultCodeException {
        if (user.getNo() == null)
            throw new InvalidArgumentException("no", "need no.");

        User savedUser = userSvc.getUser(user.getNo());
        if (savedUser == null)
            throw new NotFoundTargetException("user", "not found");

        if (user.getWoodongyi() != null && user.getWoodongyi()) {
            throw new InvalidArgumentException("woodongyi", "woodongyi user can not levelup");
        }

        if (StringUtils.isEmpty(user.getName()))
            throw new InvalidArgumentException("name", "need name.");

//		if (user.getVerification() == null || user.getVerification().getToken() == null)
//			throw new InvalidArgumentException("verification", "need token");

        if (user.getNumber() == null || StringUtils.isEmpty(user.getNumber().getNumber()))
            throw new InvalidArgumentException("number", "need number");

        Page savedPage = pageSvc.getPageByUser(user);
        if (savedPage != null)
            throw new AlreadyExistsException("page", "already exists.");

        Verification v = verificationSvc.get(user.getVerification());
        if (!v.getNumber().equals(user.getVerification().getNumber())
                || StringUtils.isEmpty(v.getMobile()))
            throw new NotMatchedVerificationException();

        user.setMobile(v.getMobile());
        if (!StringUtils.isEmpty(user.getLoginId()))
            savedUser.setLoginId(user.getLoginId());

        if (!StringUtils.isEmpty(user.getName()))
            savedUser.setName(user.getName());

        if (!StringUtils.isEmpty(user.getGender()))
            savedUser.setGender(user.getGender());

        if (!StringUtils.isEmpty(user.getBirthday()))
            savedUser.setBirthday(user.getBirthday());

        if (user.getMarried() != null)
            savedUser.setMarried(user.getMarried());

        if (user.getHasChild() != null)
            savedUser.setHasChild(user.getHasChild());

        if (user.getPage() != null)
            savedUser.setPage(user.getPage());


        List<Terms> compulsoryList = null;
        if (StringUtils.isEmpty(user.getAppType())) {
            ParamMap map = new ParamMap();
            map.put("app", user.getApp());
            map.put("url", TERMS_BASEURL);
            map.put("ext", TERMS_EXTENSION);
            compulsoryList = sqlSession.selectList("Auth.getActiveCompulsoryTermsAll", map);
        } else {
            ParamMap map = new ParamMap();
            map.put("appType", user.getAppType());
            map.put("url", TERMS_BASEURL);
            map.put("ext", TERMS_EXTENSION);
            compulsoryList = sqlSession.selectList("Auth.getActiveCompulsoryTermsAllByAppType", map);
        }
        if (compulsoryList != null && compulsoryList.size() > 0) {
            if (user.getTermsList() == null || user.getTermsList().size() == 0)
                throw new NeedAgreeCompulsoryTerms();

            for (Terms terms : compulsoryList) {
                if (!existsTerms(terms, user.getTermsList()))
                    throw new NeedAgreeCompulsoryTerms();
            }
        }

        if (user.getTermsList() != null && user.getTermsList().size() > 0) {
            ParamMap params = new ParamMap("userNo", user.getNo());
            for (Terms terms : user.getTermsList()) {
                params.put("termsNo", terms.getNo());
                sqlSession.insert("Auth.agreeTerms", params);
            }
        }

//		if (v.getMedia().equals("external") && !StringUtils.isEmpty(v.getMobile())) {
//			//본인 인증 한 번호를 SMS 발신 번호로 저장한다.
//			if (userSvc.existsUserAuthedNumber(user, user.getMobile()) == false)
//				userSvc.insertUserAuthedNumber(user, v.getMobile());
//		}

        user.setPassword(null);
        user.setTermsList(null);


        //사용자의 전화번호를 연락처에 가지고 있는 회원과 친구 관계를 맺는다.
        contactSvc.remappingFriendByUser(user, savedUser.getMobile());

        savedUser.setMobile(user.getMobile());
        userSvc.update(savedUser);

        //기본 페이지 생성
        Page page = savedUser.getPage();
        if (page == null) {
            page = new Page();
        }

        if (page.getUser() == null) {
            page.setUser(new User(savedUser.getNo()));
            savedUser.setPage(page);
        }


        // pending 상태는 아직 홍보샵의 정보를 직접 입력하지 않은 상태이다. 이 상태인 경우 로그인은 성공하지만 existsDevice, registDevice는 실패한다.
        // 홍보샵의 정보를 직접 입력한 후에 normal 상태로 변경한다.
        if (StringUtils.isEmpty(page.getStatus()))
            page.setStatus("pending");

        if (StringUtils.isEmpty(page.getType()))
            page.setType("store");

        if (StringUtils.isEmpty(page.getName())) {
            if (!StringUtils.isEmpty(savedUser.getNickname()))
                page.setName(savedUser.getNickname() + "님의 페이지");
            else
                page.setName(savedUser.getName() + "님의 페이지");
        }

        if (StringUtils.isEmpty(page.getOpenBound()))
            page.setOpenBound("everybody");

        page.setTodayViewCount((long) 0);
        page.setTotalViewCount((long) 0);

        if (page.getBlind() == null)
            page.setBlind(false);

        if (StringUtils.isEmpty(page.getTalkRecvBound()))
            page.setTalkRecvBound("everybody");

        if (page.getCooperation() == null) {
            page.setCooperation(new Cooperation());
            page.getCooperation().setNo((long) 1);
        }
        page.setVirtualPage(false);

        pageSvc.insert(page, null, null, null);

        if (user.getNumber().getReserved() == null)
            user.getNumber().setReserved(false);

        if (user.getNumber().getDeleted() == null)
            user.getNumber().setDeleted(false);

        // 가상 번호 매핑
//		numSvc.allocateVirtualNumberToPage(savedUser, savedUser.getPage(), user.getNumber(), StoreUtil.getDefaultAllocateNumberDuration());

        //고객 기본 그룹 생성
        CustomerGroup group = new CustomerGroup();
        group.setDefaultGroup(true);
        group.setName("ALL");
        group.setPriority(100);
        group.setPage(page);
        custSvc.insertGroup(group);

        //custSvc.updateTargetsByUser(user);
        if (savedUser.getCertificationLevel() < 11) {
            savedUser.setCertificationLevel((short) 11);
            userSvc.updateCertificationLevel(savedUser);
        }

        user.setPage(pageSvc.getPageByUser(savedUser));
        user.setNumber(null);
        return Const.E_SUCCESS;
    }

    private void verificationSns(User user) throws InvalidSnsVerificationException {
        //TODO. accoutType, loginId, properties.snsToken 값을 이용해서 SNS 계정에 대한 검증을 실시한다.

        return;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void joinSuccessProc(User user) throws ResultCodeException {

        giveBolOrLottoTicketByRecommendationCode(user);

        putJoinInviteMsg(user);
    }

    private void giveBolOrLottoTicketByRecommendationCode(User user) throws ResultCodeException {
        //추천인이 존재하면 추천인을 찾아서 BOL 지급
        //추천인을 통해 가입한 현재 사용자에게도 BOL 지급

        if (StringUtils.isEmpty(user.getRecommendationCode()))
            return;

        //ToDo [ 로또 기능 ios 추가후 삭제해야 하는 임시 코드
        User recommender = userSvc.getUserByRecommendKey(user.getRecommendationCode());

        Country country = null;
        if (user.getCountry() != null && user.getCountry().getNo() != null) {
            country = user.getCountry();
        } else {
            country = new Country();
            country.setNo(1L);
        }

        CountryConfig config = commonSvc.getCountryConfig(country);
        if (config.getProperties() == null)
            return;


        if (recommender != null) {

            //추천한 사람에게 BOL 지급
            if (recommender.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {

//                Calendar calendar = Calendar.getInstance();
//                calendar.set(Calendar.YEAR, 2023);
//                calendar.set(Calendar.MONTH, 6);
//                calendar.set(Calendar.DAY_OF_MONTH, 17);
//                calendar.set(Calendar.HOUR_OF_DAY, 0);
//                calendar.set(Calendar.MINUTE, 0);
//                calendar.set(Calendar.SECOND, 0);
//
//                if(System.currentTimeMillis() < calendar.getTimeInMillis()){
//                    int result = userSvc.increaseRecommendCount(recommender.getNo());
//                    if(result > 0){
//                        recommender = userSvc.getUser(recommender.getNo());
//                        if(recommender.getRecommendCount() > 0 && recommender.getRecommendCount() % 10 == 0){
//                            InviteReward inviteReward = new InviteReward();
//                            inviteReward.setMemberSeqNo(recommender.getNo());
//                            inviteReward.setStatus("before");
//                            userSvc.insertInviteReward(inviteReward);
//                        }
//                    }
//                }



                Float recommendBol = ((Integer) config.getProperties().get("recommendBol")).floatValue();
                if (recommendBol != null && recommendBol > 0) {

                    kr.co.pplus.store.api.jpa.model.BolHistory bolHistory = new kr.co.pplus.store.api.jpa.model.BolHistory();
                    bolHistory.setAmount(recommendBol);
                    bolHistory.setMemberSeqNo(recommender.getNo());
                    bolHistory.setSubject("친구 초대");
                    bolHistory.setPrimaryType("increase");
                    bolHistory.setSecondaryType("invite");
                    bolHistory.setTargetType("member");
                    bolHistory.setTargetSeqNo(recommender.getNo());
                    bolHistory.setHistoryProp(new HashMap<String, Object>());
                    bolHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");

                    bolService.increaseBol(recommender.getNo(), bolHistory);

                    NotificationBox notificationBox = new NotificationBox();
                    notificationBox.setSubject("친구초대");
                    notificationBox.setContents("친구 초대로 럭키볼을 적립해드립니다.");

                    notificationBox.setMemberSeqNo(recommender.getNo());
                    notificationBox.setMoveSeqNo(bolHistory.getSeqNo());
                    notificationBox.setMoveType1("inner");
                    notificationBox.setMoveType2("bolDetail");
                    notificationBoxService.save(notificationBox);

                    Map<String, String> data = new HashMap<String, String>();
                    data.put("title", "친구 초대");
                    data.put("contents", "친구 초대로 럭키볼을 적립해드립니다.");

                    data.put("move_type1", "inner");
                    data.put("move_type2", "bolDetail");
                    data.put("move_target", String.valueOf(bolHistory.getSeqNo()));

                    queueSvc.sendPush(recommender.getNo(), data, recommender.getAppType());

                }


                Float recommendPoint = ((Integer) config.getProperties().get("recommendPoint")).floatValue();

                if (recommendPoint != null && recommendPoint > 0) {


                    kr.co.pplus.store.api.jpa.model.PointHistory pointHistory = new kr.co.pplus.store.api.jpa.model.PointHistory();
                    pointHistory.setMemberSeqNo(recommender.getNo());
                    pointHistory.setType("charge");
                    pointHistory.setPoint(recommendPoint);
                    pointHistory.setSubject("친구 초대");
                    pointHistory.setHistoryProp(new HashMap<>());
                    pointHistory.getHistoryProp().put("지급처", "캐시픽 운영팀");
                    pointService.updatePoint(recommender.getNo(), pointHistory);

                    NotificationBox notificationBox = new NotificationBox();
                    notificationBox.setSubject("친구초대");
                    notificationBox.setContents("친구 초대로 캐시를 적립해드립니다.");
                    notificationBox.setMemberSeqNo(recommender.getNo());
                    notificationBox.setMoveSeqNo(pointHistory.getSeqNo());
                    notificationBox.setMoveType1("inner");
                    notificationBox.setMoveType2("pointHistory");
                    notificationBoxService.save(notificationBox);

                    Map<String, String> data = new HashMap<String, String>();
                    data.put("title", "친구 초대");
                    data.put("contents", "친구 초대로 캐시를 적립해드립니다.");
                    data.put("move_type1", "inner");
                    data.put("move_type2", "pointHistory");
                    data.put("move_target", String.valueOf(pointHistory.getSeqNo()));

                    queueSvc.sendPush(recommender.getNo(), data, recommender.getAppType());
                }

                Integer recommendLotto = (Integer) config.getProperties().get("recommendLotto");

                if (recommendLotto != null && recommendLotto > 0) {

                    try {
                        Lottery lottery = lotteryService.getLottery();
                        if(lottery != null){
                            lotteryService.joinLottery(recommender.getNo(), lottery.getSeqNo(), recommendLotto, "invite");
                        }
                    }catch (Exception e){
                        logger.error(e.toString());
                    }



                    NotificationBox notificationBox = new NotificationBox();
                    notificationBox.setSubject("친구초대");
                    notificationBox.setContents("친구 초대로 캐시로또 "+recommendLotto+"장 응모");
                    notificationBox.setMemberSeqNo(recommender.getNo());
                    notificationBox.setMoveType1("inner");
                    notificationBox.setMoveType2("lottoJoin");
                    notificationBoxService.save(notificationBox);

                    Map<String, String> data = new HashMap<String, String>();
                    data.put("title", "친구 초대");
                    data.put("contents", "친구 초대로 캐시로또 "+recommendLotto+"장 응모");
                    data.put("move_type1", "inner");
                    data.put("move_type2", "lottoJoin");

                    queueSvc.sendPush(recommender.getNo(), data, recommender.getAppType());
                }

            }else if (recommender.getAppType().equals(Const.APP_TYPE_LUCKYPICK)) {
                Float recommendBol = ((Integer) config.getProperties().get("recommendLuckyPickBol")).floatValue();
                if (recommendBol != null && recommendBol > 0) {

                    kr.co.pplus.store.api.jpa.model.BolHistory bolHistory = new kr.co.pplus.store.api.jpa.model.BolHistory();
                    bolHistory.setAmount(recommendBol);
                    bolHistory.setMemberSeqNo(recommender.getNo());
                    bolHistory.setSubject("친구 초대");
                    bolHistory.setPrimaryType("increase");
                    bolHistory.setSecondaryType("invite");
                    bolHistory.setTargetType("member");
                    bolHistory.setTargetSeqNo(recommender.getNo());
                    bolHistory.setHistoryProp(new HashMap<String, Object>());
                    bolHistory.getHistoryProp().put("지급처", "럭키픽 운영팀");

                    bolService.increaseBol(recommender.getNo(), bolHistory);

                    NotificationBox notificationBox = new NotificationBox();
                    notificationBox.setSubject("친구초대");
                    notificationBox.setContents("친구 초대로 럭키볼을 적립해드립니다.");

                    notificationBox.setMemberSeqNo(recommender.getNo());
                    notificationBox.setMoveSeqNo(bolHistory.getSeqNo());
                    notificationBox.setMoveType1("inner");
                    notificationBox.setMoveType2("bolDetail");
                    notificationBoxService.save(notificationBox);

                    Map<String, String> data = new HashMap<String, String>();
                    data.put("title", "친구 초대");
                    data.put("contents", "친구 초대로 럭키볼을 적립해드립니다.");

                    data.put("move_type1", "inner");
                    data.put("move_type2", "bolDetail");
                    data.put("move_target", String.valueOf(bolHistory.getSeqNo()));

                    queueSvc.sendPush(recommender.getNo(), data, recommender.getAppType());

                }


                Float recommendPoint = ((Integer) config.getProperties().get("recommendLuckyPickPoint")).floatValue();

                if (recommendPoint != null && recommendPoint > 0) {


                    kr.co.pplus.store.api.jpa.model.PointHistory pointHistory = new kr.co.pplus.store.api.jpa.model.PointHistory();
                    pointHistory.setMemberSeqNo(recommender.getNo());
                    pointHistory.setType("charge");
                    pointHistory.setPoint(recommendPoint);
                    pointHistory.setSubject("친구 초대");
                    pointHistory.setHistoryProp(new HashMap<>());
                    pointHistory.getHistoryProp().put("지급처", "럭키픽 운영팀");
                    pointService.updatePoint(recommender.getNo(), pointHistory);

                    NotificationBox notificationBox = new NotificationBox();
                    notificationBox.setSubject("친구초대");
                    notificationBox.setContents("친구 초대로 캐시를 적립해드립니다.");
                    notificationBox.setMemberSeqNo(recommender.getNo());
                    notificationBox.setMoveSeqNo(pointHistory.getSeqNo());
                    notificationBox.setMoveType1("inner");
                    notificationBox.setMoveType2("pointHistory");
                    notificationBoxService.save(notificationBox);

                    Map<String, String> data = new HashMap<String, String>();
                    data.put("title", "친구 초대");
                    data.put("contents", "친구 초대로 캐시를 적립해드립니다.");
                    data.put("move_type1", "inner");
                    data.put("move_type2", "pointHistory");
                    data.put("move_target", String.valueOf(pointHistory.getSeqNo()));

                    queueSvc.sendPush(recommender.getNo(), data, recommender.getAppType());
                }
            }


        }
    }

    private void putJoinInviteMsg(User user) {

        //TODO. 확인 후 이 조건 삭제
        if (user != null)
            return;

        if (user.getProperties() == null || !user.getProperties().containsKey("needRecommendMsg"))
            return;

        if (pageSvc.existsPendingPage(user))
            return;

        //TODO. 초대한 사람에게 메시지 넣어준다.
        if (StringUtils.isEmpty(user.getRecommendationCode()))
            return;


    }

    private String getNewRecommendKey() {
        String newVal = StoreUtil.getRandomString(RECOMMEND_CHARS, 6);
        if (userSvc.existsUesrByRecommendKey(newVal)) {
            return getNewRecommendKey();
        } else {
            Agent agent = new Agent();
            agent.setCode(newVal);
            agent = this.getAgentByCode(agent);
            if (agent != null)
                return getNewRecommendKey();
            else
                return newVal;
        }
    }

    private boolean existsTerms(Terms src, List<Terms> destList) {
        if (destList == null && destList.size() == 0)
            return false;

        boolean exists = false;
        for (Terms dest : destList) {
            logger.debug("src.no: {}, dest.no: {}", src.getNo(), dest.getNo());
            if (src.getNo().equals(dest.getNo())) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    public User getUserByLoginIdAndMobile(User user) throws ResultCodeException {

        if (StringUtils.isNotEmpty(user.getAppType()) && !user.getAppType().equals("pplus")) {
            user.setLoginId(user.getAppType() + "##" + user.getLoginId());
            user.setMobile(user.getAppType() + "##" + user.getMobile());
        }

        User saved = sqlSession.selectOne("Auth.getUserByLoginIdAndMobile", user);
        if (saved == null)
            throw new NotMatchUserException();

        saved.setPassword(null);
        saved.setPage(pageSvc.getPageByUser(saved));
        return saved;
    }


    public Map<String, Object> login(User user, Boolean encrypted) {

        User existsUser = null;

        if (user.getAccountType() != null && user.getAccountType().equals("naver") && !AppUtil.isEmpty(user.getEmail())) {


            if (!user.getLoginId().startsWith(user.getAppType() + "##")) {
                user.setLoginId(user.getAppType() + "##" + user.getLoginId());
            }

            if (!user.getEmail().startsWith(user.getAppType() + "##")) {
                user.setEmail(user.getAppType() + "##" + user.getEmail());
            }

            String encryptedPwd = SecureUtil.encryptPassword(user.getLoginId().replace(user.getAppType() + "##", ""), SecureUtil.decryptMobileNumber(user.getPassword()));
            user.setPassword(encryptedPwd);
            sqlSession.update("Auth.updateNaverAccount", user);

            RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "loginId", user.getLoginId(), "1");

        } else {

            String pwd = null;
            if (encrypted != null && encrypted) {
                pwd = SecureUtil.decryptMobileNumber(user.getPassword());
            } else {
                pwd = user.getPassword();
            }

            if (StringUtils.isEmpty(user.getAppType())) {
                user.setAppType("pplus");
            }

            if (!user.getAppType().equals("pplus") && !user.getLoginId().startsWith(user.getAppType() + "##")) {
                user.setLoginId(user.getAppType() + "##" + user.getLoginId());
            }

            String encryptedPwd = SecureUtil.encryptPassword(user.getLoginId().replace(user.getAppType() + "##", ""), pwd);
            user.setPassword(encryptedPwd);

        }

        existsUser = sqlSession.selectOne("Auth.getUserByLoginInfo", user);
        if (existsUser == null) {
            existsUser = sqlSession.selectOne("Auth.getUserByLoginId", user);
            if (existsUser != null) {

                sqlSession.update("Auth.loginFailProc", existsUser);

                existsUser = sqlSession.selectOne("Auth.getUserByNo", existsUser);
                existsUser.setNo(null);
                existsUser.setPassword(null);
                Map<String, Object> err = new HashMap<String, Object>();
                err.put("resultCode", Const.E_NOTMATCHEDPWD);
                err.put("row", existsUser);
                return err;

            }
        }

        if (existsUser == null) {
            Map<String, Object> err = new HashMap<String, Object>();
            err.put("resultCode", Const.E_NOTFOUND);
            return err;
        }

        sqlSession.update("Auth.loginSuccessProc", existsUser);
        existsUser = sqlSession.selectOne("Auth.getUserByNo", existsUser);

        existsUser.setPassword(null);

        if (!existsUser.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
            existsUser.setPage(pageSvc.getPageByUser(existsUser));

            if (existsUser.getAppType().equals("biz") && existsUser.getPage() != null) {
                if (!pageSvc.existPageSalesType(existsUser.getPage().getNo())) {
                    List<PageSalesType> pageSalesTypeList = new ArrayList<>();
                    if (existsUser.getPage().getStoreType().equals(StoreType.online.name())) {
                        PageSalesType pageSalesType = new PageSalesType();
                        pageSalesType.setPageSeqNo(existsUser.getPage().getNo());
                        pageSalesType.setSalesTypeSeqNo(SalesType.SHIPPING.getType());
                        pageSalesTypeList.add(pageSalesType);
                    } else if (existsUser.getPage().getStoreType().equals(StoreType.personal.name())) {
                        PageSalesType pageSalesType = new PageSalesType();
                        pageSalesType.setPageSeqNo(existsUser.getPage().getNo());
                        pageSalesType.setSalesTypeSeqNo(SalesType.SHIPPING.getType());
                        pageSalesTypeList.add(pageSalesType);

                        pageSalesType = new PageSalesType();
                        pageSalesType.setPageSeqNo(existsUser.getPage().getNo());
                        pageSalesType.setSalesTypeSeqNo(SalesType.RESERVATION.getType());
                        pageSalesTypeList.add(pageSalesType);
                    } else if (existsUser.getPage().getStoreType().equals(StoreType.offline.name())) {
                        PageSalesType pageSalesType = new PageSalesType();
                        pageSalesType.setPageSeqNo(existsUser.getPage().getNo());
                        pageSalesType.setSalesTypeSeqNo(SalesType.STORE.getType());
                        pageSalesTypeList.add(pageSalesType);

                        pageSalesType = new PageSalesType();
                        pageSalesType.setPageSeqNo(existsUser.getPage().getNo());
                        pageSalesType.setSalesTypeSeqNo(SalesType.DELIVERY.getType());
                        pageSalesTypeList.add(pageSalesType);

                        pageSalesType = new PageSalesType();
                        pageSalesType.setPageSeqNo(existsUser.getPage().getNo());
                        pageSalesType.setSalesTypeSeqNo(SalesType.SHIPPING.getType());
                        pageSalesTypeList.add(pageSalesType);

                        pageSalesType = new PageSalesType();
                        pageSalesType.setPageSeqNo(existsUser.getPage().getNo());
                        pageSalesType.setSalesTypeSeqNo(SalesType.RESERVATION.getType());
                        pageSalesTypeList.add(pageSalesType);

                        pageSalesType = new PageSalesType();
                        pageSalesType.setPageSeqNo(existsUser.getPage().getNo());
                        pageSalesType.setSalesTypeSeqNo(SalesType.PICKUP.getType());
                        pageSalesTypeList.add(pageSalesType);
                    }

                    for (PageSalesType pageSalesType : pageSalesTypeList) {
                        pageSvc.insertPageSalesType(pageSalesType);
                    }
                }
            }
        }


        //로그인 후 처리..각종 통계 등등등
        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("resultCode", Const.E_SUCCESS);
        ret.put("row", existsUser);
        return ret;
    }

    public Map<String, Object> existsDevice(UserDevice device) throws ResultCodeException {
        if (device == null
                || device.getDevice() == null
                || device.getDevice().getDeviceId() == null
                || device.getDevice().getInstalledApp() == null
                || device.getDevice().getInstalledApp().getVersion() == null
                || device.getDevice().getInstalledApp().getAppKey() == null) {
            throw new InvalidArgumentException();
        }

        // 앱이 사용 가능한지 우선 검사한다.
        AppVersion saved = getAppVersion(device.getDevice().getInstalledApp());
        if (saved == null)
            throw new NotFoundTargetException("appversion", "not found");

        if (needAppUpdate(saved))
            throw new NeedAppUpdateException(
                    "currentVersion", saved.getVersion()
                    , "lastVersion", saved.getVersionProp().get("lastVersion")
                    , "mustUpdate", saved.getVersionProp().get("mustUpdate"));

        Session exists = sqlSession.selectOne("Auth.getUserDeviceByDeviceId", device);
        if (exists == null) {
            throw new NotFoundTargetException("userdevice", "not found");
        }

        if (exists.getAppType().equals(Const.APP_TYPE_USER)) {
            exists.setAppType("pplus");
        }
        if (!exists.getNo().equals(device.getNo())) {
            Map<String, Object> err = new HashMap<String, Object>();
            err.put("resultCode", Const.E_NOTMATCHEDUSER);
            err.put("row", exists);
            return err;
        }
		
		/*if (!pageSvc.existsNormalPage(device)) {
			throw new InvalidPageException();
		}*/

        //exists에 세션키를 할당하고 Redis에 저장한다.
        registSession(exists);
        registRefreshKey(exists);

        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("resultCode", Const.E_SUCCESS);
        ret.put("row", exists);
        return ret;
    }

    private boolean needAppUpdate(AppVersion appVersion) {
        if (appVersion.getVersionProp() == null
                || !appVersion.getVersionProp().containsKey("lastVersion")
                || !appVersion.getVersionProp().containsKey("mustUpdate")
                || !appVersion.getVersionProp().containsKey("downloadUrl")
        )
            return false;

        boolean needUpdate = false;
        String lastVersion = (String) appVersion.getVersionProp().get("lastVersion");
        boolean mustUpdate = (Boolean) appVersion.getVersionProp().get("mustUpdate");

        if (!lastVersion.equals(appVersion.getVersion())) {
            needUpdate = mustUpdate;
        }

        return needUpdate;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updatePushKey(Session session, String pushKey) throws ResultCodeException {
        if (session == null || session.getNo() == null
                || session.getDevice() == null || session.getDevice().getDeviceId() == null
                || session.getDevice().getInstalledApp() == null
                || session.getDevice().getInstalledApp().getVersion() == null
                || session.getDevice().getInstalledApp().getAppKey() == null) {
            throw new InvalidArgumentException();
        }
        session.getDevice().getInstalledApp().setPushKey(pushKey);
        sqlSession.update("Auth.updateInstalledApp", session.getDevice());
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Map<String, Object> registDevice(Session session) throws ResultCodeException {
        if (session == null || session.getNo() == null
                || session.getDevice() == null || session.getDevice().getDeviceId() == null
                || session.getDevice().getInstalledApp() == null
                || session.getDevice().getInstalledApp().getVersion() == null
                || session.getDevice().getInstalledApp().getAppKey() == null) {
            throw new InvalidArgumentException();
        }

        // 앱이 사용 가능한지 우선 검사한다.
        AppVersion saved = getAppVersion(session.getDevice().getInstalledApp());
        if (saved == null)
            throw new NotFoundTargetException("appversion", "not found");

        if (needAppUpdate(saved))
            throw new NeedAppUpdateException(
                    "currentVersion", saved.getVersion()
                    , "lastVersion", saved.getVersionProp().get("lastVersion")
                    , "mustUpdate", saved.getVersionProp().get("mustUpdate"));

        User existsUser = sqlSession.selectOne("Auth.getUserByNo", session);
        if (existsUser == null) {
            throw new NotFoundTargetException();
        }
		
		/*if (!pageSvc.existsNormalPage(session)) {
			throw new InvalidPageException();
		}*/

        Device device = session.getDevice();
        device.setOwner(new User());
        device.getOwner().setNo(session.getNo());
        if (device.getInstalledApp().getPushActivate() == null)
            device.getInstalledApp().setPushActivate(true);

        if (device.getInstalledApp().getPushMask() == null)
            device.getInstalledApp().setPushMask("1111111111111111");

        Device existsDevice = sqlSession.selectOne("Auth.getDeviceById", device);
        boolean deleteAllApp = false;
        if (existsDevice != null) {
            device.setNo(existsDevice.getNo());

            if (!existsDevice.getOwner().getNo().equals(session.getNo())) {
                //TODO. 기존 사용자에서 다른 사용자로 변경된 경우이다. 이 경우에는 기존 이 단말과 연결되어 있는 모든 installedApp 정보를 삭제해야 한다. 왜냐하면 다른 사용자니까.
                existsDevice.setInstalledApp(device.getInstalledApp());
                sqlSession.delete("Auth.deleteInstalledAppByDevice", existsDevice);
                deleteAllApp = true;
            }
        }

        if (device.getNo() == null) {
            sqlSession.insert("Auth.insertDevice", device);
            sqlSession.insert("Auth.insertInstalledApp", device);
        } else {
            sqlSession.update("Auth.updateDevice", device);
            if (deleteAllApp == true || ((Integer) sqlSession.selectOne("Auth.existsInstalledApp", device)) == 0)
                sqlSession.insert("Auth.insertInstalledApp", device);
            else
                sqlSession.update("Auth.updateInstalledApp", device);
        }

        Session read = sqlSession.selectOne("Auth.getUserDeviceByDeviceId", session);
        if (read.getAppType().equals(Const.APP_TYPE_USER)) {
            read.setAppType("pplus");
        }
        registSession(read);
        registRefreshKey(read);

        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("resultCode", Const.E_SUCCESS);
        ret.put("row", read);
        return ret;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Map<String, Object> registDeviceNotUser(Device device) throws ResultCodeException {
        if (device == null || device.getDeviceId() == null
                || device.getInstalledApp() == null
                || device.getInstalledApp().getVersion() == null
                || device.getInstalledApp().getAppKey() == null) {
            throw new InvalidArgumentException();
        }

        // 앱이 사용 가능한지 우선 검사한다.
        AppVersion saved = getAppVersion(device.getInstalledApp());
        if (saved == null)
            throw new NotFoundTargetException("appversion", "not found");

        if (needAppUpdate(saved))
            throw new NeedAppUpdateException(
                    "currentVersion", saved.getVersion()
                    , "lastVersion", saved.getVersionProp().get("lastVersion")
                    , "mustUpdate", saved.getVersionProp().get("mustUpdate"));


		/*if (!pageSvc.existsNormalPage(session)) {
			throw new InvalidPageException();
		}*/

        if (device.getInstalledApp().getPushActivate() == null)
            device.getInstalledApp().setPushActivate(true);

        if (device.getInstalledApp().getPushMask() == null)
            device.getInstalledApp().setPushMask("1111111111111111");

        Device existsDevice = sqlSession.selectOne("Auth.getDeviceByDeviceId", device);
        boolean deleteAllApp = false;
        if (existsDevice != null) {
            device.setNo(existsDevice.getNo());
        }

        if (device.getNo() == null) {
            sqlSession.insert("Auth.insertDevice", device);
            sqlSession.insert("Auth.insertInstalledApp", device);
        } else {
            sqlSession.update("Auth.updateDevice", device);
            if (deleteAllApp == true || ((Integer) sqlSession.selectOne("Auth.existsInstalledApp", device)) == 0)
                sqlSession.insert("Auth.insertInstalledApp", device);
            else
                sqlSession.update("Auth.updateInstalledApp", device);
        }

        Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("resultCode", Const.E_SUCCESS);
        return ret;
    }

    public User getUserByLoginId(User user) {

        if (StringUtils.isNotEmpty(user.getAppType()) && !user.getAppType().equals("pplus") && !user.getLoginId().startsWith(user.getAppType() + "##")) {
            user.setLoginId(user.getAppType() + "##" + user.getLoginId());
        }

        return sqlSession.selectOne("Auth.getUserByLoginId", user);
    }

    public List<Terms> getActiveTermsAll(App app) {
        ParamMap map = new ParamMap();
        map.put("app", app);
        map.put("url", TERMS_BASEURL);
        map.put("ext", TERMS_EXTENSION);
        return sqlSession.selectList("Auth.getActiveTermsAll", map);
    }

    public List<Terms> getNotSignedActiveTermsAll(User user, App app) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("app", app);
        map.put("url", TERMS_BASEURL);
        map.put("ext", TERMS_EXTENSION);
        return sqlSession.selectList("Auth.getNotSignedActiveTermsAll", map);
    }

    public List<Terms> getActiveTermsAllByAppType(String appType, String type) {
        ParamMap map = new ParamMap();
        map.put("appType", appType);
        map.put("type", type);
        map.put("url", TERMS_BASEURL);
        map.put("ext", TERMS_EXTENSION);
        return sqlSession.selectList("Auth.getActiveTermsAllByAppType", map);
    }

    public List<Terms> getNotSignedActiveTermsAllByAppType(User user, String appType) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("appType", appType);
        map.put("url", TERMS_BASEURL);
        map.put("ext", TERMS_EXTENSION);
        return sqlSession.selectList("Auth.getNotSignedActiveTermsAllByAppType", map);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int agreeTerms(User user, Terms terms) {
        logger.debug("user.seqNo : ", user.getNo());
        logger.debug("terms.seqNo : ", terms.getNo());
        int effected = sqlSession.insert("Auth.agreeTerms", new ParamMap("userNo", user.getNo(), "termsNo", terms.getNo()));
        return effected > 0 ? Const.E_SUCCESS : Const.E_NOTFOUND;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int agreeTermsList(User user, List<Terms> termsList) {
        for (Terms terms : termsList) {
            agreeTerms(user, terms);
        }
        return Const.E_SUCCESS;
    }

    public Agent getAgentByCode(Agent agent) {
        return sqlSession.selectOne("Auth.getAgentByCode", agent);
    }

    public Integer activatePage(JoinUser user) throws ResultCodeException {
        Page saved = pageSvc.getPageWithUser(user.getPage());
        if (saved == null)
            throw new InvalidPageException();

        if (saved.getUser() == null)
            throw new NotFoundTargetException();

        if (!saved.getUser().getNo().equals(user.getNo()))
            throw new NotPermissionException();

        user.getPage().setUser(new User(user.getNo()));
        user.getPage().setStatus("normal");
        if (user.getPage().getProperties() == null)
            user.getPage().setProperties(new HashMap<String, Object>());

        if (user.getPage().getCooperation() == null) {
            if (saved.getCooperation() != null)
                user.getPage().setCooperation(saved.getCooperation());
            else
                user.getPage().setCooperation(new Cooperation());
        }


        if (user.getPage().getCooperation().getNo() == null)
            user.getPage().getCooperation().setNo(1L);


        //페이지 개설에 대해 친구들에게 알림이 필요함을 뜻한다. 하루 단위의 스케쥴러에서 처리한다.
        user.getPage().getProperties().put("needOpenMsg", true);

        int effected = pageSvc.activate(user.getPage());
		
		/*if (effected > 0) {
			UserRequest req = new UserRequest();
			req.setUser(user);
			req.setType("pageApproval");
			req.setProperties(new HashMap<String, Object>());
			req.getProperties().put("pageNo", user.getPage().getNo());
			req.setStatus("request");
			commonSvc.insertUserRequest(user, req);
		}*/
        if (effected > 0) {
            Country country = null;
            if (user.getCountry() != null && user.getCountry().getNo() != null) {
                country = user.getCountry();
            } else {
                country = new Country();
                country.setNo(1L);
            }

            CountryConfig config = commonSvc.getCountryConfig(country);
            Lotto lotto = lottoRepository.findBySeqNo(1L);
            User recommender = null;
            User actor = StoreUtil.getCommonAdmin();
            if (!StringUtils.isEmpty(user.getPage().getRecommendationCode())) {


                if (config.getProperties().containsKey("activateRecommendBol")) {

                    //추천한 사람에게 BOL 지급
                    recommender = userSvc.getUserByRecommendKey(user.getPage().getRecommendationCode());
                    if (recommender != null) {
                        Float amount = ((Integer) config.getProperties().get("activateRecommendBol")).floatValue();
                        if (amount != null && amount > 0) {
                            BolHistory history = new BolHistory();
                            history.setAmount(amount);
                            history.setUser(recommender);
                            history.setSubject("친구 초대(초대자)");
                            history.setPrimaryType("increase");
                            history.setSecondaryType("invite");
                            history.setTargetType("member");
                            history.setTarget(user);
                            history.setProperties(new HashMap<String, Object>());
                            history.getProperties().put("지급처", "오리마켓 운영팀");
                            cashBolSvc.increaseBol(recommender, history);

                            MsgOnly msg = new MsgOnly();
                            msg.setInput("system");
                            msg.setStatus("ready");
                            msg.setType("push");
                            msg.setMoveType1("inner");
                            msg.setMoveType2("bolHistory");
                            msg.setMoveTarget(history);
                            msg.setSubject(user.getName() + "님이 회원님의 초대코드를 입력 후 가입하였습니다.");
                            msg.setContents(msg.getSubject());
                            msg.setAppType(Const.APP_TYPE_USER);
                            queueSvc.insertMsgBox(actor, msg, recommender, Const.APP_TYPE_USER);
                        }
                    }
                }
            }
            return Const.E_SUCCESS;
        }

        return Const.E_UNKNOWN;

    }

    public Integer startPage(User user) throws ResultCodeException {
        return pageSvc.start(user);
    }

    public Integer requestApprovalPage(User user) throws ResultCodeException {
        return pageSvc.requestApproval(user);
    }

    public Session getReloadSession(Session session) throws ResultCodeException {
        Session saved = sqlSession.selectOne("Auth.getUserDeviceByDeviceId", session);
        if (saved == null || !saved.getRestrictionStatus().equals("none"))
            throw new SessionNotFoundException();


        if (saved.getAppType().equals(Const.APP_TYPE_USER)) {
            saved.setAppType("pplus");
        }
        saved.setRefreshKey(getRefreshKey(String.valueOf(saved.getNo())));
        saved.setSessionKey(session.getSessionKey());
        reloadSession(saved);

        if (!saved.getRestrictionStatus().equals("none")) {
            throw new SessionNotFoundException();
        }

        return saved;
    }

    public String refreshSessionKey(String sessionKey, String refreshKey, String appKey, String deviceId) throws ResultCodeException {
        Session session = null;
        try {
            session = getSession(sessionKey);

        } catch (Exception e) {
            logger.error(e.toString());
        }
        if (session != null) {
            throw new NotPermissionException();
        }

        String decryptedSessionKey = SecureUtil.decryptMobileNumber(sessionKey);
        refreshKey = SecureUtil.decryptMobileNumber(refreshKey);
        try {
            String memberSeqNo = decryptedSessionKey.split("-")[0];
            String savedRefreshKey = getRefreshKey(memberSeqNo);
            if (savedRefreshKey == null || !savedRefreshKey.equals(refreshKey)) {
                throw new RefreshKeyNotFoundException();
            }

            ParamMap params = new ParamMap();
            params.put("deviceId", deviceId);
            params.put("appKey", appKey);
            params.put("memberSeqNo", Long.valueOf(memberSeqNo));

            Session saved = sqlSession.selectOne("Auth.getUserDeviceForRefreshSession", params);

            registSession(saved);

            return saved.getSessionKey();

        } catch (Exception e) {
            logger.error(e.toString());
            throw new RefreshKeyNotFoundException();
        }

    }

    public User getUserByVerification(Verification verification) throws ResultCodeException {
        Integer v = verificationSvc.confirm(verification);
        if (Const.E_SUCCESS.equals(v)) {
            User user = null;
            if (verification.getMedia().equals("sms")) {
                if (StringUtils.isNotEmpty(verification.getAppType()) && !verification.getAppType().equals("pplus")) {
                    verification.setMobile(verification.getAppType() + "##" + verification.getMobile());
                }
                user = userSvc.getUserByMobile(verification.getMobile());
            } else if (verification.getMedia().equals("email")) {
                user = userSvc.getUserByEmail(verification.getEmail());
            }
            return user;
        }
        return null;
    }

    public Integer changePasswordByVerification(Verification verification, Boolean encrypted) throws ResultCodeException {

        Integer v = verificationSvc.confirm(verification);
        if (Const.E_SUCCESS.equals(v)) {
            User temp = new User();
            temp.setAppType(verification.getAppType());

            if (StringUtils.isEmpty(temp.getAppType())) {
                temp.setAppType("pplus");
            }

            temp.setLoginId(verification.getLoginId());
            User user = getUserByLoginId(temp);

            if (user == null)
                throw new NotMatchUserException();

            if (encrypted != null && encrypted) {
                verification.setPassword(SecureUtil.decryptMobileNumber(verification.getPassword()));
            }

            user.setPassword(SecureUtil.encryptPassword(user.getLoginId().replace(verification.getAppType() + "##", ""), verification.getPassword()));
            Integer r = userSvc.changePassword(user);
            if (Const.E_SUCCESS.equals(r)) {
                sqlSession.update("Auth.loginSuccessProc", user);
            }

            if (verification.getAppType().equals(Const.APP_TYPE_LUCKYBOL)) {
                String buffResult = buffWalletService.duplicateUser(user);
                if (!buffResult.equals("SUCCESS")) {
                    String password = SecureUtil.encryptMobileNumber(verification.getPassword());
                    buffWalletService.walletSignUp(user, password);
                }
            }


            return r;
        }
        return v;
    }

    public Integer initializationPassword(String loginId) throws ResultCodeException {
        User temp = new User();
        temp.setLoginId(loginId);
        User user = getUserByLoginId(temp);

        if (user == null)
            throw new NotMatchUserException();

        user.setPassword(SecureUtil.encryptPassword(user.getLoginId(), "1111"));
        Integer r = userSvc.changePassword(user);
        if (Const.E_SUCCESS.equals(r)) {
            sqlSession.update("Auth.loginSuccessProc", user);
        }
        return r;
    }

    public Integer updateMobileByVerification(User user, Verification verification) throws ResultCodeException {
        Integer v = verificationSvc.confirm(verification);
        if (Const.E_SUCCESS.equals(v)) {
            if (StringUtils.isEmpty(verification.getAppType()) || verification.getAppType().equals("pplus")) {
                user.setMobile(verification.getMobile());
            } else {
                user.setMobile(verification.getAppType() + "##" + verification.getMobile());
            }

            Integer r = userSvc.updateMobile(user);
            return r;
        }
        return v;
    }

    public Integer updateEmailByVerification(User user, Verification verification) throws ResultCodeException {
        Integer v = verificationSvc.confirm(verification);
        if (Const.E_SUCCESS.equals(v)) {
            user.setEmail(verification.getEmail());
            Integer r = userSvc.updateEmail(user);
            return r;
        }
        return v;
    }

    public Integer updateAuthCodeByVerification(User user, Verification verification) throws ResultCodeException {
        Integer v = verificationSvc.confirm(verification);
        if (Const.E_SUCCESS.equals(v)) {
            Page page = pageSvc.getPageByUser(user);
            page.setAuthCode(verification.getAuthCode());
            Integer r = pageSvc.updateAuthCode(page);
            return r;
        }
        return v;
    }

    public void checkSessionTimestamp(String timestamp) throws Exception {
        //ToDo : HTTP Request timestamp 체크 (보안 추가 정보로 추가가 필요할 수도 있음)
        return;
    }

    public Agent getAgentSession(String agentId, String timestamp, String sessionKey) throws Exception {
        try {

            logger.debug("getAgentSession info --- agentId [" + agentId + "], timestamp [" + timestamp + "], sessionKey [" + sessionKey + "] \n");

            checkSessionTimestamp(timestamp);
            Agent agent = RedisUtil.getInstance().getOpsHash(REDIS_PREFIX + "agentAccessToken", sessionKey);


            if (agent == null || !agent.getChargeId().equals(agentId)) {

                String fields[] = SecureUtil.decryptBase64(sessionKey).split(":");
                if (fields.length == 1) {
                    String code = fields[0];
                    agent = new Agent();
                    agent.setCode(code);
                    agent = this.getAgentByCode(agent);
                    if (agent == null) {
                        throw new Exception("getAgentSession() : Agent Sessionkey is not valid");
                    }
                    RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "agentAccessToken", sessionKey, agent);

                    return agent;
                } else if (fields.length != 2) {
                    String id = fields[0];
                    String password = fields[1];
                    agent = agentSvc.getAgent(id, SecureUtil.encryptPassword(id, password));
                    if (agent == null) {
                        throw new Exception("getAgentSession() : Agent Sessionkey is not valid");
                    }
                    RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "agentAccessToken", sessionKey, agent);

                    return agent;
                } else {
                    logger.error("getAgentSession() : Agent SessionKey is not valid !!! - " + sessionKey);
                    return null;
                }
            } else
                return agent;
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new SessionNotFoundException("getAgentSession() Error", e);
        }
    }

    public Session getGuestSession(String sessionKey) throws Exception {
        try {

            logger.debug("getGuestSession info --- sessionKey [" + sessionKey + "] \n");
            String fields[] = SecureUtil.decryptBase64(sessionKey).split(":");
            if (fields.length != 3) {
                logger.error("getGuestSession() : guestSessionKey is not valid !!! - " + sessionKey);
                return null;
            }

            checkSessionTimestamp(fields[0]);

            String mobile = fields[1];
            String name = fields[2];
            mobile = StoreUtil.getValidatePhoneNumber(mobile);
            if (mobile.length() < 10) {
                logger.error("getGuestSession() : guestSessionKey is not valid !!! - " + sessionKey);
                return null;
            }

            sessionKey = SecureUtil.encryptBase64(mobile + ":" + name);
            Session session = RedisUtil.getInstance().getOpsHash(REDIS_PREFIX + "guestSessionUser", sessionKey);

            if (session == null) {
                User user = userSvc.getUserByMobile(mobile);
                if (user == null) {
                    Date now = new Date();
                    user = new User();
                    user.setLoginId("guest_" + KeyGenerator.generateKey());
                    user.setNickname(user.getLoginId());
                    user.setMobile(mobile);
                    user.setName(name);
                    user.setCountry(new Country());
                    user.getCountry().setNo((long) 1);
                    user.setMemberType("guest");
                    user.setAccountType("guest");
                    user.setPassword("guest");
                    user.setUseStatus("normal");
                    Verification verification = new Verification();
                    verification.setMedia("external");
                    user.setVerification(verification);
                    user.setRestrictionStatus("none");
                    user.setRestrictionClsDate(now);
                    user.setTalkRecvBound(TalkRecvBound.nobody);
                    user.setLastLoginDate(now);
                    user.setJoinDate(now);
                    user.setPlatform(Platform.mobileweb.toString());
                    user.setModDate(now);
                    user.setCertificationLevel((short) 0);
                    user.setLottoDefaultTicketCount(0);
                    user.setLottoTicketCount(0);
                    // mgk_add : 추가 board[member] [
                    BulletinBoard board = new BulletinBoard();
                    board.setType("member");
                    board.setName(user.getLoginId());
                    Integer ret = articleSvc.insertBoard(board);
                    user.setBoardSeqNo(board.getNo());
                    // mgk_add ]

                    int effected = sqlSession.insert("Auth.join", user);
                    if (effected > 0) {
                        user = this.getUserByLoginId(user);
                    }
                }

                session = new Session();
                session.setModUser(user);
                session.setUserNo(user.getNo());
				/*
				Page page = new Page() ;
				page.setNo(1L) ;
				page.setName("PR NUMBER");
				page.setStatus("normal");
				page.setUser(user) ;
				page.setModUser(user) ;
				page.setTalkRecvBound("everybody");
				page.setValuationPoint(0L);
				 */
                session.setPage(null);
                session.setSessionKey(sessionKey);
                session.setMobile(user.getMobile());
                session.setCertificationLevel(user.getCertificationLevel());
                session.setUseStatus(user.getUseStatus());
                session.setAccountType(user.getAccountType());
                session.setEmail(user.getEmail());
                session.setCountry(new Country());
                session.getCountry().setNo(1L);
                session.setTotalBol(user.getTotalBol());
                session.setCash(user.getCash());
                RedisUtil.getInstance().putOpsHash(REDIS_PREFIX + "guestSessionUser", session.getSessionKey(), session);
            }
            if (session == null) {
                throw new Exception("Not Found GuestSessionUser or Mobile SessionKye is not valid");
            } else {
                return session;
            }
        } catch (Exception e) {
            logger.error("getGuestSession() ERROR : " + AppUtil.excetionToString(e));
            throw new SessionNotFoundException("getGuestSession() ERROR", e.getMessage());
        }
    }
}
