package kr.co.pplus.store.type.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("MemberKakao")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberKakao extends AbstractModel implements Serializable {

    private Long seqNo;
    private String loginId;
    private String memberName;
    private String mobileNumber;
    private Double point;
    private Boolean changed;
}
