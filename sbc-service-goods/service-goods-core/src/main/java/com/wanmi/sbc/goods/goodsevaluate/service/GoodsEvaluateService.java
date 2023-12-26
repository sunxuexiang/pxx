package com.wanmi.sbc.goods.goodsevaluate.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.goodsevaluate.GoodsEvaluateCountRequset;
import com.wanmi.sbc.goods.api.request.goodsevaluate.GoodsEvaluatePageRequest;
import com.wanmi.sbc.goods.api.request.goodsevaluate.GoodsEvaluateQueryRequest;
import com.wanmi.sbc.goods.api.request.goodsevaluateimage.GoodsEvaluateImageQueryRequest;
import com.wanmi.sbc.goods.api.request.goodstobeevaluate.GoodsTobeEvaluateQueryRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsEvaluateImageVO;
import com.wanmi.sbc.goods.bean.vo.GoodsEvaluateVO;
import com.wanmi.sbc.goods.goodsevaluate.model.root.GoodsEvaluate;
import com.wanmi.sbc.goods.goodsevaluate.repository.GoodsEvaluateRepository;
import com.wanmi.sbc.goods.goodsevaluateimage.model.root.GoodsEvaluateImage;
import com.wanmi.sbc.goods.goodsevaluateimage.service.GoodsEvaluateImageService;
import com.wanmi.sbc.goods.goodstobeevaluate.model.root.GoodsTobeEvaluate;
import com.wanmi.sbc.goods.goodstobeevaluate.repository.GoodsTobeEvaluateRepository;
import com.wanmi.sbc.goods.goodstobeevaluate.service.GoodsTobeEvaluateWhereCriteriaBuilder;
import com.wanmi.sbc.goods.images.service.GoodsImageService;
import com.wanmi.sbc.goods.info.repository.GoodsRepository;
import com.wanmi.sbc.goods.mq.GoodsEvaluateNumMqService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>商品评价业务逻辑</p>
 *
 * @author liutao
 * @date 2019-02-25 15:14:16
 */
@Service("GoodsEvaluateService")
public class GoodsEvaluateService {
    @Autowired
    private GoodsEvaluateRepository goodsEvaluateRepository;

    @Autowired
    private GoodsImageService goodsImageService;

    @Autowired
    private GoodsEvaluateImageService goodsEvaluateImageService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsEvaluateNumMqService goodsEvaluateNumMqService;

    @Autowired
    private GoodsTobeEvaluateRepository goodsTobeEvaluateRepository;

    /**
     * 新增商品评价
     *
     * @author liutao
     */
    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public GoodsEvaluate add(GoodsEvaluate entity) {
        //获取待评价数据的商品图片以及商品购买时间
        GoodsTobeEvaluateQueryRequest queryRequest = new GoodsTobeEvaluateQueryRequest();
        queryRequest.setOrderNo(entity.getOrderNo());
        queryRequest.setGoodsInfoId(entity.getGoodsInfoId());
        GoodsTobeEvaluate goodsTobeEvaluate = goodsTobeEvaluateRepository.findOne(GoodsTobeEvaluateWhereCriteriaBuilder.build(queryRequest)).orElse(new GoodsTobeEvaluate());
        entity.setGoodsImg(goodsTobeEvaluate.getGoodsImg());
        entity.setBuyTime(goodsTobeEvaluate.getBuyTime());
        entity.setGoodNum(0);
        goodsEvaluateRepository.save(entity);
        //增加评论数
        goodsEvaluateNumMqService.updateGoodsEvaluateNum(entity);
        return entity;
    }

    /**
     * 批量新增商品评价
     *
     * @author lvzhenwei
     */
    public void addList(List<GoodsEvaluate> entityList) {
        goodsEvaluateRepository.saveAll(entityList);
        //增加评论数
        entityList.forEach(goodsEvaluate -> {
            goodsEvaluateNumMqService.updateGoodsEvaluateNum(goodsEvaluate);
        });
    }

    /**
     * 修改商品评价
     *
     * @author liutao
     */
    @Transactional
    public GoodsEvaluate modify(GoodsEvaluate entity) {
        GoodsEvaluateImageQueryRequest queryReq = new GoodsEvaluateImageQueryRequest();
        queryReq.setEvaluateId(entity.getEvaluateId());
        List<GoodsEvaluateImage> goodsEvaluateImages = goodsEvaluateImageService.list(queryReq);
        entity.setGoodsEvaluateImages(goodsEvaluateImages);
        goodsEvaluateRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除商品评价
     *
     * @author liutao
     */
    @Transactional
    public void deleteById(String id) {
        goodsEvaluateRepository.deleteById(id);
    }

    /**
     * 批量删除商品评价
     *
     * @author liutao
     */
    @Transactional
    public void deleteByIdList(List<String> ids) {
        goodsEvaluateRepository.deleteAll(ids.stream().map(id -> {
            GoodsEvaluate entity = new GoodsEvaluate();
            entity.setEvaluateId(id);
            return entity;
        }).collect(Collectors.toList()));
    }

    /**
     * 单个查询商品评价
     *
     * @author liutao
     */
    public GoodsEvaluate getById(String id) {
        return goodsEvaluateRepository.findById(id).orElse(null);
    }

    /**
     * 分页查询商品评价
     *
     * @author liutao
     */
    public Page<GoodsEvaluate> page(GoodsEvaluateQueryRequest queryReq) {
        return goodsEvaluateRepository.findAll(
                GoodsEvaluateWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }

    /**
     * 列表查询商品评价
     *
     * @author liutao
     */
    public List<GoodsEvaluate> list(GoodsEvaluateQueryRequest queryReq) {
        return goodsEvaluateRepository.findAll(GoodsEvaluateWhereCriteriaBuilder.build(queryReq));
    }

    /**
     * 获取已评价商品数量
     *
     * @param queryReq
     * @return
     */
    public Long getGoodsEvaluateNum(GoodsEvaluateQueryRequest queryReq) {
        return goodsEvaluateRepository.count(GoodsEvaluateWhereCriteriaBuilder.build(queryReq));
    }

    /**
     * @param requset {@link GoodsEvaluateCountRequset}
     * @Description: 商品好评率
     * @Author: Bob
     * @Date: 2019-04-09 16:05
     */
    public String getGoodsPraise(GoodsEvaluateCountRequset requset) {
        return goodsEvaluateRepository.queryPraise(requset.getGoodsId());
    }

    /**
     * @Description: 查询最新评价<排除系统评价>
     * @param request
     * @Author: Bob
     * @Date: 2019-05-29 17:43
     */
    public List<GoodsEvaluate> getTop(GoodsEvaluatePageRequest request){
        return goodsEvaluateRepository.queryTopData(request, request.getPageRequest());
    }

    /**
     * 将实体包装成VO
     *
     * @author liutao
     */
    public GoodsEvaluateVO wrapperVo(GoodsEvaluate goodsEvaluate) {
        if (goodsEvaluate != null) {
            GoodsEvaluateVO goodsEvaluateVO = new GoodsEvaluateVO();
            KsBeanUtil.copyPropertiesThird(goodsEvaluate, goodsEvaluateVO);
            goodsEvaluateVO.setGoodsImages(goodsImageService.findByGoodsId(goodsEvaluateVO.getGoodsId()));
            GoodsEvaluateImageQueryRequest goodsEvaluateImageQueryRequest = new GoodsEvaluateImageQueryRequest();
            goodsEvaluateImageQueryRequest.setEvaluateId(goodsEvaluateVO.getEvaluateId());
            List<GoodsEvaluateImage> goodsEvaluateImageList = goodsEvaluateImageService.list(goodsEvaluateImageQueryRequest);
            if (CollectionUtils.isNotEmpty(goodsEvaluateImageList)) {
                List<GoodsEvaluateImageVO> goodsEvaluateImageVOList = goodsEvaluateImageList.stream()
                        .map(goodsEvaluateImage -> goodsEvaluateImageService.wrapperVo(goodsEvaluateImage))
                        .collect(Collectors.toList());
                goodsEvaluateVO.setEvaluateImageList(goodsEvaluateImageVOList);
            }
            return goodsEvaluateVO;
        }
        return null;
    }
}
