package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Buy;
import kr.co.pplus.store.api.jpa.model.BuyDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuyRepository extends JpaRepository<Buy, Long> {

    Buy findBySeqNo(Long seqNo);

    Buy findByOrderId(String orderId) ;

    Buy findByPgTranId(String pgTranId);

    Buy findByPgAcceptId(String pgAcceptId);

    Integer deleteByOrderId(String orderId);

    Page<Buy> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);

    @Query( value = "select * from buy  where is_payment_point = :isPaymentPoint and ((pay_type = 'qr' and process = 1) or (process = 3)) and (select count(1) from member m where m.seq_no = member_seq_no) > 0", nativeQuery = true)
    List<Buy> findAllByIsPaymentPoint(Boolean isPaymentPoint);

    Integer countAllByMemberSeqNoAndProcessGreaterThan(Long memberSeqNo, Integer process);

    Integer countAllByProcessGreaterThan(Integer process);

    Integer countAllBy() ;

    @Query(value="select * from buy "
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
            +" and ( isnull(:process) = 1 or process = :process ) "
            +" and ( type = 1 or isnull(:orderType) = 1 or order_type = :orderType ) "
            +" and ( type = 1 or isnull(:orderProcess) = 1 or order_process = :orderProcess ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
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
                    +" and ( isnull(:process) = 1 or process = :process ) "
                    +" and ( type = 1 or isnull(:orderType) = 1 or order_type = :orderType ) "
                    +" and ( type = 1 or isnull(:orderProcess) = 1 or order_process = :orderProcess ) "
                    +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
                    +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Page<Buy> findAllByWith(@Param("seqNo") Long seqNo,
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

    @Query(value="select * from buy "
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
            +" and ( isnull(:process) = 1 or process = :process ) "
            +" and ( isnull(:orderType) = 1 or order_type = :orderType ) "
            +" and ( isnull(:orderProcess) = 1 or order_process = :orderProcess ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
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
                    +" and ( isnull(:process) = 1 or process = :process ) "
                    +" and ( isnull(:orderType) = 1 or order_type = :orderType ) "
                    +" and ( isnull(:orderProcess) = 1 or order_process = :orderProcess ) "
                    +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
                    +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Page<Buy> findAllByWithCustom(@Param("seqNo") Long seqNo,
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
    /*
    @Query(value="select count(1) from buy "
            +" where 1=1 "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:type) = 1 or type = :type ) "
            +" and ( order_process is not null and ( isnull(:orderProcess) = 1 or order_process = :orderProcess ) ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Integer countAllByOrderProcess(@Param("orderProcess") Integer orderProcess,
                                                                    @Param("pageSeqNo") Long  pageSeqNo,
                                                                    @Param("type") Integer type,
                                                                    @Param("startDuration") Date startDuration,
                                                                    @Param("endDuration") Date endDuration);

    @Query(value="select order_type, count(1) from buy "
            +" where 1=1 "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:type) = 1 or type = :type ) "
            +" and ( order_type is not null and ( isnull(:orderType) = 1 or order_type = :orderType ) ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Integer countAllByOrderType(@Param("orderType") Integer orderType,
                                                                    @Param("pageSeqNo") Long  pageSeqNo,
                                                                    @Param("type") Integer type,
                                                                    @Param("startDuration") Date startDuration,
                                                                    @Param("endDuration") Date endDuration);

     */

    @Query(value="select ifnull(sum(bg.price), 0) as price, count(1) as count "
            +"  from buy_goods bg "
            +"      inner join goods g on g.seq_no = bg.goods_seq_no "
            +" where 1=1 "
            +" and ( isnull(:pageSeqNo)     = 1 or bg.page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:type)          = 1 or g.type = :type ) "
            +" and ( ( :isHotdeal = true   and :isPlus = true  and  (g.is_hotdeal = 1 or g.is_plus = 1) )  "
            + "   or ( :isHotdeal = false  and :isPlus = false and g.is_hotdeal = 0 and g.is_plus = 0 )  "
            + "   or ( :isHotdeal = true   and :isPlus = false and g.is_hotdeal = 1 )  "
            + "   or ( :isHotdeal = false  and :isPlus = true  and g.is_plus = 1 )  "
            + "   or ( isnull(:isHotdeal) = 1  and isnull(:isPlus) = 1  )  "
            + "   ) "
            +" and ( isnull(:startDuration) = 1 or bg.reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration)   = 1 or bg.reg_datetime <= :endDuration ) "
            +" and (   ( g.type = 0 and (bg.order_process is not null and bg.order_process = 2 ) ) "
            +"      or ( g.type = 1 and (bg.process is not null and bg.process = 3 ) )  "
            +"      or ( g.type = 1 and (bg.process is not null and bg.process = 3 ) )  )",
            nativeQuery = true)
    Map<String,Object> priceAllByGoodsType(@Param("pageSeqNo") Long pageSeqNo,
                                           @Param("type") Integer type,
                                           @Param("isHotdeal") Boolean isHotdeal,
                                           @Param("isPlus") Boolean isPlus,
                                           @Param("startDuration") Date startDuration,
                                           @Param("endDuration") Date endDuration);


    @Query(value = "select ifnull(sum(price), 0) as price from buy "
            +" where 1=1 "
            +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
            +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
            +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
            +" and ( isnull(:process) = 1 or process = :process ) "
            +" and ( type = :type ) "
            +" and ( isnull(:orderType) = 1 or order_type = :orderType) "
            +" and ( isnull(:orderProcess) = 1 or order_process = :orderProcess)  "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Float priceAllByWithCustom(@Param("seqNo") Long seqNo,
                         @Param("memberSeqNo") Long memberSeqNo,
                         @Param("orderId") String orderId,
                         @Param("pgTranId") String pgTranId,
                         @Param("pgAcceptId") String pgAcceptId,
                         @Param("pageSeqNo") Long pageSeqNo,
                         @Param("process") Integer process,
                         @Param("type") Integer type,
                         @Param("orderType") Integer orderType,
                         @Param("orderProcess") Integer orderProcess,
                         @Param("startDuration") Date startDuration,
                         @Param("endDuration") Date endDuration) ;


    @Query(value = "select ifnull(sum(price), 0) as price from buy "
            +" where 1=1 "
            +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
            +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
            +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
            +" and ( isnull(:process) = 1 or process = :process ) "
            +" and ( type = :type ) "
            +" and ( "
            +"      (   ( type = 0 and (isnull(:orderType) = 1 or order_type = :orderType) ) "
            +"      and ( type = 0 and ( (isnull(:orderProcess) = 1 and order_process =2) or (isnull(:orderProcess) = 0 and order_process = :orderProcess) ) ) "
            +"      )  or ( type = 1 and ( (isnull(:process) = 1 and process = 3) or (isnull(:process) = 0 and process = :process) ) ) "
            +"     )"
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Float priceAllByWith(@Param("seqNo") Long seqNo,
                         @Param("memberSeqNo") Long memberSeqNo,
                         @Param("orderId") String orderId,
                         @Param("pgTranId") String pgTranId,
                         @Param("pgAcceptId") String pgAcceptId,
                         @Param("pageSeqNo") Long pageSeqNo,
                         @Param("process") Integer process,
                         @Param("type") Integer type,
                         @Param("orderType") Integer orderType,
                         @Param("orderProcess") Integer orderProcess,
                         @Param("startDuration") Date startDuration,
                         @Param("endDuration") Date endDuration) ;


    @Query(value = "select count(1) from buy "
            +" where 1=1 "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( type = :type ) "
            +" and ( process =  1 or process = 4 ) "
            +" and ( isnull(order_process)=1 or order_process <> 3 ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Integer countAllTotal(@Param("memberSeqNo") Long memberSeqNo,
                          @Param("pageSeqNo") Long pageSeqNo,
                          @Param("type") Integer type,
                          @Param("startDuration") Date startDuration,
                          @Param("endDuration") Date endDuration) ;



    @Query(value = "select count(1) from buy "
            +" where 1=1 "
            +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
            +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
            +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
            +" and ( isnull(:process) = 1 or process = :process ) "
            +" and ( type = :type ) "
            +" and ( "
            +"      (   ( type = 0 and (isnull(:orderType) = 1 or order_type = :orderType) ) "
            +"      and ( type = 0 and ( (isnull(:orderProcess) = 1 and order_process =2) or (isnull(:orderProcess) = 0 and order_process = :orderProcess) ) ) "
            +"      )  or ( type = 1 and ( (isnull(:process) = 1 and process = 3) or (isnull(:process) = 0 and process = :process) ) ) "
            +"     )"
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Integer countAllByWith(@Param("seqNo") Long seqNo,
                           @Param("memberSeqNo") Long memberSeqNo,
                           @Param("orderId") String orderId,
                           @Param("pgTranId") String pgTranId,
                           @Param("pgAcceptId") String pgAcceptId,
                           @Param("pageSeqNo") Long pageSeqNo,
                           @Param("process") Integer process,
                           @Param("type") Integer type,
                           @Param("orderType") Integer orderType,
                           @Param("orderProcess") Integer orderProcess,
                           @Param("startDuration") Date startDuration,
                           @Param("endDuration") Date endDuration) ;


    @Query(value = "select order_process as orderProcess, count(*) as count from buy "
            +" where 1=1 "
            +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
            +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
            +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
            +" and ( type = :type ) "
            +" and ( type = 0 and order_type is not null )"
            +" and ( isnull(:orderType) = 1 or order_type = :orderType ) "
            +" and ( isnull(:orderProcess) = 1 or order_process = :orderProcess ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) group by order_process",
            nativeQuery = true)
    List<Map<String,Object>> countAllPerOrderProcess(@Param("seqNo") Long seqNo,
                                                     @Param("memberSeqNo") Long memberSeqNo,
                                                     @Param("orderId") String orderId,
                                                     @Param("pgTranId") String pgTranId,
                                                     @Param("pgAcceptId") String pgAcceptId,
                                                     @Param("pageSeqNo") Long pageSeqNo,
                                                     @Param("type") Integer type,
                                                     @Param("orderType") Integer orderType,
                                                     @Param("orderProcess") Integer orderProcess,
                                                     @Param("startDuration") Date startDuration,
                                                     @Param("endDuration") Date endDuration) ;

    @Query(value = "select order_type as orderType, count(*) as count from buy "
            +" where 1=1 "
            +" and ( isnull(:seqNo) = 1 or seq_no = :seqNo ) "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( isnull(:orderId) = 1 or order_id = :orderId ) "
            +" and ( isnull(:pgTranId) = 1 or pg_tran_id = :pgTranId ) "
            +" and ( isnull(:pgAcceptId) = 1 or pg_accept_id = :pgAcceptId ) "
            +" and ( type = :type and type = 0 ) "
            +" and ( ( isnull(:process) = 0 and process = :process ) or ( isnull(:process) = 1 and process > 0 ) ) "
            +" and ( order_type is not null and ( isnull(:orderType) = 1 or order_type = :orderType ) ) "
            +" and ( order_process is not null and ( isnull(:orderProcess) = 1 or order_process = :orderProcess ) ) "
            +" and ( isnull(:orderType) = 1 or order_type = :orderType ) "
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) group by order_type",
            nativeQuery = true)
    List<Map<String,Object>> countAllPerOrderType(@Param("seqNo") Long seqNo,
                                                  @Param("memberSeqNo") Long memberSeqNo,
                                                  @Param("orderId") String orderId,
                                                  @Param("pgTranId") String pgTranId,
                                                  @Param("pgAcceptId") String pgAcceptId,
                                                  @Param("pageSeqNo") Long pageSeqNo,
                                                  @Param("type") Integer type,
                                                  @Param("process") Integer process,
                                                  @Param("orderType") Integer orderType,
                                                  @Param("orderProcess") Integer orderProcess,
                                                  @Param("startDuration") Date startDuration,
                                                  @Param("endDuration") Date endDuration) ;

    @Query(value = "select ifnull(sum(price), 0) as price from buy "
            +" where 1=1 "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( (process =  1 and pay_type = 'qr') or (process = 3 and pay_type = 'online' and order_type = 0) or (order_type = 3 and process = 1) )"
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Float priceCompleteQrOrUse(@Param("memberSeqNo") Long memberSeqNo,
                               @Param("pageSeqNo") Long pageSeqNo,
                               @Param("startDuration") Date startDuration,
                               @Param("endDuration") Date endDuration) ;

    @Query(value = "select count(1) from buy "
            +" where 1=1 "
            +" and ( isnull(:memberSeqNo) = 1 or member_seq_no = :memberSeqNo ) "
            +" and ( isnull(:pageSeqNo) = 1 or page_seq_no = :pageSeqNo ) "
            +" and ( (process =  1 and pay_type = 'qr') or (process = 3 and pay_type = 'online' and order_type = 0) or (order_type = 3 and process = 1) )"
            +" and ( isnull(:startDuration) = 1 or reg_datetime >= :startDuration ) "
            +" and ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Integer countCompleteQrOrUseOrShipping(@Param("memberSeqNo") Long memberSeqNo,
                               @Param("pageSeqNo") Long pageSeqNo,
                               @Param("startDuration") Date startDuration,
                               @Param("endDuration") Date endDuration) ;

    @Modifying
    @Query("UPDATE buy set process = :process, mod_datetime = :modDatetime where seq_no = :seqNo")
    void updateExpiredBySeqNo(@Param("process") Integer process, @Param("seqNo") Long seqNo, @Param("modDatetime") String modDatetime);


}
