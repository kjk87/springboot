package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ComboEvent;
import kr.co.pplus.store.api.jpa.model.FirstServed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface FirstServedRepository extends JpaRepository<FirstServed, Long> {

    FirstServed findFirstByStatusInAndAosAndStartDatetimeLessThanEqualAndEndDatetimeGreaterThanEqualOrderBySeqNoDesc(List<String> statusList, Boolean aos, String startDate, String endDate);


}