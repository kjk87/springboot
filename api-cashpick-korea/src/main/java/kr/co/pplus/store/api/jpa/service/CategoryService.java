package kr.co.pplus.store.api.jpa.service;

import kr.co.pplus.store.api.jpa.model.*;
import kr.co.pplus.store.api.jpa.repository.*;
import kr.co.pplus.store.mvc.service.RootService;
import kr.co.pplus.store.type.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(transactionManager = "jpaTransactionManager")
public class CategoryService extends RootService {
    private final static Logger logger = LoggerFactory.getLogger(CategoryService.class);


    @Autowired
    CategoryMajorOnlyRepository categoryMajorOnlyRepository;

    @Autowired
    CategoryMajorRepository categoryMajorRepository;

    @Autowired
    CategoryMinorRepository categoryMinorRepository;

    @Autowired
    CategoryFavoriteRepository categoryFavoriteRepository;

    @Autowired
    CategoryFirstRepository categoryFirstRepository;


    public List<CategoryFirst> getCategoryFirstList() {
        return categoryFirstRepository.findByStatusOrderByArrayAscNameAsc("active");
    }

    public List<CategoryMajorOnly> getCategoryMajorOnlyList() {
        return categoryMajorOnlyRepository.findAllByStatusOrderByArrayAsc("active");
    }

    public CategoryMajorOnly getCategoryMajorOnly(Long seqNo) {
        return categoryMajorOnlyRepository.findBySeqNo(seqNo);
    }

    public List<CategoryMajor> getCategoryMajorList() {
        return categoryMajorRepository.findAllByStatusOrderByArrayAsc("active");
    }

    public List<CategoryMinor> getCategoryMinorList(Long major) {
        return categoryMinorRepository.findByMajorAndStatusOrderByArrayAsc(major, "active");
    }

    public CategoryMinor getCategoryMinorBySeqNo(Long seqNo) {
        return categoryMinorRepository.findBySeqNo(seqNo);
    }

    public List<CategoryFavorite> getCategoryFavoriteList(User user){
        return categoryFavoriteRepository.findAllByMemberSeqNo(user.getNo());
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public CategoryFavorite insertCategoryFavorite(CategoryFavorite categoryFavorite){
        categoryFavorite.setId(null);
        categoryFavorite = categoryFavoriteRepository.saveAndFlush(categoryFavorite);
        categoryFavorite.setCategoryMinor(getCategoryMinorBySeqNo(categoryFavorite.getCategoryMinorSeqNo()));

        return categoryFavorite;
    }

    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteCategoryFavorite(User user, Long categoryMinorSeqNo){
        categoryFavoriteRepository.deleteByMemberSeqNoAndCategoryMinorSeqNo(user.getNo(), categoryMinorSeqNo);
    }
}
