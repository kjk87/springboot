package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.type.model.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class AgentService extends RootService {
	private final static Logger logger = LoggerFactory.getLogger(AgentService.class);

	
	public List<Agent> getPendingAgentAll() {
		return sqlSession.selectList("Agent.getPendingAgentAll");
	}
	
	public List<Agent> getNeedExpiredAgentAll() {
		return sqlSession.selectList("Agent.getNeedExpireAgentAll");
	}
	
	public int updateStatus(Agent agent) {
		return sqlSession.update("Agent.updateStatus",agent);
	}

	public Agent getAgent(String id, String password) {
		Agent agent = new Agent() ;
		agent.setChargeId(id);
		agent.setChargePwd(password);
		return sqlSession.selectOne("Agent.getAgent",agent);
	}
}
