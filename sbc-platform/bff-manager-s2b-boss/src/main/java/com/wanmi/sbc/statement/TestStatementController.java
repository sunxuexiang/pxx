package com.wanmi.sbc.statement;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.job.CcbStatementHandler;
import com.wanmi.sbc.job.CcbStatementSummaryHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 对账单测试类
 * @author hudong
 * 2023-09-14
 */
@Api(tags="TestStatementController", description = "对账单测试控制类")
@RestController
@RequestMapping("/testStatement")
public class TestStatementController {

    @Autowired
    private CcbStatementHandler ccbStatementHandler;

    @Autowired
    private CcbStatementSummaryHandler ccbStatementSummaryHandler;


    /**
     * 对账单测试控制类
     *
     * @return
     */
    @ApiOperation(value = "对账单测试控制类")
    @RequestMapping(value = "/ccbStatement/{param}", method = RequestMethod.GET)
    public BaseResponse<String> ccbStatement(@PathVariable String param) {
        try {
            ccbStatementHandler.execute(param);
        } catch (Exception e) {
            return BaseResponse.success("定时任务执行失败");
        }
        return BaseResponse.success("定时任务执行成功");
    }


    /**
     * 对账单分账汇总测试控制类
     *
     * @return
     */
    @ApiOperation(value = "对账单分账汇总测试控制类")
    @RequestMapping(value = "/ccbClrg/{param}", method = RequestMethod.GET)
    public BaseResponse<String> ccbClrg(@PathVariable String param) {
        try {
            ccbStatementSummaryHandler.execute(param);
        } catch (Exception e) {
            return BaseResponse.success("定时任务执行失败");
        }
        return BaseResponse.success("定时任务执行成功");
    }

}
