package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.MsgJpa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MsgRepository extends JpaRepository<MsgJpa, Long>{

}
