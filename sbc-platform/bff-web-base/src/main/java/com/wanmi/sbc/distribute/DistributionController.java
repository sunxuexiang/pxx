package com.wanmi.sbc.distribute;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.distribution.DistributionCustomerQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.DistributionInviteNewByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.distribution.DistributionCustomerByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.distribution.DistributionCustomerEnableByCustomerIdRequest;
import com.wanmi.sbc.customer.api.request.distribution.DistributionCustomerSimByIdRequest;
import com.wanmi.sbc.customer.api.response.distribution.DistributionCustomerByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.distribution.DistributionCustomerEnableByCustomerIdResponse;
import com.wanmi.sbc.customer.api.response.distribution.DistributionCustomerSimByIdResponse;
import com.wanmi.sbc.customer.bean.enums.StoreState;
import com.wanmi.sbc.es.elastic.EsGoods;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.model.root.GoodsInfoNest;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoQueryRequest;
import com.wanmi.sbc.es.elastic.response.EsGoodsResponse;
import com.wanmi.sbc.goods.api.constant.GoodsErrorCode;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.marketing.api.provider.distributionrecord.DistributionRecordQueryProvider;
import com.wanmi.sbc.marketing.api.request.distribution.DistributionSettingByDistributorIdRequest;
import com.wanmi.sbc.marketing.api.request.distributionrecord.DistributionRecordByCustomerIdRequest;
import com.wanmi.sbc.marketing.api.response.distribution.DistributionSetting4StoreBagsResponse;
import com.wanmi.sbc.marketing.api.response.distribution.DistributionSetting4StoreBagsVerifyResponse;
import com.wanmi.sbc.marketing.api.response.distribution.DistributionSimSettingResponse;
import com.wanmi.sbc.marketing.api.response.distributionrecord.DistributionRecordByInviteeIdResponse;
import com.wanmi.sbc.marketing.bean.enums.RecruitApplyType;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Author : baijz
 * @Date : 2019/3/4 14 49
 * @Description : 分销相关的控制器
 */
@Api(tags = "DistributionController", description = "分销相关API")
@RestController
@RequestMapping("/distribute")
public class DistributionController {

    @Autowired
    private DistributionRecordQueryProvider distributionRecordQueryProvider;

    @Autowired
    private DistributionCacheService distributionCacheService;

    @Autowired
    private DistributionCustomerQueryProvider distributionCustomerQueryProvider;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Resource
    private CommonUtil commonUtil;

    /**
     * 查询分销设置信息--首页判断使用
     *
     * @return
     */
    @ApiOperation(value = "查询分销设置信息")
    @RequestMapping(value = "/setting", method = RequestMethod.POST)
    public BaseResponse<DistributionSimSettingResponse> getSetting(@RequestBody @Valid DistributionSettingByDistributorIdRequest request) {
        DistributionCustomerSimByIdResponse simByIdResponse =
                distributionCustomerQueryProvider.getSimInfoById(new DistributionCustomerSimByIdRequest(request.getInviteeId())).getContext();
        DistributionSimSettingResponse response = new DistributionSimSettingResponse();
        response.setDistributionCustomerSimVO(simByIdResponse.getDistributionCustomerSimVO());
        response.setDistributionSettingSimVO(distributionCacheService.getSimDistributionSetting());
        return BaseResponse.success(response);
    }

    /**
     * 查询分销设置和邀请人信息--mobile我的判断使用
     *
     * @return
     */
    @ApiOperation(value = "查询分销设置和邀请人信息")
    @RequestMapping(value = "/setting-invitor", method = RequestMethod.GET)
    public BaseResponse<DistributionSimSettingResponse> getSettingAndInvitor() {
        DistributionCustomerSimByIdResponse simByIdResponse = distributionCustomerQueryProvider
                .findDistributionCutomerByCustomerId(new DistributionInviteNewByCustomerIdRequest(commonUtil.getOperatorId()))
                .getContext();
        DistributionSimSettingResponse response = new DistributionSimSettingResponse();
        response.setDistributionCustomerSimVO(simByIdResponse.getDistributionCustomerSimVO());
        response.setDistributionSettingSimVO(distributionCacheService.getSimDistributionSetting());
        return BaseResponse.success(response);
    }

    /**
     * 根据会员的Id查询该会员的分销员信息
     *
     * @return
     */
    @ApiOperation(value = "根据会员的Id查询该会员的分销员信息")
    @RequestMapping(value = "/distributor-info", method = RequestMethod.GET)
    public BaseResponse<DistributionCustomerByCustomerIdResponse> queryDistributorInfoByCustomerId() {
        return distributionCustomerQueryProvider.getByCustomerIdAndDistributorFlagAndDelFlag(new DistributionCustomerByCustomerIdRequest(commonUtil.getOperatorId()));
    }


    /**
     * 查询分销员的销售业绩
     *
     * @return
     */
    @ApiOperation(value = "查询分销员的销售业绩")
    @RequestMapping(value = "/sales/performance", method = RequestMethod.GET)
    public BaseResponse<DistributionRecordByInviteeIdResponse> getPerformanceByInviteedId() {
        return distributionRecordQueryProvider.getPerformanceByCustomerId(new DistributionRecordByCustomerIdRequest(commonUtil.getOperatorId()));
    }


    /**
     * 查询小店分销状态
     *
     * @return
     */
    @ApiOperation(value = "查询小店分销状态")
    @RequestMapping(value = "/check/status", method = RequestMethod.GET)
    public BaseResponse<Boolean> checkStatus() {
        return BaseResponse.success(distributionService.checkInviteeIdEnable());
    }

    /**
     * 登录人员是否分销员
     *
     * @return
     */
    @ApiOperation(value = "登录人员是否分销员")
    @RequestMapping(value = "/check/loginIsDistributor", method = RequestMethod.GET)
    public BaseResponse<Boolean> loginIsDistributor() {
        String customerId = commonUtil.getOperatorId();
        return BaseResponse.success(distributionService.isDistributor(customerId));
    }

    /**
     * 开店礼包
     *
     * @return
     */
    @ApiOperation(value = "开店礼包")
    @RequestMapping(value = "/storeBags", method = RequestMethod.POST)
    public BaseResponse<DistributionSetting4StoreBagsResponse> storeBags() {
        DistributionSetting4StoreBagsResponse storeBagsResponse = distributionCacheService.storeBags();
        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        if (CollectionUtils.isEmpty(storeBagsResponse.getGoodsInfos())) {
            return BaseResponse.success(storeBagsResponse);
        }
        List<String> goodsInfoIds =
                storeBagsResponse.getGoodsInfos().stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        queryRequest.setGoodsInfoIds(goodsInfoIds);
        queryRequest.setQueryGoods(true);
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        queryRequest.setStoreState(StoreState.OPENING.toValue());
        EsGoodsResponse response = esGoodsInfoElasticService.pageByGoods(queryRequest);
        storeBagsResponse.setGoodsInfos(response.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                .flatMap(Collection::stream).filter(goodsInfoVO -> goodsInfoIds.contains(goodsInfoVO.getGoodsInfoId()))
                .map(goods -> KsBeanUtil.convert(goods, GoodsInfoVO.class)).collect(Collectors.toList()));
        return BaseResponse.success(storeBagsResponse);
    }


    @ApiOperation(value = "验证开店礼包商品状态")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "goodsInfoId", value = "商品Id", required = true)
    @RequestMapping(value = "/verify/storeBags/sku/{goodsInfoId}", method = RequestMethod.GET)
    public BaseResponse<DistributionSetting4StoreBagsVerifyResponse> verifyStoreBagsSku(@PathVariable String goodsInfoId) {
        DistributionSetting4StoreBagsResponse storeBagsResponse = distributionCacheService.storeBags();

        EsGoodsInfoQueryRequest queryRequest = new EsGoodsInfoQueryRequest();
        queryRequest.setGoodsInfoIds(Collections.singletonList(goodsInfoId));
        queryRequest.setQueryGoods(true);
        queryRequest.setAddedFlag(AddedFlag.YES.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.setAuditStatus(CheckStatus.CHECKED.toValue());
        queryRequest.setStoreState(StoreState.OPENING.toValue());
        EsGoodsResponse response = esGoodsInfoElasticService.pageByGoods(queryRequest);
        //sku信息
        GoodsInfoNest goodsInfo = response.getEsGoods().getContent().stream().map(EsGoods::getGoodsInfos)
                .flatMap(Collection::stream).filter(goodsInfoVO -> goodsInfoId.equals(goodsInfoVO.getGoodsInfoId())).findFirst().orElse(new GoodsInfoNest());


        //分销开关以及商品状态验证
        if (Objects.equals(DeleteFlag.NO, storeBagsResponse.getOpenFlag()) || Objects.equals(DeleteFlag.NO,
                storeBagsResponse.getApplyFlag()) || !Objects.equals(RecruitApplyType.BUY,
                storeBagsResponse.getApplyType()) || Objects.isNull(goodsInfo)
                || Objects.equals(DeleteFlag.YES, goodsInfo.getDelFlag())
                || (!Objects.equals(CheckStatus.CHECKED, goodsInfo.getAuditStatus())) || AddedFlag.NO.toValue() == goodsInfo.getAddedFlag()) {
            throw new SbcRuntimeException(GoodsErrorCode.NOT_EXIST);
        }
        return BaseResponse.success(DistributionSetting4StoreBagsVerifyResponse.builder().result(Boolean.TRUE).build());
    }

    /**
     * 查询平台端-社交分销总开关状态
     *
     * @return
     */
    @ApiOperation(value = "查询平台端-社交分销总开关状态")
    @RequestMapping(value = "/queryOpenFlag", method = RequestMethod.GET)
    public BaseResponse<Integer> queryOpenFlag() {
        return BaseResponse.success(distributionCacheService.queryOpenFlag().toValue());
    }

    /**
     * 查询分销员状态
     *
     * @return
     */
    @ApiOperation(value = "查询分销员状态")
    @RequestMapping(value = "/getDistributorStatus", method = RequestMethod.GET)
    public BaseResponse<DistributionCustomerEnableByCustomerIdResponse> getDistributorStatus() {
        return distributionCustomerQueryProvider.checkEnableByCustomerId(
                new DistributionCustomerEnableByCustomerIdRequest(commonUtil.getOperatorId()));
    }
}
