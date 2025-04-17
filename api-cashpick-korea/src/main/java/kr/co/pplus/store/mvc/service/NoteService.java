package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.AlreadyExistsException;
import kr.co.pplus.store.exception.InvalidArgumentException;
import kr.co.pplus.store.exception.NotFoundTargetException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.Note;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(transactionManager = "transactionManager")
public class NoteService extends RootService {
//	@Autowired
//	NoteDao dao;
	
	@Autowired
	UserService userSvc;
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insert(User user, Note note) throws ResultCodeException {
		if (note.getAuthor() == null)
			note.setAuthor(user);
		
		if (StringUtils.isEmpty(note.getContents()) 
				|| note.getReceiverList() == null || note.getReceiverList().size() == 0)
			throw new InvalidArgumentException();
		
		int effected = sqlSession.insert("Note.insert", note);
		if (effected > 0) {
			note.setMainReceiver(note.getReceiverList().get(0));
			note.setReceiverCount(note.getReceiverList().size());

			ParamMap map = new ParamMap() ;
			map.put("user", note.getAuthor()) ;
			map.put("note", note) ;
			sqlSession.insert("Note.insertSendNote", map) ; //MGK note, note.getAuthor());
			
			
			Map<String, Object> properties = new HashMap<String, Object>();
			for (User receiver : note.getReceiverList()) {
				map.clear();
				map.put("user", receiver) ;
				map.put("note", note) ;
				sqlSession.insert("Note.insertReceiver", map) ; //MGK note, receiver);
				sqlSession.insert("Note.insertReceiveNote", map) ; //MGK note, receiver);

				User saved = userSvc.getUser(receiver.getNo());
				int newNoteCount = 0;
				if (saved.getProperties() != null && saved.getProperties().containsKey("newNoteCount")) {
					newNoteCount = (Integer)saved.getProperties().get("newNoteCount");
				}
				properties.put("newNoteCount", newNoteCount + 1);
				userSvc.updateProperties(saved, properties);
			}
			
			return Const.E_SUCCESS;
		}
		
		return Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer reply(User user, Note origin, Note note) throws ResultCodeException {
		if (note.getAuthor() == null)
			note.setAuthor(user);
		
		if (origin == null || origin.getNo() == null 
				|| StringUtils.isEmpty(note.getContents()))
			throw new InvalidArgumentException();

		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("note", origin) ;
		Note saved = sqlSession.selectOne("Note.getReceiveNote", map) ; //MGK user, origin);
		
		if (saved == null)
			throw new NotFoundTargetException("origin", "not exists");
		
		if (saved.getReplyNo() != null)
			throw new AlreadyExistsException("reply", "exists reply");
		
		int effected = sqlSession.insert("Note.insert", note);
		if (effected > 0) {
			note.setOriginNo(origin.getNo());
			note.setMainReceiver(saved.getAuthor());
			note.setReceiverCount(1);
			map.put("user", note.getAuthor()) ;
			map.put("note", note) ;
			sqlSession.insert("Note.insertSendNote", map) ; //MGK note, note.getAuthor());

			map.clear() ;
			map.put("user", saved.getAuthor()) ;
			map.put("note", note) ;
			sqlSession.insert("Note.insertReceiver", map) ; //MGK note, saved.getAuthor());
			sqlSession.insert("Note.insertReceiveNote", map) ; //MGK note, saved.getAuthor());
			
			Map<String, Object> properties = new HashMap<String, Object>();
			User author = userSvc.getUser(saved.getAuthor().getNo());
			int newNoteCount = 0;
			if (author.getProperties() != null && author.getProperties().containsKey("newNoteCount")) {
				newNoteCount = (Integer)author.getProperties().get("newNoteCount");
			}
			properties.put("newNoteCount", newNoteCount + 1);
			userSvc.updateProperties(author, properties);
			
			saved.setReplyNo(note.getNo());

			map.clear() ;
			map.put("user", user) ;
			map.put("note", saved) ;
			sqlSession.update("Note.updateReplyNo", map) ; //MGK saved, user);
			return Const.E_SUCCESS;
		}
		
		return Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer read(User user, Note note) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("note", note) ;
		int effected = sqlSession.selectOne("Note.read", map) ; //MGK note, user);
		if (effected == 0)
			return Const.E_UNKNOWN;
		
		return Const.E_SUCCESS;
	}
	
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteReceiveNote(User user, Note note) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("note", note) ;
		int effected = sqlSession.delete("Note.deleteReceiveNote", map) ; //MGK user, note);
		if (effected > 0) {
			int exists = sqlSession.selectOne("Note.existsSendNote", note);
			if (exists == 0) {
				sqlSession.delete("Note.deleteOriginRef", note);
				sqlSession.delete("Note.deleteNoteReceiver", note);
				sqlSession.delete("Note.deleteNote", note);
			}
			return Const.E_SUCCESS;
		}
		return Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteSendNote(User user, Note note) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("note", note) ;
		int effected = sqlSession.delete("Note.deleteSendNote", map) ;  //MGK user, note);
		if (effected > 0) {
			int exists = sqlSession.selectOne("Note.existsReceiveNote", note);
			if (exists == 0) {
				sqlSession.delete("Note.deleteReplyRef", note);
				sqlSession.delete("Note.deleteNoteReceiver", note);
				sqlSession.delete("Note.deleteNote", note);
			}
			return Const.E_SUCCESS;
		}
		return Const.E_UNKNOWN;
	}
	
	public int getSendNoteCount(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Note.getSendNoteCount", map) ; //MGK user, opt);
	}
	
	public List<Note> getSendNoteList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Note.getSendNoteList", map) ; //MGK user, opt);
	}
	
	public int getReceiveNoteCount(User user, Note note, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("note", note) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Note.getReceiveNoteCount", map) ; //MGK user, note, opt);
	}
	
	public List<Note> getReceiveNoteList(User user, Note note, SearchOpt opt) throws ResultCodeException {
		User saved = userSvc.getUser(user.getNo());
		if (saved.getProperties() != null && saved.getProperties().containsKey("newNoteCount")) {
			Integer newNoteCount = (Integer)saved.getProperties().get("newNoteCount");
			if (newNoteCount != null && newNoteCount > 0) {
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put("newNoteCount", 0);
				userSvc.updateProperties(saved, properties);
			}
		}
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("note", note) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Note.getReceiveNoteList", map) ; //MGK user, note, opt);
	}
	
	public Note getSendNote(User user, Note note) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("note", note) ;
		Note saved = sqlSession.selectOne("Note.getSendNote", map) ; //MGK user, note);
		if (saved == null)
			throw new NotFoundTargetException("note", "not exists");
		return saved;
	}
	
	public Note getReceiveNote(User user, Note note) throws ResultCodeException {
		ParamMap map = new ParamMap() ;
		map.put("user", user) ;
		map.put("note", note) ;
		Note saved = sqlSession.selectOne("Note.getReceiveNote", map) ; //MGK user, note);
		if (saved == null)
			throw new NotFoundTargetException("note", "not exists");
		return saved;
	}
	
	public List<User> getReceiverAll(Note note) {
		return sqlSession.selectList("Note.getReceiverAll", note);
	}
}
