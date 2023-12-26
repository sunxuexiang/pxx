package com.wanmi.sbc.imMessage;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.provider.onlineservice.CustomerServiceLimitWordProvider;
import com.wanmi.sbc.setting.api.request.imonlineservice.CustomerServiceLimitWordRequest;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "CustomerServiceLimitWordController", description = "客服聊天限制词库功能")
@RestController
@RequestMapping("/customerWord")
@Slf4j
@Validated
public class CustomerServiceLimitWordController {

    @Autowired
    private CustomerServiceLimitWordProvider customerServiceLimitWordProvider;

    @PostMapping("/add")
    public BaseResponse add (@RequestBody CustomerServiceLimitWordRequest request) {
        BaseResponse checkResponse = checkParam(request);
        if (checkResponse != null) return checkResponse;
        return customerServiceLimitWordProvider.add(request);
    }

    @PostMapping(value = "/update")
    public BaseResponse update (@RequestBody CustomerServiceLimitWordRequest request) {
        if (ObjectUtils.isEmpty(request.getWordId())) {
            return BaseResponse.error("ID不能为空");
        }
        BaseResponse checkResponse = checkParam(request);
        if (checkResponse != null) return checkResponse;
        return customerServiceLimitWordProvider.update(request);
    }

    @PostMapping(value = "/delete")
    public BaseResponse delete (@RequestBody CustomerServiceLimitWordRequest request) {
        if (ObjectUtils.isEmpty(request.getWordId())) {
            return BaseResponse.error("ID不能为空");
        }
        return customerServiceLimitWordProvider.delete(request);
    }

    @PostMapping(value = "/getAll")
    public BaseResponse getAll () {
        return customerServiceLimitWordProvider.getAll();
    }

    @PostMapping(value = "getByType")
    public BaseResponse getByType(@RequestBody CustomerServiceLimitWordRequest request) {
        if (ObjectUtils.isEmpty(request.getWordType())) {
            return BaseResponse.error("词组类型不能为空");
        }
        return customerServiceLimitWordProvider.getByType(request);
    }

    private static BaseResponse checkParam(CustomerServiceLimitWordRequest request) {
        if (ObjectUtils.isEmpty(request.getWordType())) {
            return BaseResponse.error("词组类型不能为空");
        }
        if (StringUtils.isEmpty(request.getWordContent())) {
            return BaseResponse.error("词组内容不能为空");
        }
//        if (request.getWordType().equals(1)) {
//            if (ObjectUtils.isEmpty(request.getDigitLength())) {
//                return BaseResponse.error("数字长度不能为空");
//            }
//        }
//        else if (request.getWordType().equals(2) || request.getWordType().equals(3)) {
//            if (StringUtils.isEmpty(request.getWordContent())) {
//                return BaseResponse.error("词组内容不能为空");
//            }
//        }
        return null;
    }

    @PostMapping("/getRegex")
    public BaseResponse getRegex () {
        return customerServiceLimitWordProvider.getRegex();
    }
}
