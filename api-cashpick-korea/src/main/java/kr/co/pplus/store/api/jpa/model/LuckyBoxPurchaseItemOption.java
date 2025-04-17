package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Data
@Entity(name = "luckyBoxPurchaseItemOption")
@Table(name = "luckybox_purchase_item_option")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBoxPurchaseItemOption implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="luckybox_purchase_item_seq_no")
    private Long luckyBoxPurchaseItemSeqNo;
    @Column(name="product_seq_no")
    private Long productSeqNo;
    @Column(name="product_option_detail_seq_no")
    private Long productOptionDetailSeqNo;
    private Integer quantity;
    private Float price;
    private String depth1;
    private String depth2;

}
