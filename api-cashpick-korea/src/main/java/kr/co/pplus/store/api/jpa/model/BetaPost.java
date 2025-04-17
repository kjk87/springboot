package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.api.client.util.Key;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterJson;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.HashMap;

@Entity(name="betaPost") // This tells Hibernate to make a table out of this class
@Table(name="beta_post")
@Data
@EqualsAndHashCode(callSuper = false)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BetaPost {

    public BetaPost(){

    }

    @Id
    @Column(name="seq_no")
    Long seqNo ; // 순번(자동생성 안함)

    @Column(name="review")
    String review  = null ; // 이벤트 당첨 리뷰

    @Column(name="content")
    String content  = null ; // 이벤트 당첨 소감 Article 내용

    @Column(name="code")
    String code  = null ; // 이벤트 경품 카테고리 코드

    @Column(name="img_seq_no")
    Long imgSeqNo  = null ; // 당첨 이미지

    @Column(name="active")
    Boolean active  = null ; // 사용가능 여부
}
