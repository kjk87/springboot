package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name="MobileBrand") // This tells Hibernate to make a table out of this class
@Table(name="mobile_brand")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MobileBrand {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo;

    @Column(name="category_seq_no")
    Long categorySeqNo = null ;

    String name = null ;
    String image = null ;
    String status = null ;
    Integer array = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    String regDatetime;

}
