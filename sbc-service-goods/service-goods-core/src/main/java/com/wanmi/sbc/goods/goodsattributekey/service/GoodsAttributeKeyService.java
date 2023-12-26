package com.wanmi.sbc.goods.goodsattributekey.service;

import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.bean.vo.GoodsAttributeKeyVO;
import com.wanmi.sbc.goods.goodsattributekey.repository.GoodsAttributeKeyReponsitory;
import com.wanmi.sbc.goods.goodsattributekey.request.GoodsAtrrKeyQueryRequest;
import com.wanmi.sbc.goods.goodsattributekey.root.GoodsAttributeKey;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
/**
 * 商品属性关联信息表
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Service
@Transactional(readOnly = true, timeout = 10)
public class GoodsAttributeKeyService {

    @Autowired
    private GoodsAttributeKeyReponsitory goodsAttributeRepository;
    /**
     * 条件查询商品属性关联关系
     *
     * @param request 参数
     * @return list
     */
    public Page<GoodsAttributeKey> page(GoodsAtrrKeyQueryRequest request) {
        return goodsAttributeRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    /**
     * 条件查询商品属性关联关系组件
     *
     * @param attributeId 属性关联关系ID
     * @return list
     */
    public GoodsAttributeKey findById(long attributeId) {
        return goodsAttributeRepository.findById(attributeId).get();
    }

    /**
     * 新增属性关联关系
     *
     * @param goodsAttribute 属性关联关系信息
     * @throws SbcRuntimeException 业务异常
     */
    @Transactional
    public GoodsAttributeKey add(GoodsAttributeKey goodsAttribute) throws SbcRuntimeException {
        goodsAttributeRepository.save(goodsAttribute);
        return goodsAttribute;
    }

    /**
     * 条件查询商品属性关联关系
     *
     * @param request 参数
     * @return list
     */
    public List<GoodsAttributeKey> queryList(List<String> request) {
        return  goodsAttributeRepository.findByGoodsIdIn(request);

    }

    /**
     * 条件查询商品属性关联关系
     *
     * @param request 参数
     * @return list
     */
    public List<GoodsAttributeKey> queryGoodsInfoList(List<String> request) {
        return  goodsAttributeRepository.findByGoodsInfoIdIn(request);

    }

    /**
     * 条件查询商品属性关联关系
     *
     * @param request 参数
     * @return list
     */
    public List<GoodsAttributeKey> query(GoodsAtrrKeyQueryRequest request) {
        List<GoodsAttributeKey> list;
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
    public GoodsAttributeKeyVO wrapperVo(GoodsAttributeKey goodsAttribute) {
        if (goodsAttribute != null) {
            GoodsAttributeKeyVO pointsGoodsVO =new GoodsAttributeKeyVO();
            KsBeanUtil.copyPropertiesThird(goodsAttribute, pointsGoodsVO);
            pointsGoodsVO.setAttributeName(goodsAttribute.getAttribute().getAttribute());
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
    public void deleteById(long id) {
        goodsAttributeRepository.deleteById(id);
    }
    /**
     * 单个查询
     *
     * @author sgy
     */

    public GoodsAttributeKey getById(long id) {
        return goodsAttributeRepository.findById(id).get();
    }
    /**
     * 修改商品属性关联关系
     *
     * @author sgy
     */
    @Transactional
    public void updateAttribute(GoodsAttributeKey goodsAttribute) {
        goodsAttributeRepository.save(goodsAttribute);
    }
}
