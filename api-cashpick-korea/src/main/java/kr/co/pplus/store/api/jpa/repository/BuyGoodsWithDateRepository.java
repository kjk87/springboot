package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuyGoods;
import kr.co.pplus.store.api.jpa.model.BuyGoodsWithDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuyGoodsWithDateRepository extends JpaRepository<BuyGoodsWithDate, Long> {

    BuyGoodsWithDate findBySeqNo(Long seqNo) ;

    List<BuyGoodsWithDate> findAllByProcessAndExpireDatetimeLessThan(Integer process, Date now) ;

    List<BuyGoodsWithDate> findAllByOrderProcessAndOrderDatetimeLessThan(Integer orderProcess, Date now) ;

}