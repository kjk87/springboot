package kr.co.pplus.store.api.jpa.model.lpng;

import lombok.Data;

@Data
public class LpngCancelResponseNew {
    String returncode;
    String errormsg;
    String orderstatus;

}
