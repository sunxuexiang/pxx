package com.wanmi.sbc.trade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.aop.EmployeeCheck;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsGoodsModifyInventoryService;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderProvider;
import com.wanmi.sbc.order.api.provider.returnorder.ReturnOrderQueryProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.returnorder.ReturnOrderAddRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCanReturnNumRequest;
import com.wanmi.sbc.order.api.request.trade.TradeCancelRequest;
import com.wanmi.sbc.order.api.request.trade.TradeGetByIdRequest;
import com.wanmi.sbc.order.api.request.trade.TradePageCriteriaRequest;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.enums.DeliverStatus;
import com.wanmi.sbc.order.bean.enums.ReturnReason;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import com.wanmi.sbc.order.bean.vo.TradePriceVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.setting.api.provider.AuditQueryProvider;
import com.wanmi.sbc.setting.api.request.TradeConfigGetByTypeRequest;
import com.wanmi.sbc.setting.api.response.TradeConfigGetByTypeResponse;
import com.wanmi.sbc.setting.bean.enums.ConfigType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sunkun on 2017/11/23.
 */
@Api(tags = "StoreTradeController", description = "店铺订单服务API")
@RestController
@RequestMapping("/trade")
public class StoreTradeController {

    @Autowired
    private TradeQueryProvider tradeQueryProvider;

    @Resource
    private CommonUtil commonUtil;

    @Autowired
    private AuditQueryProvider auditQueryProvider;


    @Autowired
    private TradeProvider tradeProvider;
    @Autowired
    private ReturnOrderQueryProvider returnOrderQueryProvider;

    @Autowired
    private ReturnOrderProvider returnOrderProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 分页查询可退订单 from ES
     *
     * @param returnQueryRequest
     * @return
     */
    @ApiOperation(value = "分页查询可退订单 from ES")
    @EmployeeCheck
    @RequestMapping(value = "/list/return", method = RequestMethod.POST)
    public ResponseEntity<Page<TradeVO>> pageCanReturn(@RequestBody TradeQueryDTO returnQueryRequest) {
        returnQueryRequest.setSortType(FieldType.Date.toString());
        returnQueryRequest.setSupplierId(commonUtil.getCompanyInfoId());
        /*TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
        request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
        TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
        JSONObject content = JSON.parseObject(config.getContext());
        Integer day = content.getObject("day", Integer.class);
        returnQueryRequest.setStatus(config.getStatus());
        if (Objects.nonNull(config.getStatus()) && config.getStatus() == 1 && Objects.nonNull(day) && day > 0) {
            returnQueryRequest.setDay(day);
        }*/
        returnQueryRequest.setStatus(1);
        returnQueryRequest.setDay(null);
        MicroServicePage<TradeVO> tradePage = tradeQueryProvider.pageCriteria(TradePageCriteriaRequest.builder()
                .isReturn(true).tradePageDTO(returnQueryRequest).build()).getContext()
                .getTradePage();
        tradePage.getContent().forEach(info -> {
            if (!DeliverStatus.NOT_YET_SHIPPED.equals(info.getTradeState().getDeliverStatus())){
               boolean canReturnFlag= returnOrderQueryProvider.canReturnFlag( TradeCanReturnNumRequest.builder()
                        .trade(KsBeanUtil.convert(info, TradeDTO.class)).build()).getContext().getCanReturnFlag();
               info.setCanReturnFlag(canReturnFlag);
            }else {
                info.setCanReturnFlag(true);
            }
        });
        tradePage.setContent(tradePage.getContent().stream().filter(v->{
            return  !(StringUtils.isNotBlank(v.getSourceChannel())&&v.getSourceChannel().contains("chains"));
        }).collect(Collectors.toList()));
        return ResponseEntity.ok(new PageImpl<>(tradePage.getContent(),
                 PageRequest.of(returnQueryRequest.getPageNum(), returnQueryRequest.getPageSize()), tradePage.getTotalElements()));
    }

    /**
     * 商家端取消订单
     */
    @ApiOperation(value = "取消订单")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "tid", value = "订单ID", required = true)
    @RequestMapping(value = "/cancel/{tid}", method = RequestMethod.GET)
    @LcnTransaction
    @MultiSubmit
    public BaseResponse cancel(@PathVariable(value = "tid") String tid) {
        Operator operator = commonUtil.getOperator();
        TradeCancelRequest tradeCancelRequest = TradeCancelRequest.builder()
                .tid(tid).operator(operator).build();
        try {
            tradeProvider.cancel(tradeCancelRequest);
            //操作日志记录
            operateLogMQUtil.convertAndSend("店铺订单服务", "取消订单", "取消订单：订单tid" + tid);
            return BaseResponse.SUCCESSFUL();
        } catch (SbcRuntimeException e) {
            // TODO 待定状态码 ，如果是已支付的情况下走退货退款流程，且调用wms取消订单接口
            if("K-050202".equals(e.getErrorCode())){
                //检验定点是否支持退货退款
                verifyIsReturnable(tid);
                ReturnOrderDTO returnOrder = new ReturnOrderDTO();
                TradeVO trade =
                        tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
                returnOrder.setCompany(CompanyDTO.builder().companyInfoId(trade.getSupplier().getSupplierId())
                        .companyCode(trade.getSupplier().getSupplierCode()).supplierName(trade.getSupplier().getSupplierName())
                        .storeId(trade.getSupplier().getStoreId()).storeName(trade.getSupplier().getStoreName())
                        .companyType(trade.getSupplier().getCompanyType())
                        .build());

                //封装退货退款基本信息
                returnOrder.setTid(tid);
                returnOrder.setChannelType(trade.getChannelType());
                returnOrder.setDistributorId(trade.getDistributorId());
                returnOrder.setInviteeId(trade.getInviteeId());
                returnOrder.setShopName(trade.getShopName());
                returnOrder.setDistributorName(trade.getDistributorName());
                returnOrder.setDistributeItems(trade.getDistributeItems());

                List<ReturnItemDTO> returnItems = KsBeanUtil.convert(trade.getTradeItems(), ReturnItemDTO.class);
                returnOrder.setReturnItems(returnItems);
                TradePriceVO tradePrice = trade.getTradePrice();
                ReturnPriceDTO returnPriceDTO = new ReturnPriceDTO();
                //金额相关
                returnPriceDTO.setTotalPrice(tradePrice.getTotalPrice());
                returnPriceDTO.setApplyStatus(false);
                returnPriceDTO.setApplyPrice(BigDecimal.ZERO);

                returnOrder.setReturnPrice(returnPriceDTO);
                returnOrder.setDescription("供应商商家取消订单");
                returnOrder.setRejectReason("{4: 0}");
                returnOrder.setWareId(trade.getWareId());
                returnOrder.setActivityType(trade.getActivityType());
                returnOrder.setReturnReason(ReturnReason.OTHER);

               returnOrderProvider.add(ReturnOrderAddRequest.builder().returnOrder(returnOrder)
                        .operator(commonUtil.getOperator()).build()).getContext().getReturnOrderId();
                //操作日志记录
                operateLogMQUtil.convertAndSend("店铺订单服务", "异常逻辑处理", "取消订单：订单tid" + tid);

            }
            return BaseResponse.SUCCESSFUL();
        }


    }


    /**
     * 取消订单退货退款的数据校验
     * @param tid
     */
    private void verifyIsReturnable(String tid) {
        TradeVO trade =
                tradeQueryProvider.getById(TradeGetByIdRequest.builder().tid(tid).build()).getContext().getTradeVO();
        if (Objects.nonNull(trade.getTradeState().getDeliverStatus()) && (trade.getTradeState().getDeliverStatus() == DeliverStatus.SHIPPED || trade.getTradeState().getDeliverStatus() == DeliverStatus.PART_SHIPPED)) {
            TradeConfigGetByTypeRequest request = new TradeConfigGetByTypeRequest();
            request.setConfigType(ConfigType.ORDER_SETTING_APPLY_REFUND);
            TradeConfigGetByTypeResponse config = auditQueryProvider.getTradeConfigByType(request).getContext();
            if (config.getStatus() == 0) {
                throw new SbcRuntimeException("K-050208");
            }
            JSONObject content = JSON.parseObject(config.getContext());
            Integer day = content.getObject("day", Integer.class);

            if (Objects.isNull(trade.getTradeState().getEndTime())) {
                throw new SbcRuntimeException("K-050002");
            }
            if (trade.getTradeState().getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < LocalDateTime.now().minusDays(day).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
                throw new SbcRuntimeException("K-050208");
            }
        }
       /* //校验wms是否符合要求取消订单
        tradeProvider.checkWms(TradeCheckWmsRequest.builder().tradeVO(trade).build());*/


    }





}
