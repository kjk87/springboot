package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="bankTransfer") // This tells Hibernate to make a table out of this class
@Table(name="bank_transfer")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankTransfer {

    public BankTransfer(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '출금계좌 처리 순번',

    @Key
    @Column(name="page_seq_no")
    Long pageSeqNo = null ; //'입금기관 상점 페이지 순번',

    @Column(name="bank_code_std")
    String bankCodeStd  = null ; //'입금기관 표준 코드 ex:097',

    @Column(name="bank_code_sub")
    String bankCodeSub  = null ; //'입금기관 점별코드 ex:1230001',

    @Column(name="bank_name")
    String bankName  = null ; //'입금기관명',

    @Column(name="account_id")
    String accountId  = null ; //'입금계좌번호',

    @Column(name="account_holder_name")
    String accountHolderName  = null ; //'수취인 성명',

    @Column(name="bank_tran_id")
    String bankTranId  = null ; // '출금 거래 고유번호',

    @Column(name="tran_bank_code_std")
    String tranBankCodeStd  = null ; //'출금기관 표준 코드 ex:098',

    @Column(name="tran_bank_code_sub")
    String tranBankCodeSub  = null ; //'출금기관 점별코드 ex:1230001',

    @Column(name="tran_bank_name")
    String tranBankName  = null ; //'출금기관명',

    @Column(name="tran_account_id")
    String tranAccountId  = null ; //'출금계좌번호',

    @Column(name="tran_account_holder_name")
    String tranAccountHolderName  = null ; //'송금인 성명',

    @Column(name="tran_account_alias")
    String tranAccountAlias  = null ; //'출금계좌별명',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="tran_datetime")
    String tran_datetime  = null ; // '출금 거래 시각',

    @Column(name="tran_amount")
    Float tran_amount  = null ; //'출금 거래금액',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String reg_datetime  = null ; //'출금 정보 등록 시각',
}
