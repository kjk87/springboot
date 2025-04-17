package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="shippingSite") // This tells Hibernate to make a table out of this class
@Table(name="shipping_site")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingSite {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    @Key
    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    @Column(name="site_name")
    String siteName = null ;

    @Column(name="post_code")
    String postCode = null ;

    @Column(name="address")
    String address  = null ;

    @Column(name="address_detail")
    String addressDetail  = null ;

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="is_default")
    Boolean isDefault   = false ;


    @Column(name="receiver_name")
    String receiverName  = null ;

    @Column(name="receiver_tel")
    String receiverTel  = null ;
}
