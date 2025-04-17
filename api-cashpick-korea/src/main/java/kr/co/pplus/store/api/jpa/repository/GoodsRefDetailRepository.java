package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Goods;
import kr.co.pplus.store.api.jpa.model.GoodsRefDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsRefDetailRepository extends JpaRepository<GoodsRefDetail, Long> {

    GoodsRefDetail findBySeqNo(Long seqNo) ;
}
