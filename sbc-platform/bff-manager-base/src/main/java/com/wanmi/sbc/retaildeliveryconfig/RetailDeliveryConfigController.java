package com.wanmi.sbc.retaildeliveryconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;
import com.wanmi.sbc.setting.api.provider.packingconfig.PackingConfigProvider;
import com.wanmi.sbc.setting.api.provider.packingconfig.PackingConfigQueryProvider;
import com.wanmi.sbc.setting.api.provider.retaildeliveryconfig.RetailDeveryConfigProvider;
import com.wanmi.sbc.setting.api.provider.retaildeliveryconfig.RetailDeveryConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.packingconfig.PackingConfigRequest;
import com.wanmi.sbc.setting.api.request.retaildeliveryconfig.RetailDeliveryConfigRequest;
import com.wanmi.sbc.setting.api.response.packingconfig.PackingConfigResponse;
import com.wanmi.sbc.setting.api.response.retaildeliveryconfig.RetailDeliveryConfigResponse;
import com.wanmi.sbc.setting.bean.vo.PackingConfigVO;
import com.wanmi.sbc.setting.bean.vo.RetailDeliverConfigVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@Api(tags = "RetailDeliveryConfigController", description = "打包服务 Api")
@RestController
@RequestMapping("/retailDeliveryConfig")
@Slf4j
public class RetailDeliveryConfigController {

    @Autowired
    private RetailDeveryConfigProvider retailDeveryConfigProvider;
    @Autowired
    private RetailDeveryConfigQueryProvider retailDeveryConfigQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 新增打包配置
     */
    @ApiOperation(value = "新增或修改零售配送")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public BaseResponse<String> modify(@RequestBody RetailDeliverConfigVO retailDeliverConfigVO) {
        if (Objects.isNull(retailDeliverConfigVO)){
            throw new SbcRuntimeException(SettingErrorCode.ILLEGAL_REQUEST_ERROR);
        }
        log.info(retailDeliverConfigVO.toString());
        retailDeveryConfigProvider.modify(RetailDeliveryConfigRequest.builder().retailDeliverConfigVO(retailDeliverConfigVO).build());
        operateLogMQUtil.convertAndSend("打包服务", "新增或修改零售配送", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 新增打包配置
     */
    @ApiOperation(value = "获取零售配置")
    @RequestMapping(value = "/getdata", method = RequestMethod.POST)
    public BaseResponse<RetailDeliveryConfigResponse> getdata(@RequestBody RetailDeliveryConfigRequest request) {
        RetailDeliveryConfigResponse context = retailDeveryConfigQueryProvider.list().getContext();
        return BaseResponse.success(context);
    }



}