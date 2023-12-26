package com.wanmi.sbc.customer;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.excel.Column;
import com.wanmi.sbc.common.util.excel.ExcelHelper;
import com.wanmi.sbc.common.util.excel.impl.SpelColumnRender;
import com.wanmi.sbc.customer.api.provider.workorderdetail.WorkOrderDetailProvider;
import com.wanmi.sbc.customer.api.provider.workorderdetail.WorkOrderDetailQueryProvider;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.BindAccountRequest;
import com.wanmi.sbc.customer.api.request.parentcustomerrela.ParentCustomerRelaModifyRequest;
import com.wanmi.sbc.customer.api.request.workorderdetail.*;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailAddResponse;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailListResponse;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailModifyResponse;
import com.wanmi.sbc.customer.api.response.workorderdetail.WorkOrderDetailPageResponse;
import com.wanmi.sbc.customer.bean.vo.WorkOrderDetailVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponCodeQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeQueryBindAccountRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponCodeQueryRequest;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
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


@Api(description = "工单明细管理API", tags = "WorkOrderDetailController")
@RestController
@RequestMapping(value = "/workorderdetail")
public class WorkOrderDetailController {

    @Autowired
    private WorkOrderDetailQueryProvider workOrderDetailQueryProvider;

    @Autowired
    private WorkOrderDetailProvider workOrderDetailProvider;

    @Autowired
    private CouponCodeQueryProvider couponCodeQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询工单明细")
    @PostMapping("/page")
    public BaseResponse<WorkOrderDetailPageResponse> getPage(@RequestBody @Valid WorkOrderDetailPageRequest pageReq) {
        pageReq.putSort("workOrderDelId", "desc");
        return workOrderDetailQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询工单明细")
    @PostMapping("/list")
    public BaseResponse<WorkOrderDetailListResponse> getList(@RequestBody @Valid WorkOrderDetailListRequest listReq) {
        listReq.putSort("workOrderDelId", "desc");
        return workOrderDetailQueryProvider.list(listReq);
    }

    @ApiOperation(value = "根据id查询工单明细")
    @GetMapping("/{workOrderDelId}")
    public BaseResponse<WorkOrderDetailListResponse> getById(@PathVariable String workOrderDelId) {
        if (workOrderDelId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        WorkOrderDetailByIdRequest idReq = new WorkOrderDetailByIdRequest();
        idReq.setWorkOrderDelId(workOrderDelId);
        return workOrderDetailQueryProvider.getById(idReq);
    }

    @ApiOperation(value = "新增工单明细")
    @PostMapping("/add")
    public BaseResponse<WorkOrderDetailAddResponse> add(@RequestBody @Valid WorkOrderDetailAddRequest addReq) {
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单明细管理", "新增工单明细","新增工单明细");
        return workOrderDetailProvider.add(addReq);
    }

    @ApiOperation(value = "修改工单明细")
    @PutMapping("/modify")
    public BaseResponse<WorkOrderDetailModifyResponse> modify(@RequestBody @Valid WorkOrderDetailModifyRequest modifyReq) {
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单明细管理", "修改工单明细","修改工单明细");
        return workOrderDetailProvider.modify(modifyReq);
    }

    @ApiOperation(value = "根据id删除工单明细")
    @DeleteMapping("/{workOrderDelId}")
    public BaseResponse deleteById(@PathVariable String workOrderDelId) {
        if (workOrderDelId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        WorkOrderDetailDelByIdRequest delByIdReq = new WorkOrderDetailDelByIdRequest();
        delByIdReq.setWorkOrderDelId(workOrderDelId);
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单明细管理", "根据id删除工单明细","根据id删除工单明细");
        return workOrderDetailProvider.deleteById(delByIdReq);
    }

    @ApiOperation(value = "根据idList批量删除工单明细")
    @DeleteMapping("/delete-by-id-list")
    public BaseResponse deleteByIdList(@RequestBody @Valid WorkOrderDetailDelByIdListRequest delByIdListReq) {
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单明细管理", "根据idList批量删除工单明细","根据idList批量删除工单明细");
        return workOrderDetailProvider.deleteByIdList(delByIdListReq);
    }

    @ApiOperation(value = "合并账号")
    @PostMapping("/mergeAccount")
    @LcnTransaction
    public BaseResponse mergeAccount(@RequestBody @Valid ParentCustomerRelaModifyRequest parentCustomerRelaModifyRequest){
        parentCustomerRelaModifyRequest.setOperator(commonUtil.getOperator());
        List<String> customerIds = workOrderDetailProvider
                .mergeAccount(parentCustomerRelaModifyRequest).getContext().getCustomerIds();
        CouponCodeQueryRequest queryRequest=new CouponCodeQueryRequest();
        queryRequest.setUseStatus(DefaultFlag.NO);
        queryRequest.setNotExpire(true);
        queryRequest.setDelFlag(DeleteFlag.NO);
        queryRequest.setCustomerIds(customerIds);
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单明细管理", "合并账号","合并账号");
       return couponCodeQueryProvider.bindAccount(CouponCodeQueryBindAccountRequest.builder()
               .couponCodeQueryRequest(queryRequest).parentId(parentCustomerRelaModifyRequest.getParentId()).build());
    }

    @ApiOperation(value = "绑定账号(优惠的绑定只绑定当前有效的优惠券)")
    @PostMapping("/bindChildAccount")
    @LcnTransaction
    public BaseResponse bindAccount(@RequestBody @Valid BindAccountRequest request) {
        if (CollectionUtils.isEmpty(request.getCustomerIds())) {
            throw new SbcRuntimeException("未选择子账户");
        }

        List<String> customerIds = workOrderDetailProvider.bindAccount(request).getContext().getCustomerIds();

        CouponCodeQueryRequest queryRequest = new CouponCodeQueryRequest();
        queryRequest.setUseStatus(DefaultFlag.NO);
        queryRequest.setNotExpire(true);
        queryRequest.setDelFlag(DeleteFlag.NO);
        queryRequest.setCustomerIds(customerIds);
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单明细管理", "绑定账号","绑定账号(优惠的绑定只绑定当前有效的优惠券)");
        return couponCodeQueryProvider.bindAccount(CouponCodeQueryBindAccountRequest.builder()
                .couponCodeQueryRequest(queryRequest).parentId(request.getParentId()).build());
    }



    @ApiOperation(value = "导出工单明细列表")
    @GetMapping("/export/{encrypted}")
    public void exportData(@PathVariable String encrypted, HttpServletResponse response) {
        String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
        WorkOrderDetailListRequest listReq = JSON.parseObject(decrypted, WorkOrderDetailListRequest.class);
        listReq.putSort("workOrderDelId", "desc");
        List<WorkOrderDetailVO> dataRecords = workOrderDetailQueryProvider.list(listReq).getContext().getWorkOrderDetailVOList();

        try {
            String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String fileName = URLEncoder.encode(String.format("工单明细列表_%s.xls", nowStr), "UTF-8");
            response.setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName));
            exportDataList(dataRecords, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            throw new SbcRuntimeException(CommonErrorCode.FAILED);
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("工单明细管理", "导出工单明细列表","操作成功");
    }

    /**
     * 导出列表数据具体实现
     */
    private void exportDataList(List<WorkOrderDetailVO> dataRecords, OutputStream outputStream) {
        ExcelHelper excelHelper = new ExcelHelper();
        Column[] columns = {
            new Column("处理时间", new SpelColumnRender<WorkOrderDetailVO>("dealTime")),
            new Column("处理状态", new SpelColumnRender<WorkOrderDetailVO>("status")),
            new Column("处理建议", new SpelColumnRender<WorkOrderDetailVO>("suggestion")),
            new Column("工单Id", new SpelColumnRender<WorkOrderDetailVO>("workOrderId"))
        };
        excelHelper.addSheet("工单明细列表", columns, dataRecords);
        excelHelper.write(outputStream);
    }

}
