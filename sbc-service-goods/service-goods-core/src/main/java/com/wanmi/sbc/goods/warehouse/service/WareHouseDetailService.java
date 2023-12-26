package com.wanmi.sbc.goods.warehouse.service;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.constant.WareHouseConstants;
import com.wanmi.sbc.goods.api.constant.WareHouseErrorCode;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseDetailRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseModifyDefaultFlagRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseModifyRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseQueryRequest;
import com.wanmi.sbc.goods.bean.vo.WareHouseDetailVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.redis.RedisService;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouseDetail;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseDetailRepository;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>仓库表业务逻辑</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@Service("WareHouseDetailService")
public class WareHouseDetailService {
    @Autowired
    private WareHouseDetailRepository wareHouseDetailRepository;


    /**
     * 列表查询仓库表
     *
     * @author zhangwenchang
     */
    public List<WareHouseDetail> list(WareHouseDetailRequest wreHouseDetailRequest) {
        return wareHouseDetailRepository.findAll(WareHouseDetailWhereCriteriaBuilder.build(wreHouseDetailRequest));
    }

    /**
     * 单个查询仓库表
     *
     * @author zhangwenchang
     */
    public WareHouseDetail getByWareId(Long wareId) {
        Optional<WareHouseDetail> optional =  wareHouseDetailRepository.findByWareIdAndDelFlag(wareId, DeleteFlag.NO);
        return optional.isPresent() ? optional.get() : null;
    }


    /**
     * 新增仓库表
     *
     * @author zhangwenchang
     */
    @Transactional
    public WareHouseDetail add(WareHouseDetail entity) {
        wareHouseDetailRepository.save(entity);
        return entity;
    }

    /**
     * 修改仓库表
     *
     * @author zhangwenchang
     */
    @Transactional
    public WareHouseDetail modify(WareHouseDetail entity) {
        wareHouseDetailRepository.save(entity);
        return entity;
    }

    /**
     * 单个删除仓库表
     *
     * @author zhangwenchang
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public void deleteById(WareHouseDetail entity) {
        wareHouseDetailRepository.deleteById(entity.getId());
    }


    /**
     * 将实体包装成VO
     *
     * @author zhangwenchang
     */
    public WareHouseDetailVO wrapperVo(WareHouseDetail wareHouse) {
        if (wareHouse != null) {
            WareHouseDetailVO wareHouseVO = KsBeanUtil.convert(wareHouse, WareHouseDetailVO.class);
            return wareHouseVO;
        }
        return null;
    }

}

