package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity(name="virtualNumber") // This tells Hibernate to make a table out of this class
@Table(name="virtual_number")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualNumber {


    @Id
    @Column(name="virtual_number")
    String virtualNumber ; // '페이지 순번',

    @Column(name="type")
    String type  = null ; // 'PRNumber 타입',

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="reserved")
    Boolean reserved  = false ; // 'reserved flag',


    @Column(name="action_source")
    String actionSource  = null ; // '액션 소스',

    @Column(name="actor_login_id")
    String actorLoginId  = null ; // '액터 로그인 아이디',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="action_datetime")
    String actionDatetime  = null ; // '액션 시각',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reserved_datetime")
    String reservedDatetime  = null ; // 'reserved 시각',

    @Column(name="note", columnDefinition = "TEXT")
    String note  = null ; //

    @Column(name="reserved_title")
    String reservedTitle  = null ; //

    @Column(name="reserved_reason")
    String reservedReason = null ; //

    @Column(name="reserved_description")
    String reservedDescriptiton = null ; //

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="number_prop", columnDefinition = "TEXT")
    String numberProp = null ; //

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="deleted")
    Boolean deleted = false ;

}
