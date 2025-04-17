package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.GiftishowBuy;
import kr.co.pplus.store.api.jpa.model.GiftishowBuyDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftishowBuyDetailRepository extends JpaRepository<GiftishowBuyDetail, Long>{

    Page<GiftishowBuyDetail> findAllByMemberSeqNoAndStatus(Long memberSeqNo, String status, Pageable pageable);

    Integer countByMemberSeqNoAndStatus(Long memberSeqNo, String status);

    GiftishowBuyDetail findBySeqNo(Long seqNo);

}
