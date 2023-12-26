package com.wanmi.sbc.historylogisticscompany;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.order.api.provider.historylogisticscompany.HistoryLogisticsCompanyProvider;
import com.wanmi.sbc.order.api.provider.historylogisticscompany.HistoryLogisticsCompanyQueryProvider;
import com.wanmi.sbc.order.api.request.historylogisticscompany.*;
import com.wanmi.sbc.order.api.response.historylogisticscompany.*;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;


@Api(description = "物流公司历史记录管理API", tags = "HistoryLogisticsCompanyController")
@RestController
@RequestMapping(value = "/historylogisticscompany")
public class HistoryLogisticsCompanyController {

    @Autowired
    private HistoryLogisticsCompanyQueryProvider historyLogisticsCompanyQueryProvider;

    @Autowired
    private HistoryLogisticsCompanyProvider historyLogisticsCompanyProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询物流公司历史记录")
    @PostMapping("/page")
    public BaseResponse<HistoryLogisticsCompanyPageResponse> getPage(@RequestBody @Valid HistoryLogisticsCompanyPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("id", "desc");
        pageReq.setSelfFlag(DefaultFlag.YES);
        return historyLogisticsCompanyQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询物流公司历史记录")
    @PostMapping("/list")
    public BaseResponse<HistoryLogisticsCompanyListResponse> getList(@RequestBody @Valid HistoryLogisticsCompanyListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        return historyLogisticsCompanyQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询物流公司历史记录")
    @GetMapping("/{id}")
    public BaseResponse<HistoryLogisticsCompanyByIdResponse> getById(@PathVariable String id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        HistoryLogisticsCompanyByIdRequest idReq = new HistoryLogisticsCompanyByIdRequest();
        idReq.setId(id);
        return historyLogisticsCompanyQueryProvider.getById(idReq);
    }



    @ApiOperation(value = "新增物流公司历史记录")
    @PostMapping("/add")
    public BaseResponse<HistoryLogisticsCompanyAddResponse> add(@RequestBody @Valid HistoryLogisticsCompanyAddRequest addReq) {
        addReq.setDelFlag(DeleteFlag.NO);
        addReq.setCreateTime(LocalDateTime.now());
        BaseResponse<HistoryLogisticsCompanyAddResponse> add = historyLogisticsCompanyProvider.add(addReq);
        //操作日志记录
        operateLogMQUtil.convertAndSend("物流公司历史记录管理", "新增物流公司历史记录", "操作成功");
        return add;
    }

    @ApiOperation(value = "修改物流公司历史记录")
    @PutMapping("/modify")
    public BaseResponse<HistoryLogisticsCompanyModifyResponse> modify(@RequestBody @Valid HistoryLogisticsCompanyModifyRequest modifyReq) {
        BaseResponse<HistoryLogisticsCompanyModifyResponse> modify = historyLogisticsCompanyProvider.modify(modifyReq);
        //操作日志记录
        operateLogMQUtil.convertAndSend("物流公司历史记录管理", "修改物流公司历史记录", "操作成功");
        return modify;
    }

    @ApiOperation(value = "根据id删除物流公司历史记录")
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable String id) {
        if (id == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        HistoryLogisticsCompanyDelByIdRequest delByIdReq = new HistoryLogisticsCompanyDelByIdRequest();
        delByIdReq.setId(id);
        BaseResponse baseResponse = historyLogisticsCompanyProvider.deleteById(delByIdReq);
        //操作日志记录
        operateLogMQUtil.convertAndSend("物流公司历史记录管理", "根据id删除物流公司历史记录", "操作成功");
        return baseResponse;
    }

    @ApiOperation(value = "根据idList批量删除物流公司历史记录")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid HistoryLogisticsCompanyDelByIdListRequest delByIdListReq) {
        BaseResponse baseResponse = historyLogisticsCompanyProvider.deleteByIdList(delByIdListReq);
        //操作日志记录
        operateLogMQUtil.convertAndSend("物流公司历史记录管理", "根据idList批量删除物流公司历史记录", "操作成功");
        return baseResponse;
    }


}
