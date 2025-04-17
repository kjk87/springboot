package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Date;

@Entity(name="pageVocation") // This tells Hibernate to make a table out of this class
@Table(name="page_vocation")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageVocation {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '페이지 순번',

    @Column(name="page_seq_no")
    Long pageSeqNo  = null ; //페이지(상점) 순번',


    @Column(name="start_date")
    Date starteDate  = null ; //휴무 시작 일

    @Column(name="end_date")
    Date endDate  = null ;


}
