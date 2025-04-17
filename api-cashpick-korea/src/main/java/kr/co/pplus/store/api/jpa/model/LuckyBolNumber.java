package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "luckyBolNumber")
@Table(name = "lucky_bol_number")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBolNumber implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "lucky_bol_seq_no")
    private Long luckyBolSeqNo;
    private String first;
    private String second;
    private String third;
    @Column(name = "unique_key", unique = true)
    private String uniqueKey; // seqNo + winNumber
    @Column(name = "win_number")
    private String winNumber;
    private Integer array;
    private Boolean used;

}
