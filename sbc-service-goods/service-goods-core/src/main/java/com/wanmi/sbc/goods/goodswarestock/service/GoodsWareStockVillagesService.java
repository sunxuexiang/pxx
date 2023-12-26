package com.wanmi.sbc.goods.goodswarestock.service;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockQueryRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoMinusStockDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoPlusStockDTO;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStockVillages;
import com.wanmi.sbc.goods.goodswarestock.repository.GoodsWareStockVillagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 乡镇件库存信息
 * @author: XinJiang
 * @time: 2022/4/27 11:09
 */
@Service
public class GoodsWareStockVillagesService {

    @Autowired
    private GoodsWareStockVillagesRepository goodsWareStockVillagesRepository;

    /**
     * 新增sku乡镇件分仓库存表信息
     * @param entity
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public GoodsWareStockVillages addVillages(GoodsWareStockVillages entity) {
        entity = goodsWareStockVillagesRepository.save(entity);
        return entity;
    }

    /**
     * 根据地区信息和商品skuIds查询乡镇件库存信息
     * @param goodsInfoIds
     * @param wareId
     * @return
     */
    public List<GoodsWareStockVillages> getGoodsStockByAreaIdAndGoodsInfoIds(List<String> goodsInfoIds, Long wareId) {
        List<Object> results =
                goodsWareStockVillagesRepository.getGoodsStockByAreaIdAndGoodsInfoIds(wareId,goodsInfoIds);
        return resultToGoodsWareStockList(results);
    }

    private List<GoodsWareStockVillages> resultToGoodsWareStockList(List<Object> results) {
        return results.stream().map(item -> {
            GoodsWareStockVillages goodsWareStock = new GoodsWareStockVillages();
            goodsWareStock.setGoodsInfoNo((String) ((Object[]) item)[0]);
            goodsWareStock.setGoodsInfoId((String) ((Object[]) item)[1]);
            goodsWareStock.setStock(((BigDecimal) ((Object[]) item)[2]));
            return goodsWareStock;
        }).collect(Collectors.toList());
    }

    /**
     * @return java.util.List<com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock>
     * @Author lvzhenwei
     * @Description 根据商品goodsInfoIdList查询对应的商品库存数量
     * @Date 18:50 2020/4/17
     * @Param [request]
     **/
    public List<GoodsWareStockVillages> findByGoodsInfoIdIn(List<String> goodsInfoIds) {
        return goodsWareStockVillagesRepository.findByGoodsInfoIdIn(goodsInfoIds);
    }

    /**
     * 列表查询sku分仓库存表
     *
     * @author zhangwenchang
     */
    public List<GoodsWareStockVillages> list(GoodsWareStockQueryRequest queryReq) {
        return goodsWareStockVillagesRepository.findAll(GoodsWareStockWhereCriteriaBuilder.villagesBuild(queryReq));
    }

    /**
     * 批量增加库存
     * @param stockList
     * @param wareId
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void batchPlusStock(List<GoodsInfoPlusStockDTO> stockList, Long wareId){
        stockList.stream().forEach(s->goodsWareStockVillagesRepository.addStockByWareIdAndGoodsInfoId(s.getStock(),s.getGoodsInfoId(),wareId));
    }

    /**
     * 批量扣减库存
     * @param skuList
     */
    @Transactional(rollbackFor = Exception.class)
    @LcnTransaction
    public void batchSubStock(List<GoodsInfoMinusStockDTO> skuList, Long wareId){
        skuList.stream().forEach(s->goodsWareStockVillagesRepository.subStockByWareIdAndGoodsInfoId(s.getStock(),s.getGoodsInfoId(),wareId));
    }
}
