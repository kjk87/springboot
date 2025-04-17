package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ContactWithMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ContactWithMemberRepository extends JpaRepository<ContactWithMember, Long> {

    Page<ContactWithMember> findAllByMemberSeqNoOrderByIsMemberDescMobileNumberAsc(Long memberSeqNo, Pageable pageable);

    Page<ContactWithMember> findAllByMemberSeqNoAndIsMember(Long memberSeqNo, Boolean isMember, Pageable pageable);

    Integer countByMemberSeqNoAndIsMember(Long memberSeqNo, Boolean isMember);
}
