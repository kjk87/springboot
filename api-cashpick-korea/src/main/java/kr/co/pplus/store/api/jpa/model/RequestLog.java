package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
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

@Entity(name="requestLog") // This tells Hibernate to make a table out of this class
@Table(name="request_log")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestLog {

    public RequestLog(){

    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ; // 오류 순번

    @Column(name="member_seq_no")
    Long memberSeqNo ; // 세션 사용자 순번

    @Key
    @Column(name="is_exception")
    Boolean isException = null ;


    @Key
    @Column(name="result_code")
    Integer resultCoce = null ;

    @Key
    @Column(name="http_status")
    Integer httpStatus = null ;

    @Key
    @Column(name="request_id")
    String requestId = null ;

    @Key
    @Column(name="uri")
    String  uri = null ;

    @Key
    @Column(name="ip")
    String  ip = null ;

    @Key
    @Column(name="server")
    String  server = null ;


    @Column(name="response")
    String response = null ;

    @Convert(converter = JpaConverterDatetime.class)
    @Column(name="reg_datetime", updatable = false)
    String regDatetime  = null ;
}
