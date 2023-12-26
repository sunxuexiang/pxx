package com.wanmi.sbc.customer;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.workorder.WorkOrderProvider;
import com.wanmi.sbc.customer.api.provider.workorder.WorkOrderQueryProvider;
import com.wanmi.sbc.customer.api.provider.workorderdetail.WorkOrderDetailProvider;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaAddRequest;
import com.wanmi.sbc.customer.api.request.workorder.*;
import com.wanmi.sbc.customer.api.response.parentcustomerrela.ParentCustomerRealStatusResponse;
import com.wanmi.sbc.customer.api.response.workorder.*;
import com.wanmi.sbc.customer.bean.vo.WorkOrderVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;


@Api(description = "工单管理API", tags = "WorkOrderController")
@RestController
@RequestMapping(value = "/workorder")
public class WorkOrderController {

    @Autowired
    private WorkOrderQueryProvider workOrderQueryProvider;

    @Autowired
    private WorkOrderProvider workOrderProvider;

    @Autowired
    private WorkOrderDetailProvider workOrderDetailProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询工单")
    @PostMapping("/page")
    public BaseResponse<WorkOrderPageResponse> getPage(@RequestBody @Valid WorkOrderPageRequest pageReq) {
        pageReq.setDelFlag(DeleteFlag.NO);
        pageReq.putSort("workOrderId", "desc");
       // BaseResponse<WorkOrderPageResponse> page =
       /* List<String> collect = page.getContext().getWorkOrderVOPage()
                .getContent().stream().map(WorkOrderVO::getWorkOrderId).distinct().collect(Collectors.toList());*/
   /*     if(CollectionUtils.isNotEmpty(collect)){
            Map<String, Long> workDetalCount = workOrderDetailProvider
                    .countGroupByWorOrderId(WorkOrderDetailCountByIdsRequest.builder().worOrderIds(collect).build()).getContext().getWorkDetalCount();
            for (WorkOrderVO inner:page.getContext().getWorkOrderVOPage()){
                Long aLong = workDetalCount.get(inner.getWorkOrderId());
                if (Objects.nonNull(aLong)){
                    inner.setDetailCount(aLong);
                }
            }
        }*/
        return  workOrderQueryProvider.page(pageReq);
    }


    /**
     * 校验是否存在未处理完的工单
     * @param request
     * @return
     */
    @ApiOperation(value = "校验是否存在未处理完的工单")
    @PostMapping("/validate/exist")
    public BaseResponse<WorkOrderExistByRegisterCustomerIdResponse> validateCustomerWorkOrder(@RequestBody @Valid
                                                            WorkOrderExistByRegisterCustomerIdRequest request) {
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单管理", "校验是否存在未处理完的工单","校验是否存在未处理完的工单");
        return workOrderQueryProvider.validateCustomerWorkOrder(request);
    }


    @ApiOperation(value = "列表查询工单")
    @PostMapping("/list")
    public BaseResponse<WorkOrderListResponse> getList(@RequestBody @Valid WorkOrderListRequest listReq) {
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.putSort("workOrderId", "desc");
        return workOrderQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询工单")
    @GetMapping("/{workOrderId}")
    public BaseResponse<WorkOrderByIdResponse> getById(@PathVariable String workOrderId) {
        if (workOrderId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        WorkOrderByIdRequest idReq = new WorkOrderByIdRequest();
        idReq.setWorkOrderId(workOrderId);
        return workOrderQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "根据id查询账号状态")
    @GetMapping("/getstatus/{customerId}")
    public BaseResponse<ParentCustomerRealStatusResponse> getStatus(@PathVariable String customerId) {
        if (customerId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        ParentCustomerRelaAddRequest idReq = new ParentCustomerRelaAddRequest();
        idReq.setCustomerId(customerId);
        return workOrderQueryProvider.getStatus(idReq);
    }



    @ApiOperation(value = "新增工单")
    @PostMapping("/add")
    public BaseResponse<WorkOrderAddResponse> add(@RequestBody @Valid WorkOrderAddRequest addReq) {
        addReq.setDelFlag(DeleteFlag.NO);
        addReq.setCreateTime(LocalDateTime.now());
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单管理", "新增工单","新增工单");
        return workOrderProvider.add(addReq);
    }

    @ApiOperation(value = "修改工单")
    @PutMapping("/modify")
    public BaseResponse<WorkOrderModifyResponse> modify(@RequestBody @Valid WorkOrderModifyRequest modifyReq) {
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单管理", "修改工单","修改工单");
        return workOrderProvider.modify(modifyReq);
    }

    @ApiOperation(value = "根据id删除工单")
    @DeleteMapping("/{workOrderId}")
    public BaseResponse deleteById(@PathVariable String workOrderId) {
        if (workOrderId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        WorkOrderDelByIdRequest delByIdReq = new WorkOrderDelByIdRequest();
        delByIdReq.setWorkOrderId(workOrderId);
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单管理", "根据id删除工单","根据id删除工单");
        return workOrderProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除工单")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid WorkOrderDelByIdListRequest delByIdListReq) {
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单管理", "根据idList批量删除工单","根据idList批量删除工单");
        return workOrderProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "导出工单列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        WorkOrderListRequest listReq = JSON.parseObject(decrypted, WorkOrderListRequest.class);
        listReq.setDelFlag(DeleteFlag.NO);
        listReq.putSort("workOrderId", "desc");
        List<WorkOrderVO> dataRecords = workOrderQueryProvider.list(listReq).getContext().getWorkOrderVOList();

        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("工单列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单管理", "导出工单列表","操作成功");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<WorkOrderVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
            new Column("工单号", new SpelColumnRender<WorkOrderVO>("workOrderNo")),
            new Column("社会信用代码", new SpelColumnRender<WorkOrderVO>("socialCreditCode")),
            new Column("注册人Id", new SpelColumnRender<WorkOrderVO>("approvalCustomerId")),
            new Column("已注册会员的Id", new SpelColumnRender<WorkOrderVO>("registedCustomerId")),
            new Column("账号合并状态", new SpelColumnRender<WorkOrderVO>("accountMergeStatus")),
            new Column("状态 0:待处理，1：已完成", new SpelColumnRender<WorkOrderVO>("status")),
            new Column("修改时间", new SpelColumnRender<WorkOrderVO>("modifyTime")),
            new Column("删除时间", new SpelColumnRender<WorkOrderVO>("deleteTiime"))
        };
        excelHelper.addSheet("工单列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

}
