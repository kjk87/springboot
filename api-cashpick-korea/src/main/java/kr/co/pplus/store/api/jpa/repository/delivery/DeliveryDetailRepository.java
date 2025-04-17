package kr.co.pplus.store.api.jpa.repository.delivery;

import kr.co.pplus.store.api.jpa.model.delivery.Delivery;
import kr.co.pplus.store.api.jpa.model.delivery.DeliveryDetail;
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
public interface DeliveryDetailRepository extends JpaRepository<DeliveryDetail, Long> {

    DeliveryDetail findBySeqNo(Long seqNo) ;

    @Query(value="select * from delivery"
                +" where 1=1 "
                +" AND company_seq_no = :companySeqNo "
                +" AND page_seq_no = :pageSeqNo "
                +" AND reg_datetime >= :startDuration "
                +" AND ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
    nativeQuery = true)
    Page<DeliveryDetail> findAllBy(@Param("companySeqNo") Long companySeqNo,
                                   @Param("pageSeqNo") Long pageSeqNo,
                                   @Param("startDuration") Date startDuration,
                                   @Param("endDuration") Date endDuration, Pageable pageable) ;
}