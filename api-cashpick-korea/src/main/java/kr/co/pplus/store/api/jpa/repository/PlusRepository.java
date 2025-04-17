package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Plus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PlusRepository extends JpaRepository<Plus, Long> {


    @Query(value = "select * from plus p "
            + " inner join news n1 on n1.seq_no = (select n.seq_no from news n where n.page_seq_no = p.page_seq_no and n.deleted = false order by n.seq_no desc limit 1)"
            + " where p.member_seq_no = :memberSeqNo order by n1.seq_no desc",
            countQuery = "select count(*) from plus p "
                    + " inner join news n1 on n1.seq_no = (select n.seq_no from news n where n.page_seq_no = p.page_seq_no and n.deleted = false order by n.seq_no desc limit 1)"
                    + " where p.member_seq_no = :memberSeqNo",
            nativeQuery = true)
    Page<Plus> findAllByMemberSeqNo(@Param("memberSeqNo")Long memberSeqNo, Pageable pageable);

    @Query(value = "select * from plus p " +
            " where p.page_seq_no = :pageSeqNo " +
            " and (case when (:buyCount is not null and :buyCount > 0) then p.buy_count is not null and p.buy_count >= :buyCount else 1 end) = 1 " +
            " and (case when (:lastBuyDay is not null and :lastBuyDay > 0) then p.last_buy_datetime is not null and TIMESTAMPDIFF(day, p.last_buy_datetime, CURRENT_TIMESTAMP) <= :lastBuyDay else 1 end) = 1 " +
            " and ( select count(1) from member m where m.seq_no = p.member_seq_no " +
            "                                     and (case when :male is null and :female is null then 1 else " +
            "                                          (case when :male = true then m.gender = 'male' else 0 end) = 1 " +
            "                                          or (case when :female = true then m.gender = 'female' else 0 end) = 1 end) = 1 " +
            "                                     and m.use_status = 'normal' " +
            "                                     and (case when :age10 is null and :age20 is null and :age30 is null and :age40 is null and :age50 is null and :age60 is null then 1 else " +
            "                                          (case when :age10 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 10 and 19) else 0 end) = 1 " +
            "                                          or (case when :age20 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 20 and 29) else 0 end) = 1 " +
            "                                          or (case when :age30 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 30 and 39) else 0 end) = 1 " +
            "                                          or (case when :age40 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 40 and 49) else 0 end) = 1 " +
            "                                          or (case when :age50 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 50 and 59) else 0 end) = 1 " +
            "                                          or (case when :age60 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) >= 60) else 0 end) = 1 end) = 1) > 0 " +
            " order by push_activate asc, seq_no desc",
            countQuery = "select count(1) from plus p " +
                    " where p.page_seq_no = :pageSeqNo "+
                    " and (case when (:buyCount is not null and :buyCount > 0) then p.buy_count is not null and p.buy_count >= :buyCount else 1 end) = 1 " +
                    " and (case when (:lastBuyDay is not null and :lastBuyDay > 0) then p.last_buy_datetime is not null and TIMESTAMPDIFF(day, p.last_buy_datetime, CURRENT_TIMESTAMP) <= :lastBuyDay else 1 end) = 1 " +
                    " and ( select count(1) from member m where m.seq_no = p.member_seq_no " +
                    "                                     and (case when :male is null and :female is null then 1 else " +
                    "                                          (case when :male = true then m.gender = 'male' else 0 end) = 1 " +
                    "                                          or (case when :female = true then m.gender = 'female' else 0 end) = 1 end) = 1 " +
                    "                                     and m.use_status = 'normal' " +
                    "                                     and (case when :age10 is null and :age20 is null and :age30 is null and :age40 is null and :age50 is null and :age60 is null then 1 else " +
                    "                                          (case when :age10 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 10 and 19) else 0 end) = 1 " +
                    "                                          or (case when :age20 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 20 and 29) else 0 end) = 1 " +
                    "                                          or (case when :age30 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 30 and 39) else 0 end) = 1 " +
                    "                                          or (case when :age40 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 40 and 49) else 0 end) = 1 " +
                    "                                          or (case when :age50 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) between 50 and 59) else 0 end) = 1 " +
                    "                                          or (case when :age60 = true then (m.birthday is not null and TIMESTAMPDIFF(year, m.birthday, CURRENT_TIMESTAMP) >= 60) else 0 end) = 1 end) = 1) > 0 ",
            nativeQuery = true)
    Page<Plus> findAllByPageSeqNo(@Param("pageSeqNo")Long pageSeqNo, @Param("male")Boolean male, @Param("female")Boolean female, @Param("age10") Boolean age10, @Param("age20") Boolean age20, @Param("age30") Boolean age30, @Param("age40") Boolean age40
            , @Param("age50") Boolean age50, @Param("age60") Boolean age60, @Param("buyCount") Integer buyCount, @Param("lastBuyDay") Integer lastBuyDay, Pageable pageable);


    @Query(value = "select count(1) from plus p " +
            " where p.page_seq_no = :pageSeqNo "+
            " and ( select count(1) from member m where m.seq_no = p.member_seq_no " +
            "                                     and m.use_status = 'normal' ) > 0", nativeQuery = true)
    Integer countAllByPageSeqNo(@Param("pageSeqNo")Long pageSeqNo);

    Boolean existsByMemberSeqNoAndPageSeqNo(Long memberSeqNo, Long pageSeqNo);

}