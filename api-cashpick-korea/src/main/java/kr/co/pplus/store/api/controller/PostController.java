package kr.co.pplus.store.api.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.mvc.service.ArticleService;
import kr.co.pplus.store.queue.MsgProducer;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ArticleWrapper;
import kr.co.pplus.store.type.model.Article;
import kr.co.pplus.store.type.model.BulletinBoard;
import kr.co.pplus.store.type.model.Comment;
import kr.co.pplus.store.type.model.GeoPosition;
import kr.co.pplus.store.type.model.PageCategory;
import kr.co.pplus.store.type.model.SearchOpt;
import kr.co.pplus.store.type.model.Session;

@RestController
public class PostController extends RootController {
	@Autowired
	ArticleService svc;

	@Autowired
    MsgProducer producer;

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/insertPost/**")
	public Map<String,Object> insertPost(Session session, @RequestBody Article post) throws ResultCodeException {
		Integer r = svc.insertArticle(session, post);
		if (r == Const.E_SUCCESS && post.getNo() != null) {
			producer.push(post);
		}
		return result(r, "row", post);
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/updatePost/**")
	public Map<String,Object> updatePost(Session session, @RequestBody Article post) throws ResultCodeException {
		return result(svc.updateArticle(session, post), "row", post);
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/updatePostProperties/**")
	public Map<String,Object> updatePostProperties(Session session, @RequestBody Article post) throws ResultCodeException {
		return result(svc.updateArticleProperties(session, post), "row", post);
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/deletePost/**")
	public Map<String,Object> deletePost(Session session, Article post) throws ResultCodeException {
		return result(svc.deleteArticle(session, post), "row", post);
	}

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getPost/**")
	public Map<String,Object> getPost(Session session, Article post) {
		ArticleWrapper dbPost = svc.getArticle(session, post);
		return result(dbPost == null ? Const.E_NOTFOUND : Const.E_SUCCESS, "row", dbPost);
	}

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getPostWithAttachment/**")
	public Map<String,Object> getPostWithAttachment(Session session, Article post) {
		ArticleWrapper dbPost = svc.getArticleWithAttachment(session, post);
		return result(dbPost == null ? Const.E_NOTFOUND : Const.E_SUCCESS, "row", dbPost);
	}

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getBoardPostCount/**")
	public Map<String,Object> getBoardPostCount(Session session, BulletinBoard board, SearchOpt opt) {
		return result(200, "row", svc.getBoardArticleCount(board, opt));
	}

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getBoardPostList/**")
	public Map<String,Object> getPostListByBoard(Session session, BulletinBoard board, SearchOpt opt) {
		return result(200, "rows", svc.getBoardArticleList(board, opt));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getMyPostCount/**")
	public Map<String,Object> getMyPostCount(Session session, SearchOpt opt) {
		return result(200, "row", svc.getUserArticleCount(session, opt));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getMyPostList/**")
	public Map<String,Object> getMyPostList(Session session, SearchOpt opt) {
		return result(200, "rows", svc.getUserArticleList(session, opt));
	}

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getPostCount/**")
	public Map<String,Object> getPostCount(Session session, PageCategory category, Boolean plus, SearchOpt opt) {
		return result(200, "row", svc.getArticleCount(session, category, opt, plus));
	}

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getPostList/**")
	public Map<String,Object> getPostList(Session session, PageCategory category, GeoPosition position, Boolean plus, SearchOpt opt) {
		return result(200, "rows", svc.getArticleList(session, category, opt, position, plus));
	}

	@SkipSessionCheck
	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getCommentAll/**")
	public Map<String,Object> getCommentAll(Session session, Article post) {
		return result(200, "rows", svc.getCommentAll(session, post));
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/insertComment/**")
	public Map<String,Object> insertComment(Session session, @RequestBody Comment comment) throws ResultCodeException {
		Integer r = svc.insertComment(session, comment);
		if (Const.E_SUCCESS.equals(r)) {
			producer.push(comment);
		}
		return result(r, "row", comment);
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/updateComment/**")
	public Map<String,Object> updateComment(Session session, @RequestBody Comment comment) throws ResultCodeException {
		return result(svc.updateComment(session, comment), "row", comment);
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/deleteComment/**")
	public Map<String,Object> deleteComment(Session session, Comment comment) throws ResultCodeException {
		return result(svc.deleteComment(session, comment), "row", comment);
	}

	@RequestMapping(produces = "application/json;charset=UTF-8", value = baseUri+"/post/getPostPage/**")
	public Map<String,Object> getPostPage(Session session, Article article) throws ResultCodeException {
		return result(200, "row", svc.getPageByArticle(article));
	}

}
