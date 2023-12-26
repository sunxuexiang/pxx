package com.wanmi.sbc.marketing.provider.impl.plugin;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.MarketingPluginService;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingLevelPluginProvider;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingLevelGoodsListFilterRequest;
import com.wanmi.sbc.marketing.api.response.plugin.MarketingLevelGoodsListFilterResponse;
import com.wanmi.sbc.marketing.plugin.impl.CustomerLevelPlugin;
import com.wanmi.sbc.marketing.request.MarketingPluginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>主插件服务操作接口</p>
 * author: daiyitian
 * Date: 2018-11-28
 */
@Validated
@RestController
public class MarketingLevelPluginController implements MarketingLevelPluginProvider {

    @Autowired
    private CustomerLevelPlugin customerLevelPlugin;

    @Autowired
    private MarketingPluginService marketingPluginService;

    /**
     * 商品列表处理
     * @param request 商品列表处理结构 {@link MarketingLevelGoodsListFilterRequest}
     * @return 处理后的商品列表 {@link MarketingLevelGoodsListFilterResponse}
     */
    @Override
    public BaseResponse<MarketingLevelGoodsListFilterResponse> goodsListFilter(@RequestBody @Valid
                                                                                      MarketingLevelGoodsListFilterRequest
                                                                                          request){
        CustomerVO customer = KsBeanUtil.convert(request.getCustomerDTO(), CustomerVO.class);
        List<GoodsInfoVO> voList = KsBeanUtil.convert(request.getGoodsInfos(), GoodsInfoVO.class);
        MarketingPluginRequest pluginRequest = new MarketingPluginRequest();
        //设定等级
        pluginRequest.setLevelMap(marketingPluginService.getCustomerLevels(voList, customer));
        //设定营销
        pluginRequest.setMarketingMap(marketingPluginService.getMarketing(voList, pluginRequest.getLevelMap()));
        pluginRequest.setCustomer(customer);
        customerLevelPlugin.goodsListFilter(voList, pluginRequest);
        return BaseResponse.success(new MarketingLevelGoodsListFilterResponse(voList));
    }

}
