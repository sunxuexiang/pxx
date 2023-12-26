package com.wanmi.sbc.customer;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.customer.api.provider.growthvalue.CustomerGrowthValueProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailQueryProvider;
import com.wanmi.sbc.customer.api.provider.points.CustomerPointsDetailSaveProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.growthvalue.CustomerGrowthValueAddRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailAddRequest;
import com.wanmi.sbc.customer.api.request.points.CustomerPointsDetailQueryRequest;
import com.wanmi.sbc.customer.api.response.points.CustomerPointsDetailPageResponse;
import com.wanmi.sbc.customer.api.response.points.CustomerPointsExpireResponse;
import com.wanmi.sbc.customer.bean.enums.GrowthValueServiceType;
import com.wanmi.sbc.customer.bean.enums.OperateType;
import com.wanmi.sbc.customer.bean.enums.PointsServiceType;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;

/**
 * 会员积分
 */
@Api(description = "PC、H5会员积分API", tags = "CustomerPointsController")
@RestController
@RequestMapping(value = "/customer/points")
public class CustomerPointsController {

    @Autowired
    private CustomerPointsDetailQueryProvider customerPointsDetailQueryProvider;

    @Autowired
    private CustomerGrowthValueProvider customerGrowthValueProvider;

    @Autowired
    private CustomerPointsDetailSaveProvider customerPointsDetailSaveProvider;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "分页查询会员积分明细")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<CustomerPointsDetailPageResponse> page(@RequestBody @Valid CustomerPointsDetailQueryRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        return customerPointsDetailQueryProvider.page(request);
    }

    @ApiOperation(value = "根据会员Id查询会员快过期积分")
    @RequestMapping(value = "/expire", method = RequestMethod.GET)
    public BaseResponse<CustomerPointsExpireResponse> queryWillExpirePoints() {
        return customerPointsDetailQueryProvider.queryWillExpirePoints(new CustomerGetByIdRequest(commonUtil.getOperatorId()));
    }

    @ApiOperation(value = "会员分享获得积分")
    @RequestMapping(value = "/share", method = RequestMethod.POST)
    public BaseResponse share() {
        if (Objects.isNull(commonUtil.getOperatorId())) return BaseResponse.SUCCESSFUL();

        // 增加成长值
        customerGrowthValueProvider.increaseGrowthValue(CustomerGrowthValueAddRequest.builder()
                .customerId(commonUtil.getOperatorId())
                .type(OperateType.GROWTH)
                .serviceType(GrowthValueServiceType.SHARE)
                .build());
        // 增加积分
        customerPointsDetailSaveProvider.add(CustomerPointsDetailAddRequest.builder()
                .customerId(commonUtil.getOperatorId())
                .type(OperateType.GROWTH)
                .serviceType(PointsServiceType.SHARE)
                .build());

        return BaseResponse.SUCCESSFUL();
    }

}
