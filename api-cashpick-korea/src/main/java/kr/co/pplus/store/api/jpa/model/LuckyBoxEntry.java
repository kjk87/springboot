package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import kr.co.pplus.store.api.jpa.converter.JpaConverterDatetime;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Entity(name = "luckyBoxEntry")
@Table(name = "luckybox_entry")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LuckyBoxEntry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "luckybox_product_group_seq_no")
    private Long luckyBoxProductGroupSeqNo;

    @Column(name = "luckybox_seq_no")
    private Long luckyBoxSeqNo;

    private Boolean temp; // 삭제시 사용


    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="luckybox_product_group_seq_no", insertable=false, updatable=false)
    @Where(clause = "status = 'active'")
    private LuckyBoxProductGroup luckyBoxProductGroup;
}
