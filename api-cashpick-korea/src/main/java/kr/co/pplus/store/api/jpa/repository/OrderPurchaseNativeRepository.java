package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.OrderPurchaseDetail;
import kr.co.pplus.store.api.jpa.model.OrderPurchaseNative;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface OrderPurchaseNativeRepository extends JpaRepository<OrderPurchaseNative, Long> {


    @Query(value="select op.* from order_purchase op "
            + " inner join member m on m.seq_no = op.member_seq_no "
            + " where op.page_seq_no = :pageSeqNo "
            + " and isnull(:status) = 1 or op.status = :status "
            + " and isnull(:nickName) = 1 or m.nickname like '%' || :nickName || '%' "
            + " and isnull(:phone) = 1 or op.phone like '%' || :phone || '%' "
            + " and op.reg_datetime >= :startDateTime "
            + " and op.reg_datetime <= :endDateTime ",
            countQuery = "select count(1) from order_purchase op "
                    + " inner join member m on m.seq_no = op.member_seq_no "
                    + " where op.page_seq_no = :pageSeqNo "
                    + " and isnull(:status) = 1 or op.status = :status "
                    + " and isnull(:nickName) = 1 or m.nickname like '%' || :nickName || '%' "
                    + " and isnull(:phone) = 1 or op.phone like '%' || :phone || '%' "
                    + " and op.reg_datetime >= :startDateTime "
                    + " and op.reg_datetime <= :endDateTime ",
            nativeQuery = true)
    Page<OrderPurchaseNative> findAllTicketByPageSeqNo(Long pageSeqNo, Integer status, String nickName, String phone, String startDateTime, String endDateTime, Pageable pageable);

}