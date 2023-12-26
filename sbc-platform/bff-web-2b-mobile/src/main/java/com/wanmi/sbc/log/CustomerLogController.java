package com.wanmi.sbc.log;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.customer.api.provider.log.CustomerLogProvider;
import com.wanmi.sbc.customer.api.request.log.CustomerLogAddRequest;
import com.wanmi.sbc.customer.api.request.log.CustomerLogPageRequest;
import com.wanmi.sbc.customer.api.response.log.CustomerLogPageResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;


@Api(description = "用户日志", tags = "CustomerLogController")
@RestController
@RequestMapping(value = "/customerLog")
public class CustomerLogController {
    @Autowired
    private CustomerLogProvider customerLogProvider;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "app用户日志记录")
    @PostMapping("/add")
    public BaseResponse add(@RequestBody @Valid CustomerLogAddRequest addReq) {
        if(addReq!=null) {
            Operator operator = commonUtil.getOperator();
            if (StringUtils.isBlank(addReq.getUserIp())) {
                addReq.setUserIp(operator.getIp());
            }
            if(StringUtils.isBlank(addReq.getCustomerId())){
                addReq.setUserIp(HttpUtil.getIpAddr());
            }
            if(StringUtils.isBlank(addReq.getUserNo())){
                addReq.setUserNo(operator.getAccount());
            }
            customerLogProvider.add(addReq);
            return BaseResponse.SUCCESSFUL();
        }
        return BaseResponse.error("参数错误");
    }

    @ApiOperation(value = "app用户日志记录")
    @PostMapping("/list")
    public BaseResponse<CustomerLogPageResponse> list(@RequestBody @Valid CustomerLogPageRequest pageRequest) {
        if(pageRequest!=null) {
           return customerLogProvider.page(pageRequest);
        }
        return BaseResponse.error("参数错误");
    }
}
