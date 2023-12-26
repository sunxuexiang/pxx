package com.wanmi.sbc.marketing;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.EnumTranslateUtil;
import com.wanmi.sbc.es.elastic.EsGoodsModifyInventoryService;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullReductionProvider;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionAddRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionModifyRequest;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftAddRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingGoodsItemRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.ReachAmountLevelRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingResponse;
import com.wanmi.sbc.marketing.bean.dto.FullReductionActivitiesDTO;
import com.wanmi.sbc.marketing.bean.dto.MarketingFullReductionLevelDTO;
import com.wanmi.sbc.marketing.bean.dto.MarketingFullReductionSaveDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 满减
 */
@Api(tags = "MarketingFullReductionController", description = "满减服务API")
@RestController
@RequestMapping("/marketing/fullReduction")
@Validated
@Slf4j
public class MarketingFullReductionController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private MarketingFullReductionProvider marketingFullReductionProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 新增满减营销信息
     * @param request
     * @return
     */
    @ApiOperation(value = "新增满减营销信息")
    @RequestMapping(method = RequestMethod.POST)
    public BaseResponse add(@Valid @RequestBody MarketingFullReductionAddRequest request) {

        request.valid();

        request.setIsBoss(BoolFlag.NO);
        request.setStoreId(commonUtil.getStoreId());
        request.setCreatePerson(commonUtil.getOperatorId());
        if (Objects.isNull(request.getIsOverlap())) {
            request.setIsOverlap(BoolFlag.NO);
        }
        if(Objects.isNull(request.getIsAddMarketingName())){
            request.setIsAddMarketingName(BoolFlag.NO);
        }

        log.info("-------营销活动新增开始：" + LocalDateTime.now());
        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = this.requestParameterAdapter(request);
        marketingFullReductionProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);
        log.info("-------营销活动新增结束：" + LocalDateTime.now());

        operateLogMQUtil.convertAndSend("营销","创建满减活动", EnumTranslateUtil.getFieldAnnotation(request.getSubType())
                .get(request.getSubType().name()) +"-"+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改满减营销信息
     * @param request
     * @return
     */
    @ApiOperation(value = "修改满减营销信息")
    @RequestMapping(method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody MarketingFullReductionModifyRequest request) {
        request.valid();

        request.setUpdatePerson(commonUtil.getOperatorId());

        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = requestParameterAdapter(request);
        marketingFullReductionProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);

        operateLogMQUtil.convertAndSend("营销","编辑促销活动","编辑促销活动："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 保存草稿
     * @param request
     * @return
     */
    @ApiOperation(value = "满减保存为草稿")
    @RequestMapping(path = "/draft", method = RequestMethod.POST)
    public BaseResponse draft(@Valid @RequestBody MarketingFullReductionAddRequest request) {
        request.valid();

        request.setIsBoss(BoolFlag.NO);
        request.setStoreId(commonUtil.getStoreId());
        request.setCreatePerson(commonUtil.getOperatorId());
        if (Objects.isNull(request.getIsOverlap())) {
            request.setIsOverlap(BoolFlag.NO);
        }
        if(Objects.isNull(request.getIsAddMarketingName())){
            request.setIsAddMarketingName(BoolFlag.NO);
        }

        log.info("-------营销活动新增开始：" + LocalDateTime.now());
        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = this.requestParameterAdapter(request);
        saveOrUpdateMarketingRequest.setIsDraft(BoolFlag.YES);
        marketingFullReductionProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);
        log.info("-------营销活动新增结束：" + LocalDateTime.now());

        operateLogMQUtil.convertAndSend("营销","保存营销活动草稿","保存营销活动草稿："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 保存草稿
     * @param request
     * @return
     */
    @ApiOperation(value = "修改满减活动草稿")
    @RequestMapping(path = "/draft", method = RequestMethod.PUT)
    public BaseResponse updateDraft(@Valid @RequestBody MarketingFullReductionAddRequest request) {
        request.valid();

        request.setUpdatePerson(commonUtil.getOperatorId());

        log.info("-------营销活动新增开始：" + LocalDateTime.now());
        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = this.requestParameterAdapter(request);
        saveOrUpdateMarketingRequest.setIsDraft(BoolFlag.YES);
        marketingFullReductionProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);
        log.info("-------营销活动新增结束：" + LocalDateTime.now());

        operateLogMQUtil.convertAndSend("营销","编辑营销活动草稿","编辑营销活动草稿："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 新老接口参数适配
     *
     * @param request
     * @return
     */
    public SaveOrUpdateMarketingRequest requestParameterAdapter(MarketingFullReductionSaveDTO request){
        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = new SaveOrUpdateMarketingRequest();
        saveOrUpdateMarketingRequest.setMarketingId(request.getMarketingId());
        saveOrUpdateMarketingRequest.setMarketingName(request.getMarketingName());
        saveOrUpdateMarketingRequest.setMarketingType(request.getMarketingType());
        saveOrUpdateMarketingRequest.setSubType(request.getSubType());
        saveOrUpdateMarketingRequest.setBeginTime(request.getBeginTime());
        saveOrUpdateMarketingRequest.setEndTime(request.getEndTime());
        saveOrUpdateMarketingRequest.setScopeType(request.getScopeType());
        saveOrUpdateMarketingRequest.setJoinLevel(request.getJoinLevel()); // 0代表全部等级
        saveOrUpdateMarketingRequest.setIsBoss(request.getIsBoss());
        saveOrUpdateMarketingRequest.setStoreId(request.getStoreId());
        saveOrUpdateMarketingRequest.setIsOverlap(request.getIsOverlap());
        saveOrUpdateMarketingRequest.setIsAddMarketingName(request.getIsAddMarketingName());
        saveOrUpdateMarketingRequest.setCreatePerson(request.getCreatePerson());
        saveOrUpdateMarketingRequest.setUpdatePerson(request.getUpdatePerson());
        saveOrUpdateMarketingRequest.setDeletePerson(request.getDeletePerson());
        saveOrUpdateMarketingRequest.setWareId(request.getWareId());
        saveOrUpdateMarketingRequest.setMarketingType(request.getMarketingType());
        saveOrUpdateMarketingRequest.setIsAddMarketingName(request.getIsAddMarketingName());
        saveOrUpdateMarketingRequest.setIsDraft(request.getIsDraft());

        // 格式化营销满赠等级
        List<MarketingFullReductionLevelDTO> fullReductionLevelList = request.getFullReductionLevelList();
        List<ReachAmountLevelRequest> requests = new ArrayList<>();

        for (MarketingFullReductionLevelDTO levelDTO : fullReductionLevelList) {
            ReachAmountLevelRequest reachAmountLevelRequest = new ReachAmountLevelRequest();
            MarketingSubType subType = request.getSubType();
            if(subType == MarketingSubType.REDUCTION_FULL_COUNT){
                reachAmountLevelRequest.setThreshold(BigDecimal.valueOf(levelDTO.getFullCount()));
            } else if (subType == MarketingSubType.REDUCTION_FULL_AMOUNT){
                reachAmountLevelRequest.setThreshold(levelDTO.getFullAmount());
            }
            reachAmountLevelRequest.setReduceAmount(levelDTO.getReduction());
            requests.add(reachAmountLevelRequest);
        }
        saveOrUpdateMarketingRequest.setReachAmountLevelRequest(requests);

        // 关联商品列表
        List<FullReductionActivitiesDTO> bundleSalesSkuIds = request.getBundleSalesSkuIds();
        List<MarketingGoodsItemRequest>  marketingGoodsItemRequests = new ArrayList<>();
        for (FullReductionActivitiesDTO fullReductionActivitiesDTO : bundleSalesSkuIds) {
            MarketingGoodsItemRequest goodsItemRequest = new MarketingGoodsItemRequest();

            goodsItemRequest.setSkuId(fullReductionActivitiesDTO.getSkuIds());
            goodsItemRequest.setPurchaseNum(fullReductionActivitiesDTO.getPurchaseNum());
            goodsItemRequest.setPerUserPurchaseNum(fullReductionActivitiesDTO.getPerUserPurchaseNum());
            goodsItemRequest.setWhetherChoice(fullReductionActivitiesDTO.getWhetherChoice());

            marketingGoodsItemRequests.add(goodsItemRequest);
        }
        saveOrUpdateMarketingRequest.setGoodsItemRequest(marketingGoodsItemRequests);

        return saveOrUpdateMarketingRequest;
    }
}
