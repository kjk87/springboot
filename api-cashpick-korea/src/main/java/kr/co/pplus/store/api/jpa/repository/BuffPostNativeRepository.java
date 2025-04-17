package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffPost;
import kr.co.pplus.store.api.jpa.model.BuffPostNative;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffPostNativeRepository extends JpaRepository<BuffPostNative, Long> {

    @Query(value="select bp.*, fn_check_friend(:memberSeqNo,bp.member_seq_no) as is_friend "
            + ", (select count(1) from buff_post_like where buff_post_seq_no = bp.seq_no) as like_count "
            + ", (select count(1) from buff_post_reply where buff_post_seq_no = bp.seq_no) as reply_count "
            + ", (select count(1) from buff_post_like where buff_post_seq_no = bp.seq_no and member_seq_no = :memberSeqNo) > 0 as is_like "
            + " from buff_post bp "
            + " where 1=1 "
            + " and bp.buff_seq_no = :buffSeqNo "
            + " and bp.deleted = false ",
            countQuery = "select count(1) "
                    + " from buff_post bp "
                    + " where 1=1 "
                    + " and bp.buff_seq_no = :buffSeqNo "
                    + " and bp.deleted = false ", nativeQuery = true)
    Page<BuffPostNative> findAllByBuffSeqNo(@Param("buffSeqNo") Long buffSeqNo, @Param("memberSeqNo") Long memberSeqNo, Pageable pageable);


}
