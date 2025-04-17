package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Attachment;
import kr.co.pplus.store.api.jpa.model.PrivateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface PrivateKeyRepository extends JpaRepository<PrivateKey, Long> {

    PrivateKey findByUuid(String uuid) ;
}