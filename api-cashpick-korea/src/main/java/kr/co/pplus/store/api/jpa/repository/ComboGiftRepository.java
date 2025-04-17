package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ComboGift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface ComboGiftRepository extends JpaRepository<ComboGift, Long> {

    ComboGift findByMonthUnique(String monthUnique);

    Page<ComboGift> findAllByOrderBySeqNoDesc(Pageable pageable);

}