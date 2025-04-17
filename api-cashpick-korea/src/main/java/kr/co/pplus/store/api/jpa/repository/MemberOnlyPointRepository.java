package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.MemberOnlyPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberOnlyPointRepository extends JpaRepository<MemberOnlyPoint, Long>{

    MemberOnlyPoint findBySeqNo(Long seqNo);

}
