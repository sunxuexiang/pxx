package com.wanmi.sbc.marketing.plugin.impl;

import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.flashsalegoods.FlashSaleGoodsListRequest;
import com.wanmi.sbc.goods.api.response.flashsalegoods.FlashSaleGoodsListResponse;
import com.wanmi.sbc.goods.bean.vo.FlashSaleGoodsVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.MarketingLabelVO;
import com.wanmi.sbc.marketing.plugin.IGoodsListPlugin;
import com.wanmi.sbc.marketing.request.MarketingPluginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: sbc-micro-service
 * @description: 秒杀活动营销插件
 * @create: 2019-07-02 18:31
 **/
@Repository("flashSalePlugin")
public class FlashSalePlugin implements IGoodsListPlugin {
    @Autowired
    private FlashSaleGoodsQueryProvider flashSaleGoodsQueryProvider;
    /**
     * 商品列表处理
     *
     * @param goodsInfos 商品数据
     * @param request    参数
     */
    @Override
    public void goodsListFilter(List<GoodsInfoVO> goodsInfos, MarketingPluginRequest request) {
        List<String> goodsinfoIds = goodsInfos.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        FlashSaleGoodsListResponse listResponse =
                flashSaleGoodsQueryProvider.list(FlashSaleGoodsListRequest.builder().goodsinfoIds(goodsinfoIds).delFlag(DeleteFlag.NO)
                        .queryDataType(1).build()).getContext();
        Map<String, FlashSaleGoodsVO> goodsVOMap =
        listResponse.getFlashSaleGoodsVOList().stream().collect(Collectors.toMap(FlashSaleGoodsVO::getGoodsInfoId,
                v->v));

        goodsInfos.forEach(goodsInfoVO -> {
            if (Objects.nonNull(goodsVOMap) && Objects.nonNull(goodsVOMap.get(goodsInfoVO.getGoodsInfoId()))) {
                FlashSaleGoodsVO vo = goodsVOMap.get(goodsInfoVO.getGoodsInfoId());
                //最小起订量大于库存量时认为活动结束
                if (vo.getStock() >= vo.getMinNum()) {
                    MarketingLabelVO marketingLabelVO = new MarketingLabelVO();
                    marketingLabelVO.setMarketingType(5);
                    marketingLabelVO.setMarketingDesc("秒杀");
                    goodsInfoVO.getMarketingLabels().add(marketingLabelVO);
                }
            }
        });
    }
}