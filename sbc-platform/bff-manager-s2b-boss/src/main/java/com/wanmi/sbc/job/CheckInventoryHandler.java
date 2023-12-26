package com.wanmi.sbc.job;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.AbstractXYYConstant;
import com.wanmi.sbc.common.enums.CompanyType;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.Constants;
import com.wanmi.sbc.goods.GoodsController;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
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
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.BatchInventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
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
 * @ClassName: CheckInventory
 * @Description: TODO
 * @Date: 2020/5/30 10:25
 * @Version: 1.0
 */
@JobHandler(value = "checkInventoryHandler")
@Component
@Slf4j
public class CheckInventoryHandler extends IJobHandler {

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private GoodsController goodsController;

    @Autowired
    private RequestWMSInventoryProvider requestWMSInventoryProvider;

    @Autowired
    private CheckInventoryHandlerService checkInventoryHandlerService;

    private Logger logger = LoggerFactory.getLogger(CheckInventoryHandler.class);

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        logger.info("wms库存校对定时器开始工作........:::: {}", LocalDateTime.now());
        List<Integer> notEqCompanyTypes = Arrays.asList(CompanyType.SUPPLIER.toValue());
        GoodsInfoCountByConditionResponse response = goodsInfoQueryProvider.countByCondition(GoodsInfoCountByConditionRequest.builder()
                .goodsInfoType(0).delFlag(0).addedFlag(1).notEqCompanyTypes(notEqCompanyTypes).build()).getContext();
        Long total = response.getCount();
        GoodsInfoPageRequest request = GoodsInfoPageRequest.builder().goodsInfoType(0).delFlag(0).addedFlag(1).notEqCompanyTypes(notEqCompanyTypes).build();
        BaseResponse<WareHouseListResponse> list = wareHouseQueryProvider.list(WareHouseListRequest.builder().delFlag(DeleteFlag.NO).build());
        List<WareHouseVO> wareHouseVOList = list.getContext().getWareHouseVOList();
        int pageSize = 100;
        int totalNum = total.intValue() / pageSize + 1;
        List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
        for (WareHouseVO inner:wareHouseVOList) {
            log.info("inner==== {}", JSONObject.toJSONString(inner));
            request.setPageSize(pageSize);
            int pageNo = 0;
            while (pageNo < totalNum) {
                request.setPageNum(pageNo);
                //设置仓库
                request.setWareId(inner.getWareId());
                GoodsInfoPageResponse infoPageResponse = goodsInfoQueryProvider.page(request).getContext();
                List<GoodsInfoVO> goodsInfoVOS = infoPageResponse.getGoodsInfoPage().getContent();
                String prefix = Constants.ERP_NO_PREFIX.get(inner.getWareId());
                goodsInfoVOS.forEach(g->{
                    g.setErpGoodsInfoNo(g.getErpGoodsInfoNo().replace(prefix, ""));
                });

                List<String> erpSkus = goodsInfoVOS.stream().map(GoodsInfoVO::getErpGoodsInfoNo).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(erpSkus)){

                    //查询
                    InventoryQueryResponse inventoryQueryResponse = requestWMSInventoryProvider.batchQueryInventory(BatchInventoryQueryRequest.builder()
                            .WarehouseID(inner.getWareCode())
                            .CustomerID(AbstractXYYConstant.CUSTOMER_ID)
                            .skuIds(erpSkus)
                            .Lotatt04(inner.getSelfErpId())
                            .build()).getContext();
//                    logger.info("查询erp商品信息：{}", JSON.toJSONString(inventoryQueryResponse));
                    //处理一下库存(除以步长)
                    if(CollectionUtils.isNotEmpty(inventoryQueryResponse.getInventoryQueryReturnVO())){
                        inventoryQueryResponse.getInventoryQueryReturnVO().parallelStream().forEach(inventoryQueryReturnVO -> {
                            Optional<GoodsInfoVO> optionalGoodsInfoVO = goodsInfoVOS.stream().filter(g->g.getErpGoodsInfoNo()
                                    .equals(inventoryQueryReturnVO.getSku())).findFirst();
                            if(optionalGoodsInfoVO.isPresent()){
                                BigDecimal addStep = optionalGoodsInfoVO.get().getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP);
                                if(Objects.nonNull(addStep) && addStep.compareTo(BigDecimal.ZERO)==1 ){
                                    BigDecimal stock = inventoryQueryReturnVO.getStockNum().divide(addStep,
                                            2,BigDecimal.ROUND_DOWN);
                                    inventoryQueryReturnVO.setStockNum(stock);
                                }
                            }
                        });
                    }
                    List<GoodsWareStockVO> goodsWareStockVOS = checkInventoryHandlerService.updateStockByWMS(inventoryQueryResponse.getInventoryQueryReturnVO(), inner.getWareId(), goodsInfoVOS);
                    goodsWareStockVOList.addAll(goodsWareStockVOS);
                }
                pageNo++;
            }
        }
        // 下架带T商品
       /* try {
            //根据GoodsId获取商品名称数据
            List<String> goodsIds = goodsWareStockVOList.stream().map(GoodsWareStockVO::getGoodsId).distinct().collect(Collectors.toList());
            GoodsByConditionRequest goodsByConditionRequest = new GoodsByConditionRequest();
            goodsByConditionRequest.setGoodsIds(goodsIds);
            Map<String, GoodsVO> goodsVOMap = goodsQueryProvider.listByCondition(goodsByConditionRequest).getContext().getGoodsVOList()
                    .stream().collect(Collectors.toMap(GoodsVO::getGoodsId, Function.identity()));
            //过滤出商品名称带T的商品数据，并对数据进行分组统计处理
            Map<String, LongSummaryStatistics> goodsGroup =
                    goodsWareStockVOList.stream()
                            .filter(goodsWareStock -> Objects.nonNull(goodsVOMap.get(goodsWareStock.getGoodsId()))
                                    && isErpTGoods(goodsVOMap.get(goodsWareStock.getGoodsId()).getGoodsName()))
                            //装填合规的商品名称
                            .collect(Collectors.groupingBy(GoodsWareStockVO::getGoodsId,
                                    Collectors.summarizingLong(GoodsWareStockVO::getStock)));//根据GoodsId分组统计
            //过滤出需要下架的商品
            List<String> offGoodsIds = new ArrayList<>();
            Iterator<Map.Entry<String, LongSummaryStatistics>> iterator = goodsGroup.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, LongSummaryStatistics> next = iterator.next();
                if(next.getValue().getSum() == 0) {
                    offGoodsIds.add(next.getKey());
                }
            }

            GoodsModifyAddedStatusRequest goodsModifyAddedStatusRequest = new GoodsModifyAddedStatusRequest();
            goodsModifyAddedStatusRequest.setGoodsIds(offGoodsIds);
            goodsController.offSale(goodsModifyAddedStatusRequest);
        } catch (Exception e) {
            logger.info("=============自动下架无库存商品操作失败============");
        }*/
        logger.info("wms库存校对定时器结束........:::: {}", LocalDateTime.now());
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
