package com.wanmi.sbc.returnorder.provider.impl.returnorder;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.returnorder.api.provider.returnorder.ReturnOrderLogisticsProvider;
import com.wanmi.sbc.returnorder.api.request.returnorder.ReturnLogisticsRequest;
import com.wanmi.sbc.returnorder.api.response.returnorder.ReturnLogisticsResponse;
import com.wanmi.sbc.returnorder.returnorder.model.root.ReturnOrderLogistics;
import com.wanmi.sbc.returnorder.returnorder.service.ReturnOrderLogisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * <p>退单服务操作接口</p>
 *
 * @Author: daiyitian
 * @Description: 退单服务操作接口
 * @Date: 2018-12-03 15:40
 */
@Validated
@RestController
@Slf4j
public class ReturnOrderLogisticsController implements ReturnOrderLogisticsProvider {

    @Autowired
    private ReturnOrderLogisticsService returnOrderLogisticsService;

    @Override
    public BaseResponse<ReturnLogisticsResponse> findReturnLogisticsByHistory(ReturnLogisticsRequest request) {
        ReturnOrderLogistics response = returnOrderLogisticsService.findReturnLogisticsByHistory(request);
        if (Objects.isNull(response)) {
            return BaseResponse.success(null);
        }
        return BaseResponse.success(KsBeanUtil.convert(response, ReturnLogisticsResponse.class));
    }

}
