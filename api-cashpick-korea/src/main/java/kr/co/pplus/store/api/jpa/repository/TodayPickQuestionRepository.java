package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.TodayPick;
import kr.co.pplus.store.api.jpa.model.TodayPickQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface TodayPickQuestionRepository extends JpaRepository<TodayPickQuestion, Long> {

    List<TodayPickQuestion> findAllByTodayPickSeqNoOrderByArrayAsc(Long todayPickSeqNo);

}
