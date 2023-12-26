package com.wanmi.sbc.goodswarestock.service;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForIdsRequest;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsWareStockService
 * @Description TODO
 * @Author lvzhenwei
 * @Date 2020/4/27 14:09
 **/
@Service
public class GoodsWareStockService {

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    /**
     * @return java.util.List<T>
     * @Author lvzhenwei
     * @Description 根据商品数据将对应的分仓数据信息完善
     * @Date 20:04 2020/4/24
     * @Param [goodsInfoList, clazz, cloum]
     **/
    public <T> List<T> copyGoodsInfoStock(List<? extends T> goodsInfoList, Class<T> clazz, String cloum) {
        List<T> newGoodsInfoList = new ArrayList<>();
        List<String> goodsInfoIds = goodsInfoList.stream().map(goodsInfo -> {
            Object goodsInfoObj = JSONObject.parseObject(JSONObject.toJSONString(goodsInfo)).get(cloum);
            if (Objects.nonNull(goodsInfoObj)) {
                return goodsInfoObj.toString();
            } else {
                return "";
            }
        }).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(goodsInfoIds)){
            return newGoodsInfoList;
        }
        List<GoodsWareStockVO> goodsWareStockVOList = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoIds(GoodsWareStockByGoodsForIdsRequest.builder().goodsForIdList(goodsInfoIds).build()).getContext().getGoodsWareStockVOList();
        Map<String, BigDecimal> goodsWareStockMap = goodsWareStockVOList.stream().collect(Collectors.toMap(GoodsWareStockVO::getGoodsInfoId, GoodsWareStockVO::getStock));
        if (CollectionUtils.isNotEmpty(goodsWareStockVOList)) {
            goodsInfoList.stream().forEach(goodsInfoVO -> {
                String goodsInfoId = JSONObject.parseObject(JSONObject.toJSONString(goodsInfoVO), GoodsInfoVO.class).getGoodsInfoId();
                JSONObject newGoodsInfoObj = JSONObject.parseObject(JSONObject.toJSONString(goodsInfoVO));
                newGoodsInfoObj.put("stock", goodsWareStockMap.get(goodsInfoId));
                T t1 = JSONObject.parseObject(JSONObject.toJSONString(newGoodsInfoObj), clazz);
                newGoodsInfoList.add(t1);
            });
        } else {
            goodsInfoList.stream().forEach(goodsInfo -> {
                T t1 = JSONObject.parseObject(JSONObject.toJSONString(goodsInfo), clazz);
                newGoodsInfoList.add(t1);
            });
        }
        return newGoodsInfoList;
    }
}
