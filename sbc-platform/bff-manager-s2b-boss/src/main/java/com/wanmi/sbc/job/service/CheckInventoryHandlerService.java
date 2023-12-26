package com.wanmi.sbc.job.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.provider.goods.BulkGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.RetailGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockProvider;
import com.wanmi.sbc.goods.api.provider.goodswarestock.GoodsWareStockQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.goods.GoodsListByIdsRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockByGoodsForNoRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockUpdateListRequest;
import com.wanmi.sbc.goods.api.request.goodswarestock.GoodsWareStockUpdateRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoBatchNosModifyRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsListByIdsResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsBatchNoDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.bean.vo.GoodsWareStockVO;
import com.wanmi.sbc.wms.bean.vo.InventoryQueryReturnVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckInventoryHandlerService
 * @Description: TODO
 * @Date: 2020/6/1 14:03
 * @Version: 1.0
 */
@Service
@Slf4j
public class CheckInventoryHandlerService {


    @Autowired
    private GoodsWareStockProvider goodsWareStockProvider;

    @Autowired
    private GoodsWareStockQueryProvider goodsWareStockQueryProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private RetailGoodsInfoProvider retailGoodsInfoProvider;


    @Autowired
    private RetailGoodsQueryProvider retailGoodsQueryProvider;


    @Autowired
    private BulkGoodsInfoProvider bulkGoodsInfoProvider;


    @Autowired
    private BulkGoodsQueryProvider bulkGoodsQueryProvider;


//    @Transactional(rollbackFor = SbcRuntimeException.class)
//    public List<GoodsWareStockVO> updateStockByWMS(List<InventoryQueryReturnVO> inventory,Long wareId,List<String> erpGoodsInfoNos){
//      /*  List<String> erpGoodsInfoNos = inventory.stream().map(InventoryQueryReturnVO::getSku).collect(Collectors.toList());
//        if(CollectionUtils.isEmpty(erpGoodsInfoNos)){
//            return ;
//        }*/
//        Map<String,InventoryQueryReturnVO> map = inventory.stream().collect(Collectors.toMap(InventoryQueryReturnVO::getSku,g->g));
//        GoodsInfoNoResponse response = goodsInfoQueryProvider.listGoodsInfoNoByErpNos(GoodsInfoNoByErpNoRequest.builder()
//                .erpGoodsInfoNo(erpGoodsInfoNos)
//                .build()).getContext();
//        GoodsInfoListByConditionResponse listByConditionResponse = null;
//        //更新货品的批次号字段
//        log.info("1. ========================== 更新批次号：===================");
//        if(Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getGoodsInfoNoList())) {
//            List<GoodsBatchNoDTO> goodsBatchNoDTOS = new ArrayList<>();
//            listByConditionResponse = goodsInfoQueryProvider.listByCondition(GoodsInfoListByConditionRequest.builder()
//                    .goodsInfoNos(response.getGoodsInfoNoList()).build()).getContext();
//            listByConditionResponse.getGoodsInfos().forEach(gi->{
//                InventoryQueryReturnVO inventoryQueryReturnVO = map.get(gi.getErpGoodsInfoNo());
//                if (Objects.nonNull(inventoryQueryReturnVO)){
//                    goodsBatchNoDTOS.add(new GoodsBatchNoDTO(gi.getGoodsInfoId(),inventoryQueryReturnVO.getLotatt01()));
//                }
//            });
//            goodsInfoProvider.batchUpdateBatchNos(GoodsInfoBatchNosModifyRequest.builder().goodsBatchNoDTOS(goodsBatchNoDTOS).build());
//        }
//
//        log.info("2. ========================== 更新库存： ====================");
//        List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
//        if(Objects.nonNull(response) && CollectionUtils.isNotEmpty(response.getGoodsInfoNoList())){
//            goodsWareStockVOList = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoNos(GoodsWareStockByGoodsForNoRequest.builder()
//                    .goodsForIdList(response.getGoodsInfoNoList())
//                    .wareId(wareId)
//                    .build()).getContext().getGoodsWareStockVOList();
//            //设置erpNo零时转换
//            if(Objects.nonNull(listByConditionResponse)){
//                Map<String,GoodsInfoVO> infoVOMap = listByConditionResponse.getGoodsInfos().stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId,g->g));
//                goodsWareStockVOList.forEach(g->{
//                    if (Objects.nonNull(infoVOMap.get(g.getGoodsInfoId()))){
//                        g.setErpNo(infoVOMap.get(g.getGoodsInfoId()).getErpGoodsInfoNo());
//                    }
//                });
//            }
//            List<GoodsWareStockUpdateRequest> oldRequest =  KsBeanUtil.convertList(goodsWareStockVOList,GoodsWareStockUpdateRequest.class);
//
//            /**查询锁定库存*/
//            GoodsListByIdsResponse goodsListByIdsResponse = goodsQueryProvider.listByGoodsIdsNoValid(GoodsListByIdsRequest.builder()
//                    .goodsIds(goodsWareStockVOList.stream().map(gs -> gs.getGoodsId()).collect(Collectors.toList()))
//                    .build())
//                    .getContext();
//
//            //新增记录
//            for (GoodsWareStockVO inner : goodsWareStockVOList) {
//                Optional<InventoryQueryReturnVO> optional = inventory.stream().filter(i -> i.getSku().equals(inner.getErpNo())).findFirst();
//                if (optional.isPresent()) {
//                    long stock = optional.get().getStockNum().setScale(0, BigDecimal.ROUND_DOWN).longValue();
//
//                    if(Objects.nonNull(goodsListByIdsResponse) && CollectionUtils.isNotEmpty(goodsListByIdsResponse.getGoodsVOList())){
//                        GoodsVO goodsVO = goodsListByIdsResponse.getGoodsVOList().stream().filter(g -> g.getGoodsId().equals(inner.getGoodsId())).findFirst().orElse(null);
//                        if(Objects.nonNull(goodsVO) && Objects.nonNull(goodsVO.getLockStock()) && goodsVO.getLockStock() > 0){
//                            stock = stock - goodsVO.getLockStock();
//                        }
//                    }
//
//                    if (stock > 0) {
//                        inner.setStock(stock);
//                    } else {
//                        inner.setStock(0L);
//                    }
//                } else {
//                    inner.setStock(0L);
//                }
//            }
//
//            //更新库存中间表
//            List<GoodsWareStockUpdateRequest> goodsWareStockAddListRequests = KsBeanUtil.convertList(goodsWareStockVOList, GoodsWareStockUpdateRequest.class);
//            goodsWareStockProvider.updateList(GoodsWareStockUpdateListRequest.builder()
//                    .goodsWareStockAddRequestList(goodsWareStockAddListRequests)
//                    .goodsWareStockOldRequestList(oldRequest).build());
//            //更新ES中分仓库存数据
//            List<String> skuIds = goodsWareStockAddListRequests.stream().map(GoodsWareStockUpdateRequest::getGoodsInfoId).collect(Collectors.toList());
//            esGoodsInfoElasticService.initEsGoodsInfo(EsGoodsInfoRequest.builder().skuIds(skuIds).build());
//
//        }
//        return goodsWareStockVOList;
//
//    }


    /**
     * 同步库存（散批）
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public List<GoodsWareStockVO> bulkUpdateStockByWMS(List<InventoryQueryReturnVO> inventory,Long wareId,List<GoodsInfoVO> goodsInfoVOList){
        if(CollectionUtils.isEmpty(goodsInfoVOList)){
            return Lists.newArrayList();
        }

        Map<String,InventoryQueryReturnVO> map = inventory.stream().collect(Collectors.toMap(InventoryQueryReturnVO::getSku,g->g));

        //更新货品的批次号字段
        log.info("1. ========================== 更新批次号：===================");
        List<GoodsBatchNoDTO> goodsBatchNoDTOS = new ArrayList<>();
        goodsInfoVOList.forEach(gi->{
            InventoryQueryReturnVO inventoryQueryReturnVO = map.get(gi.getErpGoodsInfoNo());
            if (Objects.nonNull(inventoryQueryReturnVO)){
                goodsBatchNoDTOS.add(new GoodsBatchNoDTO(gi.getGoodsInfoId(),inventoryQueryReturnVO.getLotatt01()));
            }
        });
        bulkGoodsInfoProvider.batchUpdateBatchNos(GoodsInfoBatchNosModifyRequest.builder().goodsBatchNoDTOS(goodsBatchNoDTOS).build());


        log.info("2. ========================== 更新库存： ====================");
        List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
        goodsWareStockVOList = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoNos(GoodsWareStockByGoodsForNoRequest.builder()
                .goodsForIdList(goodsInfoVOList.stream().map(g->g.getGoodsInfoNo()).collect(Collectors.toList()))
                .wareId(wareId)
                .build()).getContext().getGoodsWareStockVOList();
        //设置erpNo零时转换
        Map<String,GoodsInfoVO> infoVOMap = goodsInfoVOList.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId,g->g));
        goodsWareStockVOList.forEach(g->{
            if (Objects.nonNull(infoVOMap.get(g.getGoodsInfoId()))){
                g.setErpNo(infoVOMap.get(g.getGoodsInfoId()).getErpGoodsInfoNo());
            }
        });
        List<GoodsWareStockUpdateRequest> oldRequest =  KsBeanUtil.convertList(goodsWareStockVOList,GoodsWareStockUpdateRequest.class);

        /**查询锁定库存*/
        GoodsListByIdsResponse goodsListByIdsResponse = bulkGoodsQueryProvider.bulkListByGoodsIdsNoValid(GoodsListByIdsRequest.builder()
                        .goodsIds(goodsWareStockVOList.stream().map(gs -> gs.getGoodsId()).collect(Collectors.toList()))
                        .build())
                .getContext();

        //新增记录
        for (GoodsWareStockVO inner : goodsWareStockVOList) {
            Optional<InventoryQueryReturnVO> optional = inventory.stream().filter(i -> i.getSku().equals(inner.getErpNo())).findFirst();
            if (optional.isPresent()) {
                BigDecimal stock = optional.get().getStockNum().setScale(0, BigDecimal.ROUND_DOWN);

                if(Objects.nonNull(goodsListByIdsResponse) && CollectionUtils.isNotEmpty(goodsListByIdsResponse.getGoodsVOList())){
                    GoodsVO goodsVO = goodsListByIdsResponse.getGoodsVOList().stream().filter(g -> g.getGoodsId().equals(inner.getGoodsId())).findFirst().orElse(null);
                    if(Objects.nonNull(goodsVO) && Objects.nonNull(goodsVO.getLockStock()) && goodsVO.getLockStock() > 0){
                        stock = stock.subtract(BigDecimal.valueOf(goodsVO.getLockStock())) ;
                    }
                }

                if (stock.compareTo(BigDecimal.ZERO) > 0) {
                    inner.setStock(stock);
                } else {
                    inner.setStock(BigDecimal.ZERO);
                }
            } else {
                inner.setStock(BigDecimal.ZERO);
            }
        }

        //更新库存中间表
        List<GoodsWareStockUpdateRequest> goodsWareStockAddListRequests = KsBeanUtil.convertList(goodsWareStockVOList, GoodsWareStockUpdateRequest.class);
        goodsWareStockProvider.updateList(GoodsWareStockUpdateListRequest.builder()
                .goodsWareStockAddRequestList(goodsWareStockAddListRequests)
                .goodsWareStockOldRequestList(oldRequest).build());

        return goodsWareStockVOList;

    }


    /**
     * 同步库存（零售）
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public List<GoodsWareStockVO> retailUpdateStockByWMS(List<InventoryQueryReturnVO> inventory,Long wareId,List<GoodsInfoVO> goodsInfoVOList){
        if(CollectionUtils.isEmpty(goodsInfoVOList)){
            return Lists.newArrayList();
        }

        Map<String,InventoryQueryReturnVO> map = inventory.stream().collect(Collectors.toMap(InventoryQueryReturnVO::getSku,g->g));

        //更新货品的批次号字段
        log.info("1. ========================== 更新批次号：===================");
        List<GoodsBatchNoDTO> goodsBatchNoDTOS = new ArrayList<>();
        goodsInfoVOList.forEach(gi->{
            InventoryQueryReturnVO inventoryQueryReturnVO = map.get(gi.getErpGoodsInfoNo());
            if (Objects.nonNull(inventoryQueryReturnVO)){
                goodsBatchNoDTOS.add(new GoodsBatchNoDTO(gi.getGoodsInfoId(),inventoryQueryReturnVO.getLotatt01()));
            }
        });
        retailGoodsInfoProvider.batchUpdateBatchNos(GoodsInfoBatchNosModifyRequest.builder().goodsBatchNoDTOS(goodsBatchNoDTOS).build());


        log.info("2. ========================== 更新库存： ====================");
        List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
        goodsWareStockVOList = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoNos(GoodsWareStockByGoodsForNoRequest.builder()
                .goodsForIdList(goodsInfoVOList.stream().map(g->g.getGoodsInfoNo()).collect(Collectors.toList()))
                .wareId(wareId)
                .build()).getContext().getGoodsWareStockVOList();
        //设置erpNo零时转换
        Map<String,GoodsInfoVO> infoVOMap = goodsInfoVOList.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId,g->g));
        goodsWareStockVOList.forEach(g->{
            if (Objects.nonNull(infoVOMap.get(g.getGoodsInfoId()))){
                g.setErpNo(infoVOMap.get(g.getGoodsInfoId()).getErpGoodsInfoNo());
            }
        });
        List<GoodsWareStockUpdateRequest> oldRequest =  KsBeanUtil.convertList(goodsWareStockVOList,GoodsWareStockUpdateRequest.class);

        /**查询锁定库存*/
        GoodsListByIdsResponse goodsListByIdsResponse = retailGoodsQueryProvider.retailListByGoodsIdsNoValid(GoodsListByIdsRequest.builder()
                .goodsIds(goodsWareStockVOList.stream().map(gs -> gs.getGoodsId()).collect(Collectors.toList()))
                .build())
                .getContext();

        //新增记录
        for (GoodsWareStockVO inner : goodsWareStockVOList) {
            Optional<InventoryQueryReturnVO> optional = inventory.stream().filter(i -> i.getSku().equals(inner.getErpNo())).findFirst();
            if (optional.isPresent()) {
                BigDecimal stock = optional.get().getStockNum().setScale(0, BigDecimal.ROUND_DOWN);

                if(Objects.nonNull(goodsListByIdsResponse) && CollectionUtils.isNotEmpty(goodsListByIdsResponse.getGoodsVOList())){
                    GoodsVO goodsVO = goodsListByIdsResponse.getGoodsVOList().stream().filter(g -> g.getGoodsId().equals(inner.getGoodsId())).findFirst().orElse(null);
                    if(Objects.nonNull(goodsVO) && Objects.nonNull(goodsVO.getLockStock()) && goodsVO.getLockStock() > 0){
                        stock = stock.subtract(BigDecimal.valueOf(goodsVO.getLockStock())) ;
                    }
                }

                if (stock.compareTo(BigDecimal.ZERO) > 0) {
                    inner.setStock(stock);
                } else {
                    inner.setStock(BigDecimal.ZERO);
                }
            } else {
                inner.setStock(BigDecimal.ZERO);
            }
        }

        //更新库存中间表
        List<GoodsWareStockUpdateRequest> goodsWareStockAddListRequests = KsBeanUtil.convertList(goodsWareStockVOList, GoodsWareStockUpdateRequest.class);
        goodsWareStockProvider.updateList(GoodsWareStockUpdateListRequest.builder()
                .goodsWareStockAddRequestList(goodsWareStockAddListRequests)
                .goodsWareStockOldRequestList(oldRequest).build());

        return goodsWareStockVOList;

    }

    /**
     * 同步库存
     */
    @Transactional(rollbackFor = SbcRuntimeException.class)
    public List<GoodsWareStockVO> updateStockByWMS(List<InventoryQueryReturnVO> inventory,Long wareId,List<GoodsInfoVO> goodsInfoVOList){
      /*  List<String> erpGoodsInfoNos = inventory.stream().map(InventoryQueryReturnVO::getSku).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(erpGoodsInfoNos)){
            return ;
        }*/

        if(CollectionUtils.isEmpty(goodsInfoVOList)){
            return Lists.newArrayList();
        }

        Map<String,InventoryQueryReturnVO> map = inventory.stream().collect(Collectors.toMap(InventoryQueryReturnVO::getSku,g->g));

        //更新货品的批次号字段
        log.info("1. ========================== 更新批次号：===================");
        List<GoodsBatchNoDTO> goodsBatchNoDTOS = new ArrayList<>();
        goodsInfoVOList.forEach(gi->{
            InventoryQueryReturnVO inventoryQueryReturnVO = map.get(gi.getErpGoodsInfoNo());
            if (Objects.nonNull(inventoryQueryReturnVO)){
                goodsBatchNoDTOS.add(new GoodsBatchNoDTO(gi.getGoodsInfoId(),inventoryQueryReturnVO.getLotatt01()));
            }
        });
        goodsInfoProvider.batchUpdateBatchNos(GoodsInfoBatchNosModifyRequest.builder().goodsBatchNoDTOS(goodsBatchNoDTOS).build());


        log.info("2. ========================== 更新库存： ====================");
        List<GoodsWareStockVO> goodsWareStockVOList = new ArrayList<>();
        goodsWareStockVOList = goodsWareStockQueryProvider.getGoodsWareStockByGoodsInfoNos(GoodsWareStockByGoodsForNoRequest.builder()
                .goodsForIdList(goodsInfoVOList.stream().map(g->g.getGoodsInfoNo()).collect(Collectors.toList()))
                .wareId(wareId)
                .build()).getContext().getGoodsWareStockVOList();
        //设置erpNo零时转换
        Map<String,GoodsInfoVO> infoVOMap = goodsInfoVOList.stream().collect(Collectors.toMap(GoodsInfoVO::getGoodsInfoId,g->g));
        goodsWareStockVOList.forEach(g->{
            if (Objects.nonNull(infoVOMap.get(g.getGoodsInfoId()))){
                g.setErpNo(infoVOMap.get(g.getGoodsInfoId()).getErpGoodsInfoNo());
            }
        });
        List<GoodsWareStockUpdateRequest> oldRequest =  KsBeanUtil.convertList(goodsWareStockVOList,GoodsWareStockUpdateRequest.class);

        /**查询锁定库存*/
        GoodsListByIdsResponse goodsListByIdsResponse = goodsQueryProvider.listByGoodsIdsNoValid(GoodsListByIdsRequest.builder()
                .goodsIds(goodsWareStockVOList.stream().map(gs -> gs.getGoodsId()).collect(Collectors.toList()))
                .build())
                .getContext();

        log.info("3. ========================== WMS返回库存DS： ====================:{}", JSONObject.toJSONString(goodsWareStockVOList));
        //新增记录
        for (GoodsWareStockVO inner : goodsWareStockVOList) {
            log.info("4. ========================== WMS返回库存DS： ====================inner:{}", JSONObject.toJSONString(inner));
            Optional<InventoryQueryReturnVO> optional = inventory.stream().filter(i -> i.getSku().equals(inner.getErpNo())).findFirst();
            log.info("5. ========================== WMS返回库存DS1： ====================optional:{}", optional.isPresent() ? JSONObject.toJSONString(optional): null);
            if (optional.isPresent()) {
                log.info("6. ========================== WMS返回库存DS2： ====================optional:{}");
                BigDecimal stock = optional.get().getStockNum().setScale(2, BigDecimal.ROUND_DOWN);
                log.info("7. ========================== WMS返回库存数量DS2： ====================stock:{}",stock);
                if(Objects.nonNull(goodsListByIdsResponse) && CollectionUtils.isNotEmpty(goodsListByIdsResponse.getGoodsVOList())){
                    GoodsVO goodsVO = goodsListByIdsResponse.getGoodsVOList().stream().filter(g -> g.getGoodsId().equals(inner.getGoodsId())).findFirst().orElse(null);
                    log.info("8. ========================== WMS返回库存数量DS3： ====================goodsVO:{}",JSONObject.toJSONString(goodsVO));
                    if(Objects.nonNull(goodsVO) && Objects.nonNull(goodsVO.getLockStock()) && goodsVO.getLockStock() > 0){
                        stock = stock.subtract(BigDecimal.valueOf(goodsVO.getLockStock()));
                    }
                }
                log.info("8. ========================== WMS返回库存数量DS2： ====================stock:{}",stock);
                if (stock.compareTo(BigDecimal.ZERO) > 0) {
                    inner.setStock(stock);
                } else {
                    inner.setStock(BigDecimal.ZERO);
                }
                log.info("9. ========================== WMS返回库存数量DS2： ====================stock:{}",stock);
            } else {
                inner.setStock(BigDecimal.ZERO);
                log.info("10. ========================== WMS返回库存数量DS2： ====================");
            }
        }
        //更新库存中间表
        List<GoodsWareStockUpdateRequest> goodsWareStockAddListRequests = KsBeanUtil.convertList(goodsWareStockVOList, GoodsWareStockUpdateRequest.class);
        log.info("11. ========================== WMS返回库存DS333： ====================:{}", JSONObject.toJSONString(goodsWareStockAddListRequests));
        log.info("12. ========================== WMS返回库存DS333： ====================:{}", JSONObject.toJSONString(oldRequest));
        goodsWareStockProvider.updateList(GoodsWareStockUpdateListRequest.builder()
                .goodsWareStockAddRequestList(goodsWareStockAddListRequests)
                .goodsWareStockOldRequestList(oldRequest).build());

        return goodsWareStockVOList;

    }

}
