package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ComboEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface ComboEventRepository extends JpaRepository<ComboEvent, Long> {

    List<ComboEvent> findAllByStatusInAndAosAndEventDatetimeGreaterThanEqualOrderByComboEventArrayDesc(List<String> statusList, Boolean aos, String startDate);


    ComboEvent findBySeqNo(Long seqNo);

}