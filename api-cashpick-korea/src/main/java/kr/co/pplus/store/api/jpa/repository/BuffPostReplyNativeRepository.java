package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffPostReplyNative;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffPostReplyNativeRepository extends JpaRepository<BuffPostReplyNative, Long> {

    @Query(value="select bpr.*, fn_check_friend(:memberSeqNo,bpr.member_seq_no) as is_friend "
            + " from buff_post_reply bpr "
            + " where 1=1 "
            + " and bpr.buff_post_seq_no = :buffPostSeqNo "
            + " and bpr.deleted = false ",
            countQuery = "select count(1) "
                    + " from buff_post_reply bpr "
                    + " where 1=1 "
                    + " and bpr.buff_post_seq_no = :buffPostSeqNo "
                    + " and bpr.deleted = false ", nativeQuery = true)
    Page<BuffPostReplyNative> findAllByBuffPostSeqNo(@Param("buffPostSeqNo") Long buffPostSeqNo, @Param("memberSeqNo") Long memberSeqNo, Pageable pageable);


}
