package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Goods;
import kr.co.pplus.store.api.jpa.model.GoodsWithDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsWithDateRepository extends JpaRepository<GoodsWithDate, Long> {

    GoodsWithDate findBySeqNo(Long seqNo) ;

    @Modifying
    @Query(value="update goods set status = -1 where (status = 1 or status= -2) and expire_datetime < now()", nativeQuery=true)
    void updateExpiredGoods() ;

    @Query(value="select * from goods where (status = 1 or status= -2) and expire_datetime < now()", nativeQuery=true)
    List<GoodsWithDate> findAllExpireGoods();

    @Modifying
    @Query(value="update goods set status = :status where seq_no = :goodsSeqNo", nativeQuery=true)
    void updateGoodsStatusByGoodsSeqNo(Long goodsSeqNo, Integer status) ;

}
