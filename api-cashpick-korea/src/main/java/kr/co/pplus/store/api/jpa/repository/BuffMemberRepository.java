package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffMemberRepository extends JpaRepository<BuffMember, Long> {

    BuffMember findBySeqNo(Long seqNo);

    BuffMember findFirstByMemberSeqNo(Long memberSeqNo);

    BuffMember findFirstByMemberSeqNoAndBuffSeqNo(Long memberSeqNo, Long buffSeqNo);

    boolean existsByMemberSeqNoAndBuffSeqNo(Long memberSeqNo, Long buffSeqNo);

    boolean existsByMemberSeqNoAndIsOwner(Long memberSeqNo, Boolean isOwner);

    void deleteByMemberSeqNoAndBuffSeqNo(Long memberSeqNo, Long buffSeqNo);

    void deleteBySeqNo(Long seqNo);


    int countByBuffSeqNo(Long buffSeqNo);
}
