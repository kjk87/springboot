package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBoxReviewDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBoxReviewDetailRepository extends JpaRepository<LuckyBoxReviewDetail, Long> {

    @Query(value="select *, (select count(1) from luckybox_reply where luckybox_review_seq_no = lr.seq_no and status = 1) as reply_count, "
            + " (select count(1) from friend where member_seq_no=:memberSeqNo and friend_seq_no = lr.member_seq_no) > 0 as friend "
            + " from luckybox_review lr "
            + " where status = 1 ",
            countQuery = "select count(1) from luckybox_review lr "
                    + " where status = 1 ",
            nativeQuery = true)
    Page<LuckyBoxReviewDetail> findAllBy(@Param("memberSeqNo") Long memberSeqNo, Pageable pageable);

    @Query(value="select *, (select count(1) from luckybox_reply where luckybox_review_seq_no = lr.seq_no and status = 1) as reply_count, "
            + " 0 as friend "
            + " from luckybox_review lr "
            + " where lr.member_seq_no = :memberSeqNo "
            + " and status = 1",
            countQuery = "select count(1) from luckybox_review lr "
                    + " where lr.member_seq_no = :memberSeqNo "
                    + " and status = 1",
            nativeQuery = true)
    Page<LuckyBoxReviewDetail> findAllMy(@Param("memberSeqNo") Long memberSeqNo, Pageable pageable);

    @Query(value="select *, (select count(1) from luckybox_reply where luckybox_review_seq_no = lr.seq_no and status = 1) as reply_count, "
            + " (select count(1) from friend where member_seq_no=:memberSeqNo and friend_seq_no = lr.member_seq_no) > 0 as friend "
            + " from luckybox_review lr "
            + " where lr.seq_no = :seqNo",
            nativeQuery = true)
    LuckyBoxReviewDetail findBySeqNo(@Param("seqNo") Long seqNo, @Param("memberSeqNo") Long memberSeqNo);
}