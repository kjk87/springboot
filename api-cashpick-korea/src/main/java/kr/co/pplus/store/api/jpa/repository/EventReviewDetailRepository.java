package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.EventReviewDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface EventReviewDetailRepository extends JpaRepository<EventReviewDetail, Long> {

    @Query(value="select *, (select count(1) from event_reply where event_review_seq_no = er.seq_no and status = 1) as reply_count, "
            + " (select count(1) from friend where member_seq_no=:memberSeqNo and friend_seq_no = er.member_seq_no) > 0 as friend "
            + " from event_review er"
            + " where status = 1 ",
            countQuery = "select count(1) from event_review er "
                    + " where status = 1 ",
            nativeQuery = true)
    Page<EventReviewDetail> findAllBy(Long memberSeqNo, Pageable pageable);

    @Query(value="select *, (select count(1) from event_reply where event_review_seq_no = er.seq_no and status = 1) as reply_count, "
            + " 0 as friend "
            + " from event_review er "
            + " where er.member_seq_no = :memberSeqNo "
            + " and status = 1",
            countQuery = "select count(1) from event_review er "
                    + " where er.member_seq_no = :memberSeqNo "
                    + " and status = 1",
            nativeQuery = true)
    Page<EventReviewDetail> findAllMy(Long memberSeqNo, Pageable pageable);

    @Query(value="select *, (select count(1) from event_reply where event_review_seq_no = er.seq_no and status = 1) as reply_count, "
            + " (select count(1) from friend where member_seq_no=:memberSeqNo and friend_seq_no = er.member_seq_no) > 0 as friend "
            + " from event_review er"
            + " where er.seq_no = :seqNo",
            nativeQuery = true)
    EventReviewDetail findBySeqNo(Long seqNo, Long memberSeqNo);
}