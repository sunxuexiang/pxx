package com.wanmi.sbc.marketing;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.marketing.api.provider.suittobuy.SuitToBuyProvider;
import com.wanmi.sbc.marketing.api.request.suittobuy.SuitToBuyAddRequest;
import com.wanmi.sbc.marketing.api.request.suittobuy.SuitToBuyModifyRequest;
import com.wanmi.sbc.marketing.bean.constant.MarketingErrorCode;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @description: 套装购买
 * @author: XinJiang
 * @time: 2022/2/4 15:30
 */
@Api(tags = "MarketingSuitToBuyController", description = "套装服务API")
@RestController
@RequestMapping("/marketing/suit-to-buy")
@Validated
@Slf4j
public class MarketingSuitToBuyController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private SuitToBuyProvider suitToBuyProvider;

    /**
     * 新增套装购买营销信息
     * @param request
     * @return
     */
    @ApiModelProperty(value = "新增套装购买营销信息")
    @RequestMapping(method = RequestMethod.POST)
    public BaseResponse add(@Valid @RequestBody SuitToBuyAddRequest request) {
        request.setIsBoss(BoolFlag.NO);
        request.setStoreId(commonUtil.getStoreId());
        request.setCreatePerson(commonUtil.getOperatorId());
        BaseResponse<List<String>> add = suitToBuyProvider.add(request);
        if(CollectionUtils.isNotEmpty(add.getContext())){
            throw new SbcRuntimeException(MarketingErrorCode.MARKETING_GOODS_TIME_CONFLICT, new Object[]{add.getContext().size(),add.getContext()});
        }
        operateLogMQUtil.convertAndSend("营销","创建套装购买活动","创建套装购买活动："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改套装购买营销信息
     * @param request
     * @return
     */
    @ApiModelProperty(value = "修改套装购买营销信息")
    @RequestMapping(method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody SuitToBuyModifyRequest request) {
        request.setUpdatePerson(commonUtil.getOperatorId());
        suitToBuyProvider.modify(request);
        operateLogMQUtil.convertAndSend("营销","编辑套装购买活动","编辑套装购买活动："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }
}
