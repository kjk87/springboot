package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> {

	int countByGoodsSeqNo(Long goodsSeqNo);

	@Transactional
	void deleteAllByGoodsSeqNo(Long goodsSeqNo);

	List<GoodsImage> findAllByGoodsSeqNoAndType(Long goodsSeqNo, String type);

}
