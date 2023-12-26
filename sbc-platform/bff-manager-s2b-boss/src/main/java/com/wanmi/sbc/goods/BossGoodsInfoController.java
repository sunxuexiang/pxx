package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.constant.CustomerErrorCode;
import com.wanmi.sbc.customer.api.provider.customer.CustomerQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.customer.CustomerGetByIdRequest;
import com.wanmi.sbc.customer.api.request.store.StoreByIdRequest;
import com.wanmi.sbc.customer.api.response.customer.CustomerGetByIdResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.*;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoByIdResponse;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewPageResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceByCustomerIdResponse;
import com.wanmi.sbc.goods.api.response.price.GoodsIntervalPriceResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.CheckStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.intervalprice.GoodsIntervalPriceService;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingLevelPluginProvider;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingLevelGoodsListFilterRequest;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @Author: gaomuwei
 * @Date: Created In 下午4:08 2019/2/26
 * @Description: 平台端单品服务
 */
@RestController
@Api(description = "平台端单品服务", tags = "BossGoodsInfoController")
public class BossGoodsInfoController {


    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private CustomerQueryProvider customerQueryProvider;

    @Autowired
    private GoodsIntervalPriceService goodsIntervalPriceService;


    @Autowired
    private MarketingLevelPluginProvider marketingLevelPluginProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 分页显示商品
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "分页显示商品")
    @RequestMapping(value = "/goods/skus", method = RequestMethod.POST)
    public BaseResponse<GoodsInfoViewPageResponse> skuList(@RequestBody GoodsInfoViewPageRequest queryRequest) {
        if(Objects.nonNull(queryRequest.getWareId()) && queryRequest.getWareId() <= 0){
            queryRequest.setWareId(null);
        }
        //获取会员
        CustomerGetByIdResponse customer = null;
        if (StringUtils.isNotBlank(queryRequest.getCustomerId())) {
            customer = customerQueryProvider.getCustomerById(new CustomerGetByIdRequest(queryRequest.getCustomerId())
            ).getContext();
            if (Objects.isNull(customer)) {
                throw new SbcRuntimeException(CustomerErrorCode.NOT_EXIST);
            }
        }
        //按创建时间倒序、ID升序
        queryRequest.putSort("addedTime", SortType.DESC.toValue());
        queryRequest.putSort("goodsInfoId", SortType.ASC.toValue());
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());//可用
        queryRequest.setAuditStatus(CheckStatus.CHECKED);//已审核
        GoodsInfoViewPageResponse response = goodsInfoQueryProvider.pageView(queryRequest).getContext();

        List<GoodsInfoVO> goodsInfoVOList = response.getGoodsInfoPage().getContent();

        if (customer != null && StringUtils.isNotBlank(customer.getCustomerId())) {
            GoodsIntervalPriceByCustomerIdResponse priceResponse =
                    goodsIntervalPriceService.getGoodsIntervalPriceVOList(goodsInfoVOList, customer.getCustomerId());
            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            goodsInfoVOList = priceResponse.getGoodsInfoVOList();
        } else {
            GoodsIntervalPriceResponse priceResponse =
                    goodsIntervalPriceService.getGoodsIntervalPriceVOList(goodsInfoVOList);
            response.setGoodsIntervalPrices(priceResponse.getGoodsIntervalPriceVOList());
            goodsInfoVOList = priceResponse.getGoodsInfoVOList();
        }

        //计算会员价
        if (customer != null && StringUtils.isNotBlank(customer.getCustomerId())) {
            goodsInfoVOList = marketingLevelPluginProvider.goodsListFilter(
                    MarketingLevelGoodsListFilterRequest.builder()
                            .customerDTO(KsBeanUtil.convert(customer, CustomerDTO.class))
                            .goodsInfos(KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class)).build())
                    .getContext().getGoodsInfoVOList();
        }

        // 查询店铺信息
        goodsInfoVOList.forEach(goodsInfoVO -> {
            goodsInfoVO.setStoreName(storeQueryProvider.getById(new StoreByIdRequest(goodsInfoVO.getStoreId()))
                    .getContext().getStoreVO().getStoreName());
        });

        response.setGoodsInfoPage(new MicroServicePage<>(goodsInfoVOList, queryRequest.getPageRequest(),
                response.getGoodsInfoPage().getTotalElements()));
        return BaseResponse.success(response);
    }


    /**
     * 批量上架商品
     */
    @ApiOperation(value = "批量上架商品")
    @RequestMapping(value = "/goods/sku/sale", method = RequestMethod.PUT)
    public BaseResponse onSale(@RequestBody GoodsInfoRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        goodsInfoProvider.modifyAddedStatus(
                GoodsInfoModifyAddedStatusRequest.builder().addedFlag(AddedFlag.YES.toValue())
                        .goodsInfoIds(request.getGoodsInfoIds())
                        .build()
        );
        //更新ES
        esGoodsInfoElasticService.updateAddedStatus(AddedFlag.YES.toValue(), null, request.getGoodsInfoIds(),null);
        //更新日志
        if (1 == request.getGoodsInfoIds().size()) {
            GoodsInfoByIdRequest goodsByIdRequest = new GoodsInfoByIdRequest();
            goodsByIdRequest.setGoodsInfoId(request.getGoodsInfoIds().get(0));
            GoodsInfoByIdResponse response = goodsInfoQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "上架",
                    "上架：SKU编码" + response.getGoodsInfoNo());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量上架", "批量上架");
        }
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 批量下架商品
     */
    @ApiOperation(value = "批量下架商品")
    @RequestMapping(value = "/goods/sku/sale", method = RequestMethod.DELETE)
    public BaseResponse offSale(@RequestBody GoodsInfoRequest request) {

        if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        goodsInfoProvider.modifyAddedStatus(
                GoodsInfoModifyAddedStatusRequest.builder().addedFlag(AddedFlag.NO.toValue())
                        .goodsInfoIds(request.getGoodsInfoIds())
                        .build()
        );
        //更新ES
        esGoodsInfoElasticService.updateAddedStatus(AddedFlag.NO.toValue(), null, request.getGoodsInfoIds(),null);
        //更新日志
        if (1 == request.getGoodsInfoIds().size()) {
            GoodsInfoByIdRequest goodsByIdRequest = new GoodsInfoByIdRequest();
            goodsByIdRequest.setGoodsInfoId(request.getGoodsInfoIds().get(0));
            GoodsInfoByIdResponse response = goodsInfoQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "下架",
                    "下架：SKU编码" + response.getGoodsInfoNo());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量下架", "批量下架");
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 批量删除商品
     */
    @ApiOperation(value = "批量删除商品")
    @RequestMapping(value = "/goods/sku", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody GoodsInfoRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsInfoProvider.deleteByIds(GoodsInfoDeleteByIdsRequest.builder().goodsInfoIds(request.getGoodsInfoIds()).build());
        //更新ES
        esGoodsInfoElasticService.delete(request.getGoodsInfoIds());
        //更新日志
        if (1 == request.getGoodsInfoIds().size()) {
            GoodsInfoByIdRequest goodsByIdRequest = new GoodsInfoByIdRequest();
            goodsByIdRequest.setGoodsInfoId(request.getGoodsInfoIds().get(0));
            GoodsInfoByIdResponse response = goodsInfoQueryProvider.getById(goodsByIdRequest).getContext();
            operateLogMQUtil.convertAndSend("商品", "删除商品",
                    "删除商品：SKU编码" + response.getGoodsInfoNo());
        } else {
            operateLogMQUtil.convertAndSend("商品", "批量删除",
                    "批量删除");
        }
        return BaseResponse.SUCCESSFUL();
    }


}
