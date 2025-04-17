package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity(name = "comboEventExample")
@Table(name = "combo_event_example")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComboEventExample implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "combo_event_seq_no")
    private Long comboEventSeqNo;
    private String title;
    private Integer array;
    private Boolean temp;

    @Formula("(select count(1) from combo_join cj where cj.combo_event_example_seq_no = seq_no)")
    private Integer joinCount;

}
