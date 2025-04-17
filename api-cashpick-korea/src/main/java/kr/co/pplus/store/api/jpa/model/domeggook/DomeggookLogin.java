package kr.co.pplus.store.api.jpa.model.domeggook;

import lombok.Data;

import java.util.Date;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class DomeggookLogin {
    private String result;
    private String sId;
    private String id;
    private String affid;
    private Date loginKeepTime;
    private String grade;
    private Date sIdRenewDate;
}
