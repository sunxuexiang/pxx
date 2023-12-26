package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.OsUtil;
import com.wanmi.sbc.es.elastic.EsBulkGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsGoodsModifyInventoryService;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.*;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.ares.DispatcherFunctionRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsExistsByIdRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.request.info.DistributionGoodsChangeRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.response.goods.GoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsListByIdsResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsViewByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByGoodsIdresponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByGoodsResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.goods.bean.vo.StoreCateGoodsRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 散批商品服务spu
 * @author: XinJiang
 * @time: 2022/3/8 16:21
 */
@Api(tags = "BulkGoodsController", description = "散批商品服务spu Api")
@RestController
@RequestMapping("/bulk/goods")
public class BulkGoodsController {
    @Autowired
    private BulkGoodsQueryProvider bulkGoodsQueryProvider;
    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;
    @Autowired
    private EsBulkGoodsInfoElasticService esBulkGoodsInfoElasticService;
    @Autowired
    private BulkGoodsInfoQueryProvider bulkGoodsInfoQueryProvider;
    @Autowired
    private BulkGoodsProvider bulkGoodsProvider;
    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private OsUtil osUtil;
    @Autowired
    private GoodsAresProvider goodsAresProvider;
    @Autowired
    private FreightTemplateGoodsQueryProvider freightTemplateGoodsQueryProvider;
    @Autowired
    private GoodsProvider goodsProvider;
    @Autowired
    private EsGoodsModifyInventoryService esGoodsModifyInventoryService;
    /**
     * 获取商品详情信息
     * * @param goodsId 商品编号
     * @return 商品详情
     */
    @ApiOperation(value = "获取商品详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "goodsId", value = "商品Id", required = true)
    @RequestMapping(value = "/spu/{goodsId}", method = RequestMethod.GET)
    public BaseResponse<GoodsViewByIdResponse> info(@PathVariable String goodsId) {
        GoodsViewByIdRequest request = new GoodsViewByIdRequest();
        request.setGoodsId(goodsId);
        GoodsViewByIdResponse response = bulkGoodsQueryProvider.getBulkViewById(request).getContext();
        //获取商品店铺分类

        if (osUtil.isS2b()) {
            StoreCateListByGoodsRequest storeCateListByGoodsRequest = new StoreCateListByGoodsRequest(Collections.singletonList(goodsId));
            BaseResponse<StoreCateListByGoodsResponse> baseResponse = storeCateQueryProvider.listByGoods(storeCateListByGoodsRequest);
            StoreCateListByGoodsResponse storeCateListByGoodsResponse = baseResponse.getContext();

            if (Objects.nonNull(storeCateListByGoodsResponse)) {
                List<StoreCateGoodsRelaVO> storeCateGoodsRelaVOList = storeCateListByGoodsResponse.getStoreCateGoodsRelaVOList();
                response.getGoods().setStoreCateIds(storeCateGoodsRelaVOList.stream()
                        .filter(rela -> rela.getStoreCateId() != null)
                        .map(StoreCateGoodsRelaVO::getStoreCateId)
                        .collect(Collectors.toList()));
            }
        }
        return BaseResponse.success(response);
    }

    /**
     * 编辑商品排序序号
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑商品排序序号")
    @RequestMapping(value = "/spu/modifySeqNum", method = RequestMethod.PUT)
    public BaseResponse modifyGoodsSeqNum(@RequestBody GoodsModifySeqNumRequest request) {
        if (StringUtils.isBlank(request.getGoodsId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        if (Objects.isNull(request.getGoodsSeqNum())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        if (request.getGoodsSeqNum() != null && request.getGoodsSeqNum() == 0) {
            request.setGoodsSeqNum(null);
        }
        GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
        goodsByIdRequest.setGoodsId(request.getGoodsId());
        GoodsByIdResponse oldGoods = bulkGoodsQueryProvider.getBulkById(goodsByIdRequest).getContext();

        bulkGoodsProvider.modifyRetailGoodsSeqNum(request);
        GoodsVO goodsVO = new GoodsVO();
        KsBeanUtil.copyPropertiesThird(request, goodsVO);
        esBulkGoodsInfoElasticService.modifyGoodsSeqNum(goodsVO);

        //ares埋点-商品-后台修改商品sku,迁移至goods微服务下
        operateLogMQUtil.convertAndSend("零售商品", "编辑排序序号",
                "SPU编码:" + oldGoods.getGoodsNo() +
                        "，操作前排序:" + oldGoods.getGoodsSeqNum() +
                        "，操作后排序:" + request.getGoodsSeqNum() +
                        "，操作时间:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                        "，操作人:" + commonUtil.getOperator().getName());

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量上架商品
     */
    @ApiOperation(value = "批量上架商品")
    @RequestMapping(value = "/spu/sale", method = RequestMethod.PUT)
    public BaseResponse onSale(@RequestBody GoodsModifyAddedStatusRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setAddedFlag(AddedFlag.YES.toValue());

        //查询上架商品中是否包含供应商商品(下架状态的)，包含则返回
        GoodsListByIdsRequest goodsListByIdsRequest = new GoodsListByIdsRequest();
        goodsListByIdsRequest.setGoodsIds(request.getGoodsIds());
        List<GoodsVO> goodsVOList = bulkGoodsQueryProvider.listBulkByIds(goodsListByIdsRequest).getContext().getGoodsVOList();
        List<String> providerGoodsId = new ArrayList<>();
        goodsVOList.forEach(goodsVO -> {
            if(StringUtils.isNotEmpty(goodsVO.getProviderGoodsId())){
                providerGoodsId.add(goodsVO.getProviderGoodsId());
            }
        });
        if(CollectionUtils.isNotEmpty(providerGoodsId)){
            goodsListByIdsRequest.setGoodsIds(providerGoodsId);
            List<GoodsVO> providerGoodsVOList = bulkGoodsQueryProvider.listBulkByIds(goodsListByIdsRequest).getContext().getGoodsVOList();
            for (GoodsVO providerGoods: providerGoodsVOList){
                if(providerGoods.getDelFlag().equals(DeleteFlag.YES)){
                    throw new SbcRuntimeException("K-030507","包含供应商删除商品");
                    //上架的商品中存在  已经下架的供应商商品
                }else if(providerGoods.getAddedFlag().equals(AddedFlag.NO.toValue())){
                    throw new SbcRuntimeException("K-030504");
                    //上架的商品中存在  部分商家的供应商商品
                }else if(providerGoods.getAddedFlag().equals(AddedFlag.PART.toValue())){
                    throw new SbcRuntimeException("K-030505");
                }
            }
        }
        bulkGoodsProvider.modifyAddedRetailStatus(request);
        //更新ES
        esBulkGoodsInfoElasticService.updateAddedStatus(AddedFlag.YES.toValue(), request.getGoodsIds(), null, null);
        //ares埋点-商品-后台批量修改商品spu的所有sku上下架状态
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("editGoodsSpuUp", new Object[]{AddedFlag.YES.toValue(), request.getGoodsIds()}));
        if (1 == request.getGoodsIds().size()) {
            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(request.getGoodsIds().get(0));
            GoodsByIdResponse response = bulkGoodsQueryProvider.getBulkById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "上架",
                    "上架：SPU编码" + response.getGoodsNo());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量上架", "批量上架");
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量下架商品
     */
    @ApiOperation(value = "批量下架商品")
    @RequestMapping(value = "/spu/sale", method = RequestMethod.DELETE)
    public BaseResponse offSale(@RequestBody GoodsModifyAddedStatusRequest request) {
        List<String> goodsIds = request.getGoodsIds();
        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        request.setAddedFlag(AddedFlag.NO.toValue());
        //如果下架商品是供应商商品，商家商品同步下架
        GoodsListByIdsRequest goodsListByIdsRequest = new GoodsListByIdsRequest();
        goodsListByIdsRequest.setGoodsIds(request.getGoodsIds());
        List<GoodsVO> goodsVOList = bulkGoodsQueryProvider.bulkListByCondition(
                GoodsByConditionRequest.builder().providerGoodsIds(goodsIds).build()).getContext().getGoodsVOList();
        if(CollectionUtils.isNotEmpty(goodsVOList)){
            goodsVOList.forEach(s->{goodsIds.add(s.getGoodsId());});
        }
        request.setGoodsIds(goodsIds);

        bulkGoodsProvider.modifyAddedRetailStatus(request);
        //更新ES //零售商品没关联ES
        esBulkGoodsInfoElasticService.updateAddedStatus(AddedFlag.NO.toValue(), request.getGoodsIds(), null,null);

        //ares埋点-商品-后台批量修改商品spu的所有sku上下架状态
        goodsAresProvider.dispatchFunction(new DispatcherFunctionRequest("editGoodsSpuUp",new Object[]{AddedFlag.NO.toValue(),request.getGoodsIds()}));

        if(1 == request.getGoodsIds().size()){
            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(request.getGoodsIds().get(0));
            GoodsByIdResponse response = bulkGoodsQueryProvider.getBulkById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "下架",
                    "下架：SPU编码"+response.getGoodsNo());
        }else {
            operateLogMQUtil.convertAndSend("商品", "批量下架", "批量下架");
        }
        return BaseResponse.SUCCESSFUL();
    }



    /**
     * 删除商品
     */
    @ApiOperation(value = "删除商品")
    @RequestMapping(value = "/spu", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody GoodsDeleteByIdsRequest request) {

        if (1 == request.getGoodsIds().size()) {
            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(request.getGoodsIds().get(0));
            GoodsByIdResponse response = bulkGoodsQueryProvider.getBulkById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "删除商品",
                    "删除商品：SPU编码" + response.getGoodsNo());

        } else {
            operateLogMQUtil.convertAndSend("商品", "批量删除",
                    "批量删除");
        }

        if (CollectionUtils.isEmpty(request.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsProvider.deleteBulkByIds(request);
        //关联供应商商品下架 因为零售商品没有ES
        GoodsListByIdsResponse goodsListByIdsResponse = bulkGoodsQueryProvider.listByProviderGoodsId(GoodsListByIdsRequest.builder().goodsIds(request.getGoodsIds()).build()).getContext();
        if (goodsListByIdsResponse != null && CollectionUtils.isNotEmpty(goodsListByIdsResponse.getGoodsVOList())) {
            List<String> providerOfGoodsIds = goodsListByIdsResponse.getGoodsVOList().stream().map(GoodsVO::getGoodsId).collect(Collectors.toList());
            GoodsInfoListByConditionResponse goodsInfoListByConditionResponse = bulkGoodsInfoQueryProvider.listByCondition(GoodsInfoListByConditionRequest.builder().goodsIds(providerOfGoodsIds).build()).getContext();
            if (goodsInfoListByConditionResponse != null && CollectionUtils.isNotEmpty(goodsInfoListByConditionResponse.getGoodsInfos())) {
                List<String> providerOfGoodInfoIds = goodsInfoListByConditionResponse.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
                //更新上下架状态
                esBulkGoodsInfoElasticService.updateAddedStatus(AddedFlag.NO.toValue(), providerOfGoodsIds, providerOfGoodInfoIds, null);
            }
        }
        //更新ES 零售商品没有关联ES
        esBulkGoodsInfoElasticService.deleteByGoods(request.getGoodsIds());
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 编辑商品
     */
    @ApiOperation(value = "编辑商品")
    @RequestMapping(value = "/spu", method = RequestMethod.PUT)
    public BaseResponse edit(@RequestBody GoodsModifyRequest request) {
        Long fId = request.getGoods().getFreightTempId();
        if (request.getGoods() == null || CollectionUtils.isEmpty(request.getGoodsInfos()) || request
                .getGoods().getGoodsId() == null || StringUtils.isEmpty(String.valueOf(fId)
        )) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        //判断运费模板是否存在
        freightTemplateGoodsQueryProvider.existsById(
                FreightTemplateGoodsExistsByIdRequest.builder().freightTempId(fId).build());

        Boolean isRemove = false;
        //上下架判断

        if (StringUtils.isNotEmpty(request.getGoods().getProviderGoodsId())) {
            GoodsByIdRequest goodsByIdRequest = new GoodsByIdRequest();
            goodsByIdRequest.setGoodsId(request.getGoods().getProviderGoodsId());
            GoodsByIdResponse context = bulkGoodsQueryProvider.getBulkById(goodsByIdRequest).getContext();
            //如果找不到这个供应商商品了

            if (context == null) {
                isRemove = true;
            }

            if (context.getAddedFlag() == AddedFlag.NO.toValue()) {
                //保持下架状态
                request.getGoods().setAddedFlag(AddedFlag.NO.toValue());
                //保证为下架状态
                isRemove = true;
            }
        }


        Map<String, Object> returnMap = goodsProvider.modifyBulkGoods(request).getContext().getReturnMap();

        //零售商品没有ES
        if (CollectionUtils.isNotEmpty((List<String>) returnMap.get("delStoreGoodsInfoIds"))) {
            esBulkGoodsInfoElasticService.delete((List<String>) returnMap.get("delStoreGoodsInfoIds"));
        }
        List<String> skuIds = request.getGoodsInfos().stream().
                filter(goodsInfoVO -> StringUtils.isNotEmpty(goodsInfoVO.getGoodsInfoId())).
                map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        //零售商品没有ES
        esBulkGoodsInfoElasticService.deleteByGoods(Collections.singletonList(request.getGoods().getGoodsId()));
        //零售商品没有ES
        esGoodsModifyInventoryService.modifyBulkInventory(skuIds,request.getGoods().getGoodsId());
        //ares埋点-商品-后台修改商品sku,迁移至goods微服务下
        operateLogMQUtil.convertAndSend("商品", "编辑商品",
                "编辑商品：SPU编码" + request.getGoods().getGoodsNo());

        if (isRemove) {
            throw new SbcRuntimeException("K-030507");
        }
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "批量修改商品类目(散批)")
    @RequestMapping(value = "/spu/cate/batch", method = RequestMethod.PUT)
    public BaseResponse batchModifyCate(@RequestBody GoodsBatchModifyCateRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsIds()) || Objects.isNull(request.getCateId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsProvider.batchModifyBulkCate(request);
        // 刷ES
        esBulkGoodsInfoElasticService.deleteByGoods(request.getGoodsIds());

        request.getGoodsIds().forEach(goodsId ->{
            BaseResponse<GoodsInfoByGoodsIdresponse> response = bulkGoodsInfoQueryProvider.getByGoodsId(DistributionGoodsChangeRequest.builder().goodsId(goodsId).build());
            List<String> skuIds = response.getContext().getGoodsInfoVOList()
                    .stream()
                    .map(GoodsInfoVO::getGoodsInfoId)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            esGoodsModifyInventoryService.modifyBulkInventory(skuIds, goodsId);
        });

        operateLogMQUtil.convertAndSend("商品", "批量修改散批类目", "批量修改散批类目: 商品ID:"
                + request.getGoodsIds() + " 类目ID：" + request.getCateId());
        return BaseResponse.SUCCESSFUL();
    }
}
