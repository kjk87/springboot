package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Buy;
import kr.co.pplus.store.api.jpa.model.BuyDetail;
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
public interface BuyDetailRepository extends JpaRepository<BuyDetail, Long> {

    @Query(value = "select *, ((select count(*) from goods_review where member_seq_no = buy.member_seq_no and buy_seq_no = buy.seq_no) > 0) as is_review_exist from buy"
            +" where seq_no=:seqNo", nativeQuery = true)
    BuyDetail findBySeqNo(Long seqNo);

    @Query(value = "select *, ((select count(*) from goods_review where member_seq_no = buy.member_seq_no and buy_seq_no = buy.seq_no) > 0) as is_review_exist from buy"
            +" where order_id=:orderId", nativeQuery = true)
    BuyDetail  findByOrderId(String orderId) ;

    @Query(value = "select *, ((select count(*) from goods_review where member_seq_no = buy.member_seq_no and buy_seq_no = buy.seq_no) > 0) as is_review_exist from buy"
            +" where pg_tran_id=:pgTranId", nativeQuery = true)
    BuyDetail  findByPgTranId(String pgTranId);

    @Query(value = "select *, ((select count(*) from goods_review where member_seq_no = buy.member_seq_no and buy_seq_no = buy.seq_no) > 0) as is_review_exist from buy"
            +" where pg_accept_id=:pgAcceptId", nativeQuery = true)
    BuyDetail  findByPgAcceptId(String pgAcceptId);

    @Query(value = "select count(1) from buy "
            +" where 1=1 "
            +" and member_seq_no = :memberSeqNo and  process > 0 ", nativeQuery = true)
    Integer countAllByMemberSeqNo(Long memberSeqNo);

    // ㄱㅜ매내역
    @Query(value="select *, ((select count(*) from goods_review where member_seq_no = buy.member_seq_no and buy_seq_no = buy.seq_no) > 0) as is_review_exist from buy"
            +" where 1=1 "
            +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:type) = 1 or type = :type ) "
            +" and ( ( :isHotdeal = true   and :isPlus = true  and  (is_hotdeal = 1 or is_plus = 1) )  "
            + "   or ( :isHotdeal = false  and :isPlus = false and is_hotdeal = 0 and is_plus = 0 )  "
            + "   or ( :isHotdeal = true   and :isPlus = false and is_hotdeal = 1 )  "
            + "   or ( :isHotdeal = false  and :isPlus = true  and is_plus = 1 )  "
            + "   or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "   ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
            +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
            +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
            +" and ( ( isnull(:process) = 0 and process = :process ) or ( isnull(:process) = 1 and process > 0 ) ) "
            +" and ( isnull(:orderType) = 1 or order_type = :orderType ) "
            +" and ( isnull(:orderProcess) = 1 or order_process = :orderProcess ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration )"
            +" and ( isnull(:exclude) = 1 or order_type != :exclude )"
            +" and ( isnull(:payType) = 1 or pay_type = :payType )",

           // +" ORDER BY CASE WHEN isnull(:sort)=1 THEN seq_no DESC ELSE :sort END",
            countQuery = "select count(1) from buy "
                    +" where 1=1 "
                    +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
                    +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
                    +" and ( isnull(:type) = 1 or type = :type ) "
                    +" and ( ( :isHotdeal = true   and :isPlus = true  and  (is_hotdeal = 1 or is_plus = 1) )  "
                    + "   or ( :isHotdeal = false  and :isPlus = false and is_hotdeal = 0 and is_plus = 0 )  "
                    + "   or ( :isHotdeal = true   and :isPlus = false and is_hotdeal = 1 )  "
                    + "   or ( :isHotdeal = false  and :isPlus = true  and is_plus = 1 )  "
                    + "   or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
                    + "   ) "
                    +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
                    +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
                    +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
                    +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
                    +" and ( ( isnull(:process) = 0 and process = :process ) or ( isnull(:process) = 1 and process > 0 ) ) "
                    +" and ( isnull(:orderType) = 1 or order_type = :orderType  ) "
                    +" and ( isnull(:orderProcess) = 1 or order_process = :orderProcess ) "
                    +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
                    +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) "
                    +" and ( isnull(:exclude) = 1 or order_type != :exclude )"
                    +" and ( isnull(:payType) = 1 or pay_type = :payType )",
            nativeQuery = true)
    Page<BuyDetail> findAllOrderByWith(@Param("seqNo") Long seqNo,
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
                                       @Param("endDuration") Date endDuration,
                                       @Param("exclude") Integer exclude, @Param("payType") String payType, Pageable pageable) ;

    @Query(value = " select ifnull(sum(price), 0) as price from buy "
            +" where 1=1 "
            +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:type) = 1 or type = :type ) "
            +" and ( ( :isHotdeal = true   and :isPlus = true  and  (is_hotdeal = 1 or is_plus = 1) )  "
            + "   or ( :isHotdeal = false  and :isPlus = false and is_hotdeal = 0 and is_plus = 0 )  "
            + "   or ( :isHotdeal = true   and :isPlus = false and is_hotdeal = 1 )  "
            + "   or ( :isHotdeal = false  and :isPlus = true  and is_plus = 1 )  "
            + "   or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "   ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
            +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
            +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
            +" and ( ( isnull(:process) = 0 and process = :process ) or ( isnull(:process) = 1 and process > 0 ) ) "
            +" and ( :type = 1 or isnull(:orderType) = 1 or (order_type is not null and ( isnull(:orderType) = 0 and order_type = :orderType) ) ) "
            +" and ( :type = 1 or isnull(:orderProcess) = 1 or (order_process is not null and ( isnull(:orderProcess) = 0 and order_process = :orderProcess) ) ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) "
            +" and ( isnull(:exclude) = 1 or order_type != :exclude )"
            +" and ( isnull(:payType) = 1 or pay_type = :payType )",
    nativeQuery = true)
    Float findAllOrderByWithPrice(@Param("seqNo") Long seqNo,
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
                                       @Param("endDuration") Date endDuration,
                                       @Param("exclude") Integer exclude,
                                       @Param("payType") String payType) ;

    /*
    @Query(value = "select count(1) from buy "
                    +" where 1=1 "
                    +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
                    +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
                    +" and ( isnull(:type) = 1 or type = :type ) "
                    +" and ( isnull(:isHotdeal) = 1 or is_hotdeal = :isHotdeal ) "
                    +" and ( isnull(:isPlus) = 1 or is_plus = :isPlus ) "
                    +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
                    +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
                    +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
                    +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
                    +" and ( ( isnull(:process) = 0 and process = :process ) or ( isnull(:process) = 1 and process > 0 ) ) "
                    +" and ( order_type is not null and ( isnull(:orderType) = 1 or order_type = :orderType ) ) "
                    +" and ( order_process is not null and ( isnull(:orderProcess) = 1 or order_process = :orderProcess ) ) "
                    +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
                    +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Page<BuyDetail> countByOrderProcess(@Param("seqNo") Long seqNo,
                                       @Param("memberSeqNo") Long memberSeqNo,
                                       @Param("type") Integer type,
                                       @Param("isHotdeal") Boolean isHotdeal,
                                       @Param("isPlus") Integer isPlus,
                                       @Param("orderId") String orderId,
                                       @Param("pgTranId") String pgTranId,
                                       @Param("pgAcceptId") String pgAcceptId,
                                       @Param("pageSeqNo") Long  pageSeqNo,
                                       @Param("process")Integer process,
                                       @Param("orderType") Integer orderType,
                                       @Param("orderProcess") Integer orderProcess,
                                       @Param("startDuration") Date startDuration,
                                       @Param("endDuration") Date endDuration, Pageable pageable) ;

     */

}
