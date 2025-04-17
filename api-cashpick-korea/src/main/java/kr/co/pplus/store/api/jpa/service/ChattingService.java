package kr.co.pplus.store.api.jpa.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import kr.co.pplus.store.api.jpa.model.ChatData;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.queue.Firebase;
import kr.co.pplus.store.queue.MsgProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class ChattingService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(ChattingService.class);

    @Autowired
    MsgProducer producer;

    public ChatData insertChat(ChatData data) throws ResultCodeException {
        long timeMillis = System.currentTimeMillis();
        Firebase firebase = Firebase.getUserInstance();
        Firestore db = FirestoreClient.getFirestore(firebase.getFirbaseApp());
        data.setTimeMillis(System.currentTimeMillis());
        data.setTimeMillis(timeMillis);
        String msgId = data.getRoomName() + data.getMemberSeqNo() + timeMillis;
        data.setMsg(msgId);
        ApiFuture<WriteResult> future = db.collection("chat").document("chat_room").collection(data.getRoomName()).document(msgId).set(data);

        producer.push(data);
        return data;
    }
}
