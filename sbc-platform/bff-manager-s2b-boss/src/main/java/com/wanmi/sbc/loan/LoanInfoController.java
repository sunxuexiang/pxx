package com.wanmi.sbc.loan;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.AppPayType;
import com.wanmi.sbc.loan.api.provider.LoanInfoProvider;
import com.wanmi.sbc.loan.api.request.LoanInfoAuditRequest;
import com.wanmi.sbc.loan.api.request.LoanInfoPageQueryRequest;
import com.wanmi.sbc.loan.api.response.LoanInfoResponse;
import com.wanmi.sbc.loan.bean.enums.LoanApplyBusinessType;
import com.wanmi.sbc.setting.api.provider.payswitch.AppPaySwitchProvider;
import com.wanmi.sbc.setting.api.request.payswitch.AppPaySwitchParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/14 9:38
 */
@Api(tags = "白鲸借款API")
@RestController
@RequestMapping("/loan")
public class LoanInfoController {

    @Autowired
    private LoanInfoProvider loanInfoProvider;

    @Autowired
    private AppPaySwitchProvider appPaySwitchProvider;


    @PostMapping("/page")
    @ApiOperation("审核列表")
    public BaseResponse<MicroServicePage<LoanInfoResponse>> page(@RequestBody @Valid LoanInfoPageQueryRequest request) {
        return loanInfoProvider.loanInfoPage(request);
    }

    @ApiOperation(value = "查看详情")
    @GetMapping("/get/{id}")
    public BaseResponse<LoanInfoResponse> get(@PathVariable("id") Long id) {
        return loanInfoProvider.queryLoanInfoById(id);
    }

    @PostMapping("/audit")
    @ApiOperation("申请审核")
    public BaseResponse loanAudit(@RequestBody @Valid LoanInfoAuditRequest request) {
        return loanInfoProvider.loanInfoAudit(request);
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


    @ApiOperation(value = "更改开关状态")
    @PostMapping("/switch/{state}")
    public BaseResponse updateSwitch(@PathVariable("state") Integer state) {
        if (!Objects.equals(0, state) && !Objects.equals(1, state)) {
            return BaseResponse.error("请输入正确的状态");
        }
        List<AppPaySwitchParam> switchList = appPaySwitchProvider.getAppPaySwitch().getContext();
        for (AppPaySwitchParam switchParam : switchList) {
            if (Objects.equals(AppPayType.bjLoan, switchParam.getPayType())) {
                switchParam.setAndroidStatus(state);
                switchParam.setIosStatus(state);
            }
        }
        appPaySwitchProvider.updateAppPaySwitch(switchList);
        return BaseResponse.success(state);
    }
}
