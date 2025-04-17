package kr.co.pplus.store.api.jpa.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name="popup")
@Table(name="popup")
public class Popup {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="seq_no")
    Long seqNo = null ;

    String title = null ;
    String contents = null;

    @Column(name="move_type1")
    String moveType1 = null ;

    @Column(name="move_type2")
    String moveType2 = null ;

    @Column(name="move_target")
    String moveTarget = null ;

    @Column(name="move_target_string")
    String moveTargetString = null ;

    @Column(name="image_path")
    String imagePath = null ;

}
