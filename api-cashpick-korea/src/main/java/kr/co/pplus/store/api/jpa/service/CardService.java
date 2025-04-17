package kr.co.pplus.store.api.jpa.service;

import com.google.gson.Gson;
import kr.co.pplus.store.api.jpa.model.Card;
import kr.co.pplus.store.api.jpa.model.DaouCardRequest;
import kr.co.pplus.store.api.jpa.model.ReapPayBillkey;
import kr.co.pplus.store.api.jpa.model.reappay.ReapPayBillKeyData;
import kr.co.pplus.store.api.jpa.repository.CardRepository;
import kr.co.pplus.store.api.jpa.repository.ReapPayBillkeyRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidCardException;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.pg.daou.auth.common.Crypto;
import kr.co.pplus.store.pg.daou.auth.common.PayStruct;
import kr.co.pplus.store.pg.daou.auth.directCard_auto.DaouDirectCardAutoAPI;
import kr.co.pplus.store.type.model.User;
import kr.co.pplus.store.util.StoreUtil;
import kr.co.pplus.store.util.WalletSecureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class CardService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(CardService.class);


    @Value("${STORE.DAOU.CPID}")
    String CPID = "CTS16374";
    @Value("${STORE.DAOU.KEY}")
    String PubS_KEY = "fintech0";
    @Value("${STORE.DAOU.IP}")
    String PubS_DIRECTCARDAUTO_IP = "123.140.121.205";
    @Value("${STORE.DAOU.PORT}")
    int PubI_DIRECTCARDAUTO_PORT = 64003;

    @Value("${STORE.TYPE}")
    String storeType;

    String PubS_LOGDIR = "/app/logs/";

    @Autowired
    CardRepository cardRepository;

    @Autowired
    ReapPayService reapPayService;

    @Autowired
    ReapPayBillkeyRepository reapPayBillkeyRepository;


    public List<Card> getCardListByMemberSeqNo(Long memberSeqNo) {

        List<Card> cardList = cardRepository.findAllByMemberSeqNoOrderByRepresentDescIdDesc(memberSeqNo);
        return cardList;
    }


    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Card updateRepresentCard(Long memberSeqNo, Long id) {
        Card representCard = null;
        List<Card> cardList = getCardListByMemberSeqNo(memberSeqNo);
        for (Card card : cardList) {
            if (card.getId() == id) {
                card.setRepresent(true);
                representCard = card;
            } else {
                card.setRepresent(false);
            }

        }
        cardRepository.saveAll(cardList);
        return representCard;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Card onlyCardRegister(DaouCardRequest cardRequest) throws ResultCodeException {
        try {
            String RESULTCODE = "";
            String ERRORMESSAGE = "";
            String AUTOKEY = "";
            String CARDCODE = "";
            String GENDATE = "";

            PayStruct struct = new PayStruct();
            DaouDirectCardAutoAPI payDirect = new DaouDirectCardAutoAPI(PubS_DIRECTCARDAUTO_IP, PubI_DIRECTCARDAUTO_PORT);
            struct.PubSet_Function = "KEYGEN_";
            struct.PubSet_Key = PubS_KEY;
            struct.PubSet_CPID = CPID;
            struct.PubSet_PayMethod = "SSL";
            struct.PubSet_OrderNo = StoreUtil.getRandomOrderId();
            ;
            struct.PubSet_ProductType = "1";
            struct.PubSet_BillType = "14";
            struct.PubSet_AutoMonths = "99";
            struct.PubSet_UserID = "kimjk";
            struct.PubSet_ProductCode = "A001";
            struct.PubSet_CardNo = Crypto.Encrypt(PubS_KEY, cardRequest.getCardNo());
            struct.PubSet_ExpireDt = Crypto.Encrypt(PubS_KEY, cardRequest.getExpireDt());
            struct.PubSet_cardAuth = Crypto.Encrypt(PubS_KEY, cardRequest.getCardAuth());
            struct.PubSet_CardPassword = Crypto.Encrypt(PubS_KEY, cardRequest.getCardPassword());

            struct = payDirect.directCardSugiAutoKeygen(struct, PubS_LOGDIR + CPID);

            RESULTCODE = struct.PubGet_ResultCode;
            ERRORMESSAGE = struct.PubGet_ErrorMessage;
            AUTOKEY = struct.PubGet_AutoKey;
            CARDCODE = struct.PubGet_CardCode;
            GENDATE = struct.PubGet_GenDate;

            if (RESULTCODE.equals("0000")) {
                Card card = new Card();
                card.setCardNumber(cardRequest.getCardNo());
                card.setAutoKey(AUTOKEY);
                card.setCardCode(CARDCODE);
                logger.debug("gendate = " + GENDATE);
                card.setGenDate(AppUtil.localDatetimeNowString());
                return card;
            } else {
                if (RESULTCODE.equals("1011")) {
                    ERRORMESSAGE = "카드번호를 확인해 주세요.";
                } else if (RESULTCODE.equals("1012")) {
                    ERRORMESSAGE = "서비스 불가능 카드 입니다.";
                } else if (RESULTCODE.equals("1014")) {
                    ERRORMESSAGE = "유효기간이 만료된 카드 입니다.";
                } else if (RESULTCODE.equals("1041")) {
                    ERRORMESSAGE = "비밀번호를 확인해 주세요.";
                } else if (RESULTCODE.equals("1042")) {
                    ERRORMESSAGE = "비밀번호를 입력해 주세요.";
                } else if (RESULTCODE.equals("1071")) {
                    ERRORMESSAGE = "도난/분실 처리된 카드 입니다.";
                } else if (RESULTCODE.equals("1072")) {
                    ERRORMESSAGE = "거래가 정지된 카드 입니다.";
                } else if (RESULTCODE.equals("2011")) {
                    ERRORMESSAGE = "카드번호를 확인해 주세요.";
                } else if (RESULTCODE.equals("2014")) {
                    ERRORMESSAGE = "유효기간이 만료된 카드 입니다.";
                } else if (RESULTCODE.equals("2015")) {
                    ERRORMESSAGE = "유효기간을 확인해 주세요.";
                } else if (RESULTCODE.equals("2016")) {
                    ERRORMESSAGE = "서비스 불가능 카드 입니다.";
                } else if (RESULTCODE.equals("2041")) {
                    ERRORMESSAGE = "비밀번호를 확인해 주세요.";
                } else if (RESULTCODE.equals("2042")) {
                    ERRORMESSAGE = "비밀번호를 확인해 주세요.";
                } else if (RESULTCODE.equals("2044") || RESULTCODE.equals("2082")) {
                    ERRORMESSAGE = "본인명의의 카드만 등록 가능합니다.";
                }

                Card card = new Card();
                card.setErrorMsg(ERRORMESSAGE);
                return card;
            }


        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("cardRegister", e);
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Card cardRegister(User user, DaouCardRequest cardRequest) throws ResultCodeException {

        try {
            String RESULTCODE = "";
            String ERRORMESSAGE = "";
            String AUTOKEY = "";
            String CARDCODE = "";
            String GENDATE = "";

            PayStruct struct = new PayStruct();
            DaouDirectCardAutoAPI payDirect = new DaouDirectCardAutoAPI(PubS_DIRECTCARDAUTO_IP, PubI_DIRECTCARDAUTO_PORT);
            struct.PubSet_Function = "KEYGEN_";
            struct.PubSet_Key = PubS_KEY;
            struct.PubSet_CPID = CPID;
            struct.PubSet_PayMethod = "SSL";
            struct.PubSet_OrderNo = StoreUtil.getRandomOrderId();
            ;
            struct.PubSet_ProductType = "1";
            struct.PubSet_BillType = "14";
            struct.PubSet_AutoMonths = "99";
            struct.PubSet_UserID = user.getLoginId();
            struct.PubSet_ProductCode = "A001";
            struct.PubSet_CardNo = Crypto.Encrypt(PubS_KEY, cardRequest.getCardNo());
            struct.PubSet_ExpireDt = Crypto.Encrypt(PubS_KEY, cardRequest.getExpireDt());
            struct.PubSet_cardAuth = Crypto.Encrypt(PubS_KEY, cardRequest.getCardAuth());
            struct.PubSet_CardPassword = Crypto.Encrypt(PubS_KEY, cardRequest.getCardPassword());

            struct = payDirect.directCardSugiAutoKeygen(struct, PubS_LOGDIR + CPID);

            RESULTCODE = struct.PubGet_ResultCode;
            ERRORMESSAGE = struct.PubGet_ErrorMessage;
            AUTOKEY = struct.PubGet_AutoKey;
            CARDCODE = struct.PubGet_CardCode;
            GENDATE = struct.PubGet_GenDate;

            logger.debug("RESULTCODE : " + RESULTCODE + " ERRORMESSAGE : " + ERRORMESSAGE);


            if (RESULTCODE.equals("0000")) {

                List<Card> cardList = getCardListByMemberSeqNo(user.getNo());
                Card card = new Card();
                card.setMemberSeqNo(user.getNo());
                card.setCardNumber(cardRequest.getCardNo().substring(cardRequest.getCardNo().length() - 4));
                card.setAutoKey(AUTOKEY);
                card.setCardCode(CARDCODE);
                logger.debug("gendate = " + GENDATE);
                card.setGenDate(AppUtil.localDatetimeNowString());
                if (cardList != null && cardList.size() > 0) {
                    card.setRepresent(false);
                } else {
                    card.setRepresent(true);
                }
                card = cardRepository.saveAndFlush(card);
                return card;
            } else {

                if (RESULTCODE.equals("1011")) {
                    ERRORMESSAGE = "카드번호를 확인해 주세요.";
                } else if (RESULTCODE.equals("1012")) {
                    ERRORMESSAGE = "서비스 불가능 카드 입니다.";
                } else if (RESULTCODE.equals("1014")) {
                    ERRORMESSAGE = "유효기간이 만료된 카드 입니다.";
                } else if (RESULTCODE.equals("1041")) {
                    ERRORMESSAGE = "비밀번호를 확인해 주세요.";
                } else if (RESULTCODE.equals("1042")) {
                    ERRORMESSAGE = "비밀번호를 입력해 주세요.";
                } else if (RESULTCODE.equals("1071")) {
                    ERRORMESSAGE = "도난/분실 처리된 카드 입니다.";
                } else if (RESULTCODE.equals("1072")) {
                    ERRORMESSAGE = "거래가 정지된 카드 입니다.";
                } else if (RESULTCODE.equals("2011")) {
                    ERRORMESSAGE = "카드번호를 확인해 주세요.";
                } else if (RESULTCODE.equals("2014")) {
                    ERRORMESSAGE = "유효기간이 만료된 카드 입니다.";
                } else if (RESULTCODE.equals("2015")) {
                    ERRORMESSAGE = "유효기간을 확인해 주세요.";
                } else if (RESULTCODE.equals("2016")) {
                    ERRORMESSAGE = "서비스 불가능 카드 입니다.";
                } else if (RESULTCODE.equals("2041")) {
                    ERRORMESSAGE = "비밀번호를 확인해 주세요.";
                } else if (RESULTCODE.equals("2042")) {
                    ERRORMESSAGE = "비밀번호를 확인해 주세요.";
                }

                Card card = new Card();
                card.setErrorMsg(ERRORMESSAGE);
                return card;
            }
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("cardRegister", e);
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteCard(Long memberSeqNo, Long id) {
        Optional<Card> optionalCard = cardRepository.findById(id);
        if (optionalCard.isPresent()) {
            Card delCard = optionalCard.get();
            boolean reset = delCard.getRepresent();
            cardRepository.delete(delCard);
            if (reset) {
                List<Card> cardList = getCardListByMemberSeqNo(memberSeqNo);
                if (cardList != null && cardList.size() > 0) {
                    Card newRepresentCard = cardList.get(0);
                    newRepresentCard.setRepresent(true);
                    cardRepository.save(newRepresentCard);
                }
            }
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void regReapPayBillkey(User user, String data) throws ResultCodeException {
        String cardData = WalletSecureUtil.decrypt(data, storeType);
        Map<String, String> map = new HashMap<>();
        Gson gson = new Gson();
        map = (Map<String, String>) gson.fromJson(cardData, map.getClass());
        ReapPayBillKeyData reapPayBillKeyData = reapPayService.billkeyregist(map);
        if (reapPayBillKeyData != null) {
            if (reapPayBillKeyData.getBillkeyrespCode().equals("0000")) {
                int count = reapPayBillkeyRepository.countByMemberSeqNo(user.getNo());

                ReapPayBillkey reapPayBillkey = new ReapPayBillkey();
                reapPayBillkey.setMemberSeqNo(user.getNo());
                reapPayBillkey.setBillkeyIssuerCardType(reapPayBillKeyData.getBillkeyissuerCardType());
                reapPayBillkey.setBillkeyIssuerCardName(reapPayBillKeyData.getBillkeyissuerCardName());
                reapPayBillkey.setBillkeyMaskedCardNumb(reapPayBillKeyData.getBillkeymaskedCardNumb());
                reapPayBillkey.setBillkeyBillingToken(reapPayBillKeyData.getBillkeybillingToken());
                reapPayBillkey.setBillkeyCardType(reapPayBillKeyData.getBillkeycardType());
                reapPayBillkey.setRegDatetime(AppUtil.localDatetimeNowString());
                if (count > 0) {
                    reapPayBillkey.setRepresent(false);
                } else {
                    reapPayBillkey.setRepresent(true);
                }
                reapPayBillkeyRepository.save(reapPayBillkey);
            }else{
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("errorMsg", reapPayBillKeyData.getBillkeyrespMessage());
                throw new UnknownException("row", result);
            }

        } else {
            throw new UnknownException();
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteReapPayBillkey(User user, Long seqNo) throws ResultCodeException {
        ReapPayBillkey reapPayBillkey = reapPayBillkeyRepository.findBySeqNo(seqNo);

        if (!reapPayBillkey.getMemberSeqNo().equals(user.getNo())) {
            throw new NotPermissionException();
        }

        reapPayService.billkeyCancel(reapPayBillkey.getBillkeyBillingToken());
        boolean isRepresent = reapPayBillkey.getRepresent();
        reapPayBillkeyRepository.deleteBySeqNo(seqNo);
        if(isRepresent){
            ReapPayBillkey recentKey = reapPayBillkeyRepository.findFirstByMemberSeqNoOrderBySeqNoDesc(user.getNo());
            if(recentKey != null){
                recentKey.setRepresent(true);
                reapPayBillkeyRepository.save(recentKey);
            }

        }
    }

    public List<ReapPayBillkey> getReapPayBillkeyList(User user) {
        return reapPayBillkeyRepository.findAllByMemberSeqNoOrderByRepresentDescSeqNoDesc(user.getNo());
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeReaPayBillkeyRepresent(User user, Long seqNo) throws ResultCodeException {

        ReapPayBillkey reapPayBillkey = reapPayBillkeyRepository.findBySeqNo(seqNo);
        if (!reapPayBillkey.getMemberSeqNo().equals(user.getNo())) {
            throw new NotPermissionException();
        }

        reapPayBillkeyRepository.updateNotRepresent(seqNo);

        reapPayBillkey.setRepresent(true);

        reapPayBillkeyRepository.save(reapPayBillkey);
    }
}
