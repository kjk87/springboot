package kr.co.pplus.store.api.jpa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatData implements Serializable {
    String msgId;
    String msg;
    String name;
    String roomName;
    Long pageSeqNo;
    Long memberSeqNo;
    Long targetMemberSeqNo;
    Long timeMillis;
}
