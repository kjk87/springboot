package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsOptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsOptionDetailRepository extends JpaRepository<GoodsOptionDetail, Long>{

	List<GoodsOptionDetail> findByGoodsSeqNoOrderBySeqNoAsc(Long goodsSeqNo);

	GoodsOptionDetail findBySeqNo(Long seqNo);

	@Modifying
	@Query("update goodsOptionDetail set sold_count = sold_count + :amount where seqNo = :seqNo")
	void updatePlusSoldCountBySeqNo(@Param("seqNo") Long seqNo, @Param("amount") Integer amount);

	@Modifying
	@Query("update goodsOptionDetail set sold_count = sold_count - :amount where seqNo = :seqNo")
	void updateMinusSoldCountBySeqNo(@Param("seqNo") Long seqNo, @Param("amount") Integer amount);

}
