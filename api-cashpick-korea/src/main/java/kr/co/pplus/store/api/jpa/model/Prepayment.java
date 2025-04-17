package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "prepayment")
@Table(name = "prepayment")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Prepayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "page_seq_no")
    private Long pageSeqNo;

    private Float price;
    @Column(name = "add_price")
    private Float addPrice;
    private String notice;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="modi_datetime")
    private String modiDatetime;

    private String status; // stop, normal
    private Float discount;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="status_datetime")
    private String statusDatetime;


    @Column(name = "wholesale_seq_no")
    private Long wholesaleSeqNo;
    @Column(name = "distributor_seq_no")
    private Long distributorSeqNo;
}
