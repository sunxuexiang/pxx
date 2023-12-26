package com.wanmi.sbc.follow;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.enums.DefaultFlag;
import com.wanmi.sbc.common.enums.ForcePileFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.customer.api.provider.address.CustomerDeliveryAddressQueryProvider;
import com.wanmi.sbc.customer.api.provider.store.StoreQueryProvider;
import com.wanmi.sbc.customer.api.request.address.CustomerDeliveryAddressRequest;
import com.wanmi.sbc.customer.api.response.address.CustomerDeliveryAddressResponse;
import com.wanmi.sbc.customer.bean.dto.CustomerDTO;
import com.wanmi.sbc.customer.bean.enums.EnterpriseCheckState;
import com.wanmi.sbc.customer.bean.vo.CustomerVO;
import com.wanmi.sbc.goods.api.provider.activitygoodspicture.ActivityGoodsPictureProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoProvider;
import com.wanmi.sbc.goods.api.provider.info.BulkGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.info.ActivityGoodsPictureGetRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoFillGoodsStatusRequest;
import com.wanmi.sbc.goods.api.response.info.ActivityGoodsResponse;
import com.wanmi.sbc.goods.api.response.info.ActivityGoodsViewResponse;
import com.wanmi.sbc.goods.bean.dto.GoodsInfoDTO;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.enums.GoodsStatus;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoNewVO;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.GoodsVO;
import com.wanmi.sbc.marketing.api.provider.pile.PileActivityProvider;
import com.wanmi.sbc.marketing.api.provider.plugin.MarketingPluginProvider;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityCloseByIdRequest;
import com.wanmi.sbc.marketing.api.request.pile.PileActivityPileActivityGoodsRequest;
import com.wanmi.sbc.marketing.api.request.plugin.MarketingPluginGoodsListFilterRequest;
import com.wanmi.sbc.marketing.bean.vo.PileActivityGoodsVO;
import com.wanmi.sbc.marketing.bean.vo.PileActivityVO;
import com.wanmi.sbc.order.api.provider.follow.FollowProvider;
import com.wanmi.sbc.order.api.provider.follow.FollowQueryProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.BulkShopCartProvider;
import com.wanmi.sbc.shopcart.api.provider.cart.ShopCartProvider;
import com.wanmi.sbc.order.api.request.follow.*;
import com.wanmi.sbc.order.api.request.follow.validGroups.FollowAdd;
import com.wanmi.sbc.order.api.request.follow.validGroups.FollowDelete;
import com.wanmi.sbc.order.api.request.follow.validGroups.FollowFilter;
import com.wanmi.sbc.shopcart.api.request.purchase.PurchaseFillBuyCountRequest;
import com.wanmi.sbc.order.api.response.follow.FollowListNewResponse;
import com.wanmi.sbc.order.api.response.follow.FollowListResponse;
import com.wanmi.sbc.shopcart.api.response.purchase.PurchaseFillBuyCountResponse;
import com.wanmi.sbc.saas.bean.vo.DomainStoreRelaVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品收藏Controller
 * Created by daiyitian on 17/4/12.
 */
@RestController
@RequestMapping("/goods")
@Slf4j
@Api(tags = "CustomerDeliveryAddressBaseController", description = "S2B web公用-商品收藏信息API")
public class GoodsFollowBaseController {

    @Autowired
    private FollowProvider followProvider;

    @Autowired
    private FollowQueryProvider followQueryProvider;

    @Autowired
    private ShopCartProvider shopCartProvider;

    @Autowired
    private BulkShopCartProvider bulkShopCartProvider;

    @Autowired
    private GoodsInfoProvider goodsInfoProvider;

    @Autowired
    private BulkGoodsInfoProvider bulkGoodsInfoProvider;

    @Autowired
    private MarketingPluginProvider marketingPluginProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private CustomerDeliveryAddressQueryProvider customerDeliveryAddressQueryProvider;

    @Autowired
    private PileActivityProvider pileActivityProvider;

    @Autowired
    private ActivityGoodsPictureProvider activityGoodsPictureProvider;

    @Autowired
    private StoreQueryProvider storeQueryProvider;


    /**
     * 获取商品收藏列表
     * @param queryRequest 查询条件
     * @return 商品收藏分页
     */
    @ApiOperation(value = "获取商品收藏列表")
    @RequestMapping(value = "/goodsFollows", method = RequestMethod.POST)
    public BaseResponse<FollowListResponse> info(@RequestBody FollowListRequest queryRequest,HttpServletRequest httpRequest) {

        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        //设置仓库id
        queryRequest.setBulkWareId(commonUtil.getBulkWareId(HttpUtil.getRequest()));
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        queryRequest.setCustomerId(customer.getCustomerId());
        //按创建时间倒序
        queryRequest.putSort("followTime", SortType.DESC.toValue());
        queryRequest.putSort("followId", SortType.DESC.toValue());

        FollowListResponse  response = followQueryProvider.list(queryRequest).getContext();

        if(CollectionUtils.isNotEmpty(response.getGoodsInfos().getContent())) {
            List<GoodsInfoVO> goodsInfoVOS = response.getGoodsInfos().getContent().stream().filter(v -> {
                if (Objects.isNull(v)) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            log.info("==================== FollowListResponse:{}", goodsInfoVOS);
            List<GoodsInfoDTO> dtoList = KsBeanUtil.convertList(goodsInfoVOS, GoodsInfoDTO.class);

            // 散批
            Map<Integer, List<GoodsInfoDTO>> collect = dtoList.stream().collect(Collectors.groupingBy(GoodsInfoDTO::getSaleType));
            List<GoodsInfoDTO> goodsInfoDTOSFromBulk = collect.get(2);

            // 批发-历史遗留问题：批发商品列表中sale_type可能为0和1
            List<GoodsInfoDTO> goodsInfoDTOSFromWhole = Optional.ofNullable(collect.get(0)).orElse(Lists.newArrayList());
            goodsInfoDTOSFromWhole.addAll(Optional.ofNullable(collect.get(1)).orElse(Lists.newArrayList()));

            List<GoodsInfoVO> goodsInfoVOList = new ArrayList<>(10);
            if(CollectionUtils.isNotEmpty(goodsInfoDTOSFromWhole)){
                //设定SKU状态
                goodsInfoVOList = goodsInfoProvider.fillGoodsStatus(
                        GoodsInfoFillGoodsStatusRequest.builder().goodsInfos(goodsInfoDTOSFromWhole).build()).getContext().getGoodsInfos();
                //计算营销价格
                MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
                filterRequest.setGoodsInfos(KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class));
                filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
                goodsInfoVOList = marketingPluginProvider.goodsListFilter(filterRequest).getContext().getGoodsInfoVOList();
                //填充购买数
                PurchaseFillBuyCountRequest purchaseFillBuyCountRequest = new PurchaseFillBuyCountRequest();
                purchaseFillBuyCountRequest.setCustomerId(customer.getCustomerId());
                purchaseFillBuyCountRequest.setGoodsInfoList(goodsInfoVOList);
                purchaseFillBuyCountRequest.setInviteeId("0");
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = shopCartProvider.fillBuyCount(purchaseFillBuyCountRequest).getContext();
                goodsInfoVOList = purchaseFillBuyCountResponse.getGoodsInfoList();

                if(Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
                    goodsInfoVOList.forEach(g->{
                        if(Objects.nonNull(g.getVipPrice()) && g.getVipPrice().compareTo(BigDecimal.ZERO) > 0){
                            g.setSalePrice(g.getVipPrice());
                        }
                    });
                }

                //标签值筛选
                dtoList.stream().filter(item -> Objects.nonNull(item.getGoodsLabels()))
                        .forEach(item->{
                            item.setGoodsLabels(item.getGoodsLabels().stream()
                                    .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                                    .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                                    .collect(Collectors.toList()));
                        });
                Map<String, GoodsVO> goodsVOMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, g -> g));
                goodsInfoVOList.forEach(g->{
                    //如果改商品有营销信息 vip价格不参与  设置0 返给前端
                    if (CollectionUtils.isNotEmpty(g.getMarketingLabels())) {
                        g.setVipPrice(BigDecimal.ZERO);
                    }
                    //填充酒水保质期
                    if(Objects.nonNull(g.getShelflife()) && g.getShelflife() == 9999){
                        g.setShelflife(0L);
                    }

                    if (Objects.nonNull(goodsVOMap.get(g.getGoodsId()))){
                        g.setGoodsSubtitle(goodsVOMap.get(g.getGoodsId()).getGoodsSubtitle());
                    }
                    dtoList.forEach(dto -> {
                        if (dto.getGoodsInfoId().equals(g.getGoodsInfoId())) {
                            g.setGoodsLabels(dto.getGoodsLabels());
                        }
                    });
                });
                //限购
                CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);
                goodsInfoVOList.forEach(goodsInfoVO -> {
                    if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())){
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }
                });
                // 商品下架
                goodsInfoVOList.forEach(goodsInfoVO -> {
                    if(AddedFlag.NO.toValue() == goodsInfoVO.getAddedFlag()){
                        goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                    }
                });
                // 设置收藏
                goodsInfoVOList.forEach(goodsInfoVO -> {
                    goodsInfoVO.setFollowType(0); // 0代表批发
                });
            }

            // 散批收藏
            if(CollectionUtils.isNotEmpty(goodsInfoDTOSFromBulk)){
                // 设置SKU的状态
                List<GoodsInfoVO> goodsInfoVOSForBulk = bulkGoodsInfoProvider.fillGoodsStatus(GoodsInfoFillGoodsStatusRequest.builder().goodsInfos(goodsInfoDTOSFromBulk).build()).getContext().getGoodsInfos();

                // 填充购物车中的购买数量
                PurchaseFillBuyCountRequest purchaseFillBuyCountRequest = new PurchaseFillBuyCountRequest();
                purchaseFillBuyCountRequest.setCustomerId(customer.getCustomerId());
                purchaseFillBuyCountRequest.setGoodsInfoList(goodsInfoVOSForBulk);
                purchaseFillBuyCountRequest.setInviteeId("0");
                PurchaseFillBuyCountResponse purchaseFillBuyCountResponse = bulkShopCartProvider.fillBuyCount(purchaseFillBuyCountRequest).getContext();
                goodsInfoVOSForBulk = purchaseFillBuyCountResponse.getGoodsInfoList();

                if(Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
                    goodsInfoVOSForBulk.forEach(g->{
                        if(Objects.nonNull(g.getVipPrice()) && g.getVipPrice().compareTo(BigDecimal.ZERO) > 0){
                            g.setSalePrice(g.getVipPrice());
                        }
                    });
                }

                // VIP价格等
                Map<String, GoodsVO> goodsVOMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, g -> g));
                goodsInfoVOSForBulk.forEach(g->{
                    //如果改商品有营销信息 vip价格不参与  设置0 返给前端
                    if (CollectionUtils.isNotEmpty(g.getMarketingLabels())) {
                        g.setVipPrice(BigDecimal.ZERO);
                    }
                    //填充酒水保质期
                    if(Objects.nonNull(g.getShelflife()) && g.getShelflife() == 9999){
                        g.setShelflife(0L);
                    }

                    if (Objects.nonNull(goodsVOMap.get(g.getGoodsId()))){
                        g.setGoodsSubtitle(goodsVOMap.get(g.getGoodsId()).getGoodsSubtitle());
                    }
                    dtoList.forEach(dto -> {
                        if (dto.getGoodsInfoId().equals(g.getGoodsInfoId())) {
                            g.setGoodsLabels(dto.getGoodsLabels());
                        }
                    });
                });
                // 限购
                CustomerDeliveryAddressResponse finalDeliveryAddress = commonUtil.getProvinceCity(httpRequest);
                goodsInfoVOSForBulk.forEach(goodsInfoVO -> {
                    if (Objects.nonNull(finalDeliveryAddress) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())){
                        List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                                .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                        //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                        if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                            goodsInfoVO.setGoodsStatus(GoodsStatus.QUOTA);
                        }
                    }
                });
                // 商品下架状态校验
                goodsInfoVOSForBulk.forEach(goodsInfoVO -> {
                    if(AddedFlag.NO.toValue() == goodsInfoVO.getAddedFlag()){
                        goodsInfoVO.setGoodsStatus(GoodsStatus.INVALID);
                    }
                });

                // 设置收藏
                goodsInfoVOSForBulk.forEach(goodsInfoVO -> {
                    goodsInfoVO.setFollowType(2); // 2代表新散批
                });
                goodsInfoVOList.addAll(goodsInfoVOSForBulk);
            }

            if(CollectionUtils.isNotEmpty(goodsInfoVOList)){
                List<GoodsInfoVO> goodsInfoVOListOrdeded = goodsInfoVOList.stream()
                        .sorted(Comparator.comparing(GoodsInfoVO::getCreateTime).reversed())
                        .collect(Collectors.toList());
                response.setGoodsInfos(new MicroServicePage<GoodsInfoVO>(goodsInfoVOListOrdeded,  queryRequest.getPageRequest(),
                        response.getGoodsInfos().getTotalElements()));
            } else {
                response.setGoodsInfos(new MicroServicePage<GoodsInfoVO>(goodsInfoVOList,  queryRequest.getPageRequest(),
                        response.getGoodsInfos().getTotalElements()));
            }
        }

        List<GoodsInfoVO> content = response.getGoodsInfos().getContent();
        List<String> goodsInfoIds = content.stream().map(GoodsInfoVO::getGoodsInfoId).collect(Collectors.toList());
        //设置上商品参与囤货活动
        if(CollectionUtils.isNotEmpty(response.getGoodsInfos().getContent())){

            BaseResponse<List<PileActivityVO>> startPileActivity = pileActivityProvider.getStartPileActivity();

            if (CollectionUtils.isNotEmpty(goodsInfoIds) && CollectionUtils.isNotEmpty(startPileActivity.getContext())) {
                PileActivityVO pileActivityVO = startPileActivity.getContext().get(0);

                BaseResponse<List<PileActivityGoodsVO>> startPileActivityPileActivityGoods =
                        pileActivityProvider.getStartPileActivityPileActivityGoods(PileActivityPileActivityGoodsRequest.builder()
                                .goodsInfoIds(goodsInfoIds).pileActivityId(pileActivityVO.getActivityId()).build());

                BaseResponse<List<PileActivityGoodsVO>> pileActivityReturn = startPileActivityPileActivityGoods;
                if(CollectionUtils.isNotEmpty(pileActivityReturn.getContext())){

                    Map<String, PileActivityGoodsVO> collect = pileActivityReturn.getContext().stream().collect(Collectors.toMap(PileActivityGoodsVO::getGoodsInfoId, g -> g,(a,b)->a));

                    content.forEach(goodsInfo -> {
                        if(Objects.nonNull(collect.get(goodsInfo.getGoodsInfoId()))){
                            goodsInfo.setVirtualStock(collect.get(goodsInfo.getGoodsInfoId()).getVirtualStock().longValue());
                            if(pileActivityVO.getForcePileFlag().toValue() == BoolFlag.YES.toValue()){
                                goodsInfo.setPileFlag(ForcePileFlag.FORCEPILE);
                            }else {
                                goodsInfo.setPileFlag(ForcePileFlag.PILE);
                            }
                        }else {
                            goodsInfo.setPileFlag(ForcePileFlag.CLOSE);

                        }
                    });
                }
            }
        }
        //活动图片处理
        Map<String, ActivityGoodsResponse> collect = new HashMap<>();
        if(CollectionUtils.isNotEmpty(goodsInfoIds)){
            BaseResponse<ActivityGoodsViewResponse> byGoods = activityGoodsPictureProvider.getByGoods(ActivityGoodsPictureGetRequest.builder().goodsInfoIds(goodsInfoIds).build());
            List<ActivityGoodsResponse> activityGoodsResponse = byGoods.getContext().getActivityGoodsResponse();
            if(CollectionUtils.isNotEmpty(activityGoodsResponse)){
                collect = activityGoodsResponse.stream().collect(Collectors.toMap(ActivityGoodsResponse::getGoodsInfoId, g -> g,(a,b)->a));
            }
        }

        Map<String, ActivityGoodsResponse> finalCollect = collect;

        content.forEach(goodsInfo -> {
            //设置参与活动商品的活动图片
            if(CollectionUtils.isNotEmpty(goodsInfo.getMarketingLabels())){
                if(CollectionUtils.isNotEmpty(goodsInfo.getGoodsImages())){
                    ActivityGoodsResponse activityGoodsResponse = finalCollect.get(goodsInfo.getGoodsInfoId());
                    goodsInfo.getGoodsImages().get(0).setArtworkUrl(activityGoodsResponse.getImgPath());
                }
            }
        });

        //检测商品是否可预售 @jkp
        this.checkGoodsPresellStock(content);

        return BaseResponse.success(response);
    }

    /**
     * 获取商品收藏列表
     * @param queryRequest 查询条件
     * @return 商品收藏分页
     */
    @ApiOperation(value = "(新)获取商品收藏列表")
    @RequestMapping(value = "/goodsFollows2", method = RequestMethod.POST)
    public BaseResponse<FollowListNewResponse> info2(@RequestBody FollowListRequest queryRequest) {
        FollowListNewResponse listNewResponse = FollowListNewResponse.builder().build();
        //获取会员
        CustomerVO customer = commonUtil.getCustomer();
        //设置仓库id
        queryRequest.setWareId(commonUtil.getWareId(HttpUtil.getRequest()));
        queryRequest.setCustomerId(customer.getCustomerId());
        //按创建时间倒序
        queryRequest.putSort("followTime", SortType.DESC.toValue());
        queryRequest.putSort("followId", SortType.DESC.toValue());
        FollowListResponse  response = followQueryProvider.list(queryRequest).getContext();
       /* System.out.println("收藏查询" + (System.currentTimeMillis() - time1));*/
        if(CollectionUtils.isNotEmpty(response.getGoodsInfos().getContent())) {
            List<GoodsInfoDTO> dtoList = KsBeanUtil.convertList(response.getGoodsInfos().getContent(), GoodsInfoDTO.class);

            //设定SKU状态
           /* Long time3 = System.currentTimeMillis();*/
            List<GoodsInfoVO> goodsInfoVOList = goodsInfoProvider.fillGoodsStatus(
                    GoodsInfoFillGoodsStatusRequest.builder().goodsInfos(dtoList).build()).getContext().getGoodsInfos();
            //通过客户收货地址和商品指定区域设置商品状态
            //根据用户ID得到收货地址
            CustomerDeliveryAddressResponse deliveryAddress = null;
            if (Objects.nonNull(customer)) {
                deliveryAddress = commonUtil.getDeliveryAddress();
            }
            CustomerDeliveryAddressResponse finalDeliveryAddress = deliveryAddress;
            goodsInfoVOList.forEach(goodsInfoVO -> {
                if (Objects.nonNull(finalDeliveryAddress) && Objects.nonNull(goodsInfoVO.getAllowedPurchaseArea()) && StringUtils.isNotBlank(goodsInfoVO.getAllowedPurchaseArea())){
                    List<Long> allowedPurchaseAreaList = Arrays.asList(goodsInfoVO.getAllowedPurchaseArea().split(","))
                            .stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
                    //如果用户的收货地址省/市都不在不在该商品的指定销售区域内,则要改变状态
                    if (!allowedPurchaseAreaList.contains(finalDeliveryAddress.getCityId()) && !allowedPurchaseAreaList.contains(finalDeliveryAddress.getProvinceId())) {
                        goodsInfoVO.setGoodsStatus(GoodsStatus.OUT_STOCK);
                    }
                }
            });

            //计算营销价格
            MarketingPluginGoodsListFilterRequest filterRequest = new MarketingPluginGoodsListFilterRequest();
            filterRequest.setGoodsInfos(KsBeanUtil.convert(goodsInfoVOList, GoodsInfoDTO.class));
            filterRequest.setCustomerDTO(KsBeanUtil.convert(customer, CustomerDTO.class));
//            goodsInfoVOList = marketingPluginProvider.goodsListFilter(filterRequest).getContext().getGoodsInfoVOList();

            if(Objects.nonNull(customer) && EnterpriseCheckState.CHECKED.equals(customer.getEnterpriseStatusXyy())){
                goodsInfoVOList.forEach(g->{
                    if(Objects.nonNull(g.getVipPrice()) && g.getVipPrice().compareTo(BigDecimal.ZERO) > 0){
                        g.setSalePrice(g.getVipPrice());
                    }
                });
            }

            //标签值筛选
            dtoList.stream().filter(item -> Objects.nonNull(item.getGoodsLabels()))
                    .forEach(item->{
                        item.setGoodsLabels(item.getGoodsLabels().stream()
                        .sorted((a, b) -> a.getSort().compareTo(b.getSort()))
                        .filter(goodsLabel -> DefaultFlag.YES.equals(goodsLabel.getVisible()))
                        .collect(Collectors.toList()));
            });
            Map<String, GoodsVO> goodsVOMap = response.getGoodses().stream().collect(Collectors.toMap(GoodsVO::getGoodsId, g -> g));
            goodsInfoVOList.forEach(g->{
                if (Objects.nonNull(goodsVOMap.get(g.getGoodsId()))){
                    g.setGoodsSubtitle(goodsVOMap.get(g.getGoodsId()).getGoodsSubtitle());
                }
                dtoList.forEach(dto -> {
                    if (dto.getGoodsInfoId().equals(g.getGoodsInfoId())) {
                        g.setGoodsLabels(dto.getGoodsLabels());
                    }
                });
            });

            //转化;
            listNewResponse.setGoodsInfos(new MicroServicePage<GoodsInfoNewVO>(KsBeanUtil.convert(goodsInfoVOList, GoodsInfoNewVO.class), queryRequest.getPageRequest(),
                    response.getGoodsInfos().getTotalElements()));

        }
        return BaseResponse.success(listNewResponse);
    }

    /**
     * 新增商品收藏
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "新增商品收藏")
    @RequestMapping(value = "/goodsFollow", method = RequestMethod.POST)
    public BaseResponse add(@Validated({FollowAdd.class}) @RequestBody FollowSaveRequest request) {
        Long wareId = commonUtil.getWareId(HttpUtil.getRequest());
        if(2 == request.getSubType()){ // 2代表散批
            wareId = commonUtil.getBulkWareId(HttpUtil.getRequest());
            request.setBulkWareId(wareId);
        }
        request.setWareId(wareId);
        request.setCustomerId(commonUtil.getOperatorId());
        followProvider.save(request);
       //goodsCustomerFollowService.save(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 取消商品收藏
     * @param request 数据
     * @return 结果
     */
    @ApiOperation(value = "取消商品收藏")
    @RequestMapping(value = "/goodsFollow", method = RequestMethod.DELETE)
    public BaseResponse delete(@Validated({FollowDelete.class}) @RequestBody FollowDeleteRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        followProvider.delete(request);
       // goodsCustomerFollowService.delete(request);
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 删除失效商品
     * @return 结果
     */
    @ApiOperation(value = "删除失效商品")
    @RequestMapping(value = "/goodsFollows", method = RequestMethod.DELETE)
    public BaseResponse delete() {
        followProvider.deleteInvalidGoods(InvalidGoodsDeleteRequest.builder().customerId(commonUtil.getOperatorId()).build());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 是否含有失效商品
     * @return 结果
     */
    @ApiOperation(value = "是否含有失效商品")
    @RequestMapping(value = "/hasInvalidGoods", method = RequestMethod.GET)
    public BaseResponse<Boolean> hasInvalid() {
        return BaseResponse.success(followQueryProvider.haveInvalidGoods(HaveInvalidGoodsRequest.builder().customerId(commonUtil.getOperatorId()).build()).getContext().getBoolValue());
    }

    /**
     * 批量验证是否是收藏商品
     * @return 结果，相应的SkuId就是已收藏的商品ID
     */
    @ApiOperation(value = "批量验证是否是收藏商品<List<String>,相应的SkuId就是已收藏的商品ID>")
    @RequestMapping(value = "/isGoodsFollow", method = RequestMethod.POST)
    public BaseResponse<List<String>> isGoodsFollow(@Validated({FollowFilter.class}) @RequestBody IsFollowRequest request) {
        request.setCustomerId(commonUtil.getOperatorId());
        return BaseResponse.success(followQueryProvider.isFollow(request).getContext().getValue());
    }

    /**
     * 获取商品收藏个数
     * @return 商品收藏个数
     */
    @ApiOperation(value = "获取商品收藏个数")
    @RequestMapping(value = "/goodsFollowNum", method = RequestMethod.GET)
    public BaseResponse<Long> count() {
        FollowCountRequest request = FollowCountRequest.builder()
                .customerId(commonUtil.getOperatorId())
                .build();
        DomainStoreRelaVO domainInfo = commonUtil.getDomainInfo();
        if(Objects.nonNull(domainInfo)){
            request.setStoreId(domainInfo.getStoreId());
        }
        return BaseResponse.success( followQueryProvider.count(request).getContext().getValue());
    }

    private CustomerDeliveryAddressResponse getDeliveryAddress() {
        CustomerDeliveryAddressRequest queryRequest = new CustomerDeliveryAddressRequest();
        String customerId = commonUtil.getOperatorId();
        queryRequest.setCustomerId(customerId);
        BaseResponse<CustomerDeliveryAddressResponse> customerDeliveryAddressResponseBaseResponse = customerDeliveryAddressQueryProvider.getDefaultOrAnyOneByCustomerId(queryRequest);
        CustomerDeliveryAddressResponse customerDeliveryAddressResponse = customerDeliveryAddressResponseBaseResponse.getContext();
        return customerDeliveryAddressResponse;
    }


    /**
     * 检测商品是否可预售
     * @param list
     */
    private void checkGoodsPresellStock(List<GoodsInfoVO> list){
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        Map<Long, List<GoodsInfoVO>> storeGoodsInfoMap = list.stream().collect(Collectors.groupingBy(s -> s.getStoreId()));

        storeGoodsInfoMap.forEach((k, v)->{
            BaseResponse<Boolean> storeResponse = storeQueryProvider.checkStoreIsPresell(k);
            if (storeResponse==null){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "检测店铺预售状态失败");
            }
            if (!storeResponse.getContext()){
                return;
            }
            v.forEach(item->{
                //判断商品是否可预售 @jkp
                boolean isPresell = false;
                if (item.getStock()!=null && item.getPresellStock()!=null){
                    isPresell = this.checkGoodsInfoIsPresell(storeResponse.getContext(), item.getStock().doubleValue(), item.getPresellStock());
                }
                item.setIsPresell(isPresell?1:0);
            });
        });
    }

    /**
     * 判断商品是否可预售 @jkp
     * @param presellState
     * @param stock
     * @param presellStock
     */
    private boolean checkGoodsInfoIsPresell(boolean presellState, double stock, long presellStock){
        return presellState//商家预售权限状态
                && stock<=0//真实库存数量
                && presellStock>0;//预售虚拟库存
    }
}
