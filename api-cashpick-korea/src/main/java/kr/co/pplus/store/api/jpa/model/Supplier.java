package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "supplier")
@Table(name = "supplier")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Supplier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    private String name;

    @Column(name = "business_number")
    private String businessNumber;

    @Column(name = "marketing_person")
    private String marketingPerson;

    @Column(name = "marketing_email")
    private String marketingEmail;

    @Column(name = "marketing_tel")
    private String marketingTel;

    @Column(name = "account_person")
    private String accountPerson;

    @Column(name = "account_email")
    private String accountEmail;

    @Column(name = "account_tel")
    private String accountTel;

    @Column(name = "order_person")
    private String orderPerson;

    @Column(name = "order_email")
    private String orderEmail;

    private String memo;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    private String regDatetime;

    private String sid;

}
