package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ShippingSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShippingSiteRepository extends JpaRepository<ShippingSite, Long>{

    List<ShippingSite> findAllByMemberSeqNoOrderByIsDefaultDesc(Long memberSeqNo);

    ShippingSite findBySeqNo(Long seqNo);
}
