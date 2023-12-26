package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSON;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.store.StorePageRequest;
import com.wanmi.sbc.customer.api.response.store.StorePageResponse;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoCountByConditionRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoCountByConditionResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoPageResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.job.service.CheckInventoryHandlerService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.BatchInventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 零售商品库存同步
 * @author: XinJiang
 * @time: 2022/3/26 16:55
 */
@JobHandler(value = "checkRetailInventoryHandler")
@Component
@Slf4j
public class CheckRetailInventoryHandler extends IJobHandler {

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;

    @Autowired
    private RequestWMSInventoryProvider requestWMSInventoryProvider;

    @Autowired
    private CheckInventoryHandlerService checkInventoryHandlerService;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    private Logger logger = LoggerFactory.getLogger(CheckInventoryHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        logger.info("零售商城-wms库存校对定时器开始工作........:::: {}", LocalDateTime.now());
        //获取零售店的店铺id
        Long storeId = storeQueryProvider.page(StorePageRequest.builder().companyType(CompanyType.RETAIL)
                .build()).getContext().getStoreVOPage().getContent().get(0).getStoreId();
        GoodsInfoCountByConditionResponse response = retailGoodsInfoQueryProvider.countByCondition(GoodsInfoCountByConditionRequest.builder()
                .goodsInfoType(0).delFlag(0).addedFlag(1).build()).getContext();
        Long total = response.getCount();
        GoodsInfoPageRequest request = GoodsInfoPageRequest.builder().goodsInfoType(0).delFlag(0).addedFlag(1).build();

        //根据店铺id获取分仓信息
        BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(WareHouseListRequest.builder().storeId(storeId).build());
        List<WareHouseVO> wareHouseVOList = list.getContext().getWareHouseVOList();
        int pageSize = 100;
        int totalNum = total.intValue() / pageSize + 1;
//        logger.info("仓库信息为：{}", JSON.toJSONString(wareHouseVOList));
        List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
        for (WareHouseVO inner:wareHouseVOList) {
            request.setPageSize(pageSize);
            int pageNo = 0;
            while (pageNo < totalNum) {
                request.setPageNum(pageNo);
                GoodsInfoPageResponse infoPageResponse = retailGoodsInfoQueryProvider.page(request).getContext();
//                logger.info("查询商品信息：{}", JSON.toJSONString(infoPageResponse));
                List<GoodsInfoVO> goodsInfoVOS = infoPageResponse.getGoodsInfoPage().getContent();
                List<String> erpSkus = goodsInfoVOS.stream().map(GoodsInfoVO::getErpGoodsInfoNo).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(erpSkus)){
                    //查询
                    InventoryQueryResponse inventoryQueryResponse = requestWMSInventoryProvider.batchQueryInventory(BatchInventoryQueryRequest.builder()
                            .WarehouseID(inner.getWareCode())
                            .CustomerID(AbstractXYYConstant.CUSTOMER_ID)
                            .skuIds(erpSkus)
//                            .Lotatt04(inner.getSelfErpId())
                            .build()).getContext();
                    // logger.info("查询erp商品信息：{}", JSON.toJSONString(inventoryQueryResponse));
                    //处理一下库存(除以步长)
                    if(CollectionUtils.isNotEmpty(inventoryQueryResponse.getInventoryQueryReturnVO())){
                        inventoryQueryResponse.getInventoryQueryReturnVO().parallelStream().forEach(inventoryQueryReturnVO -> {
                            Optional<GoodsInfoVO> optionalGoodsInfoVO = goodsInfoVOS.stream().filter(g->g.getErpGoodsInfoNo()
                                    .equals(inventoryQueryReturnVO.getSku())).findFirst();
                            if(optionalGoodsInfoVO.isPresent()){
                                BigDecimal addStep = optionalGoodsInfoVO.get().getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP);
                                if(Objects.nonNull(addStep) && addStep.compareTo(BigDecimal.ZERO)==1 ){
                                    BigDecimal stock = inventoryQueryReturnVO.getQty().divide(addStep,
                                            0,BigDecimal.ROUND_DOWN);
                                    inventoryQueryReturnVO.setStockNum(stock);
                                }
                            }
                        });
                    }
                    List<GoodsWareStockVO> goodsWareStockVOS = checkInventoryHandlerService.retailUpdateStockByWMS(inventoryQueryResponse.getInventoryQueryReturnVO(), inner.getWareId(), goodsInfoVOS);
                    goodsWareStockVOList.addAll(goodsWareStockVOS);
                }
                pageNo++;
            }
        }
        logger.info("零售商城-wms库存校对定时器结束........:::: {}", LocalDateTime.now());
        return SUCCESS;
    }

    private Boolean isErpTGoods(String goodsName) {
        String substring = goodsName.substring(goodsName.length() - 1);
        if ("T".equals(substring) || "t".equals(substring)) {
            return true;
        }
        return false;
    }
}
