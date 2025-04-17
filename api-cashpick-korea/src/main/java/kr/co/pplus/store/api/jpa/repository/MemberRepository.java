package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Modifying
    @Query(value="update member set bol = bol + :amount where seq_no = :memberSeqNo")
    void updateIncreaseBol(@Param("memberSeqNo") Long memberSeqNo, @Param("amount") Double amount) ;

    @Modifying
    @Query(value="update member set bol = bol - :amount where seq_no = :memberSeqNo")
    void updateDecreaseBol(@Param("memberSeqNo") Long memberSeqNo, @Param("amount") Double amount) ;

    @Modifying
    @Query(value="update member set cash = cash + :amount where seq_no = :memberSeqNo")
    void updateCash(@Param("memberSeqNo") Long memberSeqNo, @Param("amount") Double amount);

    @Modifying
    @Query(value="update member set point = point + :amount where seq_no = :memberSeqNo")
    void updatePoint(@Param("memberSeqNo") Long memberSeqNo, @Param("amount") Double amount) ;

    @Modifying
    @Query(value="update member set ad_reward_count = ifnull(ad_reward_count, 0) + 1, ad_reward_datetime = now() where seq_no = :memberSeqNo", nativeQuery = true)
    void updateAdCount(@Param("memberSeqNo") Long memberSeqNo) ;

    @Modifying
    @Query(value="update member set ad_reward_count = 0 where app_type='luckyball'", nativeQuery = true)
    void updateResetAdCount() ;

    @Modifying
    @Query(value="update member set login_fail_count = :loginFailCount, last_login_fail_datetime = now() where seq_no = :memberSeqNo", nativeQuery = true)
    void updateLoginFailCount(@Param("memberSeqNo") Long memberSeqNo, @Param("loginFailCount") Integer loginFailCount) ;

    @Modifying
    @Query(value="update member set nickname = :nickname, gender = :gender, birthday = :birthday, job = :job, region_code = :regionCode, region1 = :region1, region2 = :region2, region3 = :region3, received_profile_reward = :receivedProfileReward, mod_datetime = now() "
            + " where seq_no = :memberSeqNo", nativeQuery = true)
    void updateProfile(Long memberSeqNo, String nickname, String gender, String birthday, String job
            , String regionCode, String region1, String region2, String region3, Boolean receivedProfileReward) ;

    Member findBySeqNo(Long seqNo);
    Member findByLoginId(String loginId);
    Member findByLoginIdAndPassword(String loginId, String password);
    Member findByRecommendUniqueKey(String recommendUniqueKey);

    Integer countByRecommendationCode(String recommendationCode);

    @Modifying
    @Query(value="update member set plus_push = :plusPush where seq_no = :memberSeqNo")
    void updatePlusPush(@Param("memberSeqNo") Long memberSeqNo, @Param("plusPush") Boolean plusPush) ;

    @Modifying
    @Query(value="update member set buff_post_public = :buffPostPublic where seq_no = :memberSeqNo")
    void updateBuffPostPublic(@Param("memberSeqNo") Long memberSeqNo, @Param("buffPostPublic") Boolean buffPostPublic) ;

    List<Member> findAllByAppType(String appType);


    @Query(value="select seq_no from member"
            +" where 1=1 "
            +" AND member_type = 'general' "
            +" AND use_status = 'normal' "
            +" AND restriction_status = 'none' "
            +" AND app_type = 'luckyball' "
            +" AND gender in (:genderList) "
            +" AND (   (:age10 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 10 AND 19) "
                + " OR (:age20 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 20 AND 29) "
                + " OR (:age30 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 30 AND 39) "
                + " OR (:age40 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 40 AND 49) "
                + " OR (:age50 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 50 AND 59) "
                + " OR (:age60 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) >= 60)) ",
            countQuery = "select count(*) from member"
                    +" where 1=1 "
                    +" AND member_type = 'general' "
                    +" AND use_status = 'normal' "
                    +" AND restriction_status = 'none' "
                    +" AND app_type = 'luckyball' "
                    +" AND gender in (:genderList) "
                    +" AND (   (:age10 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 10 AND 19) "
                    + " OR (:age20 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 20 AND 29) "
                    + " OR (:age30 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 30 AND 39) "
                    + " OR (:age40 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 40 AND 49) "
                    + " OR (:age50 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) BETWEEN 50 AND 59) "
                    + " OR (:age60 = true AND birthday IS NOT NULL AND TIMESTAMPDIFF(year, birthday, CURRENT_TIMESTAMP) >= 60)) ",
            nativeQuery = true)
    Page<BigInteger> findAllByTarget(List<String> genderList, boolean age10, boolean age20, boolean age30, boolean age40, boolean age50, boolean age60, Pageable pageable);


    @Query(value="select seq_no from member"
            +" where 1=1 "
            +" AND member_type = 'general' "
            +" AND use_status = 'normal' "
            +" AND restriction_status = 'none' "
            +" AND app_type = 'luckyball' ",
            countQuery = "select count(*) from member"
                    +" where 1=1 "
                    +" AND member_type = 'general' "
                    +" AND use_status = 'normal' "
                    +" AND restriction_status = 'none' "
                    +" AND app_type = 'luckyball' ",
            nativeQuery = true)
    Page<BigInteger> findAllLuckyball(Pageable pageable);
}