package kr.co.pplus.store.queue;

import java.io.Serializable;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ScheduledMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

public class MsgProducer {
	private final static Logger logger = LoggerFactory.getLogger(MsgProducer.class);


	private JmsTemplate jmsTemplate;
	
	
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		//System.out.println("Producer.setJmsTemplate : jmsTemplate : " + jmsTemplate.toString()) ;
		logger.debug("Producer.setJmsTemplate : " + jmsTemplate.toString());
		this.jmsTemplate = jmsTemplate;
	}

	@SendTo(value = "${STORE.MQ_QUEUE}")
	public void push(Serializable model) {
		//System.out.println("Producer.push : model : " + model.toString()) ;
		logger.debug("Producer.push model : " + model.toString());

		jmsTemplate.convertAndSend(model);
	}

	@SendTo(value = "${STORE.MQ_QUEUE}")
	public void push(Serializable model, String dest) {

		//System.out.println("Producer.push : model : " + model.toString()) ;
		logger.debug("Producer.push msg : " + model.toString());
		//System.out.println("Producer.send : dest : " + dest) ;
		logger.debug("Producer.send : dest : " + dest) ;
		jmsTemplate.convertAndSend(dest, model);
	}

	@SendTo(value = "${STORE.MQ_QUEUE}")
	public void push(final Serializable model, String dest, final long delayMilis) {
		//System.out.println("Producer.push : model : " + model.toString()) ;
		logger.debug("Producer.push msg : " + model.toString());
		//System.out.println("Producer.send : dest : " + dest) ;
		logger.debug("Producer.send : dest : " + dest) ;
		MessageCreator msgCreator = new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				Message m = getJmsTemplate().getMessageConverter().toMessage(model, session);
				m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delayMilis);
				return m;
			}
		};
		System.out.println("Producer.send") ;
		jmsTemplate.send(msgCreator);
	}
}
