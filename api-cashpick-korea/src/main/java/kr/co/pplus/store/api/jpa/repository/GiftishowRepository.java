package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Giftishow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftishowRepository extends JpaRepository<Giftishow, Long>{

    Giftishow findBySeqNo(Long seqNo);

    Page<Giftishow> findAllBySaleAndGoodsStateCdAndDiscountRateGreaterThanEqualOrderByPriorityDescSeqNoDesc(Boolean sale, String goodsStateCd, Float discountRate, Pageable pageable);

    Page<Giftishow> findAllByGiftishowCategorySeqNoAndSaleAndGoodsStateCdAndDiscountRateGreaterThanEqualOrderByPriorityDescSeqNoDesc(Long giftishowCategorySeqNo, Boolean sale, String goodsStateCd, Float discountRate, Pageable pageable);

    Page<Giftishow> findAllByBrandSeqNoAndSaleAndGoodsStateCdOrderByPriorityDescSeqNoDesc(Long brandSeqNo, Boolean sale, String goodsStateCd, Pageable pageable);

    Page<Giftishow> findAllByBrandSeqNoAndSaleAndGoodsStateCdAndRealPriceGreaterThanEqualOrderByPriorityDescSeqNoDesc(Long brandSeqNo, Boolean sale, String goodsStateCd, Integer realPrice, Pageable pageable);
}
