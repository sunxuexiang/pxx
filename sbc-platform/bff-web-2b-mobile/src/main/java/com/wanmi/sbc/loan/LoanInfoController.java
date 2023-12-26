package com.wanmi.sbc.loan;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.AppPayType;
import com.wanmi.sbc.loan.api.provider.LoanInfoProvider;
import com.wanmi.sbc.loan.api.request.LoanInfoApplyRequest;
import com.wanmi.sbc.loan.api.request.LoanInfoApplySmsSendRequest;
import com.wanmi.sbc.loan.api.response.LoanInfoResponse;
import com.wanmi.sbc.loan.bean.enums.LoanApplyBusinessType;
import com.wanmi.sbc.setting.api.provider.payswitch.AppPaySwitchProvider;
import com.wanmi.sbc.setting.api.request.payswitch.AppPaySwitchParam;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/13 11:12
 */
@Api(tags = "白鲸借款API")
@RestController
@RequestMapping("/loan")
public class LoanInfoController {

    @Autowired
    private LoanInfoProvider loanInfoProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private AppPaySwitchProvider appPaySwitchProvider;

    @ApiOperation(value = "查询当前用户白鲸借款信息")
    @GetMapping("/get")
    public BaseResponse<LoanInfoResponse> get() {
        String operatorId = commonUtil.getOperatorId();
        return loanInfoProvider.queryLoanInfoByCustomerId(operatorId);
    }

    @ApiOperation(value = "白鲸借款申请")
    @PostMapping("/apply")
    public BaseResponse applyLoan(@RequestBody @Valid LoanInfoApplyRequest request) {
        // 检验手机验证码
        String applyPhone = request.getApplyPhone();
        String smsCode = request.getSmsCode();
        Boolean verify = loanInfoProvider.verifySmsCode(applyPhone, smsCode).getContext();
        if (!verify) {
            return BaseResponse.error("验证码错误");
        }
        String operatorId = commonUtil.getOperatorId();
        request.setCustomerId(operatorId);
        return loanInfoProvider.loanApply(request);
    }

    @ApiOperation(value = "白鲸借款申请发送短信验证码")
    @PostMapping("/apply/sms")
    public BaseResponse applyLoanSms(@RequestBody @Valid LoanInfoApplySmsSendRequest request) {
        String phone = request.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            if (!Pattern.matches(commonUtil.REGEX_MOBILE, phone)) {
                return BaseResponse.error("请输入正确的手机号码");
            }
        }else{
            return BaseResponse.error("手机号码不能为空");
        }
        return loanInfoProvider.sendApplyLoanSms(request);
    }

    @ApiOperation(value = "查询申请类型列表")
    @GetMapping("/apply/types")
    public BaseResponse<List<Map<String, Object>>> applyTypes() {
        List<Map<String, Object>> mapBusinessTypes = LoanApplyBusinessType.getMapBusinessTypes();
        return BaseResponse.success(mapBusinessTypes);
    }

    @ApiOperation(value = "获取开关状态")
    @GetMapping("/switch/state")
    public BaseResponse<Integer> switchState() {
        List<AppPaySwitchParam> switchList = appPaySwitchProvider.getAppPaySwitch().getContext();
        AppPaySwitchParam loanSwitch = switchList.stream().filter(o -> Objects.equals(AppPayType.bjLoan, o.getPayType())).findFirst().orElse(null);
        int state = 0;
        if (Objects.nonNull(loanSwitch)) {
            Integer androidStatus = loanSwitch.getAndroidStatus();
            Integer iosStatus = loanSwitch.getIosStatus();
            if (Objects.equals(1, androidStatus) || Objects.equals(1, iosStatus)) {
                state = 1;
            }
        }
        return BaseResponse.success(state);
    }
}
