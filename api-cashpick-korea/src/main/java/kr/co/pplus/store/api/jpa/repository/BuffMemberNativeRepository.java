package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffMemberNative;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffMemberNativeRepository extends JpaRepository<BuffMemberNative, Long> {


    @Query(value="select bm.*, fn_check_friend(:memberSeqNo,m.seq_no) as is_friend "
            + " from buff_member bm "
            + " left join member m on m.seq_no = bm.member_seq_no "
            + " where 1=1 "
            + " and bm.buff_seq_no = :buffSeqNo "
            + " and ( ISNULL(:search) = 1 or m.nickname like %:search% ) "
            + " order by bm.is_owner desc",
            countQuery = "select count(1) "
                    + " from buff_member bm "
                    + " where 1=1 "
                    + " and bm.buff_seq_no = :buffSeqNo ", nativeQuery = true)
    Page<BuffMemberNative> findAllByBuffSeqNo(@Param("buffSeqNo") Long buffSeqNo, @Param("memberSeqNo") Long memberSeqNo, @Param("search") String search, Pageable pageable);

    @Query(value="select bm.*, fn_check_friend(:memberSeqNo,m.seq_no) as is_friend "
            + " from buff_member bm "
            + " left join member m on m.seq_no = bm.member_seq_no "
            + " where 1=1 "
            + " and bm.buff_seq_no = :buffSeqNo "
            + " and bm.member_seq_no != :memberSeqNo "
            + " and ( ISNULL(:search) = 1 or m.nickname like %:search% ) "
            + " order by bm.is_owner desc",
            countQuery = "select count(1) "
                    + " from buff_member bm "
                    + " where 1=1 "
                    + " and bm.buff_seq_no = :buffSeqNo "
                    + " and bm.member_seq_no != :memberSeqNo "
                    + " and ( ISNULL(:search) = 1 or m.nickname like %:search% ) ", nativeQuery = true)
    Page<BuffMemberNative> findAllByBuffSeqNoExcludeMe(@Param("buffSeqNo") Long buffSeqNo, @Param("memberSeqNo") Long memberSeqNo, @Param("search") String search, Pageable pageable);

}
