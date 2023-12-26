package com.wanmi.sbc.customer;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.CustomerLevelErrorCode;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelProvider;
import com.wanmi.sbc.customer.api.provider.level.CustomerLevelQueryProvider;
import com.wanmi.sbc.customer.api.provider.log.CustomerLogProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerListByConditionRequest;
import com.wanmi.sbc.customer.api.request.level.*;
import com.wanmi.sbc.customer.api.request.log.CustomerLogPageRequest;
import com.wanmi.sbc.customer.api.response.log.CustomerLogPageResponse;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelVO;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.customer.validator.CustomerLevelValidator;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;


/**
 * @description  用户操作日志
 * @author  shiy
 * @date    2023/4/12 16:13
 * @params
 * @return
*/
@Api(description = "用户操作日志", tags = "CustomerLogController")
@RestController
@RequestMapping("/customer/log")
public class CustomerLogController {

    @Autowired
    private CustomerLogProvider customerLogProvider;

    /**
     * 分页查询
     * @return
     */
    @ApiOperation(value = "分页查询")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> list(@RequestBody CustomerLogPageRequest customerLogPageRequest) {
        return ResponseEntity.ok(customerLogProvider.page(customerLogPageRequest));
    }

    /**
     * 根据用户查版本更新记录
     * @return
     */
    @ApiOperation(value = "根据用户查版本更新记录")
    @RequestMapping(value = "/findUpdateRecordByUserNo", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> findUpdateRecordByUserNo(@RequestBody CustomerLogPageRequest customerLogPageRequest) {
        return ResponseEntity.ok(customerLogProvider.findUpdateRecordByUserNo(customerLogPageRequest));
    }
}
