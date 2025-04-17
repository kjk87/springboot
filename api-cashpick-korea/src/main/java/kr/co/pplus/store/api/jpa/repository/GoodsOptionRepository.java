package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface GoodsOptionRepository extends JpaRepository<GoodsOption, Long>{

	List<GoodsOption> findByGoodsSeqNoOrderBySeqNoAsc(Long goodsSeqNo);

	GoodsOption findBySeqNo(Long seqNo);

	void deleteByGoodsSeqNo(Long goodsSeqNo);

}
