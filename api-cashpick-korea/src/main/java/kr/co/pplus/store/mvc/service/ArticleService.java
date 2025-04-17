package kr.co.pplus.store.mvc.service;

import java.util.ArrayList;
import java.util.List;

import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import kr.co.pplus.store.exception.InvalidArgumentException;
import kr.co.pplus.store.exception.NotFoundTargetException;
import kr.co.pplus.store.exception.NotPermissionException;
import kr.co.pplus.store.exception.NotPossibleDeleteException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ArticleWrapper;
import kr.co.pplus.store.util.StoreUtil;

@Service
@Transactional(transactionManager = "transactionManager")
public class ArticleService extends RootService {
//	@Autowired
//	ArticleDao dao;
	
	@Autowired
	AttachmentService attachSvc;
	
	@Autowired
	FanService fanSvc;
	
	@Autowired
	PageService pageSvc;

	@Autowired
    MsgProducer producer;


	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertBoard(BulletinBoard board) {
		int effected = sqlSession.insert("Article.insertBoard",board);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer bindPageAndBoards(Page page, BulletinBoard prBoard, BulletinBoard reviewBoard) {
		page.setPrBoard(prBoard);
		page.setReviewBoard(reviewBoard);
		int effected = sqlSession.insert("Article.bindPageBoard", page);
		return effected > 0 ? Const.E_SUCCESS : Const.E_UNKNOWN;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertArticle(User user, Article article) throws ResultCodeException {
		if (article.getPriority() == null)
			article.setPriority(1);
		
		article.setAuthor(user);

		if(StringUtils.isEmpty(article.getAppType())){
			article.setAppType("pplus");
		}
		
		int effected = sqlSession.insert("Article.insert",article);
		if (effected > 0 
				&& article.getNo() != null 
				&& article.getNo() > 0) {
			
			if (article.getAttachList() != null && article.getAttachList().size() > 0)
				sqlSession.insert("Article.allocateToAttachList", article);
			
			BulletinBoard board = sqlSession.selectOne("Article.getBoard", article.getBoard());
			if (board.getType().equals("page_review")) {
				Page page = sqlSession.selectOne("Article.getPageByArticle", article);
				PageAction action = pageSvc.getPageAction(user, page);
				
				if (action != null) {
					if (action.getReview() != null) {
						Article review = sqlSession.selectOne("Article.get",action.getReview());
						if (review != null)
							deleteArticle(user, action.getReview());
					}
					action.setReview(article);
					pageSvc.updatePageAction(action);
				} else {
					action = new PageAction();
					action.setUser(user);
					action.setPage(page);
					action.setRecvReviewBol(false);
					action.setReview(article);
					action.setUseCount(0);
					pageSvc.insertPageAction(action);
				}
				
				if (article.getProperties() != null && article.getProperties().containsKey("starPoint")) {
					Integer point = (Integer)article.getProperties().get("starPoint");
					pageSvc.increaseValuation(page, point);
				}
			} else if( board.getType().equals("member") ) { // mgk_add_20190312 [ member sns feed

				// ToDo : Post 추가 후 필요한 사항이 있으면 기록

			} // mgk_add ]
		}
		return (effected > 0 && article.getNo() != null) ? Const.E_SUCCESS : Const.E_UNKNOWN;
		
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateArticle(User user, Article article) throws ResultCodeException {
		if (article.getNo() == null
				|| (StringUtils.isEmpty(article.getSubject()) && (StringUtils.isEmpty(article.getContents()))))
			throw new InvalidArgumentException();
		
		Article dbVal = sqlSession.selectOne("Article.getWithAttachment",article);
		
		if (dbVal.getAuthor() == null || !dbVal.getAuthor().getNo().equals(user.getNo()))
			throw new NotPermissionException();
		
		int effected = sqlSession.update("Article.update",article);
		
		if (effected == 0)
			throw new UnknownException();

		if (dbVal.getAttachList() != null) {
			if (article.getAttachList() != null) {
				for (Attachment attach : article.getAttachList()) {
					StoreUtil.exceptAttachment(dbVal.getAttachList(), attach);
				}
			}

			sqlSession.delete("Article.deallocateAttachmentAll", dbVal);
			
			for (Attachment attached : dbVal.getAttachList()) {
				attachSvc.delete(attached);
			}
		}
		
		if (article.getAttachList() != null) {
			sqlSession.insert("Article.allocateToAttachList", article);
		}
		
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateArticleProperties(User user, Article article) throws ResultCodeException {
		if (article.getNo() == null
				|| (StringUtils.isEmpty(article.getSubject()) && (StringUtils.isEmpty(article.getContents()))))
			throw new InvalidArgumentException();
		
		Article dbVal = sqlSession.selectOne("Article.get", article);
		
		if (dbVal.getAuthor() == null || !dbVal.getAuthor().getNo().equals(user.getNo()))
			throw new NotPermissionException();
		
		dbVal.setProperties(article.getProperties());
		int effected = sqlSession.update("Article.update", dbVal);
		
		if (effected == 0)
			throw new UnknownException();

		return Const.E_SUCCESS;
	}
	
	private void exceptAttachment(List<Attachment> src, Attachment dest) {
		int idx = -1;
		for (int j = 0; j < src.size(); j++) {
			Attachment s = src.get(j);
			if (dest.getNo().equals(s.getNo())) {
				idx = j;
				break;
			}
		}
		
		if (idx >= 0)
			src.remove(idx);
		return;
		
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteArticle(User user, Article article) throws ResultCodeException {
		if (article.getNo() == null)
			throw new InvalidArgumentException();
		
		int exists = sqlSession.selectOne("Article.existsAdvertise", article);
		if (exists > 0)
			throw new NotPossibleDeleteException("advertise", "exists");
		
		Article dbVal = sqlSession.selectOne("Article.getWithAttachment",article);
		
		if (dbVal.getAuthor() == null || !dbVal.getAuthor().getNo().equals(user.getNo()))
			throw new NotPermissionException();
		
		if (dbVal.getType().equals("review") && dbVal.getProperties() != null && dbVal.getProperties().containsKey("starPoint")) {
			Integer point = (Integer)dbVal.getProperties().get("starPoint");
			PageAction action = pageSvc.getPageActionByReview(user, dbVal);
			if (action != null && action.getPage() != null)
				pageSvc.decreaseValuation(action.getPage(), point);
			
			action.setReview(null);
			pageSvc.updatePageAction(action);
		}
		
		
		if (dbVal.getAttachList() != null) {

			sqlSession.delete("Article.deallocateAttachmentAll", article);
			
			for (Attachment attachment : dbVal.getAttachList()) {
				attachSvc.delete(attachment);
			}
		}

		sqlSession.delete("Article.delete", article);
		
		return Const.E_SUCCESS;
	}
	
	public Page getPageByArticle(Article article) {
		return sqlSession.selectOne("Article.getPageByArticle", article);
	}
	

	public List<CountPerValue> getArticleCountList(List<BulletinBoard> boardList) {
		List<CountPerValue> resultList = new ArrayList<CountPerValue>();
		for (BulletinBoard board : boardList) {
			int count = getBoardArticleCount(board, new SearchOpt());
			CountPerValue cv = new CountPerValue();
			cv.setNo(board.getNo());
			cv.setCount(count);
			resultList.add(cv);
		}
		return  resultList;
	}
	
	public int getBoardArticleCount(BulletinBoard board, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("board",board) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Article.getBoardArticleCount", map) ;
	}

	
	public List<ArticleWrapper> getBoardArticleList(BulletinBoard board, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("board",board) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Article.getBoardArticleList", map) ;
	}
	
	public int getUserArticleCount(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user",user) ;
		map.put("opt", opt) ;
		return sqlSession.selectOne("Article.getUserArticleCount", map) ;
	}

	
	public List<ArticleWrapper> getUserArticleList(User user, SearchOpt opt) {
		ParamMap map = new ParamMap() ;
		map.put("user",user) ;
		map.put("opt", opt) ;
		return sqlSession.selectList("Article.getUserArticleList", map) ;
	}
	
	public int getArticleCount(User user, PageCategory category, SearchOpt opt, Boolean plus) {
		ParamMap map = new ParamMap() ;
		if(user != null){
			map.put("user",user) ;
		}
		map.put("opt", opt) ;

		if (category == null || category.getNo() == null) {
			if (user != null && plus != null && plus == true){
				return sqlSession.selectOne("Article.getPlusArticleCount", map) ;
			}else{
				return sqlSession.selectOne("Article.getArticleCount", map) ;
			}
		}
		map.put("category", category) ;
		if (user != null && plus != null && plus == true){
			return sqlSession.selectOne("Article.getPlusArticleCountByCategory", map) ;
		}else{
			return sqlSession.selectOne("Article.getArticleCountByCategory", map) ;
		}
	}

	
	public List<ArticleWrapper> getArticleList(User user, PageCategory category, SearchOpt opt, GeoPosition position, Boolean plus) {
		ParamMap map = new ParamMap() ;
		if(user != null){
			map.put("user",user) ;
		}

		map.put("opt", opt) ;
		map.put("position", position) ;

		if (category == null || category.getNo() == null) {
			if (user != null && plus != null && plus == true){
				return sqlSession.selectList("Article.getPlusArticleList", map) ;
			}else{
				return sqlSession.selectList("Article.getArticleList", map) ;
			}
		}
		map.put("category", category) ;
		if (user != null && plus != null && plus == true){
			return sqlSession.selectList("Article.getPlusArticleListByCategory", map) ;
		}else{
			return sqlSession.selectList("Article.getArticleListByCategory", map) ;
		}
	}

	public ArticleWrapper getArticle(User user, Article post) {
		return sqlSession.selectOne("Article.get", post);
	}
	
	public ArticleWrapper getArticleWithAttachment(User user, Article post) {
		return sqlSession.selectOne("Article.getWithAttachment", post);
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer insertComment(User user, Comment comment) throws ResultCodeException {
		if (comment.getPost() == null || comment.getPost().getNo() == null)
			throw new InvalidArgumentException("postNo", "not found");

		// mgk_add_20190312 [ member feed 추가를 위한 수정
		ArticleWrapper dbPost = sqlSession.selectOne("Article.get", comment.getPost());
		if( dbPost.getType().startsWith("page") ) {
			Page page = sqlSession.selectOne("Article.getPageByArticle", comment.getPost());
			if (fanSvc.isBlockUser(user, page)) {
				throw new NotPermissionException("page", page.getName(), "blocked", true);
			}
		} // mgk_add ]
		
		if (comment.getParent() != null && comment.getParent().getNo() != null) {
			comment.setGroup(comment.getParent().getNo());
			comment.setDepth(2);
			
			comment.setPriority((Integer)sqlSession.selectOne("Article.getMaxSortNumByParent", comment) + 1);
			
		} else {
			comment.setDepth(1);
			comment.setPriority(1);
		}
		
		comment.setAuthor(user);
		sqlSession.insert("Article.insertComment", comment); //parent가 없는 댓글은 group_seq_no는 trigger에 의해서 들어간다.
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer updateComment(User user, Comment comment) throws ResultCodeException {
		Comment dbVal = sqlSession.selectOne("Article.getComment", comment);
		if (!dbVal.getAuthor().getNo().equals(user.getNo()))
			throw new NotPermissionException();

		// mgk_add_20190312 [ member feed 추가를 위한 수정
		ArticleWrapper dbPost = sqlSession.selectOne("Article.get", comment.getPost());
		if( dbPost.getType().startsWith("page") ) {
			Page page = sqlSession.selectOne("Article.getPageByComment", comment);
			if (fanSvc.isBlockUser(user, page)) {
				throw new NotPermissionException("page", page.getName(), "blocked", true);
			}
		}
		// mgk_add ]

		int effected  = sqlSession.update("Article.updateComment", comment);
		if (effected == 0)
			throw new UnknownException();
		
		return Const.E_SUCCESS;
	}
	
	@Transactional(transactionManager="transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public Integer deleteComment(User user, Comment comment) throws ResultCodeException {
		Comment dbVal = sqlSession.selectOne("Article.getComment", comment);
		
		if (dbVal == null)
			throw new NotFoundTargetException();
		
		if (!dbVal.getAuthor().getNo().equals(user.getNo()))
			throw new NotPermissionException();
		
		if (dbVal.getDeleted())
			throw new NotFoundTargetException();
		
		comment.setAuthor(user);

		if (1 == dbVal.getDepth()) {
			int replyCount = sqlSession.selectOne("Article.getChildCommentCount", comment);
			if (replyCount == 0) {
				//답글이 없는 경우에는 실제 삭제
				sqlSession.delete("Article.deleteCommentReport",comment);
				sqlSession.delete("Article.deleteComment", comment);
			} else {
				//답글이 있는 경우에는 삭제 Marking
				sqlSession.delete("Article.deleteCommentReport", comment);
				sqlSession.delete("Article.deleteMarkComment", comment);
			}
		} else if (2 == dbVal.getDepth()) {
			sqlSession.delete("Article.deleteCommentReport", comment);
			sqlSession.delete("Article.deleteComment", comment);
		} else
			throw new UnknownException("depth", dbVal.getDepth());

		return Const.E_SUCCESS;
	}
	
	public List<Comment> getCommentAll(User user, Article post) {
		return sqlSession.selectList("Article.getCommentAll", post);
	}
	
	public Comment getComment(Comment comment) {
		return sqlSession.selectOne("Article.getComment", comment);
	}
	
}
