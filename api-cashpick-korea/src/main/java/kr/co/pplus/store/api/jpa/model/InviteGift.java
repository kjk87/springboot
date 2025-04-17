package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity(name = "inviteGift")
@Table(name = "invite_gift")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InviteGift {
    @Id
    @Column(name = "seq_no")
    private Long seqNo;

    private String gift;

    @Column(name = "gift_image")
    private String giftImage;

}
