package kr.co.pplus.store.api.jpa.repository;

import kr.co.pplus.store.api.jpa.model.PageSeller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional(transactionManager = "jpaTransactionManager")
public interface PageSellerRepository extends JpaRepository<PageSeller, Long> {

    void deleteAllByMemberSeqNo(Long memberSeqNo) ;
    void deleteByPageSeqNo(Long pageSeqNo) ;
    PageSeller  findByPageSeqNo(Long pageSeqNo) ;

    Page<PageSeller>  findAllByMemberSeqNo(Long memberSeqNo, Pageable pageable) ;
    Page<PageSeller>  findAllByMemberSeqNoAndIsSellerAndIsTermsAccept(Long memberSeqNo, Boolean isSeller, Boolean isTermsAccept, Pageable pageable) ;
    Page<PageSeller>  findAllByMemberSeqNoAndIsSeller(Long memberSeqNo, Boolean isSeller, Pageable pageable) ;
    Page<PageSeller>  findAllByMemberSeqNoAndIsTermsAccept(Long memberSeqNo, Boolean isTermsAccept, Pageable pageable) ;

    Page<PageSeller>  findAllBy(Pageable pageable) ;
    Page<PageSeller>  findAllByIsSellerAndIsTermsAccept(Boolean isSeller, Boolean isTermsAccept, Pageable pageable) ;
    Page<PageSeller>  findAllByIsSeller(Boolean isSeller, Pageable pageable) ;
    Page<PageSeller>  findAllByIsTermsAccept(Boolean isTermsAccept, Pageable pageable) ;

    Integer  countAllByMemberSeqNo(Long memberSeqNo) ;
    Integer  countAllByMemberSeqNoAndIsSellerAndIsTermsAccept(Long memberSeqNo, Boolean isSeller, Boolean isTermsAccept) ;
    Integer  countAllByMemberSeqNoAndIsSeller(Long memberSeqNo, Boolean isSeller) ;
    Integer  countAllByMemberSeqNoAndIsTermsAccept(Long memberSeqNo, Boolean isTermsAccept) ;

    Integer  countAllByIsSellerAndIsTermsAccept(Boolean isSeller, Boolean isTermsAccept) ;
    Integer  countAllByIsSeller(Boolean isSeller) ;
    Integer  countAllByIsTermsAccept(Boolean isTermsAccept) ;
    Integer countByPageSeqNo(Long pageSeqNo) ;
    Integer countAllBy() ;

    @Query(value="select *  "
            + " from page_seller ps"
            + " inner join page p on p.seq_no = ps.page_seq_no "
            + " inner join member m on m.seq_no = ps.member_seq_no "
            + " where  1=1  "
            + "   and ( isnull(:memberSeqNo) = 1 or m.seq_no = :memberSeqNo) "
            + "   and ( isnull(:pageSeqNo) = 1 or p.seq_no = :pageSeqNo) "
            + "   and ( isnull(:isSeller) = 1 or ps.is_seller = :isSeller) "
            + "   and ( isnull(:status) = 1 or ps.status = :status) "
            + "   and ( isnull(:isTermsAccept) = 1 or ps.is_terms_accept = :isTermsAccept) "
            + "   and (  (:approvalWait = 1 and ps.status = 0 ) "
            + "       or (:approval = 1 and ps.status = 1 )  "
            + "       or (:reject = 1 and ps.status = 2 )  "
            + "       or (:secondRequest = 1 and ps.status = 3 )  "
            + "       or (:stop = 1 and ps.status = 4 )  )"
            + "   and ( isnull(:searchWord) = 1 "
            + "         or (:searchType = 'pageName' and p.page_name like :searchWord)  "
            + "         or (:searchType = 'loginId' and m.login_id like :searchWord)  "
            + "         or (:searchType = 'bizBankBookOwner' and ps.biz_bank_book_owner like :searchWord)  "
            + "         or (:searchType = 'phoneNumber' and p.phone_number like :searchWord)    )"
            + "   and ( isnull(:startDate) = 1 or ps.mod_datetime >= :startDate )"
            + "   and ( isnull(:endDate) = 1 or ps.mod_datetime <= :endDate )",

            countQuery = "select count(1) from page_seller ps"
                    + " inner join page p on p.seq_no = ps.page_seq_no "
                    + " inner join member m on m.seq_no = ps.member_seq_no "
                    + " where  1=1  "
                    + "   and ( isnull(:memberSeqNo) = 1 or m.seq_no = :memberSeqNo) "
                    + "   and ( isnull(:pageSeqNo) = 1 or p.seq_no = :pageSeqNo) "
                    + "   and ( isnull(:isSeller) = 1 or ps.is_seller = :isSeller) "
                    + "   and ( isnull(:status) = 1 or ps.status = :status) "
                    + "   and ( isnull(:isTermsAccept) = 1 or ps.is_terms_accept = :isTermsAccept) "
                    + "   and (  (:approvalWait = 1 and ps.status = 0 ) "
                    + "       or (:approval = 1 and ps.status = 1 )  "
                    + "       or (:reject = 1 and ps.status = 2 )  "
                    + "       or (:secondRequest = 1 and ps.status = 3 )  "
                    + "       or (:stop = 1 and ps.status = 4 )  )"
                    + "   and ( isnull(:searchWord) = 1 "
                    + "         or (:searchType = 'pageName' and p.page_name like :searchWord)  "
                    + "         or (:searchType = 'loginId' and m.login_id like :searchWord)  "
                    + "         or (:searchType = 'bizBankBookOwner' and ps.biz_bank_book_owner like :searchWord)  "
                    + "         or (:searchType = 'phoneNumber' and p.phone_number like :searchWord)  )"
                    + "   and ( isnull(:startDate) = 1 or ps.mod_datetime >= :startDate )"
                    + "   and ( isnull(:endDate) = 1 or ps.mod_datetime <= :endDate )"
            ,nativeQuery=true)
    Page<PageSeller> findAllBy(@Param("memberSeqNo") Long memberSeqNo, @Param("pageSeqNo") Long pageSeqNo,
                               @Param("isSeller") Boolean isSeller, @Param("status") Integer status, @Param("searchType") String searchType,
                               @Param("searchWord") String searchWord, @Param("startDate") Date startDate,
                               @Param("endDate") Date endDate, @Param("approvalWait") Boolean approvalWait,
                               @Param("approval") Boolean approval, @Param("reject") Boolean reject,
                               @Param("secondRequest") Boolean secondRequest, @Param("stop") Boolean stop,
                               @Param("isTermsAccept") Boolean isTermsAccept, @Param("pageable") Pageable pageable) ;

}