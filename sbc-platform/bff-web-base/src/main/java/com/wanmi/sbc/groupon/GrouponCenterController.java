package com.wanmi.sbc.groupon;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.marketing.api.provider.grouponcenter.GrouponCenterQueryProvider;
import com.wanmi.sbc.marketing.api.request.grouponcenter.GrouponCenterListRequest;
import com.wanmi.sbc.marketing.api.response.grouponcenter.GrouponCenterListResponse;
import com.wanmi.sbc.marketing.bean.vo.GrouponCenterVO;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 拼团活动首页Controller
 */
@RestController
@RequestMapping("/groupon/center")
@Api(tags = "GrouponCenterController", description = "S2B web公用-拼团营销")
public class GrouponCenterController {

    @Autowired
    private GrouponCenterQueryProvider grouponCenterQueryProvider;

    @Autowired
    private CommonUtil commonUtil;


    /**
     * 查询拼团活动spu列表信息
     * @return
     */
    @ApiModelProperty(value = "查询拼团活动spu列表信息")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public BaseResponse<GrouponCenterListResponse> list(@RequestBody GrouponCenterListRequest request){
        GrouponCenterListResponse response = grouponCenterQueryProvider.list(request).getContext();
        if(Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getGrouponCenterVOList().getContent())){
            List<GrouponCenterVO> centerVOS = response.getGrouponCenterVOList().getContent();
            //如果是大客户
            if(commonUtil.isVipCustomer()){
                centerVOS.stream().forEach(c->{
                    if(Objects.nonNull(c.getVipPrice()) && c.getVipPrice().compareTo(BigDecimal.ZERO) > 0){
                        c.setMarketPrice(c.getVipPrice());
                    }
                });
            }
        }
        return BaseResponse.success(response);
    }

}
