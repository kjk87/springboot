package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffPostLikeNative;
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
public interface BuffPostLikeNativeRepository extends JpaRepository<BuffPostLikeNative, Long> {

    @Query(value="select bpl.*, fn_check_friend(:memberSeqNo,bpl.member_seq_no) as is_friend "
            + " from buff_post_like bpl "
            + " where 1=1 "
            + " and bpl.buff_post_seq_no = :buffPostSeqNo ",
            countQuery = "select count(1) "
                    + " from buff_post_like bpl "
                    + " where 1=1 "
                    + " and bpl.buff_post_seq_no = :buffPostSeqNo ", nativeQuery = true)
    Page<BuffPostLikeNative> findAllByBuffPostSeqNo(@Param("buffPostSeqNo") Long buffPostSeqNo, @Param("memberSeqNo") Long memberSeqNo, Pageable pageable);


}
