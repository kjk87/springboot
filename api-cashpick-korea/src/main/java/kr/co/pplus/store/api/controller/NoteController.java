//package kr.co.pplus.store.api.controller;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import kr.co.pplus.store.exception.ResultCodeException;
//import kr.co.pplus.store.mvc.service.NoteService;
//import kr.co.pplus.store.mvc.service.QueueService;
//import kr.co.pplus.store.type.model.Note;
//import kr.co.pplus.store.type.model.ReplyNote;
//import kr.co.pplus.store.type.model.SearchOpt;
//import kr.co.pplus.store.type.model.Session;
//
//@RestController
//public class NoteController extends RootController {
//	@Autowired
//	NoteService svc;
//
//	@Autowired
//	QueueService queueSvc;
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/insertNote/**")
//	public Map<String,Object> insertNote(Session session, @RequestBody Note note) throws ResultCodeException {
//		return result(queueSvc.insertNote(session, note), "row", note);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/replyNote/**")
//	public Map<String,Object> replyNote(Session session, @RequestBody ReplyNote note) throws ResultCodeException {
//		return result(queueSvc.replyNote(session, note.getOrigin(), note), "row", note);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/readNote/**")
//	public Map<String,Object> readNote(Session session, Note note) throws ResultCodeException {
//		return result(svc.read(session, note));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/deleteReceiveNote/**")
//	public Map<String,Object> deleteReceiveNote(Session session, Note note) throws ResultCodeException {
//		return result(svc.deleteReceiveNote(session, note));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/deleteSendNote/**")
//	public Map<String,Object> deleteSendNote(Session session, Note note) throws ResultCodeException {
//		return result(svc.deleteSendNote(session, note));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/getSendNoteCount/**")
//	public Map<String,Object> getSendNoteCount(Session session, SearchOpt opt) {
//		return result(200, "row", svc.getSendNoteCount(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/getSendNoteList/**")
//	public Map<String,Object> getSendNoteList(Session session, SearchOpt opt) {
//		return result(200, "rows", svc.getSendNoteList(session, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/getReceiveNoteCount/**")
//	public Map<String,Object> getReceiveNoteCount(Session session, Note note, SearchOpt opt) {
//		return result(200, "row", svc.getReceiveNoteCount(session, note, opt));
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/getReceiveNoteList/**")
//	public Map<String,Object> getReceiveNoteList(Session session, Note note, SearchOpt opt) throws ResultCodeException {
//		int newNoteCount = 0;
//		if (session.getProperties() != null && session.getProperties().containsKey("newNoteCount")) {
//			newNoteCount = (Integer)session.getProperties().get("newNoteCount");
//		}
//
//		List<Note> noteList = svc.getReceiveNoteList(session, note, opt);
//
//		if (newNoteCount > 0) {
//			svc.reloadSession(session);
//		}
//
//		return result(200, "rows", noteList);
//	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/getSendNote/**")
//	public Map<String,Object> getSendNote(Session session, Note note) throws ResultCodeException {
//		return result(200, "row", svc.getSendNote(session, note));	}
//
//	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/note/getReceiveNote/**")
//	public Map<String,Object> getReceiveNote(Session session, Note note) throws ResultCodeException {
//		return result(200, "row", svc.getReceiveNote(session, note));
//	}
//}
