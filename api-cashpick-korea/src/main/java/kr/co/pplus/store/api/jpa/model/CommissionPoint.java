package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import kr.co.pplus.store.api.jpa.converter.JpaConverterSeoulDatetime;
import kr.co.pplus.store.util.RedisUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity(name="commissionPoint") // This tells Hibernate to make a table out of this class
@Table(name="commission_point")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommissionPoint {

    public CommissionPoint(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    Long id;

    @Column(name="commission")
    Float commission;

    @Column(name="point")
    Float point;

    @Column(name="card")
    Float card;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="update_date")
    String updateDate  = null ;  //'변경 시각',

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="woodongyi")
    Boolean woodongyi;
}
