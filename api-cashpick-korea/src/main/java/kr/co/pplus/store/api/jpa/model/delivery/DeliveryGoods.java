package kr.co.pplus.store.api.jpa.model.delivery;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="deliveryGoods") // This tells Hibernate to make a table out of this class
@Table(name="delivery_goods")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeliveryGoods {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    @Column(name="delivery_seq_no")
    Long deliverySeqNo ;

    @Column(name="name")
    String name ;

    @Column(name="count")
    Integer count ;

    @Column(name="price")
    Float price ;
}
