package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuyDetail;
import kr.co.pplus.store.api.jpa.model.BuyDetailGuest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuyDetailGuestRepository extends JpaRepository<BuyDetailGuest, Long> {

    BuyDetailGuest findBySeqNo(Long seqNo);


    BuyDetailGuest  findByOrderId(String orderId) ;

    BuyDetailGuest  findByPgTranId(String pgTranId);

    BuyDetailGuest  findByPgAcceptId(String pgAcceptId);

    Page<BuyDetailGuest> findAllByMemberSeqNoAndProcessGreaterThan(Long memberSeqNo, Integer process, Pageable pageable);

    Page<BuyDetailGuest> findAllByProcessGreaterThan(Integer process, Pageable pageable) ;

    @Query(value="select * from buy "
            +" where 1=1 "
            +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:type) = 1 or type = :type ) "
            +" and ( "
            + "      ( :isHotdeal = true   and :isPlus = true  and is_hotdeal = 1 or is_plus = 1 )  "
            + "   or ( :isHotdeal = false  and :isPlus = false and is_hotdeal = 0 and is_plus = 0 )  "
            + "   or ( :isHotdeal = true   and :isPlus = false and is_hotdeal = 1 )  "
            + "   or ( :isHotdeal = false  and :isPlus = true  and is_plus = 1 )  "
            + "   or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "   ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
            +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
            +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
            +" and ( isnull(:process) = 1 or process = :process ) "
            +" and ( :type = 1 or isnull(:orderType) = 1 or (order_type is not null and isnull(:orderType) = 0 and order_type = :orderType) ) "
            +" and ( :type = 1 or isnull(:orderProcess) = 1 or (order_process is not null and isnull(:orderProcess) = 0 and order_process = :orderProcess) ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            countQuery = "select count(1) from buy "
                    +" where 1=1 "
                    +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
                    +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
                    +" and ( isnull(:type) = 1 or type = :type ) "
                    +" and ( ( :isHotdeal = true   and :isPlus = true  and is_hotdeal = 1 or is_plus = 1 )  "
                    + "   or ( :isHotdeal = false  and :isPlus = false and is_hotdeal = 0 and is_plus = 0 )  "
                    + "   or ( :isHotdeal = true   and :isPlus = false and is_hotdeal = 1 )  "
                    + "   or ( :isHotdeal = false  and :isPlus = true  and is_plus = 1 )  "
                    + "   or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
                    + "   ) "
                    +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
                    +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
                    +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
                    +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
                    +" and ( isnull(:process) = 1 or process = :process ) "
                    +" and ( :type = 1 or isnull(:orderType) = 1 or (order_type is not null and isnull(:orderType) = 0 and order_type = :orderType) ) "
                    +" and ( :type = 1 or isnull(:orderProcess) = 1 or (order_process is not null and isnull(:orderProcess) = 0 and order_process = :orderProcess) ) "
                    +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
                    +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Page<BuyDetailGuest> findAllByWith(@Param("seqNo") Long seqNo,
                                       @Param("memberSeqNo") Long memberSeqNo,
                                       @Param("type") Integer type,
                                       @Param("isHotdeal") Boolean isHotdeal,
                                       @Param("isPlus") Boolean isPlus,
                                       @Param("orderId") String orderId,
                                       @Param("pgTranId") String pgTranId,
                                       @Param("pgAcceptId") String pgAcceptId,
                                       @Param("pageSeqNo") Long pageSeqNo,
                                       @Param("process") Integer process,
                                       @Param("orderType") Integer orderType,
                                       @Param("orderProcess") Integer orderProcess,
                                       @Param("startDuration") Date startDuration,
                                       @Param("endDuration") Date endDuration, Pageable pageable) ;

}