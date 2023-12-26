package com.wanmi.sbc.goods.provider.impl.info;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.WareHouseType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.response.info.*;
import com.wanmi.sbc.goods.ares.GoodsAresService;
import com.wanmi.sbc.goods.bean.dto.DistributionGoodsInfoModifyDTO;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoPresellRecordDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.DistributionGoodsAudit;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.goods.devanninggoodsinfo.service.DevanningGoodsInfoService;
import com.wanmi.sbc.goods.distributor.goods.service.DistributorGoodsInfoService;
import com.wanmi.sbc.goods.goodswarestock.model.root.GoodsWareStock;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockService;
import com.wanmi.sbc.goods.goodswarestock.service.GoodsWareStockVillagesService;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.repository.GoodsInfoPresellRecordRepository;
import com.wanmi.sbc.goods.info.request.GoodsInfoSaveRequest;
import com.wanmi.sbc.goods.info.request.SpecialGoodsInfoSaveRequest;
import com.wanmi.sbc.goods.info.service.GoodsInfoService;
import com.wanmi.sbc.goods.price.model.root.GoodsCustomerPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsIntervalPrice;
import com.wanmi.sbc.goods.price.model.root.GoodsLevelPrice;
import com.wanmi.sbc.goods.warehouse.service.WareHouseService;
import com.wanmi.sbc.order.bean.vo.TradeItemVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>对商品info操作接口</p>
 * Created by daiyitian on 2018-11-5-下午6:23.
 */
@RestController
@Validated
@Slf4j
public class GoodsInfoController implements GoodsInfoProvider {

    @Autowired
    private GoodsInfoService goodsInfoService;

    @Autowired
    private DevanningGoodsInfoService devanningGoodsInfoService;

    @Autowired
    private GoodsAresService goodsAresService;

    @Autowired
    private DistributorGoodsInfoService distributorGoodsInfoService;

    @Autowired
    private GoodsWareStockService goodsWareStockService;

    @Autowired
    private GoodsWareStockVillagesService goodsWareStockVillagesService;

    @Autowired
    private WareHouseService wareHouseService;

    @Autowired
    private GoodsInfoPresellRecordRepository goodsInfoPresellRecordRepository;


    /**
     * 根据商品sku编号批量删除商品sku信息
     *
     * @param request 包含商品sku编号商品sku信息删除结构 {@link GoodsInfoDeleteByIdsRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override

    public BaseResponse deleteByIds(@RequestBody @Valid GoodsInfoDeleteByIdsRequest request){
        goodsInfoService.delete(request.getGoodsInfoIds());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改商品sku信息
     *
     * @param request 商品sku信息修改结构 {@link GoodsInfoModifyRequest}
     * @return 商品sku信息 {@link GoodsInfoModifyResponse}
     */
    @Override

    public BaseResponse<GoodsInfoModifyResponse> modify(@RequestBody @Valid GoodsInfoModifyRequest request){
        GoodsInfo info = KsBeanUtil.convert(request.getGoodsInfo(), GoodsInfo.class);
        GoodsInfoSaveRequest saveRequest = new GoodsInfoSaveRequest();
        saveRequest.setGoodsInfo(info);
        info = goodsInfoService.edit(saveRequest);
//        devanningGoodsInfoService.edit(saveRequest);
//        Long stock = info.getGoodsWareStocks().stream().collect(Collectors.summingLong(GoodsWareStock::getStock));
//        if (info.getGoods().getAddedFlag() == AddedFlag.YES.toValue() && info.getStock() > 0) {
//            goodsService.sendMessage(info, stock);
//        }

        //ares埋点-商品-后台修改单个商品sku
        goodsAresService.dispatchFunction("editOneGoodsSku",new Object[]{info});

        GoodsInfoModifyResponse response = new GoodsInfoModifyResponse();
        KsBeanUtil.copyPropertiesThird(info, response);
        return BaseResponse.success(response);
    }

    /**
     * 修改商品sku设价信息
     *
     * @param request 商品sku设价信息修改结构 {@link GoodsInfoPriceModifyRequest}
     * @return 商品sku设价信息 {@link GoodsInfoPriceModifyResponse}
     */
    @Override

    public BaseResponse<GoodsInfoPriceModifyResponse> modifyPrice(@RequestBody @Valid GoodsInfoPriceModifyRequest
                                                                              request){
        GoodsInfoSaveRequest saveRequest = new GoodsInfoSaveRequest();
        GoodsInfo info = new GoodsInfo();
        KsBeanUtil.copyPropertiesThird(request.getGoodsInfo(), info);
        saveRequest.setGoodsInfo(info);
        //等级设价
        if(CollectionUtils.isNotEmpty(request.getGoodsLevelPrices())) {
            saveRequest.setGoodsLevelPrices(KsBeanUtil.convert(request.getGoodsLevelPrices(), GoodsLevelPrice.class));
        }
        //客户设价
        if(CollectionUtils.isNotEmpty(request.getGoodsCustomerPrices())) {
            saveRequest.setGoodsCustomerPrices(KsBeanUtil.convert(request.getGoodsCustomerPrices(), GoodsCustomerPrice.class));
        }
        //区间设价
        if(CollectionUtils.isNotEmpty(request.getGoodsIntervalPrices())) {
            saveRequest.setGoodsIntervalPrices(KsBeanUtil.convert(request.getGoodsIntervalPrices(), GoodsIntervalPrice.class));
        }

        info = goodsInfoService.editPrice(saveRequest);
        //ares埋点-商品-后台修改单个商品sku
        goodsAresService.dispatchFunction("editOneGoodsSku",new Object[]{info});

        GoodsInfoPriceModifyResponse response = new GoodsInfoPriceModifyResponse();
        KsBeanUtil.copyPropertiesThird(info, response);
        return BaseResponse.success(response);
    }

    /**
     * 修改商品sku特价信息
     *
     * @param request 商品sku设价信息修改结构 {@link SpecialGoodsModifyRequest}
     * @return 商品sku设价信息 {@link SpecialGoodsModifyResponse}
     */
    @Override

    public BaseResponse<SpecialGoodsModifyResponse> setSpecialPrice(@RequestBody @Valid SpecialGoodsModifyRequest
                                                                          request){
        SpecialGoodsInfoSaveRequest saveRequest = new SpecialGoodsInfoSaveRequest();
        BigDecimal disCount = new BigDecimal(request.getGoodDiscount());
        com.wanmi.sbc.goods.info.reponse.GoodsInfoResponse goodsInfoResponse = new
                com.wanmi.sbc.goods.info.reponse.GoodsInfoResponse();
        if(!request.getGoodsInfoIdList().isEmpty()){
            goodsInfoResponse = goodsInfoService.findGoodInfoByIds(request);
            //判断是否存在非特价商品
            if (CollectionUtils.isNotEmpty(goodsInfoResponse.getGoodsInfos()
                    .stream()
                    .filter(goodsInfo -> Objects.isNull(goodsInfo.getGoodsInfoType()) || goodsInfo.getGoodsInfoType() != 1)
                    .collect(Collectors.toList()))) {
                throw new SbcRuntimeException(CommonErrorCode.FAILED, "参数错误");
            }
        }
        if(!request.getGoodDiscount().isEmpty()&&!goodsInfoResponse.getGoodsInfos().isEmpty()){
            for(int i=0;i<goodsInfoResponse.getGoodsInfos().size();i++){

                goodsInfoResponse.getGoodsInfos().get(i).setSpecialPrice(goodsInfoResponse.getGoodsInfos().get(i)
                        .getMarketPrice().multiply(disCount));
            }
        }
        saveRequest.setGoodsInfoList(goodsInfoResponse.getGoodsInfos());
        List<GoodsInfo> goodsInfos = goodsInfoService.editSpeciaPrice(saveRequest);
        List<GoodsInfoVO> goodsInfoVOS = new ArrayList<>();
        SpecialGoodsModifyResponse response = new SpecialGoodsModifyResponse();

        KsBeanUtil.copyList(goodsInfos,goodsInfoVOS);
        response.setGoodsInfoList(goodsInfoVOS);
        return BaseResponse.success(response);
    }

    /**
     * 修改商品sku上下架
     *
     * @param request 商品上下架修改结构 {@link GoodsInfoModifyAddedStatusRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override

    public BaseResponse modifyAddedStatus(@RequestBody @Valid GoodsInfoModifyAddedStatusRequest request){
        goodsInfoService.updateAddedStatus(request.getAddedFlag(), request.getGoodsInfoIds());
        return BaseResponse.SUCCESSFUL();
    }
    /**
     * 修改商品sku上下架
     *
     * @param request 商品上下架修改结构 {@link GoodsInfoModifyAddedStatusRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override

    public BaseResponse storeModifyAddedStatus(@RequestBody @Valid GoodsInfoModifyAddedStatusRequest request){
        goodsInfoService.updateAddedStatus(request.getAddedFlag(), request.getGoodsInfoIds());
        return BaseResponse.SUCCESSFUL();
    }
    /**
     * 根据商品skuId增加商品sku库存
     *
     * @param request 包含skuId的商品sku库存增量结构 {@link GoodsInfoPlusStockByIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override

    public BaseResponse plusStockById(@RequestBody @Valid GoodsInfoPlusStockByIdRequest request){
        goodsInfoService.addStockById(BigDecimal.valueOf(request.getStock()), request.getGoodsInfoId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量增量商品sku库存
     *
     * @param request 包含多个库存的sku库存增量结构 {@link GoodsInfoBatchPlusStockRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse batchPlusStock(@RequestBody @Valid GoodsInfoBatchPlusStockRequest request){
        goodsWareStockService.batchPlusStock(request.getStockList(),request.getWareId());
//        goodsInfoService.batchAddStock(request.getStockList());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchVillagesPlusStock(GoodsInfoBatchPlusStockRequest request) {
        goodsWareStockVillagesService.batchPlusStock(request.getStockList(), request.getWareId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据商品skuId扣除商品sku库存
     *
     * @param request 包含skuId的商品sku库存减量结构 {@link GoodsInfoMinusStockByIdRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override

    public BaseResponse minusStockById(@RequestBody @Valid GoodsInfoMinusStockByIdRequest request){
        goodsInfoService.subStockById(BigDecimal.valueOf(request.getStock()), request.getGoodsInfoId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量扣除商品sku库存
     *
     * @param request 包含多个库存的sku库存减量结构 {@link GoodsInfoBatchMinusStockRequest}
     * @return 操作结果 {@link BaseResponse}
     */
    @Override
    public BaseResponse batchMinusStock(@RequestBody @Valid GoodsInfoBatchMinusStockRequest request){
        goodsWareStockService.batchSubStockNew(request.getStockList(),request.getWareId(),request.isNeedUpdateLockStock());
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse batchVillagesMinusStock(GoodsInfoBatchMinusStockRequest request) {
        goodsWareStockVillagesService.batchSubStock(request.getStockList(), request.getWareId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据库存状态/上下架状态/相关店铺状态来填充商品数据的有效性
     *
     * @param request 商品列表数据结构 {@link GoodsInfoFillGoodsStatusRequest}
     * @return 包含商品有效状态的商品列表数据 {@link GoodsInfoFillGoodsStatusResponse}
     */
    @Override

    public BaseResponse<GoodsInfoFillGoodsStatusResponse> fillGoodsStatus(@RequestBody @Valid
                                                                           GoodsInfoFillGoodsStatusRequest request){
        List<GoodsInfo> goodsInfoList =
                goodsInfoService.fillGoodsStatus(KsBeanUtil.convert(request.getGoodsInfos(), GoodsInfo.class));

//        Map<String, Long> goodsNumsMap = goodsInfoService.getGoodsPileNumBySkuIds(goodsInfoList.stream()
//                .map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
        //获取对应的商品分仓库存
        List<GoodsWareStock> goodsWareStockVOList=goodsWareStockService.findByGoodsInfoIdIn(goodsInfoList.stream()
                .map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
        log.info("传输库存------------------------------------"+goodsInfoList.stream()
                .map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));
        Map<String, BigDecimal> getskusstock = goodsWareStockService.getskusstock(goodsInfoList.stream()
                .map(GoodsInfo::getGoodsInfoId).collect(Collectors.toList()));


        List<Long> unOnline =new ArrayList<>(10);
        //如果不能匹配到仓，需要去除线上仓
        if (Objects.nonNull(request.getMatchWareHouseFlag())&&!request.getMatchWareHouseFlag()){
            List<Long> storeList = goodsWareStockVOList.stream().map(GoodsWareStock::getStoreId).distinct().collect(Collectors.toList());
            unOnline=wareHouseService.queryWareHouses(storeList, WareHouseType.STORRWAREHOUSE)
                    .stream().map(WareHouseVO::getWareId).collect(Collectors.toList());
        }
        if(CollectionUtils.isNotEmpty(goodsWareStockVOList)){
            for (GoodsInfo g : goodsInfoList) {
                List<GoodsWareStock> stockList;
                if (CollectionUtils.isNotEmpty(unOnline)) {
                    List<Long> finalUnOnline = unOnline;
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())
                                    && finalUnOnline.contains(goodsWareStock.getWareId())).
                            collect(Collectors.toList());
                } else {
                    stockList = goodsWareStockVOList.stream().
                            filter(goodsWareStock -> g.getGoodsInfoId().equals(goodsWareStock.getGoodsInfoId())).
                            collect(Collectors.toList());
                }

                if (CollectionUtils.isNotEmpty(stockList)) {
                    BigDecimal sumStock = stockList.stream().map(GoodsWareStock::getStock).reduce(BigDecimal.ZERO,BigDecimal::add);
                    g.setStock(sumStock);
                    g.setStock(getskusstock.getOrDefault(g.getGoodsInfoId(),BigDecimal.ZERO));


                } else {
                    g.setStock(BigDecimal.ZERO);
                }
                //计算库存 加上虚拟库存 减去囤货数量
//                goodsInfoService.calGoodsInfoStock(g,goodsNumsMap);
                if (Objects.nonNull(g.getMarketingId()) && Objects.nonNull(g.getPurchaseNum()) && g.getPurchaseNum() > 0){
                    g.setStock(BigDecimal.valueOf(g.getPurchaseNum()));
                }
                if (g.getStock().compareTo(BigDecimal.ZERO) <= 0){
                    g.setGoodsStatus(GoodsStatus.OUT_STOCK);
                }
            }
        }
        if(CollectionUtils.isNotEmpty(goodsInfoList)){
            // 商品下架
            goodsInfoList.forEach(goodsInfo -> {
                if(AddedFlag.NO.toValue() == goodsInfo.getAddedFlag()){
                    goodsInfo.setGoodsStatus(GoodsStatus.INVALID);
                }
            });
        }
        return BaseResponse.success(GoodsInfoFillGoodsStatusResponse.builder()
                .goodsInfos(KsBeanUtil.convert(goodsInfoList, GoodsInfoVO.class)).build());
    }

    @Override
    public BaseResponse updateSkuSmallProgram(@RequestBody @Valid
                                               GoodsInfoSmallProgramCodeRequest request){
          goodsInfoService.updateSkuSmallProgram(request);
          return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse clearSkuSmallProgramCode(){
        goodsInfoService.clearSkuSmallProgramCode();
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 分销商品审核通过(单个)
     * @param request
     * @return
     */
    @Override
    public BaseResponse checkDistributionGoods(@RequestBody @Valid DistributionGoodsCheckRequest request) {
        goodsInfoService.checkDistributionGoods(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量审核分销商品
     * @param request
     * @return
     */
    @Override
    public BaseResponse batchCheckDistributionGoods(@RequestBody @Valid DistributionGoodsBatchCheckRequest request) {
        goodsInfoService.batchCheckDistributionGoods(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 驳回分销商品
     * @param request
     * @return
     */
    @Override
    public BaseResponse refuseCheckDistributionGoods(@RequestBody @Valid DistributionGoodsRefuseRequest request) {
        goodsInfoService.refuseCheckDistributionGoods(request.getGoodsInfoId(),
                DistributionGoodsAudit.NOT_PASS,
                request.getDistributionGoodsAuditReason());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 禁止分销商品
     * @param request
     * @return
     */
    @Override
    public BaseResponse forbidCheckDistributionGoods(@RequestBody @Valid DistributionGoodsForbidRequest request) {
        goodsInfoService.refuseCheckDistributionGoods(request.getGoodsInfoId(),DistributionGoodsAudit.FORBID,
                request.getDistributionGoodsAuditReason());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除分销商品
     * @param request
     * @return
     */
    @Override
    public BaseResponse delDistributionGoods(@RequestBody @Valid DistributionGoodsDeleteRequest request) {
        goodsInfoService.delDistributionGoods(request);
        // 同步删除分销员与商品关联表
        distributorGoodsInfoService.deleteByGoodsInfoId(request.getGoodsInfoId());

        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 添加分销商品
     * @param request
     * @return
     */
    @Override
    public BaseResponse<DistributionGoodsAddResponse> addDistributionGoods(@RequestBody @Valid DistributionGoodsAddRequest request) {
        DistributionGoodsAddResponse goodsAddResponse = new DistributionGoodsAddResponse();
        if (CollectionUtils.isEmpty(request.getDistributionGoodsInfoModifyDTOS())){
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        List<String> goodsInfoIds = request.getDistributionGoodsInfoModifyDTOS().stream().map(DistributionGoodsInfoModifyDTO :: getGoodsInfoId).collect(Collectors.toList());
        // 添加分销商品前，验证所添加的sku是否符合条件
        List<String> invalidList = goodsInfoService.getInvalidGoodsInfoByGoodsInfoIds(goodsInfoIds);
        if (CollectionUtils.isNotEmpty(invalidList)){
            goodsAddResponse.setGoodsInfoIds(invalidList);
        }else {
            for (DistributionGoodsInfoModifyDTO modifyDTO : request.getDistributionGoodsInfoModifyDTOS()) {
                goodsInfoService.modifyCommissionDistributionGoods(modifyDTO.getGoodsInfoId(), modifyDTO
                                .getCommissionRate(),modifyDTO.getDistributionCommission()
                        , request.getDistributionGoodsAudit());
            }
        }
        return BaseResponse.success(goodsAddResponse);
    }

    /**
     * 已审核通过 编辑分销商品佣金
     * @param request
     * @return
     */
    @Override
    public BaseResponse modifyDistributionGoodsCommission(@RequestBody @Valid DistributionGoodsModifyRequest request) {
        goodsInfoService.modifyCommissionDistributionGoods(request.getGoodsInfoId(),request.getCommissionRate(),
                request.getDistributionCommission(), DistributionGoodsAudit.CHECKED);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 审核未通过或禁止分销的商品重新编辑后，状态为待审核
     * @param request
     * @return
     */
    @Override
    public BaseResponse modifyDistributionGoods(@RequestBody @Valid DistributionGoodsModifyRequest request) {
        goodsInfoService.modifyCommissionDistributionGoods(request.getGoodsInfoId(),request.getCommissionRate()
                ,request.getDistributionCommission(), DistributionGoodsAudit.WAIT_CHECK);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 商品ID<spu> 修改商品审核状态
     *
     * @param request
     */
    @Override
    public BaseResponse distributeTogeneralGoods(@RequestBody @Valid DistributionGoodsChangeRequest request) {
        goodsInfoService.modifyDistributeState(request.getGoodsId(), DistributionGoodsAudit.COMMON_GOODS);
        distributorGoodsInfoService.deleteByGoodsId(request.getGoodsId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     *  添加特价sku
     *
     * @param request
     */
    @Override
    public BaseResponse<SpecialGoodsAddRequest> addSpecialGoods(@RequestBody @Valid SpecialGoodsAddRequest request) {
        SpecialGoodsAddRequest specialGoodsAddRequest = new SpecialGoodsAddRequest();
        List<GoodsInfo> goodsInfoList = new ArrayList<>();
        request.getGoodsInfoVOS().stream().forEach(goodsInfoVO -> {
            GoodsInfo goodsInfo = KsBeanUtil.copyPropertiesThird(goodsInfoVO,GoodsInfo.class);
            GoodsInfo goodsInfoBack = goodsInfoService.addSpecialGoods(goodsInfo);
            goodsInfoList.add(goodsInfoBack);
        });
        specialGoodsAddRequest.setGoodsInfoVOS(KsBeanUtil.convertList(goodsInfoList,GoodsInfoVO.class));
        return  BaseResponse.success(specialGoodsAddRequest);
    }


    @Override
    public BaseResponse batchUpdateBatchNos(@RequestBody @Valid GoodsInfoBatchNosModifyRequest request) {
        return goodsInfoService.batchUpdateGoodsInfoBatchNo(request.getGoodsBatchNoDTOS());
    }

    @Override
    public BaseResponse updateGoodsInfoPurchaseNum(UpdateGoodsInfoPurchaseNumRequest request) {
        List<GoodsInfoPurchaseNumDTO> goodsInfoPurchaseNumDTOS = request.getGoodsInfoPurchaseNumDTOS();
        //所有需要操作的sku集合
        List<String> goodsInfoIds = request.getGoodsInfoPurchaseNumDTOS().stream().map(GoodsInfoPurchaseNumDTO::getGoodsInfoId).collect(Collectors.toList());
        List<GoodsInfo> goodsInfos = goodsInfoService.queryByGoodsInfoIds(goodsInfoIds);
        if (CollectionUtils.isNotEmpty(goodsInfoIds)) {
            //添加
            if (request.getB()) {
                goodsInfos.forEach(goodsInfo -> {
                    for (GoodsInfoPurchaseNumDTO goodsInfoPurchaseNumDTO : goodsInfoPurchaseNumDTOS) {
                        if (goodsInfo.getGoodsInfoId().equals(goodsInfoPurchaseNumDTO.getGoodsInfoId())) {
                            goodsInfo.setPurchaseNum(goodsInfoPurchaseNumDTO.getPurchaseNum());
                            goodsInfo.setMarketingId(goodsInfoPurchaseNumDTO.getMarketingId());
                            break;
                        }
                    }
                });
            } else {
                //删除
                goodsInfos.forEach(goodsInfo -> {
                    for (GoodsInfoPurchaseNumDTO goodsInfoPurchaseNumDTO : goodsInfoPurchaseNumDTOS) {
                        if (goodsInfo.getGoodsInfoId().equals(goodsInfoPurchaseNumDTO.getGoodsInfoId())) {
                            goodsInfo.setPurchaseNum(-1L);
                            goodsInfo.setMarketingId(-1L);
                            break;
                        }
                    }
                });
            }
            goodsInfoService.saveAll(goodsInfos);
        }
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse modifyRecommendSort(List<GoodsInfoModifyRecommendSortRequest> request) {
        goodsInfoService.modifyRecommendSort(request);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse clearAllRecommendSort(Long wareId) {
        goodsInfoService.clearAllRecommendSort(wareId);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse<Map<String,Integer>> lockStock(List<GoodsInfoLockStockRequest> requests) {
        Map<String, Integer> resultMap;
        try {
             resultMap = goodsInfoService.lockStock(requests);
        } catch (Exception ex) {
            ex.printStackTrace();
            return BaseResponse.error("锁定库存失败！");
        }
        return BaseResponse.success(resultMap);
    }

    @Override
    public BaseResponse<Integer> unlockStock(List<GoodsInfoUnlockStockRequest> requests) {
        try {
            int unlockCount = goodsInfoService.unlockStock(requests);
            return BaseResponse.success(unlockCount);
        } catch (Exception ex) {
            return BaseResponse.success(0);
        }
    }

    @Override
    public BaseResponse<Boolean> checkIsLocked(String businessId) {
        return BaseResponse.success(goodsInfoService.checkIsLocked(businessId));
    }

    @Override
    public BaseResponse updatePresellGoodsInfoStock(List<GoodsInfoPresellRecordDTO> recordDTOList) {
        goodsInfoService.updatePresellGoodsInfoStock(recordDTOList);
        return BaseResponse.SUCCESSFUL();
    }
}
