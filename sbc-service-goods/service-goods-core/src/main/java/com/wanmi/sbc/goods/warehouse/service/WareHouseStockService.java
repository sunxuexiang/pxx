package com.wanmi.sbc.goods.warehouse.service;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseDetailRequest;
import com.wanmi.sbc.goods.bean.vo.WareHouseDetailVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseStockVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouse;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouseDetail;
import com.wanmi.sbc.goods.warehouse.model.root.WareHouseStock;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseDetailRepository;
import com.wanmi.sbc.goods.warehouse.repository.WareHouseStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>商品库位关联表业务逻辑</p>
 *
 * @author zhangwenchang
 * @date 2020-04-06 17:21:37
 */
@Service("WareHouseStockService")
public class WareHouseStockService {
    @Autowired
    private WareHouseStockRepository wareHouseStockRepository;



    /**
     * 单个查询仓库表
     *
     * @author zhangwenchang
     */
    public List<WareHouseStock> getByWareIdAndSkuId(String wareHouseId,String skuId) {
        return wareHouseStockRepository.findByWareHouseIdAndSkuId(wareHouseId, skuId);
    }

    /**
     * 将实体包装成VO
     *
     * @author zhangwenchang
     */
    public List<WareHouseStockVO> wrapperVo(List<WareHouseStock> wareHouseStockList) {
        if (!CollectionUtils.isEmpty(wareHouseStockList)) {
            List<WareHouseStockVO> wareHouseStockVOList = new ArrayList<>();
            for(WareHouseStock wareHouseStock : wareHouseStockList){
                WareHouseStockVO wareHouseStockVO = KsBeanUtil.convert(wareHouseStock, WareHouseStockVO.class);
                wareHouseStockVOList.add(wareHouseStockVO);
            }
            return wareHouseStockVOList;
        }
        return Collections.EMPTY_LIST;
    }

}

