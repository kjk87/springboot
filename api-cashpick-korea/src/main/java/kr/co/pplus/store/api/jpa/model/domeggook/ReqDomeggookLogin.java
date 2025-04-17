package kr.co.pplus.store.api.jpa.model.domeggook;

import lombok.Data;

import java.util.Date;

/**
 * Created by ehowlsla on 2017. 8. 3..
 */
@Data
public class ReqDomeggookLogin {
    private String ver;
    private String mode;
    private String aid;
    private String id;
    private String pw;
    private String loginKeep;
    private String userAgent;
    private String ip;
    private String device;
}
