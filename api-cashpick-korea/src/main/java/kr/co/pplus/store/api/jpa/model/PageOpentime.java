package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterBoolean;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

@Entity(name="pageOpentime") // This tells Hibernate to make a table out of this class
@Table(name="page_opentime")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageOpentime {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '페이지 순번',

    @Column(name="page_seq_no")
    Long pageSeqNo  = null ; //페이지(상점) 순번',


    @Column(name="type")
    Integer type  = null ; //형식',

    @Column(name="week_day")
    String weekDay  = null ; // 'mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'

    @Convert(converter = JpaConverterTime.class)
    @Column(name="start_time")
    String startTime  = null ; // 영업 시작 시각

    @Convert(converter = JpaConverterTime.class)
    @Column(name="end_time")
    String endTime  = null ; // 영업 종료 시각

    @Convert(converter = JpaConverterBoolean.class)
    @Column(name="next_day")
    Boolean nextDay = false ;

}
