package com.wanmi.sbc.goods.goodstypeconfig.service;


import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantRecommendCatSortRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantTypeConfigQueryRequest;
import com.wanmi.sbc.goods.bean.vo.MerchantRecommendTypeVO;
import com.wanmi.sbc.goods.bean.vo.RecommendTypeVO;
import com.wanmi.sbc.goods.goodstypeconfig.repository.MerchantTypeConfigRepository;
import com.wanmi.sbc.goods.goodstypeconfig.repository.TypeConfigRepository;
import com.wanmi.sbc.goods.goodstypeconfig.root.MerchantRecommendType;
import com.wanmi.sbc.goods.goodstypeconfig.root.RecommendType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * <p>分类推荐分类业务逻辑</p>
 *
 * @author sgy
 * @date 2023-06-09 10:53:36
 */
@Service("merchantTypeConfigService")
public class MerchantTypeConfigService {

    @Autowired
    private MerchantTypeConfigRepository merchantTypeConfigRepository;

    @Autowired
    private TypeConfigRepository typeConfigRepository;


    /**
     * 新增分类推荐分类
     *
     * @author sgy
     */
    @Transactional
    public RecommendType add(RecommendType entity) {
        // 推荐分类设置不能超过10个校验

        List<RecommendType> goodsTypeRecommends = typeConfigRepository.findByCompanyInfoId(entity.getCompanyInfoId());
        int size = goodsTypeRecommends.size();
        if (CollectionUtils.isNotEmpty(goodsTypeRecommends)) {
            size = size + goodsTypeRecommends.size();
            if (size > 10) {
                throw new SbcRuntimeException(GoodsErrorCode.GOODS_CATE_GRADE_RECOMMEND);
            }
        }
        typeConfigRepository.save(entity);
        return entity;
    }

    /**
     * 修改分类推荐分类
     *
     * @author sgy
     */
    @Transactional
    public MerchantRecommendType modify(MerchantRecommendType entity) {
        merchantTypeConfigRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除分类推荐分类
     *
     * @author sgy
     */
    @Transactional
    public void deleteById(String id) {
        typeConfigRepository.deleteById(id);
    }

    /**
     *删除  根据分类信息
     *
     * @author sgy
     */
    @Transactional
    public void deleteByCateId(RecommendType type) {
        typeConfigRepository.deleteByCateAndStoreId(type.getStoreCateId(),type.getCompanyInfoId().toString());
    }

    /**
     * 批量删除分类推荐分类
     *
     * @author sgy
     */
    @Transactional
    public void deleteByIdList(List<String> ids) {
        ids.forEach(id -> typeConfigRepository.deleteById(id));
    }

    /**
     * 批量删除分类推荐分类
     *
     * @author sgy
     */
    @Transactional
    public void deleteAll() {
        merchantTypeConfigRepository.findAll().forEach(goodsRecommendGoods -> merchantTypeConfigRepository.delete(goodsRecommendGoods));
    }

    /**
     * 批量删除推荐分类信息
     * @param companyInfoId
     */
    @Transactional
    public void delByStoreId(Long companyInfoId) {
        merchantTypeConfigRepository.deleteByCompanyInfoId(companyInfoId);
    }

    /**
     * 单个查询分类推荐分类
     *
     * @author sgy
     */
    public MerchantRecommendType getById(String id) {
        return merchantTypeConfigRepository.findById(id).orElse(null);
    }

    /**
     * 分页查询分类推荐分类
     *
     * @author sgy
     */
    public Page<MerchantRecommendType> page(MerchantTypeConfigQueryRequest queryReq) {

        return merchantTypeConfigRepository.findAll(
                MerchantTypeConfigWhereCriteriaBuilder.build(queryReq),
                queryReq.getPageRequest());
    }
    /**
     * 列表查询分类推荐分类
     *
     * @author sgy
     */
    public List<MerchantRecommendType> sotreList(MerchantTypeConfigQueryRequest queryReq) {
        return merchantTypeConfigRepository.findAll(
                MerchantTypeConfigWhereCriteriaBuilder.build(queryReq),
                queryReq.getSort());
    }

    /**
     * 列表查询分类推荐分类
     *
     * @author sgy
     */
    public List<RecommendType> list(MerchantTypeConfigQueryRequest queryReq) {
        return typeConfigRepository.findAll(
                MerchantTypeConfigWhereCriteriaBuilder.storeBuild(queryReq),
                queryReq.getSort());
    }


    /**
     * 列表查询分类推荐分类
     *
     * @author sgy
     */
    public List<RecommendType> list() {
        return typeConfigRepository.findAll();
    }

    public List<RecommendType> findByCompanyInfoId(Long companyInfoId) {
        return typeConfigRepository.findByCompanyInfoId(companyInfoId);
    }



    /**
     * 将实体包装成VO
     *
     * @author sgy
     */
    public RecommendTypeVO wrapperTypeVo(RecommendType goodsRecommendGoods) {
        if (goodsRecommendGoods != null) {
            RecommendTypeVO goodsRecommendGoodsVO = new RecommendTypeVO();
            KsBeanUtil.copyPropertiesThird(goodsRecommendGoods, goodsRecommendGoodsVO);
            goodsRecommendGoodsVO.setStoreCateId(Long.parseLong(goodsRecommendGoods.getStoreCateId()));
            return goodsRecommendGoodsVO;
        }
        return null;
    }


    /**
     * 将实体包装成VO
     *
     * @author sgy
     */
    public MerchantRecommendTypeVO wrapperVo(MerchantRecommendType goodsRecommendGoods) {
        if (goodsRecommendGoods != null) {
            MerchantRecommendTypeVO goodsRecommendGoodsVO = new MerchantRecommendTypeVO();
            KsBeanUtil.copyPropertiesThird(goodsRecommendGoods, goodsRecommendGoodsVO);
            if (null!=goodsRecommendGoods&&null!=goodsRecommendGoods.getStoreCate()&&!Objects.isNull(goodsRecommendGoods.getStoreCate().getCateName())){
                goodsRecommendGoodsVO.setCateName(  goodsRecommendGoods.getStoreCate().getCateName());
            }
            if (null!=goodsRecommendGoods&&null!=goodsRecommendGoods.getStoreCate()&&!Objects.isNull(goodsRecommendGoods.getStoreCate().getCateImg())){
                goodsRecommendGoodsVO.setCateImg(  goodsRecommendGoods.getStoreCate().getCateImg());
            }
            return goodsRecommendGoodsVO;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sortMerchantRecommendCat(MerchantRecommendCatSortRequest request) {
        final String id = request.getMerchantTypeId();
        MerchantRecommendType recommendType = merchantTypeConfigRepository.findById(id).orElse(null);
        if (null == recommendType || null == recommendType.getStoreId()) {
            throw new SbcRuntimeException("参数异常");
        }
        final List<MerchantRecommendType> merchantRecommendTypes = sotreList(MerchantTypeConfigQueryRequest.builder().storeId(recommendType.getStoreId()).build());
        merchantRecommendTypes.sort(Comparator.comparing(MerchantRecommendType::getSort));
        List<String> ids = new ArrayList<>();
        if (request.getSort() > merchantRecommendTypes.size()) {
            throw new SbcRuntimeException("参数异常,排序超出范围");
        }
        merchantRecommendTypes.forEach(o -> {
            if (o.getMerchantTypeId().equals(id)) return;
            ids.add(o.getMerchantTypeId());
        });
        ids.add(request.getSort() - 1, id);
        for (int i = 0; i < ids.size(); i++) {
            merchantTypeConfigRepository.updateSortById(ids.get(i), i + 1);
        }
    }
}
