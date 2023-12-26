package com.wanmi.sbc.goods.goodsattribute.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.UUIDUtil;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.constant.GoodsBrandErrorCode;
import com.wanmi.sbc.goods.api.constant.PointsGoodsErrorCode;
import com.wanmi.sbc.goods.bean.enums.PointsGoodsStatus;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.company.model.root.GoodsCompany;
import com.wanmi.sbc.goods.goodsattribute.reponse.GoodsAttrQueryResponse;
import com.wanmi.sbc.goods.goodsattribute.repository.GoodsAttributeReponsitory;
import com.wanmi.sbc.goods.goodsattribute.request.GoodsAtrrQueryRequest;
import com.wanmi.sbc.goods.goodsattribute.request.GoodsAttributeQueryRequest;
import com.wanmi.sbc.goods.goodsattribute.root.GoodsAttribute;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.reponse.GoodsQueryResponse;
import com.wanmi.sbc.goods.pointsgoods.model.root.PointsGoods;
import com.wanmi.sbc.goods.pointsgoodscate.model.root.PointsGoodsCate;
import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品属性
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Service
@Transactional(readOnly = true, timeout = 10)
public class GoodsAttributeService {

    @Autowired
    private GoodsAttributeReponsitory goodsAttributeRepository;
    /**
     * 条件查询商品属性
     *
     * @param request 参数
     * @return list
     */
    public Page<GoodsAttribute> page(GoodsAtrrQueryRequest request) {
        return goodsAttributeRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    /**
     * 条件查询商品属性组件
     *
     * @param attributeId 属性ID
     * @return list
     */
    public GoodsAttribute findById(String attributeId) {
        return goodsAttributeRepository.getByAttributeId(attributeId);
    }

    /**
     * 新增属性
     *
     * @param goodsAttribute 属性信息
     * @throws SbcRuntimeException 业务异常
     */
    @Transactional
    public GoodsAttribute add(GoodsAttribute goodsAttribute) throws SbcRuntimeException {
        GoodsAttributeQueryRequest request = new GoodsAttributeQueryRequest();
        request.setDelFlag(DeleteFlag.NO.toValue());
        //限制重复名称
        request.setAttribute(goodsAttribute.getAttribute());
        if (goodsAttributeRepository.count(request.getWhereCriteria()) > 0) {
            throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"属性已存在");
        }


        goodsAttribute.setStatus(1);
        goodsAttribute.setDelFlag(DeleteFlag.NO);
        goodsAttribute.setCreateTime(LocalDateTime.now());
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        goodsAttributeRepository.save(goodsAttribute);
        return goodsAttribute;
    }


    @Transactional
    public GoodsAttribute set(GoodsAttribute goodsAttribute) throws SbcRuntimeException {
        GoodsAttribute oldgoodsAttribute = goodsAttributeRepository.findById(Long.valueOf(goodsAttribute.getAttributeId())).orElse(null);
        if (oldgoodsAttribute == null || oldgoodsAttribute.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsBrandErrorCode.NOT_EXIST);
        }
        //更新属性
        goodsAttribute.setUpdateTime(LocalDateTime.now());
        KsBeanUtil.copyProperties(goodsAttribute, oldgoodsAttribute);
        goodsAttributeRepository.save(oldgoodsAttribute);
        return oldgoodsAttribute;
    }


    /**
     * 条件查询商品属性
     *
     * @param request 参数
     * @return list
     */
    public List<GoodsAttribute> query(GoodsAtrrQueryRequest request) {
        List<GoodsAttribute> list;
        Sort sort = request.getSort();
        if (Objects.nonNull(sort)) {
            list = goodsAttributeRepository.findAll(request.getWhereCriteria(), sort);
        } else {
            list = goodsAttributeRepository.findAll(request.getWhereCriteria());
        }
        return ListUtils.emptyIfNull(list);
    }


    /**
     * 将实体包装成VO
     *
     * @author sgy
     */
    public GoodsAttributeVo wrapperVo(GoodsAttribute goodsAttribute) {
        if (goodsAttribute != null) {
            GoodsAttributeVo pointsGoodsVO = new GoodsAttributeVo();
            KsBeanUtil.copyPropertiesThird(goodsAttribute, pointsGoodsVO);


            return pointsGoodsVO;
        }
        return null;
    }
    /**
     * 单个删除
     *
     * @author sgy
     */
    @Transactional
    public void deleteById(String id) {
        goodsAttributeRepository.modifyDelFlagById(id);
    }
    /**
     * 单个查询
     *
     * @author sgy
     */

    public GoodsAttribute getById(String id) {
        return goodsAttributeRepository.getByAttributeId(id);
    }
    /**
     * 修改商品属性
     *
     * @author sgy
     */
    @Transactional
    public void updateAttribute(GoodsAttribute goodsAttribute) {
        goodsAttributeRepository.save(goodsAttribute);
    }
}
