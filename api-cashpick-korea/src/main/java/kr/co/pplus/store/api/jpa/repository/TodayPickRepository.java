package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.BuffCoinHistory;
import kr.co.pplus.store.api.jpa.model.TodayPick;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface TodayPickRepository extends JpaRepository<TodayPick, Long> {

    List<TodayPick> findAllByStatusInAndAosAndOpenStartDateLessThanEqualAndOpenEndDateGreaterThanEqualOrderByArrayAscSeqNoDesc(List<String> statusList, Boolean aos, String openStartDate, String openEndDate);
    Page<TodayPick> findAllByStatusInAndAosAndOpenStartDateLessThanEqualAndOpenEndDateGreaterThanEqualOrderByArrayAscSeqNoDesc(List<String> statusList, Boolean aos, String openStartDate, String openEndDate, Pageable pageable);

    TodayPick findBySeqNo(Long seqNo);
}
