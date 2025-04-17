package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffMemberWithMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface BuffMemberWithMemberRepository extends JpaRepository<BuffMemberWithMember, Long> {

    BuffMemberWithMember findFirstByMemberSeqNo(Long memberSeqNo);

    Page<BuffMemberWithMember> findAllByBuffSeqNo(Long buffSeqNo, Pageable pageable);


}
