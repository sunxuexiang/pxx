package com.wanmi.sbc.goodswarestock;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.api.provider.goods.BulkGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsListByIdsRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.*;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsListByIdsResponse;
import com.wanmi.sbc.goods.api.response.goodswarestock.*;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import com.wanmi.sbc.wms.api.provider.wms.RequestWMSInventoryProvider;
import com.wanmi.sbc.wms.api.request.wms.InventoryQueryRequest;
import com.wanmi.sbc.wms.api.response.wms.InventoryQueryResponse;
import com.wanmi.sbc.wms.bean.vo.InventoryQueryReturnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangwenchang
 */
@Slf4j
@Api(description = "sku分仓库存表管理API", tags = "BulkGoodsWareStockController")
@RestController
@RequestMapping(value = "/bulk/goods/ware/stock")
public class BulkGoodsWareStockController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private RequestWMSInventoryProvider requestWMSInventoryProvider;

    @Autowired
    private BulkGoodsInfoQueryProvider bulkGoodsInfoQueryProvider;

    @Autowired
    private BulkGoodsQueryProvider bulkGoodsQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    /**
     * 同步获取最新商品库存信息
     */
    @ApiOperation(value = "同步获取最新商品库存信息")
    @RequestMapping(value = "/syncStockNew", method = RequestMethod.POST)
    public BaseResponse<GoodsWareStockSyncNewResponse> syncErpStock(@RequestBody GoodsWareStockSyncNewRequest request) {
        operateLogMQUtil.convertAndSend("sku分仓库存表管理", "同步获取最新商品库存信息","同步获取最新商品库存信息：erp的商品编码" + (Objects.nonNull(request) ? request.getErpGoodsInfoNo() : ""));
        Long storeId = commonUtil.getStoreId();
        GoodsInfoByIdResponse goodsInfoByIdResponse = bulkGoodsInfoQueryProvider.getBulkById(GoodsInfoByIdRequest.builder()
                .goodsInfoId(request.getGoodsInfoId()).storeId(storeId).build()).getContext();
        BigDecimal addStep = goodsInfoByIdResponse.getAddStep().setScale(2,BigDecimal.ROUND_HALF_UP);
        //获取所有的分仓信息
        List<WareHouseVO> wareHouseVOS = wareHouseQueryProvider.list(WareHouseListRequest.builder()
                .delFlag(DeleteFlag.NO)
                .build()).getContext().getWareHouseVOList();
        Map<Long,WareHouseVO> wareCodeHouseMap = wareHouseVOS.stream().collect(Collectors.toMap(WareHouseVO::getWareId,g->g));
        //查询系统的库存
        GoodsWareStockListResponse stockListResponse = goodsWareStockQueryProvider
                .list(GoodsWareStockListRequest.builder()
                        .goodsInfoId(request.getGoodsInfoId())
                        .build()).getContext();
        //查询WMS库存信息

        String[] split = request.getErpGoodsInfoNo().split("-");

        InventoryQueryResponse response = requestWMSInventoryProvider.
                queryInventoryBySku(new InventoryQueryRequest()
                        .setSKU(split[split.length-1])
                        .setWareHouseCode(wareHouseVOS.stream().map(WareHouseVO::getSelfErpId).collect(Collectors.toList()))
                ).getContext();
        //查询商品锁定库存
        List<String> goodsIdList = Lists.newArrayList();
        goodsIdList.add(goodsInfoByIdResponse.getGoodsId());
        GoodsListByIdsResponse goodsListByIdsResponse = bulkGoodsQueryProvider.bulkListByGoodsIdsNoValid(GoodsListByIdsRequest.builder().goodsIds(goodsIdList).build()).getContext();
        if(Objects.nonNull(stockListResponse) && CollectionUtils.isNotEmpty(stockListResponse.getGoodsWareStockVOList())){
            List<GoodsWareStockVO> stockVOS = stockListResponse.getGoodsWareStockVOList();
            if(Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getInventoryQueryReturnVO())){
                List<InventoryQueryReturnVO> inventoryQueryReturnVOS = response.getInventoryQueryReturnVO();
                Map<String ,InventoryQueryReturnVO> skuInventorys = inventoryQueryReturnVOS.stream().collect(Collectors.toMap(InventoryQueryReturnVO::getWarehouseId,g->g));
                stockVOS.forEach(s->{
                    WareHouseVO wareHouseVO = wareCodeHouseMap.get(s.getWareId());
                    if(Objects.nonNull(wareHouseVO)){
                        InventoryQueryReturnVO inventoryQueryReturnVO = skuInventorys.get(wareHouseVO.getWareCode());
                        s.setWareName(wareHouseVO.getWareName());
                        if(Objects.nonNull(inventoryQueryReturnVO) && inventoryQueryReturnVO.getStockNum().compareTo(BigDecimal.ZERO)==1){
                            BigDecimal stock =
                                   inventoryQueryReturnVO.getStockNum().divide(addStep,0,
                                            BigDecimal.ROUND_DOWN);
                            /**获取锁定库存*/
                            if(Objects.nonNull(goodsListByIdsResponse) && CollectionUtils.isNotEmpty(goodsListByIdsResponse.getGoodsVOList())){
                                List<GoodsVO> goodsVOList = goodsListByIdsResponse.getGoodsVOList();
                                GoodsVO goodsVO = goodsVOList.stream().filter(g -> g.getGoodsId().equals(s.getGoodsId())).findFirst().orElse(null);
                                if(Objects.nonNull(goodsVO) && Objects.nonNull(goodsVO.getLockStock()) && goodsVO.getLockStock() > 0){
                                    stock = stock.subtract(BigDecimal.valueOf(goodsVO.getLockStock()));
                                }
                            }
                            if(stock.compareTo(BigDecimal.ZERO) > 0){
                                s.setStock(stock);
                            }else{
                                s.setStock(BigDecimal.ZERO);
                            }
                        }else{
                            s.setStock(BigDecimal.ZERO);
                        }
                    }
                });
            }else {
                stockVOS.forEach(s->s.setStock(BigDecimal.ZERO));
            }
            stockVOS.forEach(s->{
                wareHouseVOS.forEach(w->{
                    if(s.getWareId().equals(w.getWareId())){
                        s.setWareName(w.getWareName());
                    }
                });
            });
            return BaseResponse.success(GoodsWareStockSyncNewResponse.builder()
                    .goodsWareStockVOS(stockVOS)
                    .build());
        }
        throw new RuntimeException(GoodsErrorCode.GOODS_ASYNC_ERROR);
    }

}
