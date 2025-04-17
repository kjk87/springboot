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
import java.util.List;
import java.util.Map;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Delivery findBySeqNo(Long seqNo) ;

    @Query(value="select * from delivery"
            +" where 1=1 "
            +" AND company_seq_no = :companySeqNo "
            +" AND page_seq_no = :pageSeqNo "
            +" AND reg_datetime >= :startDuration "
            +" AND ( isnull(:endDuration) = 1 or reg_datetime <= :endDuration ) ",
            nativeQuery = true)
    Page<Delivery> findAllBy(@Param("companySeqNo") Long companySeqNo,
                             @Param("pageSeqNo") Long pageSeqNo,
                             @Param("startDuration") Date startDuration,
                             @Param("endDuration") Date endDuration, Pageable pageable) ;

    @Query(value = "SELECT d.company_seq_no AS companySeqNo,  "
            + "       Sum(d.price) AS price,  "
            + "       Sum(d.count) AS count  "
            + " FROM  ("
            + "         (SELECT 1                     AS company_seq_no,  "
            + "                Ifnull(Sum(price), 0) AS price,  "
            + "                Count(1)              AS count  "
            + "         FROM   buy  "
            + "         WHERE  page_seq_no = :pageSeqNo  "
            + "                AND order_process = 2 AND type = 0  "
            + "                AND :startDuration <= reg_datetime  "
            + "                AND ( Isnull(:endDuration) = 1  "
            + "                       OR reg_datetime < :endDuration ) "
            + "         )  "
            + "        UNION ALL  "
            + "        (SELECT company_seq_no,  "
            + "                Sum(total_price) AS price,  "
            + "                Count(1)         AS count  "
            + "         FROM   delivery  "
            + "         WHERE  page_seq_no = :pageSeqNo  "
            + "                AND :startDuration <= reg_datetime  "
            + "                AND ( Isnull(:endDuration) = 1  "
            + "                       OR reg_datetime < :endDuration )  "
            + "         GROUP  BY company_seq_no "
            + "         )  "
            + "        UNION ALL  "
            + "        (SELECT 2 AS company_seq_no,  "
            + "                0 AS price,  "
            + "                0 AS count)  "
            + "        UNION ALL  "
            + "        (SELECT 3 AS company_seq_no,  "
            + "                0 AS price,  "
            + "                0 AS count)  "
            + "        UNION ALL  "
            + "        (SELECT 4 AS company_seq_no,  "
            + "                0 AS price,  "
            + "                0 AS count) "
            + " ) d  "
            + "GROUP  BY d.company_seq_no  "
            + "ORDER  BY d.company_seq_no  ", nativeQuery = true)
    List<Map<String,Object>> selectDeliveryTotal(@Param("pageSeqNo") Long pageSeqNo,
                                                 @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration);

    @Query(value = "SELECT d.payment,  "
            + "       Sum(d.price) AS price,  "
            + "       Sum(d.count) AS count  "
            + "FROM ( "
            + "         (SELECT payment,  "
            + "                Sum(total_price) AS price,  "
            + "                Count(1)         AS count  "
            + "         FROM   delivery  "
            + "         WHERE  page_seq_no = :pageSeqNo  "
            + "                AND company_seq_no = :companySeqNo  "
            + "                AND :startDuration <= reg_datetime  "
            + "                AND ( Isnull(:endDuration) = 1  "
            + "                       OR reg_datetime < :endDuration )  "
            + "         GROUP  BY payment "
            + "         )  "
            + "        UNION ALL  "
            + "        (SELECT '선결제' AS payment,  "
            + "                0           AS price,  "
            + "                0           AS count)  "
            + "        UNION ALL  "
            + "        (SELECT '카드' AS payment,  "
            + "                0        AS price,  "
            + "                0        AS count)  "
            + "        UNION ALL  "
            + "        (SELECT '현금' AS payment,  "
            + "                0        AS price,  "
            + "                0        AS count) "
            + " ) d  "
            + "GROUP  BY d.payment  "
            + "ORDER  BY d.payment  ", nativeQuery = true)
    List<Map<String,Object>> selectDeliveryCompanyTotal(@Param("pageSeqNo") Long pageSeqNo, @Param("companySeqNo") Long companySeqNo,
                                                        @Param("startDuration") Date startDuration, @Param("endDuration") Date endDuration);
}
