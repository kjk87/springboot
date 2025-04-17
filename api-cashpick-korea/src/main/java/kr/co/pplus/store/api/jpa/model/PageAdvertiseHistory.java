package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="pageAdvertiseHistory") // This tells Hibernate to make a table out of this class
@Table(name="page_advertise_history")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageAdvertiseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Long seqNo;


    @Column(name = "page_seq_no")
    Long pageSeqNo = null;

    String type = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    Integer price = null;

    @Column(name = "visitor_point_give_history_seq_no")
    Long visitorPointGiveHistorySeqNo ;

    @Column(name="page_attendance_seq_no")
    Long pageAttendanceSeqNo;

    @Column(name = "purchase_product_seq_no")
    Long purchaseProductSeqNo ;

    @Column(name = "purchase_seq_no")
    Long purchaseSeqNo ;

    @Column(name = "subscription_download_seq_no")
    Long subscriptionDownloadSeqNo ;

    @Column(name = "distribute_code")
    String distributeCode ;//대행사코드

    @Column(name = "wholesale_code")
    String wholesaleCode ;//총판코드

    @Column(name = "recommend_seq_no")
    Long recommendSeqNo ;


    @Column(name = "recommend_user_seq_no")
    Long recommendUserSeqNo ;


    @Column(name = "recommend_app_type")
    String recommendAppType ;


    @Column(name = "is_exceed")
    Boolean isExceed ;

    @Column(name = "recommend_profit")
    Float recommendProfit ;


}