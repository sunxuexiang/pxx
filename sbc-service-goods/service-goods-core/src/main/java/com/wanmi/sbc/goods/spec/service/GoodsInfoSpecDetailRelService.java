package com.wanmi.sbc.goods.spec.service;

import com.wanmi.sbc.goods.spec.model.root.GoodsInfoSpecDetailRel;
import com.wanmi.sbc.goods.spec.repository.GoodsInfoSpecDetailRelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;

/**
 * @author: wanggang
 * @createDate: 2018/11/13 14:54
 * @version: 1.0
 */
@Service
public class GoodsInfoSpecDetailRelService {

    @Autowired
    private GoodsInfoSpecDetailRelRepository goodsInfoSpecDetailRelRepository;


    /**
     * 根据spuid 和skuid查询
     * @param goodsId
     * @param goodsInfoId
     * @return
     */
    public List<GoodsInfoSpecDetailRel> findByGoodsIdAndGoodsInfoId(String goodsId, String goodsInfoId){
       return goodsInfoSpecDetailRelRepository.findByGoodsIdAndGoodsInfoId(goodsId, goodsInfoId);
    }

    /**
     * 根据多个SkuID查询
     * @param goodsInfoIds 多SkuID
     * @return
     */
    public List<GoodsInfoSpecDetailRel> findByGoodsInfoIds(List<String> goodsInfoIds){
        return goodsInfoSpecDetailRelRepository.findByGoodsInfoIds(goodsInfoIds);
    }

    /**
     * 根据多个SkuID返回格式化的规格
     *
     * @param goodsInfoIds 多SkuID
     * @return <skuId,specText>
     */
    public Map<String, String> textByGoodsInfoIds(List<String> goodsInfoIds) {
        return this.findByGoodsInfoIds(goodsInfoIds).stream().collect(
                Collectors.groupingBy(GoodsInfoSpecDetailRel::getGoodsInfoId,
                        mapping(GoodsInfoSpecDetailRel::getDetailName, joining(" "))));
    }
}
