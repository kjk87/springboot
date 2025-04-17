package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Member;
import kr.co.pplus.store.api.jpa.model.MemberHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface MemberHashtagRepository extends JpaRepository<MemberHashtag, Long> {

    @Modifying
    public void deleteByMemberSeqNo(Long memberSeqNo) ;

    public MemberHashtag findByMemberSeqNo(Long memberSeqNo) ;
}