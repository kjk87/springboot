package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsPriceOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsPriceOnlyRepository extends JpaRepository<GoodsPriceOnly, Long>{

	GoodsPriceOnly findBySeqNo(Long seqNo);

	GoodsPriceOnly findByGoodsSeqNoAndIsWholesale(Long goodsSeqNo, Boolean isWholesale);

	List<GoodsPriceOnly> findByGoodsSeqNo(Long goodsSeqNo);

	@Modifying
	@Query(value = "UPDATE goods_price set status = :status where goods_seq_no = :goodsSeqNo", nativeQuery = true)
	void updateGoodsPriceStatusByGoodsSeqNo(@Param("status") Integer status, @Param("goodsSeqNo") Long goodsSeqNo);

	@Modifying
	@Query(value = "UPDATE goods_price set status = :status where goods_seq_no = :goodsSeqNo and status = 0", nativeQuery = true)
	void updateGoodsPriceStatusByGoodsSeqNoAndSoldOut(@Param("status") Integer status, @Param("goodsSeqNo") Long goodsSeqNo);

	@Modifying
	@Query(value = "UPDATE goods_price set status = :status where seq_no = :seqNo", nativeQuery = true)
	void updateGoodsPriceStatusBySeqNo(@Param("status") Integer status, @Param("seqNo") Long seqNo);
}
