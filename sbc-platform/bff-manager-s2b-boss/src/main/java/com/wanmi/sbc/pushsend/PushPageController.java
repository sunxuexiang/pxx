package com.wanmi.sbc.pushsend;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyPageRequest;
import com.wanmi.sbc.customer.api.request.store.ListStoreByIdsRequest;
import com.wanmi.sbc.customer.api.response.company.CompanyReponse;
import com.wanmi.sbc.customer.api.response.store.ListStoreByIdsResponse;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.EmployeeVO;
import com.wanmi.sbc.customer.bean.vo.StoreVO;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.flashsalegoods.FlashSaleGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.groupongoodsinfo.GrouponGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.api.request.flashsalegoods.FlashSaleGoodsPageRequest;
import com.wanmi.sbc.goods.api.request.goods.GoodsPageRequest;
import com.wanmi.sbc.goods.api.request.groupongoodsinfo.GrouponGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.info.DistributionGoodsPageRequest;
import com.wanmi.sbc.goods.api.response.flashsalegoods.FlashSaleGoodsPageResponse;
import com.wanmi.sbc.goods.api.response.goods.GoodsPageResponse;
import com.wanmi.sbc.goods.api.response.groupongoodsinfo.GrouponGoodsInfoPageResponse;
import com.wanmi.sbc.goods.bean.enums.AuditStatus;
import com.wanmi.sbc.goods.bean.enums.YesOrNo;
import com.wanmi.sbc.goods.bean.vo.FlashSaleGoodsVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.goods.bean.vo.GrouponGoodsInfoVO;
import com.wanmi.sbc.marketing.api.provider.grouponcate.GrouponCateQueryProvider;
import com.wanmi.sbc.marketing.api.provider.market.MarketingQueryProvider;
import com.wanmi.sbc.marketing.api.request.grouponcate.GrouponCateByIdsRequest;
import com.wanmi.sbc.marketing.api.request.market.MarketingPageRequest;
import com.wanmi.sbc.marketing.api.response.grouponcate.GrouponCateListResponse;
import com.wanmi.sbc.marketing.api.response.market.MarketingPageResponse;
import com.wanmi.sbc.marketing.bean.dto.MarketingPageDTO;
import com.wanmi.sbc.marketing.bean.vo.GrouponCateVO;
import com.wanmi.sbc.marketing.bean.vo.MarketingPageVO;
import com.wanmi.sbc.pushsend.request.MarketingPageListRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * @program: sbc-micro-service
 * @description: 推送落地页
 * @create: 2020-02-03 20:34
 **/
@Api(description = "推送落地页管理API", tags = "PushPageController")
@RestController
@RequestMapping(value = "/pushpage")
public class PushPageController {
    @Autowired
    private GoodsQueryProvider goodsQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    private CompanyInfoQueryProvider companyInfoQueryProvider;

    @Autowired
    private MarketingQueryProvider marketingQueryProvider;

    @Autowired
    private GrouponCateQueryProvider grouponCateQueryProvider;

    @Autowired
    private GrouponGoodsInfoQueryProvider grouponGoodsInfoQueryProvider;

    @Autowired
    private FlashSaleGoodsQueryProvider flashSaleGoodsQueryProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;


    @ApiOperation(value = "商品列表")
    @PostMapping(value = "/goodsPage")
    public BaseResponse<GoodsPageResponse> goodsPage(@RequestBody GoodsPageRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        pageRequest.putSort("createTime", SortType.DESC.toValue());
        return goodsQueryProvider.page(pageRequest);
    }

    /**
     * 查询商品分类
     *
     * @param queryRequest 商品
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品分类")
    @GetMapping(value = "/goodsCates")
    public BaseResponse<List<GoodsCateVO>> list(GoodsCateListByConditionRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.putSort("isDefault", SortType.DESC.toValue());
        queryRequest.putSort("sort", SortType.ASC.toValue());
        queryRequest.putSort("createTime", SortType.DESC.toValue());
        return BaseResponse.success(goodsCateQueryProvider.listByCondition(queryRequest).getContext().getGoodsCateVOList());
    }

    /**
     * 供应商列表
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "店铺列表")
    @PostMapping(value = "/storePage")
    public BaseResponse<Page<CompanyReponse>> storePage(@RequestBody CompanyPageRequest request) {
        request.setDeleteFlag(DeleteFlag.NO);
        request.putSort("createTime", SortType.DESC.toValue());
        Page<CompanyInfoVO> page = companyInfoQueryProvider.pageCompanyInfo(request).getContext()
                .getCompanyInfoVOPage();

        List<CompanyReponse> companyReponseList = new ArrayList<>();
        page.getContent().forEach(info -> {
            //组装返回结构
            CompanyReponse companyReponse = new CompanyReponse();
            companyReponse.setCompanyInfoId(info.getCompanyInfoId());
            companyReponse.setCompanyCode(info.getCompanyCode());
            companyReponse.setCompanyType(info.getCompanyType());
            companyReponse.setSupplierName(info.getSupplierName());
            if (CollectionUtils.isNotEmpty(info.getEmployeeVOList())) {
                EmployeeVO employee = info.getEmployeeVOList().get(0);
                companyReponse.setAccountName(employee.getAccountName());
                companyReponse.setAccountState(employee.getAccountState());
                companyReponse.setAccountDisableReason(employee.getAccountDisableReason());
            }
            if (nonNull(info.getStoreVOList()) && !info.getStoreVOList().isEmpty()) {
                StoreVO store = info.getStoreVOList().get(0);
                companyReponse.setStoreId(store.getStoreId());
                companyReponse.setStoreName(store.getStoreName());
                companyReponse.setContractStartDate(store.getContractStartDate());
                companyReponse.setContractEndDate(store.getContractEndDate());
                companyReponse.setAuditState(store.getAuditState());
                companyReponse.setAuditReason(store.getAuditReason());
                companyReponse.setStoreState(store.getStoreState());
                companyReponse.setStoreClosedReason(store.getStoreClosedReason());
                companyReponse.setApplyEnterTime(store.getApplyEnterTime());
            }
            companyReponseList.add(companyReponse);
        });
        return BaseResponse.success(new PageImpl<>(companyReponseList, request.getPageable(), page.getTotalElements()));
    }

    /**
     * 获取营销活动列表
     * @param marketingPageListRequest {@link MarketingPageRequest}
     * @return
     */
    @ApiOperation(value = "获取营销活动列表")
    @PostMapping(value = "/marketingPage")
    public BaseResponse<MicroServicePage<MarketingPageVO>> marketingPage(@RequestBody MarketingPageListRequest marketingPageListRequest) {
        MarketingPageRequest marketingPageRequest = new MarketingPageRequest();
        marketingPageRequest.setMarketingPageDTO(KsBeanUtil.convert(marketingPageListRequest, MarketingPageDTO.class));
        BaseResponse<MarketingPageResponse> pageResponse = marketingQueryProvider.page(marketingPageRequest);
        return BaseResponse.success(pageResponse.getContext().getMarketingVOS());
    }

    /**
     * 分页查询拼团活动
     *
     * @param pageRequest 商品 {@link DistributionGoodsPageRequest}
     * @return 拼团活动分页
     */
    @ApiOperation(value = "分页查询拼团活动")
    @RequestMapping(value = "page", method = RequestMethod.POST)
    public BaseResponse<GrouponGoodsInfoPageResponse> page(@RequestBody @Valid GrouponGoodsInfoPageRequest
                                                                         pageRequest) {
        pageRequest.setYesOrNo(YesOrNo.YES);
        pageRequest.setAuditStatus(AuditStatus.CHECKED);
        GrouponGoodsInfoPageResponse response = grouponGoodsInfoQueryProvider.page(pageRequest).getContext();
        List<String> grouponCateIds = response.getGrouponGoodsInfoVOPage().stream()
                .map(GrouponGoodsInfoVO::getGrouponCateId).collect(Collectors.toList());
        Map<String,String> cateMap = new HashMap<>(grouponCateIds.size());
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(grouponCateIds)){
            GrouponCateListResponse cateListResponse =
                    grouponCateQueryProvider.getByIds(GrouponCateByIdsRequest.builder().grouponCateIds(grouponCateIds).build()).getContext();
            cateMap =
                    cateListResponse.getGrouponCateVOList().stream().collect(Collectors.toMap(GrouponCateVO::getGrouponCateId,
                    GrouponCateVO::getGrouponCateName));
        }

        final Map<String,String> mapResult = cateMap;
        response.getGrouponGoodsInfoVOPage().getContent().forEach(entry->{
            if (MapUtils.isNotEmpty(mapResult)){
                entry.setCateName(mapResult.get(entry.getGrouponCateId()));
            }

            if (Objects.nonNull(entry.getStartTime()) && Objects.nonNull(entry.getEndTime())){
                if (LocalDateTime.now().isBefore(entry.getStartTime())){
                    entry.setStatus(0);
                } else {
                    entry.setStatus(1);
                }
            }
        });
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "列表查询秒杀活动列表")
    @PostMapping("/flashSalePage")
    public BaseResponse<FlashSaleGoodsPageResponse> flashSalePage(@RequestBody @Valid FlashSaleGoodsPageRequest req) {

        req.putSort("activityFullTime", "asc");
        req.putSort("id", "asc");
        FlashSaleGoodsPageResponse response = flashSaleGoodsQueryProvider.page(req).getContext();
        List<Long> storeIds = response.getFlashSaleGoodsVOPage().getContent().stream().map(FlashSaleGoodsVO::getStoreId)
                .collect(Collectors.toList());
        ListStoreByIdsResponse storeResponse =
                storeQueryProvider.listByIds(ListStoreByIdsRequest.builder().storeIds(storeIds).build()).getContext();

        response.getFlashSaleGoodsVOPage().getContent().forEach(flashSaleGoodsVO -> {
            storeResponse.getStoreVOList().forEach(storeVO -> {
                if (flashSaleGoodsVO.getStoreId().equals(storeVO.getStoreId())) {
                    flashSaleGoodsVO.setStoreName(storeVO.getStoreName());
                }
            });
        });
        return BaseResponse.success(response);
    }
}