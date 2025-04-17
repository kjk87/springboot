package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name="menuOptionDetail")
@Table(name="menu_option_detail")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuOptionDetail implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;

    @Column(name="menu_option_seq_no")
    private Long menuOptionSeqNo;
    private String title;

    private Float price;

    private Boolean deleted;


}
