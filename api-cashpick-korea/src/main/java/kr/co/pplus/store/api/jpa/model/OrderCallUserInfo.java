package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Entity(name="orderCallUserInfo")
@Table(name="order_call_user_info")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCallUserInfo {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo ;


    @Column(name="page_seq_no", updatable = false)
    private Long pageSeqNo;

    private String phone;
    private String name;
    private String address;
    @Column(name="address_detail")
    private String addressDetail;
}
