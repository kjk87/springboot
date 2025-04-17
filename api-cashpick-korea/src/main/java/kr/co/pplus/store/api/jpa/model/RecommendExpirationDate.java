package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity(name="recommendExpirationDate")
@Table(name="recommend_expiration_date")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecommendExpirationDate {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo ;

    String type = null ;

    @Column(name="expiration_date")
    LocalDate expirationDate  = null ;

}
