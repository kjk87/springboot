package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.NotificationBox;
import kr.co.pplus.store.api.jpa.repository.NotificationBoxRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.mvc.service.RootService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class NotificationBoxService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(NotificationBoxService.class);


    @Autowired
    NotificationBoxRepository notificationBoxRepository;


    public Page<NotificationBox> getNotificationBoxList(Long memberSeqNo, Pageable pageable) {
        return notificationBoxRepository.findAllByMemberSeqNoOrderBySeqNoDesc(memberSeqNo, pageable);
    }

    public void save(NotificationBox notificationBox) {

        notificationBox.setRegDatetime(AppUtil.localDatetimeNowString());
        notificationBox.setIsRead(false);
        notificationBoxRepository.save(notificationBox);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void delete(Long memberSeqNo, Long seqNo){
        notificationBoxRepository.deleteByMemberSeqNoAndSeqNo(memberSeqNo, seqNo);
    }

    public NotificationBox getBuffWithdrawNotification(Long memberSeqNo){
        return notificationBoxRepository.findFirstByMemberSeqNoAndMoveType2AndIsReadOrderBySeqNoDesc(memberSeqNo, "withdraw", false);
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void read(Long notificationBoxSeqNo){
        notificationBoxRepository.updateRead(notificationBoxSeqNo);
    }

}
