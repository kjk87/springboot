package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.NotificationBox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface NotificationBoxRepository extends JpaRepository<NotificationBox, Long> {

    Page<NotificationBox> findAllByMemberSeqNoOrderBySeqNoDesc(Long memberSeqNo, Pageable pageable);

    void deleteByMemberSeqNoAndSeqNo(Long memberSeqNo, Long seqNo);

    NotificationBox findFirstByMemberSeqNoAndMoveType2AndIsReadOrderBySeqNoDesc(Long memberSeqNo, String moveType2, Boolean isRead);

    @Modifying
    @Query(value="update notification_box set is_read = true where seq_no = :seqNo", nativeQuery = true)
    void updateRead(Long seqNo) ;
}