package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.MemberOnlyBol;
import kr.co.pplus.store.api.jpa.model.MemberOnlyPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberOnlyBolRepository extends JpaRepository<MemberOnlyBol, Long>{

    MemberOnlyBol findBySeqNo(Long seqNo);

}
