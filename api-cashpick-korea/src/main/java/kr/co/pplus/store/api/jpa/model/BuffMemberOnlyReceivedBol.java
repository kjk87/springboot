package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity(name = "buffMemberOnlyReceivedBol")
@Table(name = "buff_member")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffMemberOnlyReceivedBol implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name = "received_bol")
    private Float receivedBol;

}
