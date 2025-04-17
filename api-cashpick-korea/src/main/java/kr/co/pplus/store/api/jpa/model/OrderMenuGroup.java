package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "orderMenuGroup")
@Table(name = "order_menu_group")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMenuGroup implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="page_seq_no")
    private Long pageSeqNo;
    private String name;
    private Integer array;
    private Boolean deleted;


}
