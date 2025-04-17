package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Giftishow;
import kr.co.pplus.store.api.jpa.model.GiftishowBuy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftishowBuyRepository extends JpaRepository<GiftishowBuy, Long>{

    Page<GiftishowBuy> findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable);

    Integer countByMemberSeqNoAndGiftishowSeqNoAndStatusAndRegDatetimeGreaterThanEqual(Long memberSeqNo, Long giftishowSeqNo, String status, String regDatetime);

}
