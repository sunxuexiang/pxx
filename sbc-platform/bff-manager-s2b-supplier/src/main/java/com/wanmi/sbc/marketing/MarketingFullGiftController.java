package com.wanmi.sbc.marketing;


import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.EnumTranslateUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;

import com.wanmi.sbc.es.elastic.EsGoodsModifyInventoryService;
import com.wanmi.sbc.marketing.api.provider.gift.FullGiftProvider;
import com.wanmi.sbc.marketing.api.provider.gift.FullGiftQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftAddRequest;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftLevelListByMarketingIdAndCustomerRequest;
import com.wanmi.sbc.marketing.api.request.gift.FullGiftModifyRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingGetByIdRequest;
import com.wanmi.sbc.marketing.api.request.market.latest.*;
import com.wanmi.sbc.marketing.api.response.gift.FullGiftLevelListByMarketingIdAndCustomerResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingResponse;
import com.wanmi.sbc.marketing.bean.dto.FullGiftDetailDTO;
import com.wanmi.sbc.marketing.bean.dto.FullGiftLevelDTO;
import com.wanmi.sbc.marketing.bean.dto.FullReductionActivitiesDTO;
import com.wanmi.sbc.marketing.bean.enums.MarketingSubType;
import com.wanmi.sbc.marketing.bean.vo.MarketingVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 满赠
 */
@Api(tags = "MarketingFullGiftController", description = "满赠服务API")
@RestController
@RequestMapping("/marketing/fullGift")
@Validated
public class MarketingFullGiftController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private FullGiftProvider fullGiftProvider;

    @Autowired
    private FullGiftQueryProvider fullGiftQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    /**
     * 新增满赠营销信息
     * @param request
     * @return
     */
    @ApiOperation(value = "新增满赠营销信息")
    @RequestMapping(method = RequestMethod.POST)
    public BaseResponse add(@Valid @RequestBody FullGiftAddRequest request) {
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

        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = this.fullGiftAddRequestAdapter(request);
        fullGiftProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);
        
        operateLogMQUtil.convertAndSend("营销","创建满赠活动", EnumTranslateUtil.getFieldAnnotation(request.getSubType())
                .get(request.getSubType().name()) +"-"+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改满赠营销信息
     * @param request
     * @return
     */
    @ApiOperation(value = "修改满赠活动草稿")
    @RequestMapping(method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody FullGiftModifyRequest request) {
        request.valid();

        request.setUpdatePerson(commonUtil.getOperatorId());

        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = fullGiftModifyRequestAdapter(request);
        fullGiftProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);

        operateLogMQUtil.convertAndSend("营销","编辑促销活动","编辑促销活动："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 保存草稿
     * @param request
     * @return
     */
    @ApiOperation(value = "满赠保存为草稿")
    @RequestMapping(path = "/draft", method = RequestMethod.POST)
    public BaseResponse draft(@Valid @RequestBody FullGiftAddRequest request) {
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

        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = this.fullGiftAddRequestAdapter(request);
        saveOrUpdateMarketingRequest.setIsDraft(BoolFlag.YES);
        fullGiftProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);

        operateLogMQUtil.convertAndSend("营销","保存营销活动草稿","保存营销活动草稿："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 保存草稿
     * @param request
     * @return
     */
    @ApiOperation(value = "满赠保存为草稿")
    @RequestMapping(path = "/draft", method = RequestMethod.PUT)
    public BaseResponse updateDraft(@Valid @RequestBody FullGiftAddRequest request) {
        request.valid();

        request.setUpdatePerson(commonUtil.getOperatorId());

        SaveOrUpdateMarketingRequest saveOrUpdateMarketingRequest = this.fullGiftAddRequestAdapter(request);
        saveOrUpdateMarketingRequest.setIsDraft(BoolFlag.YES);
        fullGiftProvider.saveOrUpdateMarketing(saveOrUpdateMarketingRequest);

        operateLogMQUtil.convertAndSend("营销","更新营销活动草稿","更新营销活动草稿："+request.getMarketingName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 根据营销Id获取赠品信息
     * @param request 参数
     * @return
     */
    @ApiOperation(value = "根据营销Id获取赠品信息")
    @RequestMapping(value = "/giftList", method = RequestMethod.POST)
    public BaseResponse<FullGiftLevelListByMarketingIdAndCustomerResponse> getGiftByMarketingId(@Valid @RequestBody FullGiftLevelListByMarketingIdAndCustomerRequest request) {
        CustomerGetByIdResponse customer = null;
        if(StringUtils.isNotBlank(request.getCustomerId())){
            customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(request.getCustomerId())).getContext();
        }
        request.setCustomer(KsBeanUtil.convert(customer,CustomerDTO.class));

        return fullGiftQueryProvider.listGiftByMarketingIdAndCustomerBoss(request);
    }

    /**
     * 新老接口参数适配
     *
     * @param request
     * @return
     */
    private SaveOrUpdateMarketingRequest fullGiftAddRequestAdapter(FullGiftAddRequest request){
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
        List<FullGiftLevelDTO> fullGiftLevelList = request.getFullGiftLevelList();
        List<ReachGiftLevelRequest> requests = new ArrayList<>();
        for (FullGiftLevelDTO levelDTO : fullGiftLevelList) {
            ReachGiftLevelRequest reachGiftLevelRequest = new ReachGiftLevelRequest();

            List<FullGiftDetailDTO> fullGiftDetailList = levelDTO.getFullGiftDetailList();
            List<MarketingGiveGoodItemRequest> marketingGiveGoodItemRequests = new ArrayList<>();
            for (FullGiftDetailDTO fullGiftDetailDTO : fullGiftDetailList) {
                MarketingGiveGoodItemRequest marketingGiveGoodItemRequest = new MarketingGiveGoodItemRequest();
                marketingGiveGoodItemRequest.setSkuId(fullGiftDetailDTO.getProductId());
                marketingGiveGoodItemRequest.setBoundsNum(fullGiftDetailDTO.getBoundsNum());
                marketingGiveGoodItemRequest.setProductNum(fullGiftDetailDTO.getProductNum());
                marketingGiveGoodItemRequests.add(marketingGiveGoodItemRequest);
            }
            reachGiftLevelRequest.setMarketingGiveGoodItemRequest(marketingGiveGoodItemRequests);

            reachGiftLevelRequest.setGiftType(levelDTO.getGiftType());
            MarketingSubType subType = request.getSubType();
            if(subType == MarketingSubType.GIFT_FULL_COUNT){
                reachGiftLevelRequest.setThreshold(BigDecimal.valueOf(levelDTO.getFullCount()));
            } else if (subType == MarketingSubType.GIFT_FULL_AMOUNT){
                reachGiftLevelRequest.setThreshold(levelDTO.getFullAmount());
            }

            requests.add(reachGiftLevelRequest);
        }
        saveOrUpdateMarketingRequest.setReachGiftLevelRequests(requests);

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


    private SaveOrUpdateMarketingRequest fullGiftModifyRequestAdapter(FullGiftModifyRequest request){
        FullGiftAddRequest fullGiftAddRequest = KsBeanUtil.convert(request, FullGiftAddRequest.class);
        return fullGiftAddRequestAdapter(fullGiftAddRequest);
    }
}
