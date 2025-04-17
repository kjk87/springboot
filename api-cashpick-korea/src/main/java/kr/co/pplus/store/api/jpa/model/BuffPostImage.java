package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="buffPostImage")
@Table(name="buff_post_image")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuffPostImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_no")
    private Long seqNo;

    @Column(name="buff_post_seq_no")
    private Long buffPostSeqNo;
    private String image;
    private Integer array;
}
