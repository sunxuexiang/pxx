package com.wanmi.sbc.marketing.plugin.impl;

import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.EnterpriseAuditState;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.plugin.IGoodsListPlugin;
import com.wanmi.sbc.marketing.request.MarketingPluginRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * 企业购插件
 * Created by dyt on 2016/12/8.
 */
@Repository("enterPrisePlugin")
public class EnterprisePlugin implements IGoodsListPlugin {


    /**
     * 商品列表处理
     * @param goodsInfos 商品数据
     * @param request    参数
     */
    @Override
    public void goodsListFilter(List<GoodsInfoVO> goodsInfos, MarketingPluginRequest request) {
        // 企业购商品状态确认 社交分销优先级>企业购
        goodsInfos.forEach(goodsInfoVO -> {
                    if (Objects.equals(goodsInfoVO.getDistributionGoodsAudit(), DistributionGoodsAudit.CHECKED)) {
                        // 分销商品去除企业购标记
                        goodsInfoVO.setEnterPriseAuditState(EnterpriseAuditState.INIT);
                        goodsInfoVO.setEnterPriseGoodsAuditReason(null);
                        goodsInfoVO.setEnterPrisePrice(null);
                    } else {
                        // 企业购默认零售模式\不显示划线价（前端加强，管理端加数据时已经做了限制）
                        if (Objects.equals(goodsInfoVO.getEnterPriseAuditState(), EnterpriseAuditState.CHECKED)) {
                            goodsInfoVO.setSaleType(1);
                        }
                    }
                }
        );
    }

}