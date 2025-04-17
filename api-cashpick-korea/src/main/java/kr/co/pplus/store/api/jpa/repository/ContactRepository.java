package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface ContactRepository extends JpaRepository<Contact, Long> {

    List<Contact> findAllByMemberSeqNoOrderByMobileNumberDesc(Long memberSeqNo);
}
