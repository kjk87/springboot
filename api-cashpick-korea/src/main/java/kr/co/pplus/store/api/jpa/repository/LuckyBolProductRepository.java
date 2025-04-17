package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.LuckyBolProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(transactionManager = "jpaTransactionManager")
public interface LuckyBolProductRepository extends JpaRepository<LuckyBolProduct, Long> {


    Page<LuckyBolProduct> findAllByLuckyBolSeqNoAndExchangePrice(Long luckyBolSeqNo, Integer exchangePrice, Pageable pageable);

    LuckyBolProduct findFirstByLuckyBolSeqNoOrderByDelegateDesc(Long luckyBolSeqNo);

    @Query(value = "select distinct exchange_price from lucky_bol_product where lucky_bol_seq_no = :luckyBolSeqNo order by exchange_price asc", nativeQuery = true)
    List<Integer> findDistinctExchangePrice(Long luckyBolSeqNo);

}