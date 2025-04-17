package kr.co.pplus.store.util.aws;

import lombok.Data;

@Data
public class SqsModel {
    private Long memberSeqNo;
    private Long itemSeqNo;
    private String type;//randomBox, randomPick
}
