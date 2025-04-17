package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;


@Entity(name="orderMenuImage")
@Table(name="order_menu_image")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderMenuImage implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="seq_no")
    private Long seqNo;
    @Column(name="menu_seq_no")
    private Long menuSeqNo;

    private String image;


}
