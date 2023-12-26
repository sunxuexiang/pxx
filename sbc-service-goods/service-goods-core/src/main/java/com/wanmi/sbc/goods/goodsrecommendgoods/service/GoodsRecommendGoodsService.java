package com.wanmi.sbc.goods.goodsrecommendgoods.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.GoodsRecommendGoodsQueryRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsRecommendGoodsVO;
import com.wanmi.sbc.goods.goodsrecommendgoods.model.root.GoodsRecommendGoods;
import com.wanmi.sbc.goods.goodsrecommendgoods.repository.GoodsRecommendGoodsRepository;
import com.wanmi.sbc.goods.info.repository.GoodsInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * <p>商品推荐商品业务逻辑</p>
 *
 * @author chenyufei
 * @date 2019-09-07 10:53:36
 */
@Service("GoodsRecommendGoodsService")
public class GoodsRecommendGoodsService {

    @Autowired
    private GoodsRecommendGoodsRepository goodsRecommendGoodsRepository;

    @Autowired
    private GoodsInfoRepository goodsInfoRepository;

    /**
     * 新增商品推荐商品
     *
     * @author chenyufei
     */
    @Transactional
    public GoodsRecommendGoods add(GoodsRecommendGoods entity) {
        goodsRecommendGoodsRepository.save(entity);
        return entity;
    }

    /**
     * 修改商品推荐商品
     *
     * @author chenyufei
     */
    @Transactional
    public GoodsRecommendGoods modify(GoodsRecommendGoods entity) {
        goodsRecommendGoodsRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除商品推荐商品
     *
     * @author chenyufei
     */
    @Transactional
    public void deleteById(String id) {
        goodsRecommendGoodsRepository.deleteById(id);
    }

    /**
     * 批量删除商品推荐商品
     *
     * @author chenyufei
     */
    @Transactional
    public void deleteByIdList(List<String> ids) {
        ids.forEach(id -> goodsRecommendGoodsRepository.deleteById(id));
    }

    /**
     * 批量删除商品推荐商品
     *
     * @author chenyufei
     */
    @Transactional
    public void deleteAll() {
        goodsRecommendGoodsRepository.findAll().forEach(goodsRecommendGoods -> goodsRecommendGoodsRepository.delete(goodsRecommendGoods));
    }

    /**
     * 批量删除推荐商品信息
     * @param wareId
     */
    @Transactional
    public void delByWareId(Long wareId) {
        goodsRecommendGoodsRepository.deleteByWareId(wareId);
    }

    /**
     * 单个查询商品推荐商品
     *
     * @author chenyufei
     */
    public GoodsRecommendGoods getById(String id) {
        return goodsRecommendGoodsRepository.findById(id).orElse(null);
    }

    /**
     * 分页查询商品推荐商品
     *
     * @author chenyufei
     */
    public Page<GoodsRecommendGoods> page(GoodsRecommendGoodsQueryRequest queryReq) {
        return goodsRecommendGoodsRepository.findAll(
                GoodsRecommendGoodsWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询商品推荐商品
     *
     * @author chenyufei
     */
    public List<GoodsRecommendGoods> list(GoodsRecommendGoodsQueryRequest queryReq) {
        return goodsRecommendGoodsRepository.findAll(
                GoodsRecommendGoodsWhereCriteriaBuilder.build(queryReq),
                queryReq.getSort());
    }


    /**
     * 列表查询商品推荐商品
     *
     * @author chenyufei
     */
    public List<GoodsRecommendGoods> list() {
        return goodsRecommendGoodsRepository.findAll();
    }

    public List<GoodsRecommendGoods> findByWareId(Long wareId) {
        return goodsRecommendGoodsRepository.findByWareId(wareId);
    }

    /**
     * 将实体包装成VO
     *
     * @author chenyufei
     */
    public GoodsRecommendGoodsVO wrapperVo(GoodsRecommendGoods goodsRecommendGoods) {
        if (goodsRecommendGoods != null) {
            GoodsRecommendGoodsVO goodsRecommendGoodsVO = new GoodsRecommendGoodsVO();
            KsBeanUtil.copyPropertiesThird(goodsRecommendGoods, goodsRecommendGoodsVO);
            return goodsRecommendGoodsVO;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void clearRecommendSort(List<String> goodsInfoIds) {
        goodsInfoRepository.clearRecommendSort(goodsInfoIds);
    }
}
