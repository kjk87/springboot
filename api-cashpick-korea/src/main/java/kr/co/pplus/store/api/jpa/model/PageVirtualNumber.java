package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name="pageVirtualNumber") // This tells Hibernate to make a table out of this class
@Table(name="page_virtual_number")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageVirtualNumber {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id ;

    @OneToOne
    @JoinColumn(name="virtual_number", insertable = false, updatable = false)
    VirtualNumber virtualNumber  ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="start_datetime")
    String startDatetime  = null ; // '제휴사 순번',

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="end_datetime")
    String endDatetime  = null ; // '제휴사 순번',

}
