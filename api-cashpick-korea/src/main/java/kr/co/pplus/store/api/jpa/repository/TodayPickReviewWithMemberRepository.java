package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.ComboReviewWithMember;
import kr.co.pplus.store.api.jpa.model.TodayPickReviewWithMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional(transactionManager = "jpaTransactionManager")
public interface TodayPickReviewWithMemberRepository extends JpaRepository<TodayPickReviewWithMember, Long> {

    Page<TodayPickReviewWithMember> findAllByTodayPickSeqNoAndStatusOrderBySeqNoDesc(Long todayPickSeqNo, String status, Pageable pageable);


}