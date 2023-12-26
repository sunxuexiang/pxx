package com.wanmi.sbc.goods.goodsrecommendsetting.service;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.goodsrecommendsetting.GoodsRecommendSettingQueryRequest;
import com.wanmi.sbc.goods.bean.enums.RecommendStrategyStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendSettingVO;
import com.wanmi.sbc.goods.goodsrecommendsetting.model.root.GoodsRecommendSetting;
import com.wanmi.sbc.goods.goodsrecommendsetting.repository.GoodsRecommendSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * <p>商品推荐配置业务逻辑</p>
 *
 * @author chenyufei
 * @date 2019-09-07 10:24:37
 */
@Service("GoodsRecommendSettingService")
public class GoodsRecommendSettingService {
    @Autowired
    private GoodsRecommendSettingRepository goodsRecommendSettingRepository;

    /**
     * 新增商品推荐配置
     *
     * @author chenyufei
     */
    @Transactional
    public GoodsRecommendSetting add(GoodsRecommendSetting entity) {
        goodsRecommendSettingRepository.save(entity);
        return entity;
    }

    /**
     * 修改商品推荐配置
     *
     * @author chenyufei
     */
    @Transactional
    public  List<GoodsRecommendSetting> modify(GoodsRecommendSetting entity) {
        BoolFlag isIntelligentRecommend = entity.getIsIntelligentRecommend();
        List<GoodsRecommendSetting> repositoryAll = goodsRecommendSettingRepository.findAll();
        GoodsRecommendSetting goodsRecommendSetting;
        GoodsRecommendSetting  newGoodsRecommendSetting;
        List<GoodsRecommendSetting> newRepositoryAll = Lists.newArrayList();
        //判断是否智能推荐
        if (BoolFlag.YES.equals(isIntelligentRecommend)) {
             goodsRecommendSetting =
                    repositoryAll.stream().filter(c -> BoolFlag.YES.equals(c.getIsIntelligentRecommend())).findFirst().get();
            entity.setIntelligentStrategy(goodsRecommendSetting.getIntelligentStrategy());
            entity.setSettingId(goodsRecommendSetting.getSettingId());
            //不更新的数据
              newGoodsRecommendSetting =
                    repositoryAll.stream().filter(c -> BoolFlag.NO.equals(c.getIsIntelligentRecommend())).findFirst().get();
        }else {
             goodsRecommendSetting =
                    repositoryAll.stream().filter(c -> BoolFlag.NO.equals(c.getIsIntelligentRecommend())).findFirst().get();
             //初始化智能状态
            entity.setSettingId(goodsRecommendSetting.getSettingId());
            entity.setIsIntelligentRecommend(BoolFlag.NO);
            entity.setIntelligentRecommendAmount(0);
            entity.setIntelligentRecommendDimensionality(0);
            entity.setIntelligentStrategy(goodsRecommendSetting.getIntelligentStrategy());
            newGoodsRecommendSetting =
                    repositoryAll.stream().filter(c -> BoolFlag.YES.equals(c.getIsIntelligentRecommend())).findFirst().get();
        }
        //告知 前端支持（手动推荐  只能推荐两种两种数据对象返回）更新两条目的是更新过后直接返回两条数据的集合 无需要再次查询
        newRepositoryAll.add(entity);
        newRepositoryAll.add(newGoodsRecommendSetting);
        List<GoodsRecommendSetting> goodsRecommendSettings = goodsRecommendSettingRepository.saveAll(newRepositoryAll);
        return goodsRecommendSettings;
    }

    /**
     * 单个删除商品推荐配置
     *
     * @author chenyufei
     */
    @Transactional
    public void deleteById(String id) {
        goodsRecommendSettingRepository.deleteById(id);
    }

    /**
     * 批量删除商品推荐配置
     *
     * @author chenyufei
     */
    @Transactional
    public void deleteByIdList(List<String> ids) {
        ids.forEach(id -> goodsRecommendSettingRepository.deleteById(id));
    }

    /**
     * 单个查询商品推荐配置
     *
     * @author chenyufei
     */
    public GoodsRecommendSetting getById(String id) {
        return goodsRecommendSettingRepository.findById(id).orElse(null);
    }

    /**
     * 分页查询商品推荐配置
     *
     * @author chenyufei
     */
    public Page<GoodsRecommendSetting> page(GoodsRecommendSettingQueryRequest queryReq) {
        return goodsRecommendSettingRepository.findAll(
                GoodsRecommendSettingWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 分页查询商品推荐配置
     *
     * @author chenyufei
     */
    public GoodsRecommendSetting find() {
        return goodsRecommendSettingRepository.findAll().stream().findFirst().orElse(new GoodsRecommendSetting());
    }


    public List<GoodsRecommendSetting> findAll() {
        return goodsRecommendSettingRepository.findAll();
    }

        /**
         * 列表查询商品推荐配置
         *
         * @author chenyufei
         */
    public List<GoodsRecommendSetting> list(GoodsRecommendSettingQueryRequest queryReq) {
        return goodsRecommendSettingRepository.findAll(
                GoodsRecommendSettingWhereCriteriaBuilder.build(queryReq),
                queryReq.getSort());
    }

    /**
     * 将实体包装成VO
     *
     * @author chenyufei
     */
    public GoodsRecommendSettingVO wrapperVo(GoodsRecommendSetting goodsRecommendSetting) {
        if (goodsRecommendSetting != null) {
            GoodsRecommendSettingVO goodsRecommendSettingVO = new GoodsRecommendSettingVO();
            KsBeanUtil.copyPropertiesThird(goodsRecommendSetting, goodsRecommendSettingVO);
            return goodsRecommendSettingVO;
        }
        return null;
    }

    @Transactional
    public List<GoodsRecommendSetting> modifyStrategy(Boolean isOPenIntelligentStrategy) {
        List<GoodsRecommendSetting> repositoryAll = goodsRecommendSettingRepository.findAll();
        // 智能推荐策略(0：手动推荐策略  1 智能推荐策略) isOPenIntelligentStrategy  true则表示是智能推荐策略 false 手动推荐策略
        for (GoodsRecommendSetting goodsRecommendSetting : repositoryAll) {
            if (isOPenIntelligentStrategy){
                //是否是智能数据
                if (BoolFlag.YES.equals(goodsRecommendSetting.getIsIntelligentRecommend())) {
                    goodsRecommendSetting.setIntelligentStrategy(RecommendStrategyStatus.OPEN);
                }else {
                    goodsRecommendSetting.setIntelligentStrategy(RecommendStrategyStatus.CLOSE);
                }
            }else {
                //手动数据
                if (BoolFlag.NO.equals(goodsRecommendSetting.getIsIntelligentRecommend())) {
                    goodsRecommendSetting.setIntelligentStrategy(RecommendStrategyStatus.OPEN);
                } else {
                    goodsRecommendSetting.setIntelligentStrategy(RecommendStrategyStatus.CLOSE);
                }
            }
        }

       return goodsRecommendSettingRepository.saveAll(repositoryAll);

    }
}
