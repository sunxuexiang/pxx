package com.wanmi.sbc.goods.mapping.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.goods.api.request.info.CatWareGetGoodsInfoMappingRequest;
import com.wanmi.sbc.goods.mapping.model.root.GoodsInfoMapping;
import com.wanmi.sbc.goods.mapping.repository.GoodsInfoMappingRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品映射服务
 * Created by daiyitian on 2017/4/11.
 */
@Service
@Log4j2
public class GoodsInfoMappingService {

    @Autowired
    private GoodsInfoMappingRepository goodsInfoMappingRepository;

    /**
     * 切换仓库获取对应商品关系
     * @return
     */
    public List<GoodsInfoMapping> catWareGetGoodsInfoMapping(CatWareGetGoodsInfoMappingRequest request){
        List<GoodsInfoMapping> byParentGoodsInfoIdInAndWareId = goodsInfoMappingRepository.findWareId(request.getGoodsInfoIds(), request.getWareId());
        return byParentGoodsInfoIdInAndWareId;
    }

    public GoodsInfoMapping findByErpGoodsInfoNo(String erpGoodsInfoNo) {
       return goodsInfoMappingRepository.findByErpGoodsInfoNo(erpGoodsInfoNo);
    }

    public void updateGoodsInfoIdByErpNo(String goodsInfoId, String erpGoodsInfoNo) {
         goodsInfoMappingRepository.updateGoodsInfoIdByErpNo(goodsInfoId,erpGoodsInfoNo);
    }

    public GoodsInfoMapping saveGoodsInfoMapping(GoodsInfoMapping mapping) {
        GoodsInfoMapping save = goodsInfoMappingRepository.save(mapping);
        return save;
    }
}
