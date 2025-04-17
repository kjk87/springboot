package kr.co.pplus.store.util.aws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AmazonSQSSender implements AmazonSQSSenderImpl {

    @Value("${cloud.aws.sqs.queue.url}")
    private String url;

    @Value("${cloud.aws.sqs.queue.url2}")
    private String url2;

    private final ObjectMapper objectMapper;
    private final AmazonSQS amazonSQS;

    @Override
    public SendMessageResult sendMessage(SqsModel msg) throws JsonProcessingException {

        String url = "";
        String groupId = "";
        if(msg.getType().equals("randomBox")){
            url = this.url;
            groupId = "luckybox"+msg.getItemSeqNo();
        }else if(msg.getType().equals("randomPick")){
            url = this.url2;
            groupId = "luckypick"+msg.getItemSeqNo();
        }

        SendMessageRequest sendMessageRequest = new SendMessageRequest(url,
                objectMapper.writeValueAsString(msg))
                .withMessageGroupId(groupId);
//                .withMessageDeduplicationId(UUID.randomUUID().toString());

        return amazonSQS.sendMessage(sendMessageRequest);
    }
}
