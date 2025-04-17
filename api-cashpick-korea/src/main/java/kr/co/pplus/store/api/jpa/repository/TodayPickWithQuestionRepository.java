package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.TodayPick;
import kr.co.pplus.store.api.jpa.model.TodayPickWithQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface TodayPickWithQuestionRepository extends JpaRepository<TodayPickWithQuestion, Long> {

    TodayPickWithQuestion findBySeqNo(Long seqNo);
}
