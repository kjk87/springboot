package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Buff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffRepository extends JpaRepository<Buff, Long> {

    Buff findBySeqNo(Long seqNo);

    @Modifying
    @Query(value="update buff set owner = :ownerSeqNo where seq_no = :buffSeqNo", nativeQuery = true)
    void updateOwner(Long buffSeqNo, Long ownerSeqNo) ;
}
