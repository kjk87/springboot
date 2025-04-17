package kr.co.pplus.store.api.jpa.model;

import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Data
@Entity(name = "virtualNumberGroupItem")
@Table(name = "virtual_number_group_item")
public class VirtualNumberGroupItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;
    @Column(name = "manage_seq_no")
    private Long manageSeqNo;
    private String type; // page, product
    @Column(name = "page_seq_no")
    private Long pageSeqNo;
    @Column(name = "product_price_seq_no")
    private Long productPriceSeqNo;

    @OneToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "page_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private PageRefDetail page;

    @OneToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "product_price_seq_no", referencedColumnName = "seq_no", insertable = false, updatable = false)
    private ProductPriceRef productPrice;
}
