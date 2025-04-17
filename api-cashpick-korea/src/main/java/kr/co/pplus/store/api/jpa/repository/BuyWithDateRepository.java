package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Buy;
import kr.co.pplus.store.api.jpa.model.BuyWithDate;
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
public interface BuyWithDateRepository extends JpaRepository<BuyWithDate, Long> {

    BuyWithDate findBySeqNo(Long seqNo);

    BuyWithDate findByOrderId(String orderId);

    List<BuyWithDate> findAllByOrderProcessAndOrderDatetimeLessThan(Integer orderProcess, Date orderDatetime) ;

}