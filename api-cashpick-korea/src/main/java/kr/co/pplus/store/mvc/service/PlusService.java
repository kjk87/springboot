package kr.co.pplus.store.mvc.service;

import kr.co.pplus.store.exception.AlreadyExistsException;
import kr.co.pplus.store.exception.NotFoundTargetException;
import kr.co.pplus.store.exception.ResultCodeException;
import kr.co.pplus.store.exception.UnknownException;
import kr.co.pplus.store.type.Const;
import kr.co.pplus.store.type.dto.ParamMap;
import kr.co.pplus.store.type.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "transactionManager")
public class PlusService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(PlusService.class);

//	@Autowired
//	PlusDao dao;

    @Autowired
    PageService pageSvc;

    @Autowired
    FanService fanSvc;


    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer insertGroup(User user, PlusGroup group) throws ResultCodeException {
        group.setUser(user);

        int exists = sqlSession.selectOne("Plus.existsGroupByName", group);
        if (exists > 0)
            throw new AlreadyExistsException();

        int effected = sqlSession.insert("Plus.insertGroup", group);
        if (effected == 0)
            throw new UnknownException();
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateGroupPriorityAll(User user, List<PlusGroup> groupList) {
        for (PlusGroup group : groupList) {
            sqlSession.update("Plus.updateGroupPriority", group);
        }
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateGroupName(User user, PlusGroup group) throws ResultCodeException {
        int exists = sqlSession.selectOne("Plus.existsGroupByName", group);
        if (exists > 0)
            throw new AlreadyExistsException();


        int effected = sqlSession.update("Plus.updateGroupName", group);
        if (effected == 0)
            throw new UnknownException();
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer deleteGroup(User user, PlusGroup group) throws ResultCodeException {
        sqlSession.delete("Plus.deleteGroupMapping", group);
        sqlSession.delete("Plus.deleteGroup", group);
        return Const.E_SUCCESS;
    }

    public List<PlusGroup> getGroupAll(User user) {
        return sqlSession.selectList("Plus.getGroupAll", user.getNo());
    }

    public PlusGroup getGroup(PlusGroup group) {
        return sqlSession.selectOne("Plus.getGroup", group.getNo());
    }

    public boolean existsDefaultGroup(User user) {
        int exists = sqlSession.selectOne("Plus.existsDefaultGroup", user.getNo());
        return exists > 0 ? true : false;
    }

    public boolean existsPlus(User user, Plus plus) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", plus);
        int exists = sqlSession.selectOne("Plus.existsPlus", map);

        return exists > 0 ? true : false;
    }

    public Plus getOnlyPlus(User user, Plus plus) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", plus);

        return sqlSession.selectOne("Plus.getOnlyPlus", map);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateAgreement(Plus plus) {
        sqlSession.update("Plus.updateAgreement", plus);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updateBuyCount(User user, Plus plus) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", plus);
        int exists = sqlSession.update("Plus.updateBuyCount", map);
        return exists;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer insert(User user, Plus plus) throws ResultCodeException {

        if (plus.getBuyCount() == null) {
            plus.setBuyCount(0);
        }

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", plus);
        int exists = sqlSession.selectOne("Plus.existsPlus", map); //MGK(user, plus);
        if (exists > 0)
            throw new AlreadyExistsException();

        plus.setUser(user);
        int effected = sqlSession.insert("Plus.insert", plus);
        if (effected == 0)
            throw new UnknownException();

        //MGK_ADD
        logger.debug("PlusService.insert() : Plus.plusNo : " + plus.getPlusNo());
        //

        PlusGroup def = getDefaultGroup(user);
        addToGroup(def, plus);

        Fan fan = new Fan();
        fan.setFanNo(plus.getPlusNo());
        FanGroup fanDef = fanSvc.getDefaultGroup(plus);
        fanSvc.addToGroup(fanDef, fan);

//		pageSvc.increasePlusCount(plus, 1);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer delete(User user, Plus plus) throws ResultCodeException {
        sqlSession.delete("Plus.deleteFanMapping", plus);
        sqlSession.delete("Plus.deletePlusMapping", plus);
        int effected = sqlSession.delete("Plus.delete", plus);
        if (effected == 0)
            throw new UnknownException();

//		pageSvc.decreasePlusCount(plus, 1);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer deleteByPage(User user, Page page) throws ResultCodeException {

        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", page);
        Plus saved = sqlSession.selectOne("Plus.getByUserPage", map); //MGK user, page);
        if (saved == null)
            throw new NotFoundTargetException("plus", "not found");

        return delete(user, saved);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer addListToGroup(PlusGroup group, List<Plus> plusList) throws ResultCodeException {
        for (Plus plus : plusList) {
            ParamMap map = new ParamMap();
            map.put("group", group);
            map.put("plus", plus);
            int exists = sqlSession.selectOne("Plus.existsInGroup", map); // group, plus);
            if (exists > 0)
                throw new AlreadyExistsException();

            int effected = sqlSession.insert("Plus.addToGroup", map); //MGK group, plus);
            if (effected == 0)
                throw new UnknownException();
        }
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer removeListFromGroup(PlusGroup group, List<Plus> plusList) throws ResultCodeException {
        for (Plus plus : plusList) {
            ParamMap map = new ParamMap();
            map.put("group", group);
            map.put("plus", plus);
            int exists = sqlSession.selectOne("Plus.existsInGroup", map); //MGK group, plus);
            if (exists == 0)
                throw new NotFoundTargetException();

            int effected = sqlSession.delete("Plus.removeFromGroup", map); //MGK group, plus);
            if (effected == 0)
                throw new UnknownException();
        }
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer addToGroup(PlusGroup group, Plus plus) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("group", group);
        map.put("plus", plus);
        int exists = sqlSession.selectOne("Plus.existsInGroup", map); //MGK group, plus);
        if (exists > 0)
            throw new AlreadyExistsException();

        logger.debug("PlusService.addToGroup() : Plus.plusNo : " + plus.getPlusNo());
        int effected = sqlSession.insert("Plus.addToGroup", map); //group, plus);
        if (effected == 0)
            throw new UnknownException();

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer removeFromGroup(PlusGroup group, Plus plus) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("group", group);
        map.put("plus", plus);
        int exists = sqlSession.selectOne("Plus.existsInGroup", map); //MGK group, plus);
        if (exists == 0)
            throw new NotFoundTargetException();

        int effected = sqlSession.delete("Plus.removeFromGroup", map); //group, plus);
        if (effected == 0)
            throw new UnknownException();

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer moveGroupToGroup(PlusGroup src, PlusGroup dest) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("src", src);
        map.put("dest", dest);
        sqlSession.selectOne("Plus.moveGroupToGroup", map); //MGK src, dest);
        sqlSession.delete("Plus.deleteGroupMapping", src);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer moveGroup(Plus plus, PlusGroup src, PlusGroup dest) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("src", src);
        map.put("plus", plus);
        int exists = sqlSession.selectOne("Plus.existsInGroup", map); //MGK src, plus);
        if (exists == 0)
            throw new NotFoundTargetException();

        int effected = sqlSession.selectOne("Plus.removeFromGroup", map); //MGK src, plus);
        if (effected == 0)
            throw new UnknownException();

        map.clear();
        map.put("dest", src);
        map.put("plus", plus);
        exists = sqlSession.selectOne("Plus.existsInGroup", map); //MGK  dest, plus);
        if (exists > 0)
            throw new AlreadyExistsException();

        effected = sqlSession.insert("Plus.addToGroup", map); //MGK dest, plus);
        if (effected == 0)
            throw new UnknownException();


        return Const.E_SUCCESS;

    }

    private PlusGroup getDefaultGroup(User user) {
        int exists = sqlSession.selectOne("Plus.existsDefaultGroup", user.getNo());
        if (exists > 0)
            return sqlSession.selectOne("Plus.getDefaultGroup", user.getNo());

        PlusGroup group = new PlusGroup();
        group.setDefaultGroup(true);
        group.setName("ALL");
        group.setPriority(100);
        group.setUser(user);
        sqlSession.insert("Plus.insertGroup", group);
        return group;
    }

    public int getCount(User user, PlusGroup group, SearchOpt opt) {
//        PlusGroup target = group;
//        if (target.getNo() == null || target.getNo() < 1)
//            target = getDefaultGroup(user);

        ParamMap map = new ParamMap();
//        map.put("group", target);
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectOne("Plus.getCount", map); //MGK target, opt);
    }

    public List<Plus> getList(User user, PlusGroup group, SearchOpt opt) throws ResultCodeException {
//        PlusGroup target = group;
//        if (target.getNo() == null || target.getNo() < 1)
//            target = getDefaultGroup(user);

        ParamMap map = new ParamMap();
//        map.put("group", target);
        map.put("user", user);
        map.put("opt", opt);
        return sqlSession.selectList("Plus.getList", map); //MGK target, opt);
    }

    public int getExcludeCount(User user, PlusGroup group, SearchOpt opt) {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("group", group);
        map.put("opt", opt);
        return sqlSession.selectOne("Plus.getExcludeCount", map); //MGK user, group, opt);
    }

    public List<Plus> getExcludeList(User user, PlusGroup group, SearchOpt opt) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("group", group);
        map.put("opt", opt);
        return sqlSession.selectList("Plus.getExcludeList", map); //MGK user, group, opt);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer deleteFan(User user, Plus plus) throws ResultCodeException {
        sqlSession.delete("Plus.deleteFanMapping", plus);
        sqlSession.delete("Plus.deletePlusMapping", plus);
        int effected = sqlSession.delete("Plus.delete", plus);
        if (effected == 0)
            throw new UnknownException();

//		pageSvc.decreasePlusCount(plus, 1);
        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteByUser(User user) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("user", user);
        List<Plus> list = sqlSession.selectList("Plus.getByUser", map);
        for (Plus plus : list) {
            delete(user, plus);
        }
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer deleteByPageFan(User user, Page page) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", page);
        Plus saved = sqlSession.selectOne("Plus.getByUserPage", map); //MGK user, page);
        if (saved == null)
            throw new NotFoundTargetException("plus", "not found");

        return delete(user, saved);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int getExistFan(User user, Page page) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", page);
        int exists = sqlSession.selectOne("Plus.existsPlus", map);
        if (exists > 0)
            throw new AlreadyExistsException();
        return Const.E_SUCCESS;
    }

    public Integer updateByPageFan(User user, Page page) throws ResultCodeException {
        ParamMap map = new ParamMap();
        map.put("user", user);
        map.put("page", page);
        Plus saved = sqlSession.selectOne("Plus.getByUserPage", map); //MGK user, page);
        if (saved == null)
            throw new NotFoundTargetException("plus", "not found");

        return updatePushActivate(saved);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updatePushActivate(Plus plus) throws ResultCodeException {
        int effected = sqlSession.update("Plus.updatePushActivate", plus);
        if (effected == 0)
            throw new UnknownException();

        return Const.E_SUCCESS;
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Integer updatePlusGift(Plus plus) throws ResultCodeException {
        int effected = sqlSession.update("Plus.updatePlusGift", plus);
        if (effected == 0)
            throw new UnknownException();

        return Const.E_SUCCESS;
    }

}
