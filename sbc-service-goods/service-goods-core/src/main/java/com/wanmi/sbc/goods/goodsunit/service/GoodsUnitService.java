package com.wanmi.sbc.goods.goodsunit.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.GoodsBrandErrorCode;
import com.wanmi.sbc.goods.bean.vo.GoodsUnitVo;
import com.wanmi.sbc.goods.goodsunit.repository.GoodsUnitReponsitory;
import com.wanmi.sbc.goods.goodsunit.request.GoodsUnitQueryRequest;
import com.wanmi.sbc.goods.goodsunit.root.StoreGoodsUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 商品单位
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:25
 * @Description: TODO
 * @Version 1.0
 */
@Service
@Slf4j
@Transactional(readOnly = true, timeout = 10)
public class GoodsUnitService {

    @Autowired
    private GoodsUnitReponsitory unitRepository;
    /**
     * 条件查询商品单位
     *
     * @param request 参数
     * @return list
     */
    public Page<StoreGoodsUnit> page(GoodsUnitQueryRequest request) {
        return unitRepository.findAll(request.getWhereCriteria(), request.getPageRequest());
    }

    /**
     * 条件查询商品单位组件
     *
     * @param unitId 单位ID
     * @return list
     */
    public StoreGoodsUnit findById(String unitId) {
        return unitRepository.getByStoreGoodsUnitId(unitId);
    }

    /**
     * 新增单位
     *
     * @param unit 单位信息
     * @throws SbcRuntimeException 业务异常
     */
    @Transactional
    public StoreGoodsUnit add(StoreGoodsUnit unit) throws SbcRuntimeException {
        unit.setStatus(1);
        unit.setDelFlag(DeleteFlag.NO);
        unit.setCreateTime(LocalDateTime.now());
        unit.setUpdateTime(LocalDateTime.now());
        unitRepository.save(unit);
        return unit;
    }


    @Transactional
    public StoreGoodsUnit set(StoreGoodsUnit unit) throws SbcRuntimeException {
        StoreGoodsUnit oldunit = unitRepository.findById(Long.valueOf(unit.getStoreGoodsUnitId())).orElse(null);
        if (oldunit == null || oldunit.getDelFlag().compareTo(DeleteFlag.YES) == 0) {
            throw new SbcRuntimeException(GoodsBrandErrorCode.NOT_EXIST);
        }
        //更新单位
        unit.setUpdateTime(LocalDateTime.now());
        KsBeanUtil.copyProperties(unit, oldunit);
        unitRepository.save(oldunit);
        return oldunit;
    }


    /**
     * 条件查询商品单位
     *
     * @param request 参数
     * @return list
     */
    public List<StoreGoodsUnit> query(GoodsUnitQueryRequest request) {
        List<StoreGoodsUnit> list;
        Sort sort = request.getSort();
        if (Objects.nonNull(sort)) {
            list = unitRepository.findAll(request.getWhereCriteria(), sort);
        } else {
            list = unitRepository.findAll(request.getWhereCriteria());
        }
        return ListUtils.emptyIfNull(list);
    }


    /**
     * 将实体包装成VO
     *
     * @author sgy
     */
    public GoodsUnitVo wrapperVo(StoreGoodsUnit unit) {
        if (unit != null) {
            GoodsUnitVo pointsGoodsVO = new GoodsUnitVo();
            KsBeanUtil.copyPropertiesThird(unit, pointsGoodsVO);


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
    public void deleteById(Long id) {
        unitRepository.deleteById(id);
    }
    /**
     * 单个查询
     *
     * @author sgy
     */

    public StoreGoodsUnit getById(String id) {
        return unitRepository.getByStoreGoodsUnitId(id);
    }
    /**
     * 修改商品单位
     *
     * @author sgy
     */
    @Transactional
    public StoreGoodsUnit updateUnit(StoreGoodsUnit unit) {
        unitRepository.save(unit);
        return unit;
    }
}
