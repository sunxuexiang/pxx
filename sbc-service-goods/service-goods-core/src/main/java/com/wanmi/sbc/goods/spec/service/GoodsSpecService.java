package com.wanmi.sbc.goods.spec.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.spec.GoodsSpecAddRequest;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpec;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品规格接口服务
 *
 * @author: daiyitian
 * @createDate: 2018/11/13 14:54
 * @version: 1.0
 */
@Service
public class GoodsSpecService {

    @Autowired
    private GoodsSpecRepository goodsSpecRepository;


    /**
     * 根据商品id批量查询规格列表
     *
     * @param goodsIds 商品id
     * @return 规格列表
     */
    public List<GoodsSpec> findByGoodsIds(List<String> goodsIds) {
        return goodsSpecRepository.findByGoodsIds(goodsIds);
    }

    /**
     * 添加规格列表
     *
     * @param goodsSpecAddRequest
     * @return 规格列表
     */
    public List<GoodsSpec> AddSpec(GoodsSpecAddRequest goodsSpecAddRequest) {
        List<GoodsSpec> goodsSpecs = new ArrayList<>();
        goodsSpecAddRequest.getGoodsSpecVOs().stream().forEach(goodsSpecVO -> {
            goodsSpecs.add(goodsSpecRepository.save(KsBeanUtil.copyPropertiesThird(goodsSpecVO, GoodsSpec.class)));
        });
        return goodsSpecs;
    }
}
