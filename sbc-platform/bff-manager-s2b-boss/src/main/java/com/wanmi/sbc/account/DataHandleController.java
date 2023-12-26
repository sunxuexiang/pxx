package com.wanmi.sbc.account;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 数据处理
 * Created by chenchang on 2023-5-26.
 */
@RestController("DataHandleController")
@RequestMapping("/data-handle")
@Api(tags = "DataHandleController", description = "数据处理")
public class DataHandleController {

    @Autowired
    NewPileTradeProvider newPileTradeProvider;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;

    /**
     * 处理南昌仓囤货数据
     *
     * @return
     */
    @ApiOperation(value = "处理南昌仓囤货数据")
    @PostMapping("/handleNanChangPileData")
    public BaseResponse<String> handlePileData(@RequestBody @Valid Map<String, String> map) {
        operateLogMQUtil.convertAndSend("运营端系统", "数据处理", "处理南昌仓囤货数据");
        return newPileTradeProvider.handleNanChangPileData(map);
    }

    @ApiOperation(value = "处理南昌仓提货数据")
    @PostMapping("/handleNanChangTakeData")
    public BaseResponse<String> handleNanChangTakeData(@RequestBody @Valid Map<String, List<String>> map) {
        operateLogMQUtil.convertAndSend("运营端系统", "数据处理", "处理南昌仓提货数据");
        return newPileTradeProvider.handleNanChangTakeData(map);
    }

}