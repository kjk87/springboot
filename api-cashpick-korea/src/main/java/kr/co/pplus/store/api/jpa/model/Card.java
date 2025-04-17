package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="card") // This tells Hibernate to make a table out of this class
@Table(name="card")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Card {

    public Card(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    Long id ;


    @Key
    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    @Key
    @Column(name="card_number")
    String cardNumber = null ;

    @Column(name="auto_key")
    String autoKey = null ;

    @Column(name="card_code")
    String cardCode = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="represent")
    Boolean represent;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="gen_date", updatable = false)
    String genDate  = null ;

    @Transient
    String errorMsg = null ;
}
