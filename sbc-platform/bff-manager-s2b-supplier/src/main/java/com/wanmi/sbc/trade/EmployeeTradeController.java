package com.wanmi.sbc.trade;

import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.PickUpFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreInfoByIdRequest;
import com.wanmi.sbc.customer.api.response.store.StoreInfoResponse;
import com.wanmi.sbc.customer.bean.dto.CompanyInfoDTO;
import com.wanmi.sbc.customer.bean.dto.StoreInfoDTO;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseByIdRequest;
import com.wanmi.sbc.goods.bean.enums.DeliverWay;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.order.api.provider.trade.TradeProvider;
import com.wanmi.sbc.order.api.provider.trade.TradeQueryProvider;
import com.wanmi.sbc.order.api.request.trade.TradeAddBatchRequest;
import com.wanmi.sbc.order.api.request.trade.TradeQueryDeliveryBatchRequest;
import com.wanmi.sbc.order.api.request.trade.TradeWrapperBackendCommitRequest;
import com.wanmi.sbc.order.bean.dto.*;
import com.wanmi.sbc.order.bean.vo.TradeCommitResultVO;
import com.wanmi.sbc.order.bean.vo.TradeVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

/**
 * <p>业务员助手订单controller</p>
 * Created by of628-wenzhi on 2018-04-13-下午2:51.
 */
@Api(tags = "EmployeeTradeController", description = "业务员助手订单服务API")
@RequestMapping("/trade/manager")
@RestController
@Validated
public class EmployeeTradeController {

    @Resource
    private CommonUtil commonUtil;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Resource
    private StoreQueryProvider storeQueryProvider;

    @Resource
    private TradeProvider tradeProvider;

    @Autowired
    private TradeQueryProvider tradeQueryProvider;
    @Autowired
    private RequestWMSInventoryProvider requestWMSInventoryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Value("${wms.api.flag}")
    private Boolean wmsAPIFlag;

    /**
     *
     * 描述：    创建订单
     * 场景：    用于业务员助手后台下单
     * @param:  请求参数 {@link TradeCreateDTO}
     * @return: 返回结果 {@link TradeCommitResultVO}
     *
     */
//    @ApiOperation(value = "创建订单")
//    @RequestMapping(value = "/create", method = RequestMethod.PUT)
//    @MultiSubmit
//    @LcnTransaction
//    public BaseResponse<TradeCommitResultVO> create(@Valid @RequestBody TradeCreateDTO tradeCreateRequest) {
//        Operator operator = commonUtil.getOperator();
//        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
//                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
//        ).getContext();
//        Long storeId = commonUtil.getStoreId();
//        StoreInfoResponse storeInfoResponse = storeQueryProvider.getStoreInfoById(new StoreInfoByIdRequest(storeId)).getContext();
//
//        //1.校验与包装订单信息-与商家代客下单公用
//        TradeVO trade = tradeQueryProvider.wrapperBackendCommit(TradeWrapperBackendCommitRequest.builder().operator(operator)
//                .companyInfo(KsBeanUtil.convert(companyInfo, CompanyInfoDTO.class)).storeInfo(KsBeanUtil.convert(storeInfoResponse, StoreInfoDTO.class))
//                .tradeCreate(tradeCreateRequest).build()).getContext().getTradeVO();
//        //2.订单入库(转换成list,传入批量创建订单的service方法,同一套逻辑,能够回滚)
//        TradeAddBatchRequest tradeAddBatchRequest = TradeAddBatchRequest.builder()
//                .tradeDTOList(KsBeanUtil.convert(Collections.singletonList(trade), TradeAddDTO.class))
//                .operator(operator)
//                .build();
//        List<TradeCommitResultVO> result = tradeProvider.addBatch(tradeAddBatchRequest).getContext().getTradeCommitResultVOS();
//        return BaseResponse.success(result.get(0));
//    }

    /**
     *
     * 描述：    创建订单
     * 场景：    用于业务员助手后台下单
     * @param:  请求参数 {@link TradeCreateDTO}
     * @return: 返回结果 {@link TradeCommitResultVO}
     *
     */
    @ApiOperation(value = "创建订单")
    @RequestMapping(value = "/create", method = RequestMethod.PUT)
    @MultiSubmit
    @LcnTransaction
    public BaseResponse<TradeCommitResultVO> createForApp(@Valid @RequestBody TradeCreateAppDTO request) {

        Operator operator = commonUtil.getOperator();
        CompanyInfoVO companyInfo = companyInfoQueryProvider.getCompanyInfoById(
                CompanyInfoByIdRequest.builder().companyInfoId(commonUtil.getCompanyInfoId()).build()
        ).getContext();
        Long storeId = commonUtil.getStoreId();
        StoreInfoResponse storeInfoResponse = storeQueryProvider.getStoreInfoById(new StoreInfoByIdRequest(storeId)).getContext();

        List<TradeVO> tradeVOList = new ArrayList<>(20);

        //根据storeID再次拆单
        List<Map<Long, List<SupplierAppOrderListDTO>>> tradeList = splitOrderByStoreId(request);

        TradeCreateDTO convert = KsBeanUtil.convert(request, TradeCreateDTO.class);

        //组装请求参数
        for (Map<Long, List<SupplierAppOrderListDTO>> map : tradeList) {
            for (Map.Entry<Long, List<SupplierAppOrderListDTO>> entry : map.entrySet()) {
                for (SupplierAppOrderListDTO inner : entry.getValue()) {
                    //重新封装
                    convert.setTradeItems(inner.getTradeItems());
                    convert.setSellerRemark(inner.getSellerRemark());
                    convert.setEncloses(inner.getEncloses());
                    convert.setWareId(inner.getWareId());
                    convert.setDeliverWay(inner.getDeliverWay());
                    convert.setWareHouseCode(inner.getWareHouseCode());
                    if (inner.getDeliverWay().equals(DeliverWay.PICK_SELF)) {
                        //如果是自自提订单需要组装自提信息
                        validPickUpPoint(inner, entry.getKey(), convert);
                    }
                    //1.校验与包装订单信息-与商家代客下单公用
                    TradeVO trade = tradeQueryProvider.wrapperBackendCommit(TradeWrapperBackendCommitRequest.builder().operator(operator)
                            .companyInfo(KsBeanUtil.convert(companyInfo, CompanyInfoDTO.class)).storeInfo(KsBeanUtil.convert(storeInfoResponse, StoreInfoDTO.class))
                            .tradeCreate(convert).build()).getContext().getTradeVO();
                    tradeVOList.add(trade);
                }
            }
        }
        //1.1 重新计算运费
        tradeVOList=tradeProvider.queryDelivery(TradeQueryDeliveryBatchRequest.builder().tradeDTOList(tradeVOList).build()).getContext().getTradeVO();
        //2.订单入库(转换成list,传入批量创建订单的service方法,同一套逻辑,能够回滚)
        TradeAddBatchRequest tradeAddBatchRequest = TradeAddBatchRequest.builder()
                .tradeDTOList(KsBeanUtil.convert(tradeVOList, TradeAddDTO.class))
                .operator(operator)
                .build();
        List<TradeCommitResultVO> result = tradeProvider.addBatch(tradeAddBatchRequest).getContext().getTradeCommitResultVOS();
        operateLogMQUtil.convertAndSend("业务员助手订单服务", "创建订单", "操作成功");
        return BaseResponse.success(result.get(0));
    }

    /**
     * 功能描述: 根据storeId二次拆单
     */
    private List<Map<Long, List<SupplierAppOrderListDTO>>> splitOrderByStoreId( TradeCreateAppDTO tradeCreateRequest) {
        List<Map<Long, List<SupplierAppOrderListDTO>>> tradeList=new ArrayList<>(20);
        for (SupplierAppOrderListDTO inner: tradeCreateRequest.getOrderList()){
            //以storeId为key再次拆单
            Map<Long, List<TradeItemDTO>> collect = inner.getTradeItems().stream().collect(groupingBy(TradeItemDTO::getStoreId));
            Map<Long, List<SupplierAppOrderListDTO>> result=new HashMap<>();
            List<SupplierAppOrderListDTO> supplierAppOrderListDTOS = new ArrayList<>();

            for (Map.Entry<Long, List<TradeItemDTO>> entry: collect.entrySet()){
                supplierAppOrderListDTOS.add(SupplierAppOrderListDTO.builder().tradeItems(entry.getValue())
                        .deliverWay(inner.getDeliverWay())
                        .sellerRemark(inner.getSellerRemark())
                        .encloses(inner.getEncloses())
                        .wareHouseCode(inner.getWareHouseCode())
                        .wareId(inner.getWareId()).build());
                result.put(entry.getKey(), supplierAppOrderListDTOS);
            }
            tradeList.add(result);
        }
        return tradeList;
    }

    /**
     * 功能描述: 验证自提门店信息,如果存在塞入自提信息
     */
    private void validPickUpPoint(SupplierAppOrderListDTO inner,Long storeId,TradeCreateDTO tradeCreateDTO){

            if (inner.getDeliverWay().equals(DeliverWay.PICK_SELF)){
                WareHouseVO wareHouseVO = wareHouseQueryProvider.getById(WareHouseByIdRequest.builder()
                        .wareId(inner.getWareId()).storeId(storeId).build()).getContext().getWareHouseVO();
                if (wareHouseVO.getPickUpFlag().toValue()== PickUpFlag.NO.toValue()){
                    throw new SbcRuntimeException("仓库不合法");
                }
                tradeCreateDTO.setWareHouseVO(wareHouseVO);
            }

    }
}
