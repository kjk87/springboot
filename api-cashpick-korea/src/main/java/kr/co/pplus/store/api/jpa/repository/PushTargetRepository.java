package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.MsgJpa;
import kr.co.pplus.store.api.jpa.model.PushTargetJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushTargetRepository extends JpaRepository<PushTargetJpa, Long>{

}
