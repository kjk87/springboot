package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

@Entity(name="pageClosed") // This tells Hibernate to make a table out of this class
@Table(name="page_closed")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageClosed {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // '페이지 순번',

    @Column(name="page_seq_no")
    Long pageSeqNo  = null ; //페이지(상점) 순번',


    @Column(name="every_week")
    Integer everyWeek  = 0 ; // 0: 매주, 1: 첫번째 주, 2: 두번째 주, 3: 세번째 주, 4: 네번째 주, 5: 다섯번째 주

    @Column(name="week_day")
    String weekDay  = null ; // 'mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'


}
