package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GiftishowTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiftishowTargetRepository extends JpaRepository<GiftishowTarget, Long>{

    List<GiftishowTarget> findAllByGiftishowBuySeqNo(Long giftshowBuySeqNo);
}
