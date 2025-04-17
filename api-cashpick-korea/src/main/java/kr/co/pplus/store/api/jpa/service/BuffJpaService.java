package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.*;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.util.Filtering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class BuffJpaService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(BuffJpaService.class);


    @Autowired
    private BuffMemberWithMemberRepository buffMemberWithMemberRepository;

    @Autowired
    private BuffRepository buffRepository;

    @Autowired
    private BuffMemberRepository buffMemberRepository;

    @Autowired
    private BuffMemberOnlyReceivedPointRepository buffMemberOnlyReceivedPointRepository;

    @Autowired
    private BuffMemberOnlyReceivedBolRepository buffMemberOnlyReceivedBolRepository;

    @Autowired
    private BuffRequestRepository buffRequestRepository;

    @Autowired
    private BuffDividedBolLogRepository buffDividedBolLogRepository;

    @Autowired
    private BuffMemberNativeRepository buffMemberNativeRepository;

    @Autowired
    private BuffPostNativeRepository buffPostNativeRepository;

    @Autowired
    private BuffPostLikeNativeRepository buffPostLikeNativeRepository;

    @Autowired
    private BuffPostLikeRepository buffPostLikeRepository;

    @Autowired
    private BuffPostReplyNativeRepository buffPostReplyNativeRepository;

    @Autowired
    private BuffPostReplyRepository buffPostReplyRepository;

    @Autowired
    BuffPostRepository buffPostRepository;

    @Autowired
    BuffPostImageRepository buffPostImageRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    BolService bolService;

    @Autowired
    MemberService memberService;

    @Autowired
    NotificationBoxService notificationBoxService;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    PurchaseProductDetailRepository purchaseProductDetailRepository;

    @Autowired
    ProductService productService;

    public Buff getBuff(Long buffSeqNo) {
        return buffRepository.findBySeqNo(buffSeqNo);
    }

    public BuffMemberWithMember getBuffMember(Long memberSeqNo) {
        return buffMemberWithMemberRepository.findFirstByMemberSeqNo(memberSeqNo);
    }

    public int getBuffMemberCount(Long buffSeqNo) {
        return buffMemberRepository.countByBuffSeqNo(buffSeqNo);
    }

    public Page<BuffPostNative> getBuffPostList(Long buffSeqNo, Long memberSeqNo, Pageable pageable) {
        return buffPostNativeRepository.findAllByBuffSeqNo(buffSeqNo, memberSeqNo, pageable);
    }

    public Page<BuffPostLikeNative> getBuffPostLikeList(Long buffPostSeqNo, Long memberSeqNo, Pageable pageable) {
        return buffPostLikeNativeRepository.findAllByBuffPostSeqNo(buffPostSeqNo, memberSeqNo, pageable);
    }

    public Page<BuffPostReplyNative> getBuffPostReplyList(Long buffPostSeqNo, Long memberSeqNo, Pageable pageable) {
        return buffPostReplyNativeRepository.findAllByBuffPostSeqNo(buffPostSeqNo, memberSeqNo, pageable);
    }

    public Page<BuffMemberNative> getBuffMemberList(Long buffSeqNo, Long memberSeqNo, Boolean includeMe, String search, Pageable pageable) {
        if (includeMe) {
            return buffMemberNativeRepository.findAllByBuffSeqNo(buffSeqNo, memberSeqNo, search, pageable);
        } else {
            return buffMemberNativeRepository.findAllByBuffSeqNoExcludeMe(buffSeqNo, memberSeqNo, search, pageable);
        }
    }

    public Page<BuffDividedBolLog> getBuffLogList(Long buffSeqNo, String moneyType, Pageable pageable) {
        return buffDividedBolLogRepository.findAllByBuffSeqNoAndMoneyType(buffSeqNo, moneyType, pageable);
    }

    public List<BuffRequest> getRequestList(Long memberSeqNo) {
        return buffRequestRepository.findAllByMemberSeqNoAndStatusOrderBySeqNoDesc(memberSeqNo, "request");
    }

    public Integer getRequestCount(Long memberSeqNo) {
        return buffRequestRepository.countByMemberSeqNoAndStatus(memberSeqNo, "request");
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void insertBuffPost(Long memberSeqNo, BuffPost buffPost) {

        List<BuffPostImage> imageList = buffPost.getImageList();

        buffPost.setTitle("일반게시글");
        buffPost.setContent(Filtering.filter(buffPost.getContent()));
        buffPost.setType("normal");
        buffPost.setMemberSeqNo(memberSeqNo);
        buffPost.setHidden(false);
        buffPost.setDeleted(false);
        buffPost.setContent(Filtering.filter(buffPost.getContent()));
        String dateStr = AppUtil.localDatetimeNowString();
        buffPost.setRegDatetime(dateStr);
        buffPost.setModDatetime(dateStr);
        buffPost = buffPostRepository.save(buffPost);

        saveBuffPostImage(imageList, buffPost.getSeqNo());

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateBuffPost(Long memberSeqNo, BuffPost buffPost) throws ResultCodeException {


        BuffPost savedBuffPost = buffPostRepository.findBySeqNo(buffPost.getSeqNo());

        if (!savedBuffPost.getType().equals("normal")) {
            throw new NotPermissionException();
        }

        if (!savedBuffPost.getMemberSeqNo().equals(memberSeqNo)) {
            throw new NotPermissionException();
        }

        savedBuffPost.setContent(Filtering.filter(buffPost.getContent()));

        List<BuffPostImage> imageList = buffPost.getImageList();
        String dateStr = AppUtil.localDatetimeNowString();
        savedBuffPost.setModDatetime(dateStr);
        savedBuffPost = buffPostRepository.saveAndFlush(savedBuffPost);

        buffPostImageRepository.deleteAllByBuffPostSeqNo(savedBuffPost.getSeqNo());

        saveBuffPostImage(imageList, savedBuffPost.getSeqNo());
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void deleteBuffPost(Long memberSeqNo, Long buffPostSeqNo) throws ResultCodeException {

        BuffPost buffPost = buffPostRepository.findBySeqNo(buffPostSeqNo);

        if (!buffPost.getType().equals("normal")) {
            throw new NotPermissionException();
        }

        if (!buffPost.getMemberSeqNo().equals(memberSeqNo)) {
            throw new NotPermissionException();
        }

        buffPost.setDeleted(true);
        String dateStr = AppUtil.localDatetimeNowString();
        buffPost.setModDatetime(dateStr);
        buffPost = buffPostRepository.save(buffPost);

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void hiddenBuffPost(Long memberSeqNo, Long buffPostSeqNo) throws ResultCodeException {

        BuffPost buffPost = buffPostRepository.findBySeqNo(buffPostSeqNo);

        if (!buffPost.getMemberSeqNo().equals(memberSeqNo)) {
            throw new NotPermissionException();
        }

        buffPost.setHidden(true);
        String dateStr = AppUtil.localDatetimeNowString();
        buffPost.setModDatetime(dateStr);
        buffPost = buffPostRepository.save(buffPost);

    }

    private void saveBuffPostImage(List<BuffPostImage> imageList, Long buffPostSeqNo) {
        if (imageList != null && imageList.size() > 0) {
            int array = 0;
            for (BuffPostImage image : imageList) {
                array++;
                image.setBuffPostSeqNo(buffPostSeqNo);
                image.setArray(array);
                buffPostImageRepository.save(image);
            }
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public String buffPostLike(Long memberSeqNo, Long buffPostSeqNo) {

        boolean exist = buffPostLikeRepository.existsByMemberSeqNoAndBuffPostSeqNo(memberSeqNo, buffPostSeqNo);
        if (exist) {
            buffPostLikeRepository.deleteByMemberSeqNoAndBuffPostSeqNo(memberSeqNo, buffPostSeqNo);
            return "cancel";
        } else {
            BuffPostLike buffPostLike = new BuffPostLike();
            buffPostLike.setBuffPostSeqNo(buffPostSeqNo);
            buffPostLike.setMemberSeqNo(memberSeqNo);
            buffPostLike.setRegDatetime(AppUtil.localDatetimeNowString());
            buffPostLikeRepository.save(buffPostLike);
            return "like";
        }

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void insertBuffPostReply(Long memberSeqNo, Long buffPostSeqNo, String reply) {
        String dateStr = AppUtil.localDatetimeNowString();
        BuffPostReply buffPostReply = new BuffPostReply();
        buffPostReply.setBuffPostSeqNo(buffPostSeqNo);
        buffPostReply.setMemberSeqNo(memberSeqNo);
        buffPostReply.setDeleted(false);
        buffPostReply.setReply(Filtering.filter(reply));
        buffPostReply.setRegDatetime(dateStr);
        buffPostReply.setModDatetime(dateStr);
        buffPostReplyRepository.save(buffPostReply);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void modifyBuffPostReply(Long memberSeqNo, Long buffPostReplySeqNo, String reply) throws ResultCodeException {

        String dateStr = AppUtil.localDatetimeNowString();
        BuffPostReply buffPostReply = buffPostReplyRepository.findBySeqNo(buffPostReplySeqNo);

        if (!buffPostReply.getMemberSeqNo().equals(memberSeqNo)) {
            throw new NotPermissionException();
        }

        buffPostReply.setReply(Filtering.filter(reply));
        buffPostReply.setModDatetime(dateStr);
        buffPostReplyRepository.save(buffPostReply);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void deleteBuffPostReply(Long memberSeqNo, Long buffPostReplySeqNo) throws ResultCodeException {
        String dateStr = AppUtil.localDatetimeNowString();
        BuffPostReply buffPostReply = buffPostReplyRepository.findBySeqNo(buffPostReplySeqNo);

        if (!buffPostReply.getMemberSeqNo().equals(memberSeqNo)) {
            throw new NotPermissionException();
        }
        buffPostReply.setDeleted(true);
        buffPostReply.setModDatetime(dateStr);
        buffPostReplyRepository.save(buffPostReply);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void buffMake(Long memberSeqNo, Buff buff) {

        String dateStr = AppUtil.localDatetimeNowString();

        if (buff.getSeqNo() != null) {
            Buff savedBuff = buffRepository.findBySeqNo(buff.getSeqNo());
            savedBuff.setCapacity(buff.getCapacity());
            savedBuff.setImage(buff.getImage());
            savedBuff.setTitle(buff.getTitle());
            savedBuff.setInfo(buff.getInfo());
            buffRepository.save(savedBuff);

        } else {
            buff.setOwner(memberSeqNo);
            buff.setLaunchers(memberSeqNo);
            buff.setDeleted(false);
            buff.setRegDatetime(dateStr);
            buff.setTotalDividedBol(0.0);
            buff.setTotalDividedPoint(0.0);
            buff = buffRepository.save(buff);

            BuffMember buffMember = new BuffMember();
            buffMember.setBuffSeqNo(buff.getSeqNo());
            buffMember.setMemberSeqNo(memberSeqNo);
            buffMember.setIsOwner(true);
            buffMember.setReceivedBol(0.0);
            buffMember.setDividedBol(0.0);
            buffMember.setReceivedPoint(0.0);
            buffMember.setDividedPoint(0.0);
            buffMember.setRegDatetime(dateStr);
            buffMemberRepository.save(buffMember);
        }


    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void buffInvite(Long memberSeqNo, BuffParam param) throws ResultCodeException {

        boolean isMember = buffMemberRepository.existsByMemberSeqNoAndBuffSeqNo(memberSeqNo, param.getBuffSeqNo());

        if (!isMember) {
            throw new NotPermissionException();
        }

        String dateStr = AppUtil.localDatetimeNowString();

        List<Long> inviteList = param.getInviteList();
        for (Long seqNo : inviteList) {
            boolean exist = buffMemberRepository.existsByMemberSeqNoAndBuffSeqNo(seqNo, param.getBuffSeqNo());
            if (!exist) {

                boolean existRequest = buffRequestRepository.existsByMemberSeqNoAndBuffSeqNoAndStatus(seqNo, param.getBuffSeqNo(), "request");

                if (!existRequest) {
                    BuffRequest buffRequest = new BuffRequest();
                    buffRequest.setBuffSeqNo(param.getBuffSeqNo());
                    buffRequest.setMemberSeqNo(seqNo);
                    buffRequest.setRequester(memberSeqNo);
                    buffRequest.setStatus("request");
                    buffRequest.setStatusDatetime(dateStr);
                    buffRequest.setRegDatetime(dateStr);
                    buffRequestRepository.save(buffRequest);
                }

            }
        }
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void changeBuffRequest(Long memberSeqNo, Long buffRequestSeqNo, String status) throws ResultCodeException {

        BuffRequest buffRequest = buffRequestRepository.findBySeqNo(buffRequestSeqNo);
        if (!buffRequest.getMemberSeqNo().equals(memberSeqNo)) {
            throw new NotPermissionException();
        }

        String dateStr = AppUtil.localDatetimeNowString();

        if (status.equals("consent")) {

            List<BuffRequest> withdrawList = buffRequestRepository.findAllByMemberSeqNoAndStatusOrderBySeqNoDesc(memberSeqNo, "withdraw");

            if (!withdrawList.isEmpty()) {
                try {
                    String recentWithdrawDatetime = withdrawList.get(0).getStatusDatetime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date withdrawDate = dateFormat.parse(recentWithdrawDatetime);
                    long duration = System.currentTimeMillis() - withdrawDate.getTime();
                    long _24hour = 1000 * 60 * 60 * 24;
                    if (duration < _24hour) {

                        Map<String, Object> result = new HashMap<String, Object>();
                        Long remainSecond = (_24hour - duration) / 1000;
                        result.put("remainSecond", remainSecond);
                        throw new NotPossibleTimeException("row", result);
                    }
                } catch (Exception e) {

                }


            }


            BuffMember buffMember = buffMemberRepository.findFirstByMemberSeqNo(memberSeqNo);
            if (buffMember != null) {
                throw new AlreadyExistsException();
            }

            Buff buff = getBuff(buffRequest.getBuffSeqNo());

            int count = buffMemberRepository.countByBuffSeqNo(buffRequest.getBuffSeqNo());
            if (count >= buff.getCapacity()) {
                throw new AlreadyLimitException();
            }

            buffMember = new BuffMember();
            buffMember.setBuffSeqNo(buffRequest.getBuffSeqNo());
            buffMember.setMemberSeqNo(memberSeqNo);
            buffMember.setIsOwner(false);
            buffMember.setReceivedBol(0.0);
            buffMember.setDividedBol(0.0);
            buffMember.setReceivedPoint(0.0);
            buffMember.setDividedPoint(0.0);
            buffMember.setRegDatetime(dateStr);
            buffMemberRepository.save(buffMember);
        }

        buffRequest.setStatus(status);
        buffRequest.setStatusDatetime(dateStr);
        buffRequestRepository.save(buffRequest);


    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void changeBuffOwner(Long memberSeqNo, Long buffRequestSeqNo, Long ownerSeqNo) throws ResultCodeException {
        BuffMember me = buffMemberRepository.findFirstByMemberSeqNo(memberSeqNo);
        if (!me.getIsOwner()) {
            throw new NotPermissionException();
        }

        if (!me.getBuffSeqNo().equals(buffRequestSeqNo)) {
            throw new NotPermissionException();
        }

        me.setIsOwner(false);
        buffMemberRepository.save(me);

        BuffMember owner = buffMemberRepository.findBySeqNo(ownerSeqNo);
        owner.setIsOwner(true);
        buffMemberRepository.save(owner);

        buffRepository.updateOwner(buffRequestSeqNo, owner.getMemberSeqNo());

    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void exitBuff(Long memberSeqNo, Long buffRequestSeqNo) throws ResultCodeException {
        BuffMember me = buffMemberRepository.findFirstByMemberSeqNo(memberSeqNo);
        if (me.getIsOwner()) {
            throw new NotPermissionException();
        }

        if (!me.getBuffSeqNo().equals(buffRequestSeqNo)) {
            throw new NotPermissionException();
        }

        String dateStr = AppUtil.localDatetimeNowString();

        BuffRequest buffRequest = new BuffRequest();
        buffRequest.setBuffSeqNo(buffRequestSeqNo);
        buffRequest.setMemberSeqNo(memberSeqNo);
        buffRequest.setRequester(memberSeqNo);
        buffRequest.setStatus("withdraw");
        buffRequest.setStatusDatetime(dateStr);
        buffRequest.setWithdrawType("oneself");
        buffRequest.setRegDatetime(dateStr);
        buffRequestRepository.save(buffRequest);

        buffMemberRepository.deleteByMemberSeqNoAndBuffSeqNo(memberSeqNo, buffRequestSeqNo);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void forcedExitBuff(Long memberSeqNo, BuffParam param) throws ResultCodeException {

        BuffMember buffMember = buffMemberRepository.findFirstByMemberSeqNoAndBuffSeqNo(memberSeqNo, param.getBuffSeqNo());

        if (buffMember == null || !buffMember.getIsOwner()) {
            throw new NotPermissionException();
        }

        String dateStr = AppUtil.localDatetimeNowString();

        List<Long> exitList = param.getExitList();
        for (Long seqNo : exitList) {
            BuffMember exitMember = buffMemberRepository.findBySeqNo(seqNo);
            if (exitMember != null) {

                BuffRequest buffRequest = new BuffRequest();
                buffRequest.setBuffSeqNo(param.getBuffSeqNo());
                buffRequest.setMemberSeqNo(exitMember.getMemberSeqNo());
                buffRequest.setRequester(memberSeqNo);
                buffRequest.setStatus("withdraw");
                buffRequest.setStatusDatetime(dateStr);
                buffRequest.setWithdrawType("compulsory");
                buffRequest.setRegDatetime(dateStr);
                buffRequest.setNote(param.getReason());
                buffRequest = buffRequestRepository.save(buffRequest);

                buffMemberRepository.deleteBySeqNo(seqNo);

                NotificationBox notificationBox = new NotificationBox();
                notificationBox.setSubject("버프강퇴");
                notificationBox.setContents(param.getReason());
                notificationBox.setMemberSeqNo(exitMember.getMemberSeqNo());
                notificationBox.setMoveSeqNo(buffRequest.getSeqNo());
                notificationBox.setMoveType1("inner");
                notificationBox.setMoveType2("withdraw");
                notificationBoxService.save(notificationBox);

            }
        }
    }

    public Boolean existBuffOwner(Long memberSeqNo) {
        BuffMember buffMember = buffMemberRepository.findFirstByMemberSeqNo(memberSeqNo);
        if (buffMember != null && buffMember.getIsOwner()) {
            int count = buffMemberRepository.countByBuffSeqNo(buffMember.getBuffSeqNo());
            if (count > 1) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
