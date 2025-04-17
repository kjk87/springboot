package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.MemberLuckyCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface MemberLuckyCouponRepository extends JpaRepository<MemberLuckyCoupon, Long> {

    @Modifying
    @Query(value="update member_lucky_coupon set status = 1, send_datetime = :datetime, update_datetime = :datetime where coupon_send_seq_no = :couponSendSeqNo", nativeQuery = true)
    void updateChoiceStatus(Long couponSendSeqNo, String datetime) ;

    @Query(value="update member_lucky_coupon set status = 3, update_datetime = :datetime where status = 1 and valid_datetime < :datetime", nativeQuery = true)
    void updateExpired(String datetime);

    @Query(value="update member_lucky_coupon set status = 2, use_datetime = now(), update_datetime = now() where status = 1 and seq_no = :seqNo", nativeQuery = true)
    void updateUse(Long seqNo);

    @Query(value="update member_lucky_coupon set status = 1, use_datetime = null, update_datetime = now() where status = 2 and seq_no = :seqNo and valid_datetime > now()", nativeQuery = true)
    void updateCancel(Long seqNo);

    List<MemberLuckyCoupon> findAllByMemberSeqNoAndStatusInOrderByValidDatetimeAsc(Long memberSeqNo, List<Integer> statusList);

    int countByMemberSeqNoAndStatusIn(Long memberSeqNo, List<Integer> statusList);

    MemberLuckyCoupon findBySeqNo(Long seqNo);
}
