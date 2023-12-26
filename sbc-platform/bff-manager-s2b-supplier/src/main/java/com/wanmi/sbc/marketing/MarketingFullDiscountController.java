package com.wanmi.sbc.marketing;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;

import com.wanmi.sbc.common.util.EnumTranslateUtil;
import com.wanmi.sbc.es.elastic.EsGoodsModifyInventoryService;
import com.wanmi.sbc.marketing.api.provider.discount.MarketingFullDiscountProvider;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountAddRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullDiscountModifyRequest;
import com.wanmi.sbc.marketing.api.request.discount.MarketingFullReductionAddRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.SaveOrUpdateMarketingRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.MarketingGoodsItemRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.ReachDiscountLevelRequest;
import com.wanmi.sbc.marketing.api.response.market.MarketingResponse;
import com.wanmi.sbc.marketing.bean.dto.FullReductionActivitiesDTO;
import com.wanmi.sbc.marketing.bean.dto.MarketingFullDiscountLevelDTO;
import com.wanmi.sbc.marketing.bean.dto.MarketingFullDiscountSaveDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

/**
 * 满折
 */
@Api(tags = "MarketingFullDiscountController", description = "满折服务API")
@RestController
@RequestMapping("/marketing/fullDiscount")
@Validated
public class MarketingFullDiscountController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private MarketingFullDiscountProvider marketingFullDiscountProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 发布营销活动
     * @param request
     * @return
     */
    @ApiOperation(value = "新增满折营销信息")
    @RequestMapping(method = RequestMethod.POST)
    public BaseResponse add(@Valid @RequestBody MarketingFullDiscountAddRequest request) {
        request.valid();

        request.setIsBoss(BoolFlag.NO);
        request.setStoreId(commonUtil.getStoreId());
        request.setCreatePerson(commonUtil.getOperatorId());

        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = this.requestParameterAdapter(request);
        marketingFullDiscountProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);

        operateLogMQUtil.convertAndSend("营销","创建满折活动", EnumTranslateUtil.getFieldAnnotation(saveOrUpdateMarketingRequest.getSubType())
                .get(saveOrUpdateMarketingRequest.getSubType().name())+"-"+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改满折营销信息
     * @param request
     * @return
     */
    @ApiOperation(value = "修改满折营销信息")
    @RequestMapping(method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody MarketingFullDiscountModifyRequest request) {
        request.valid();
        request.setUpdatePerson(commonUtil.getOperatorId());

        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = requestParameterAdapter(request);
        marketingFullDiscountProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);

        operateLogMQUtil.convertAndSend("营销","编辑促销活动","编辑促销活动："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 保存草稿
     * @param request
     * @return
     */
    @ApiOperation(value = "满折保存为草稿")
    @RequestMapping(path = "/draft", method = RequestMethod.POST)
    public BaseResponse draft(@Valid @RequestBody MarketingFullDiscountAddRequest request) {
        request.valid();

        request.setIsBoss(BoolFlag.NO);
        request.setStoreId(commonUtil.getStoreId());
        request.setCreatePerson(commonUtil.getOperatorId());

        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = this.requestParameterAdapter(request);
        saveOrUpdateMarketingRequest.setIsDraft(BoolFlag.YES);
        marketingFullDiscountProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);

        operateLogMQUtil.convertAndSend("营销","保存营销活动草稿","保存营销活动草稿："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 保存草稿
     * @param request
     * @return
     */
    @ApiOperation(value = "修改满折活动草稿")
    @RequestMapping(path = "/draft", method = RequestMethod.PUT)
    public BaseResponse updateDraft(@Valid @RequestBody MarketingFullDiscountAddRequest request) {
        request.valid();

        request.setUpdatePerson(commonUtil.getOperatorId());

        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = this.requestParameterAdapter(request);
        saveOrUpdateMarketingRequest.setIsDraft(BoolFlag.YES);
        marketingFullDiscountProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);

        operateLogMQUtil.convertAndSend("营销","更新营销活动草稿","更新营销活动草稿："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 新老接口参数适配
     *
     * @param request
     * @return
     */
    public SaveOrUpdateMarketingRequest requestParameterAdapter(MarketingFullDiscountSaveDTO request){
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

        // 格式化营销满折等级信息
        List<MarketingFullDiscountLevelDTO> fullDiscountLevelList = request.getFullDiscountLevelList();
        List<ReachDiscountLevelRequest> requests = new ArrayList<>();

        for (MarketingFullDiscountLevelDTO levelDTO : fullDiscountLevelList) {
            ReachDiscountLevelRequest reachDiscountLevelRequest = new ReachDiscountLevelRequest();
            MarketingSubType subType = request.getSubType();
            if(subType == MarketingSubType.DISCOUNT_FULL_COUNT){
                reachDiscountLevelRequest.setThreshold(BigDecimal.valueOf(levelDTO.getFullCount()));
            } else if (subType == MarketingSubType.DISCOUNT_FULL_AMOUNT){
                reachDiscountLevelRequest.setThreshold(levelDTO.getFullAmount());
            }
            reachDiscountLevelRequest.setDiscountRate(levelDTO.getDiscount());
            requests.add(reachDiscountLevelRequest);
        }
        saveOrUpdateMarketingRequest.setReachDiscountLevelRequests(requests);

        // 关联商品列表
        List<FullReductionActivitiesDTO> bundleSalesSkuIds = request.getBundleSalesSkuIds();
        List<MarketingGoodsItemRequest>  marketingGoodsItemRequests = new ArrayList<>();
        for (FullReductionActivitiesDTO fullReductionActivitiesDTO : bundleSalesSkuIds) {
            MarketingGoodsItemRequest goodsItemRequest = new MarketingGoodsItemRequest();

            goodsItemRequest.setSkuId(fullReductionActivitiesDTO.getSkuIds());

            Long purchaseNum = fullReductionActivitiesDTO.getPurchaseNum();
            if(Objects.nonNull(purchaseNum)){
                Long max = Long.max(purchaseNum, 0L);
                goodsItemRequest.setPurchaseNum(max);
            } else {
                goodsItemRequest.setPurchaseNum(purchaseNum);
            }

            Long perUserPurchaseNum = fullReductionActivitiesDTO.getPerUserPurchaseNum();
            if(Objects.nonNull(perUserPurchaseNum)){
                Long max = Long.max(perUserPurchaseNum, 0L);
                goodsItemRequest.setPerUserPurchaseNum(max);
            } else {
                goodsItemRequest.setPerUserPurchaseNum(perUserPurchaseNum);
            }

            goodsItemRequest.setWhetherChoice(fullReductionActivitiesDTO.getWhetherChoice());

            marketingGoodsItemRequests.add(goodsItemRequest);
        }
        saveOrUpdateMarketingRequest.setGoodsItemRequest(marketingGoodsItemRequests);

        return saveOrUpdateMarketingRequest;
    }
}
