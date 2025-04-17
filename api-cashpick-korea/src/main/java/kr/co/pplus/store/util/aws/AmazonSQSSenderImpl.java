package kr.co.pplus.store.util.aws;

import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;

public interface AmazonSQSSenderImpl {
    SendMessageResult sendMessage(SqsModel message) throws JsonProcessingException;
}
