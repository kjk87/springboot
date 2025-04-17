package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity(name="memberOnlyBol") // This tells Hibernate to make a table out of this class
@Table(name="member")
@Data
@EqualsAndHashCode(callSuper = false)
//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberOnlyBol {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo; //'회원 순번',

    @Column(name="bol")
    Float bol = null ;


}
