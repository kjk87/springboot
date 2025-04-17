package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="plus") // This tells Hibernate to make a table out of this class
@Table(name="plus")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Plus {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // 'Plus 순번',

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="block")
    Boolean block = false ; //`

    @Column(name="member_seq_no")
    Long memberSeqNo ; // 'Plus 사용자  순번',

    @Column(name="page_seq_no")
    Long pageSeqNo ; // 'Plus 페이지 순번',

    @Convert(converter = JpaConverterYNBoolean.class)
    @Column(name="push_activate")
    private Boolean pushActivate;

    @Column(name="buy_count")
    private Integer buyCount;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="last_buy_datetime")
    private String lastBuyDatetime;

    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    @Where(clause = "status = 'normal'")
    private PageRefDetail page = null ; // '플러스 페이지 정보',


    @Key
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    Member user = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime")
    private String regDatetime ; // 'Plus 순번',

    private Boolean agreement;

    @Column(name="plus_gift_received")
    private Boolean plusGiftReceived;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="plus_gift_received_datetime")
    private String plusGiftReceivedDatetime;

    @Transient
    private List<News> newsList = new ArrayList<News>();

    @Transient
    private Long totalNewsElements;
}
