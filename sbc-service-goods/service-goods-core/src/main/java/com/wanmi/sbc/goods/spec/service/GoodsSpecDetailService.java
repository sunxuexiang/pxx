package com.wanmi.sbc.goods.spec.service;

import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.request.spec.GoodsSpecDetailAddRequest;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpec;
import com.wanmi.sbc.goods.spec.model.root.GoodsSpecDetail;
import com.wanmi.sbc.goods.spec.repository.GoodsSpecDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class GoodsSpecDetailService {
    @Autowired
    private GoodsSpecDetailRepository goodsSpecDetailRepository;
    /**
     * 添加规格值
     *
     * @param goodsSpecDetailAddRequest
     * @return 规格值列表
     */
    public List<GoodsSpecDetail> AddSpecDetail(GoodsSpecDetailAddRequest goodsSpecDetailAddRequest) {
        List<GoodsSpecDetail> goodsSpecDetails = new ArrayList<>();
        goodsSpecDetailAddRequest.getGoodsSpecDetailVOS().stream().forEach(goodsSpecDetailVO -> {
            goodsSpecDetails.add(goodsSpecDetailRepository
                    .save(KsBeanUtil.copyPropertiesThird(goodsSpecDetailVO, GoodsSpecDetail.class)));
        });
        return goodsSpecDetails;
    }
}
