package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDate;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import kr.co.pplus.store.api.jpa.converter.JpaConverterYNBoolean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Entity(name="memberHashtag") // This tells Hibernate to make a table out of this class
@Table(name="member_hashtag")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberHashtag {

    @Id
    @Column(name="member_seq_no")
    Long memberSeqNo = null ;

    @Column(name="hashtag")
    String hashtag = null ;


}
