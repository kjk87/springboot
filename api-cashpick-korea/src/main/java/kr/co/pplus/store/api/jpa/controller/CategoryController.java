package kr.co.pplus.store.api.jpa.controller;

import kr.co.pplus.store.api.annotation.SkipSessionCheck;
import kr.co.pplus.store.api.controller.RootController;
import kr.co.pplus.store.api.jpa.model.CategoryFavorite;
import kr.co.pplus.store.api.jpa.service.CategoryService;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.exception.InvalidCardException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class CategoryController extends RootController {

    private Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    CategoryService categoryService;

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/category/first/list")
    public Map<String, Object> getCategoryFirstList() throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "rows", categoryService.getCategoryFirstList());
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/category/major/list", "ERROR");
        }
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/category/major/list")
    public Map<String, Object> getCategoryMajorList() throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "rows", categoryService.getCategoryMajorList());
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/category/major/list", "ERROR");
        }
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/category/majorOnly/list")
    public Map<String, Object> getCategoryMajorOnlyList() throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "rows", categoryService.getCategoryMajorOnlyList());
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/category/majorOnly/list", "ERROR");
        }
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/category/majorOnly")
    public Map<String, Object> getCategoryMajorOnly(Long seqNo) throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "row", categoryService.getCategoryMajorOnly(seqNo));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/category/majorOnly", "ERROR");
        }
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/category/minor/list")
    public Map<String, Object> getCategoryMinorList(Long major) throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "rows", categoryService.getCategoryMinorList(major));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/category/minor/list", "ERROR");
        }
    }

    @SkipSessionCheck
    @GetMapping(value = baseUri + "/category/minor")
    public Map<String, Object> getCategoryMinorBySeqNo(Long seqNo) throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "row", categoryService.getCategoryMinorBySeqNo(seqNo));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/category/minor/list", "ERROR");
        }
    }

    @GetMapping(value = baseUri + "/category/myFavorite")
    public Map<String, Object> getCategoryFavoriteList(Session session) throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "rows", categoryService.getCategoryFavoriteList(session));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/category/myFavorite", "ERROR");
        }
    }

    @PostMapping(value = baseUri + "/category/favorite/insert")
    public Map<String, Object> insertCategoryFavorite(Session session, @RequestBody CategoryFavorite categoryFavorite) throws ResultCodeException {

        try {
            return result(Const.E_SUCCESS, "row", categoryService.insertCategoryFavorite(categoryFavorite));
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/category/favorite/insert", "ERROR");
        }
    }

    @DeleteMapping(value = baseUri + "/category/favorite/delete")
    public Map<String, Object> deleteCategoryFavorite(Session session, Long categoryMinorSeqNo) throws ResultCodeException {

        try {
            categoryService.deleteCategoryFavorite(session, categoryMinorSeqNo);
            return result(Const.E_SUCCESS);
        } catch (Exception e) {
            logger.error(AppUtil.excetionToString(e));
            throw new InvalidCardException("[GET]/category/favorite/delete", "ERROR");
        }
    }
}
