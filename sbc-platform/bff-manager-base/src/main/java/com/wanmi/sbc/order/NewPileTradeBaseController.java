package com.wanmi.sbc.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.Platform;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.SiteResultCode;
import com.wanmi.sbc.customer.api.provider.employee.EmployeeQueryProvider;
import com.wanmi.sbc.customer.api.request.employee.EmployeeListRequest;
import com.wanmi.sbc.customer.bean.vo.EmployeeListVO;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByIdsRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.order.api.provider.manualrefund.ManualRefundQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.newPileTrade.NewPileTradeProvider;
import com.wanmi.sbc.order.api.request.manualrefund.ManualRefundResponseByOrderCodeRequest;
import com.wanmi.sbc.order.api.request.purchase.ManualRefundByOrderCodeRequest;
import com.wanmi.sbc.order.api.request.trade.*;
import com.wanmi.sbc.order.api.request.trade.newpile.NewPileTradeListByParentIdRequest;
import com.wanmi.sbc.order.api.request.trade.newpile.NewPileTradeListExportRequest;
import com.wanmi.sbc.order.api.response.manualrefund.ManualRefundResponse;
import com.wanmi.sbc.order.bean.dto.NewPileTradeQueryDTO;
import com.wanmi.sbc.order.bean.vo.NewPileTradeVO;
import com.wanmi.sbc.order.bean.vo.ProviderNewPileTradeExportVO;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.request.DisabledExportRequest;
import com.wanmi.sbc.order.request.NewPileTradeExportRequest;
import com.wanmi.sbc.order.service.NewPileTradeExportService;
import com.wanmi.sbc.order.service.ProviderNewPileTradeExportBaseService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: s2b囤货订单api
 * @author: chenchang
 * @create: 2022-09-25
 */
@Api(tags = "NewPileTradeController", description = "囤货订单服务 Api")
@RestController
@RequestMapping("/newPileTrade")
@Slf4j
@Validated
public class NewPileTradeBaseController {

    @Autowired
    private NewPileTradeProvider tradeProvider;
    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private EmployeeQueryProvider employeeQueryProvider;
    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private NewPileTradeExportService newPileTradeExportService;

    @Autowired
    private ProviderNewPileTradeExportBaseService providerNewPileTradeExportBaseService;

    @Autowired
    private ManualRefundQueryProvider manualRefundQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 审核订单
     *
     * @param tid
     * @param request 订单审核参数结构
     * @return
     */
    @ApiOperation(value = "审核订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单id", required = true)
    @RequestMapping(value = "/audit/{tid}", method = RequestMethod.POST)
//    @LcnTransaction
    public ResponseEntity<BaseResponse> audit(@PathVariable String tid, @RequestBody TradeAuditRequest request) {

        TradeAuditRequest tradeAuditRequest
                = TradeAuditRequest.builder()
                .tid(tid)
                .auditState(request.getAuditState())
                .reason(request.getReason())
                .financialFlag(request.getFinancialFlag())
                .operator(commonUtil.getOperator())
                .build();
        tradeProvider.audit(tradeAuditRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "审核订单", "审核订单：交易id" + (StringUtils.isNotBlank(tid) ? tid : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 批量审核订单
     *
     * @param request 批量审核请求参数结构
     * @return
     */
    @ApiOperation(value = "批量审核订单")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    @LcnTransaction
    public ResponseEntity<BaseResponse> batchAudit(@RequestBody TradeAuditBatchRequest request) {


        TradeAuditBatchRequest tradeAuditBatchRequest =

                TradeAuditBatchRequest.builder()
                        .auditState(request.getAuditState())
                        .ids(request.getIds())
                        .reason(request.getReason())
                        .operator(commonUtil.getOperator())
                        .build();

        tradeProvider.auditBatch(tradeAuditBatchRequest);
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "批量审核订单", "操作成功" );
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    @ApiOperation(value = "导出囤货订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted", value = "解密", required = true)
    @RequestMapping(value = "/export/params/{encrypted}/{encryptedable}", method = RequestMethod.GET)
    public void export(@PathVariable String encrypted, @PathVariable String encryptedable, Boolean isDetailed, HttpServletResponse response) {
        try {
            String decrypted = new String(Base64.getUrlDecoder().decode(encrypted.getBytes()));
            String decryptedable = new String(Base64.getUrlDecoder().decode(encryptedable.getBytes()));
            boolean tecryptedDetauled = isDetailed == null ? false : isDetailed;

            NewPileTradeExportRequest tradeExportRequest = JSON.parseObject(decrypted, NewPileTradeExportRequest.class);
            DisabledExportRequest disabledDTO = JSON.parseObject(decryptedable, DisabledExportRequest.class);

            NewPileTradeQueryDTO tradeQueryDTO = KsBeanUtil.convert(tradeExportRequest, NewPileTradeQueryDTO.class);

            // log.info("====decrypted:{}，decryptedable:{},isDetailed:{},NewPileTradeQueryDTO:{},disabledDTO:{}", decrypted, decryptedable, isDetailed,
            //         JSONObject.toJSONString(tradeQueryDTO), JSONObject.toJSONString(disabledDTO));

            WareHouseListResponse wareHouseListResponse = wareHouseQueryProvider.list(WareHouseListRequest.builder().build().setDelFlag(DeleteFlag.NO)).getContext();

            // log.info("WareHouseListResponse:{}", JSONObject.toJSONString(wareHouseListResponse));

            List<NewPileTradeVO> trades = tradeProvider.listTradeExport(NewPileTradeListExportRequest.builder().newPileTradeQueryDTO(tradeQueryDTO).build()).getContext().getNewPileTradeVOList();

            // log.info("====NewPileTradeVO:{}", JSONObject.toJSONString(trades));


            if (CollectionUtils.isNotEmpty(trades)) {
                trades.forEach(var -> {
                    if (Objects.nonNull(var.getWareId())) {
                        var.setWareName(wareHouseListResponse.getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(var.getWareId())).findFirst().get().getWareName());
                    }
                });
                List<EmployeeListVO> employeeList = employeeQueryProvider.list(new EmployeeListRequest()).getContext().getEmployeeList();
                if (CollectionUtils.isNotEmpty(employeeList)) {
                    trades = trades.stream().map(tradeVO -> {
                        for (EmployeeListVO employeeListVO : employeeList) {
                            if (tradeVO.getBuyer().getEmployeeId().equals(employeeListVO.getEmployeeId())) {
                                tradeVO.setEmployeeName(employeeListVO.getEmployeeName());
                                break;
                            }
                        }
                        return tradeVO;
                    }).collect(Collectors.toList());
                }
            }
            //按下单时间降序排列
            Comparator<NewPileTradeVO> c = Comparator.comparing(a -> a.getTradeState().getCreateTime());
            trades = trades.stream().sorted(
                    c.reversed()
            ).collect(Collectors.toList());

            String headerKey = "Content-Disposition";
            LocalDateTime dateTime = LocalDateTime.now();
            String fileName = String.format("批量导出囤货订单_%s.xls", dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("/newPileTrade/export/params, fileName={},", fileName, e);
            }
            String headerValue = String.format("attachment;filename=\"%s\";filename*=\"utf-8''%s\"", fileName, fileName);
            response.setHeader(headerKey, headerValue);

            //只导出子订单
//            List<String> parentIdList = new ArrayList<>();

//            if (Boolean.valueOf(disabledDTO.getDisabled())) {
//                trades.forEach(vo -> {
//                    parentIdList.add(vo.getId());
//                });
//                log.info("=====parentIdList:{}", JSONObject.toJSONString(parentIdList));
//                List<NewPileTradeVO> tradeVOList = tradeProvider.getOrderListByParentIds(NewPileTradeListByParentIdRequest.builder().parentTid(parentIdList).build()).getContext().getTradeVOList();
//                log.info("=====newPileTradeVoList:{}", JSONObject.toJSONString(tradeVOList));
//
//                // 遍历封装导出信息
//                List<ProviderNewPileTradeExportVO> tradeExportVOs = new ArrayList<>();
//                tradeVOList.forEach(tradeVO -> {
//                    ProviderNewPileTradeExportVO exportVO;
//                    // 商家信息
//                    String supplierName = StringUtils.isNotEmpty(tradeVO.getSupplierName()) ? tradeVO.getSupplierName() : "";
//                    String supplierCode = StringUtils.isNotEmpty(tradeVO.getSupplierCode()) ? tradeVO.getSupplierCode() : "";
//                    String supplierInfo = supplierName + "  " + supplierCode;
//                    for (int i = 0; i < tradeVO.getTradeItems().size(); i++) {
//                        TradeItemVO tradeItemVO = tradeVO.getTradeItems().get(i);
//                        exportVO = new ProviderNewPileTradeExportVO();
//                        if (i == 0) {
//                            KsBeanUtil.copyProperties(tradeVO, exportVO);
//                            // 下单时间
//                            exportVO.setCreateTime(tradeVO.getTradeState().getCreateTime());
//                            // 商家信息
//                            exportVO.setSupplierInfo(supplierInfo);
//                            // 供应商名称
//                            exportVO.setSupplierName(supplierName);
//                            // 订单商品金额
//                            exportVO.setOrderGoodsPrice(tradeVO.getTradePrice().getTotalPrice());
//                            // 订单状态
//                            exportVO.setFlowState(tradeVO.getTradeState().getFlowState());
//                            // 发货状态
//                            exportVO.setDeliverStatus(tradeVO.getTradeState().getDeliverStatus());
//                            exportVO.setConsigneeName(tradeVO.getConsignee().getName());
//                            exportVO.setDetailAddress(tradeVO.getConsignee().getDetailAddress());
//                            exportVO.setConsigneePhone(tradeVO.getConsignee().getPhone());
//                        }
//                        if (Objects.nonNull(tradeVO.getWareId())) {
//                            exportVO.setWareName(wareHouseListResponse.getWareHouseVOList().stream().filter(wareHouse -> wareHouse.getWareId().equals(tradeVO.getWareId())).findFirst().get().getWareName());
//                        }
//                        exportVO.setPayOrderId(tradeVO.getPayOrderId());
//                        exportVO.setSkuName(tradeItemVO.getSkuName());
//                        exportVO.setSpecDetails(tradeItemVO.getSpecDetails());
//                        exportVO.setSkuNo(tradeItemVO.getSkuNo());
//                        exportVO.setNum(tradeItemVO.getNum());
//
//                        tradeExportVOs.add(exportVO);
//                    }
//                });
//
//                log.info("=====tradeExportVOs:{}", JSONObject.toJSONString(tradeExportVOs));
//
//                try {
//                    providerNewPileTradeExportBaseService.exportProvider(tradeExportVOs, response.getOutputStream(), Platform.BOSS);
//                    response.flushBuffer();
//                } catch (IOException e) {
//                    throw new SbcRuntimeException(e);
//                }
//            } else {
            try {
                if (tecryptedDetauled) {
                    Map<String, String> erpNoMap = new HashMap<>();
                    List<String> skuIds = new ArrayList<>();
                    trades.forEach(var -> {
                        skuIds.addAll(var.getTradeItems().stream().map(m -> m.getSkuId()).collect(Collectors.toList()));
                        skuIds.addAll(var.getGifts().stream().map(m -> m.getSkuId()).collect(Collectors.toList()));
                    });
                    List<GoodsInfoVO> goodsInfos = goodsInfoQueryProvider.listByIds(GoodsInfoListByIdsRequest.builder().goodsInfoIds(skuIds).build()).getContext().getGoodsInfos();
                    goodsInfos.forEach(goodsInfoVO -> {
                        erpNoMap.put(goodsInfoVO.getGoodsInfoId(), goodsInfoVO.getErpGoodsInfoNo());
                    });
                    newPileTradeExportService.export(trades, erpNoMap, response.getOutputStream(),
                            false, tecryptedDetauled);
                } else {
                    newPileTradeExportService.export(trades, response.getOutputStream(),
                            false);
                }
                response.flushBuffer();
            } catch (IOException e) {
                throw new SbcRuntimeException(e);
            }
//            }
        } catch (Exception e) {
            log.error("/supplier/newPileTrade/export/params error: ", e);
            throw new SbcRuntimeException(SiteResultCode.ERROR_000001);
        }
        //记录操作日志
        operateLogMQUtil.convertAndSend("囤货订单服务", "导出囤货订单", "操作成功" );
    }


    /**
     * 根据订单号查询退款列表
     *
     * @return BaseResponse<List < com.wanmi.sbc.order.api.response.manualrefund.ManualRefundResponse>>
     */
    @ApiOperation(value = "根据囤货订单编号查询退款列表")
    @RequestMapping(value = "/manualRefundByOrderCode")
    public BaseResponse<List<ManualRefundResponse>> newPileTradeRefundByOrderCode(@RequestBody @Valid ManualRefundByOrderCodeRequest manualRefundByOrderCodeRequest) {
        ManualRefundResponseByOrderCodeRequest request = KsBeanUtil.convert(manualRefundByOrderCodeRequest, ManualRefundResponseByOrderCodeRequest.class);
        request.setOperator(commonUtil.getOperator());
        return manualRefundQueryProvider.newPileTradeRefundByOrderCode(request);
    }
}
