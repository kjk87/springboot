package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import kr.co.pplus.store.api.jpa.converter.JpaConverterSeoulDatetime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity(name="shortUrl") // This tells Hibernate to make a table out of this class
@Table(name="short_url")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortUrl {


    public ShortUrl() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    Long seqNo; // '순번',


    @Column(name = "member_seq_no")
    Long memberSeqNo = null;  //사용자 순번

    @OneToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="short_url_seq_no", insertable = false, updatable = false)
    private List<ShortUrlEvent> eventList = new ArrayList<ShortUrlEvent>();


    @Column(name = "target_type")
    String targetType = null;  // goods, page, event

    @Column(name = "target_seq_no")
    Long targetSeqNo ;

    @Column(name = "id")
    String id = null;  //short URL key unique key

    @Column(name = "real_url")
    String realUrl = null;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "reg_datetime")
    String regDatetime;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name = "mod_datetime")
    String modDatetime;

    @Convert(converter = JpaConverterSeoulDatetime.class)
    @Column(name = "expire_datetime")
    String expireDatetime;

    @Column(name = "status")
    Integer status = 1; //0:disable, 1:enable

    @Column(name = "phase")
    String phase; //서버 phase : LOCAL, DEV, STAGE, PROD

    @Column(name= "click_count")
    Long clickCount ;

    @Column(name= "buy_count")
    Long buyCount ;

    @Column(name= "reward_bol")
    Long rewardBol ;

    @Convert(converter = JpaConverterJson.class)
    @Column(name = "prop")
    Map<String, Object> prop = null;
}