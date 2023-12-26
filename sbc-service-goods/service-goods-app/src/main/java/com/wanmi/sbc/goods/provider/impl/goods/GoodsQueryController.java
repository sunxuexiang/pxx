package com.wanmi.sbc.goods.provider.impl.goods;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.request.info.GoodsCountByConditionRequest;
import com.wanmi.sbc.goods.api.response.goods.*;
import com.wanmi.sbc.goods.api.response.info.GoodsCountByConditionResponse;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.brand.model.root.GoodsBrand;
import com.wanmi.sbc.goods.brand.request.GoodsBrandQueryRequest;
import com.wanmi.sbc.goods.brand.service.GoodsBrandService;
import com.wanmi.sbc.goods.info.model.root.Goods;
import com.wanmi.sbc.goods.info.model.root.GoodsInfo;
import com.wanmi.sbc.goods.info.model.root.GoodsPropDetailRel;
import com.wanmi.sbc.goods.info.reponse.GoodsEditResponse;
import com.wanmi.sbc.goods.info.reponse.GoodsQueryResponse;
import com.wanmi.sbc.goods.info.reponse.GoodsResponse;
import com.wanmi.sbc.goods.info.request.GoodsQueryRequest;
import com.wanmi.sbc.goods.info.service.*;
import com.wanmi.sbc.goods.redis.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * com.wanmi.sbc.goods.provider.impl.goods.GoodsQueryController
 *
 * @author lipeng
 * @dateTime 2018/11/7 下午3:20
 */
@RestController
@Validated
public class GoodsQueryController implements GoodsQueryProvider {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RetailGoodsService retailGoodsService;

    @Autowired
    private S2bGoodsService s2bGoodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private GoodsInfoService goodsInfoService;

    @Autowired
    private GoodsBrandService goodsBrandService;

    @Autowired
    private GoodsTagRelService goodsTagRelService;

    /**
     * 分页查询商品信息
     *
     * @param request {@link GoodsPageRequest}
     * @return 分页商品信息 {@link GoodsPageResponse}
     */
    @Override
    
    public BaseResponse<GoodsPageResponse> page(@RequestBody @Valid GoodsPageRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request, GoodsQueryRequest.class);
        GoodsQueryResponse goodsQueryResponse = goodsService.page(goodsQueryRequest);
        Page<Goods> goodsPage = goodsQueryResponse.getGoodsPage();
        GoodsPageResponse response = new GoodsPageResponse();
        MicroServicePage<GoodsVO> microServicePage = new MicroServicePage<>();
        if(Objects.nonNull(goodsPage) && CollectionUtils.isNotEmpty(goodsPage.getContent())) {
            response.setGoodsBrandList(KsBeanUtil.convert(goodsQueryResponse.getGoodsBrandList(), GoodsBrandVO.class));
            response.setGoodsCateList(KsBeanUtil.convert(goodsQueryResponse.getGoodsCateList(), GoodsCateVO.class));
            response.setGoodsInfoList(KsBeanUtil.convert(goodsQueryResponse.getGoodsInfoList(), GoodsInfoVO.class));
            response.setGoodsInfoSpecDetails(KsBeanUtil.convert(goodsQueryResponse.getGoodsInfoSpecDetails(), GoodsInfoSpecDetailRelVO.class));
            response.setImportStandard(goodsQueryResponse.getImportStandard());
            microServicePage = KsBeanUtil.convertPage(goodsPage, GoodsVO.class);
        }
        response.setGoodsPage(microServicePage);
        return BaseResponse.success(response);
    }


    /**
     * 分页查询商品信息
     *
     * @param request {@link GoodsPageRequest}
     * @return 分页商品信息 {@link GoodsPageResponse}
     */
    @Override
    public BaseResponse<GoodsPageResponse> pagedevanning(@RequestBody @Valid GoodsPageRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request, GoodsQueryRequest.class);
        goodsQueryRequest.setStoreIds(request.getStoreIds());
        GoodsQueryResponse goodsQueryResponse = goodsService.pagedevanning(goodsQueryRequest);
        GoodsInsidePageResponse goodsPage = goodsQueryResponse.getGoodsPages();
        GoodsPageResponse response = new GoodsPageResponse();
//        MicroServicePage<GoodsVO> microServicePage = new MicroServicePage<>();
        if(Objects.nonNull(goodsPage) && CollectionUtils.isNotEmpty(goodsPage.getContent())) {
            response.setGoodsBrandList(KsBeanUtil.convert(goodsQueryResponse.getGoodsBrandList(), GoodsBrandVO.class));
            response.setGoodsCateList(KsBeanUtil.convert(goodsQueryResponse.getGoodsCateList(), GoodsCateVO.class));
            response.setGoodsInfoList(KsBeanUtil.convert(goodsQueryResponse.getGoodsInfoList(), GoodsInfoVO.class));
            response.setGoodsInfoSpecDetails(KsBeanUtil.convert(goodsQueryResponse.getGoodsInfoSpecDetails(), GoodsInfoSpecDetailRelVO.class));
            response.setImportStandard(goodsQueryResponse.getImportStandard());
//            microServicePage = KsBeanUtil.convertPage(goodsPage, GoodsVO.class);
            response.setGoodsAttributeKeyVOList(KsBeanUtil.convert(goodsQueryResponse.getGoodsAttributeKeyVOList(), GoodsAttributeKeyVO.class));
        }
        response.setGoodsPages(goodsQueryResponse.getGoodsPages());
//        response.setGoodsPage(microServicePage);
        return BaseResponse.success(response);
    }



    /**
     * 分页查询商品信息
     *
     * @param request {@link GoodsPageRequest}
     * @return 分页商品信息 {@link GoodsPageResponse}
     */
    @Override
    public BaseResponse<GoodsPageResponse> bpagedevanning(@RequestBody @Valid GoodsPageRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request, GoodsQueryRequest.class);
        GoodsQueryResponse goodsQueryResponse = goodsService.bpagedevanning(goodsQueryRequest);
        GoodsInsidePageResponse goodsPage = goodsQueryResponse.getGoodsPages();
        GoodsPageResponse response = new GoodsPageResponse();
//        MicroServicePage<GoodsVO> microServicePage = new MicroServicePage<>();
        if(Objects.nonNull(goodsPage) && CollectionUtils.isNotEmpty(goodsPage.getContent())) {
            response.setGoodsBrandList(KsBeanUtil.convert(goodsQueryResponse.getGoodsBrandList(), GoodsBrandVO.class));
            response.setGoodsCateList(KsBeanUtil.convert(goodsQueryResponse.getGoodsCateList(), GoodsCateVO.class));
            response.setGoodsInfoList(KsBeanUtil.convert(goodsQueryResponse.getGoodsInfoList(), GoodsInfoVO.class));
            response.setGoodsInfoSpecDetails(KsBeanUtil.convert(goodsQueryResponse.getGoodsInfoSpecDetails(), GoodsInfoSpecDetailRelVO.class));
            response.setImportStandard(goodsQueryResponse.getImportStandard());
//            microServicePage = KsBeanUtil.convertPage(goodsPage, GoodsVO.class);
        }
        response.setGoodsPages(goodsQueryResponse.getGoodsPages());
//        response.setGoodsPage(microServicePage);
        return BaseResponse.success(response);
    }



    /**
     * 根据编号查询商品视图信息
     *
     * @param request {@link GoodsViewByIdRequest}
     * @return 商品视图信息 {@link GoodsViewByIdResponse}
     */
    @Override
    
    public BaseResponse<GoodsViewByIdResponse> getViewById(@RequestBody @Valid GoodsViewByIdRequest request) {
        String goodsId = request.getGoodsId();
        GoodsEditResponse goodsEditResponse = goodsService.findInfoById(goodsId,request.getWareId(),request.getMatchWareHouseFlag());
        GoodsViewByIdResponse goodsByIdResponse = KsBeanUtil.convert(goodsEditResponse, GoodsViewByIdResponse.class);

        return BaseResponse.success(goodsByIdResponse);
    }

    @Override
    public BaseResponse<GoodsViewByIdResponse> getViewNewById(@RequestBody @Valid GoodsNewViewByIdRequest goodsByIdRequest) {
        GoodsInfo goodsInfo = goodsInfoService.findOne(goodsByIdRequest.getGoodsInfoId());
        if (Objects.nonNull(goodsInfo) && goodsInfo.getDelFlag().equals(DeleteFlag.NO)){
            Goods goods = goodsService.getGoodsById(goodsInfo.getGoodsId());
            //goods 和 goodsinfo 填充数据 todo
            //查询营销 todo
            //获取最优营销 todo
            //查询库存 区域限购 营销限购 商品状态 todo
        }


        return null;
    }


    /**
     * 根据SPU编号和SkuId集合查询商品视图信息
     *
     * @param request {@link GoodsViewByIdAndSkuIdsRequest}
     * @return 商品视图信息 {@link GoodsViewByIdAndSkuIdsResponse}
     */
    @Override
    public BaseResponse<GoodsViewByIdAndSkuIdsResponse> getViewByIdAndSkuIds(@RequestBody @Valid GoodsViewByIdAndSkuIdsRequest request) {
        GoodsEditResponse goodsEditResponse = goodsService.findInfoByIdAndSkuIds(request.getGoodsId(),request.getSkuIds());
        GoodsViewByIdAndSkuIdsResponse goodsByIdResponse = KsBeanUtil.convert(goodsEditResponse, GoodsViewByIdAndSkuIdsResponse.class);
        return BaseResponse.success(goodsByIdResponse);
    }

    /**
     * 根据积分商品Id查询商品视图信息
     * @param request {@link GoodsViewByPointsGoodsIdRequest}
     * @return 商品视图信息 {@link GoodsViewByPointsGoodsIdResponse}
     */
    @Override
    public BaseResponse<GoodsViewByPointsGoodsIdResponse> getViewByPointsGoodsId(@RequestBody @Valid GoodsViewByPointsGoodsIdRequest request) {
        GoodsEditResponse goodsEditResponse = goodsService.findInfoByPointsGoodsId(request.getPointsGoodsId(),request.getWareId());
        GoodsViewByPointsGoodsIdResponse goodsByIdResponse = KsBeanUtil.convert(goodsEditResponse, GoodsViewByPointsGoodsIdResponse.class);
        return BaseResponse.success(goodsByIdResponse);
    }

    /**
     * 根据编号查询商品信息
     *
     * @param request {@link GoodsByIdRequest}
     * @return 商品信息 {@link GoodsByIdResponse}
     */
    @Override
    public BaseResponse<GoodsByIdResponse> getById(@RequestBody @Valid GoodsByIdRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(goodsService.getGoodsById(request.getGoodsId()),GoodsByIdResponse.class));
    }



    /**
     * 根据多个SpuID查询属性关联
     *
     * @param request {@link GoodsPropDetailRelByIdsRequest}
     * @return 属性关联信息 {@link GoodsPropDetailRelByIdsResponse}
     */
    @Override
    public BaseResponse<GoodsPropDetailRelByIdsResponse> getRefByGoodIds(
            @RequestBody @Valid GoodsPropDetailRelByIdsRequest request) {
        List<String> goodsIds = request.getGoodsIds();
        List<GoodsPropDetailRel> goodsPropDetailRelList = goodsService.findRefByGoodIds(goodsIds);

        GoodsPropDetailRelByIdsResponse response = new GoodsPropDetailRelByIdsResponse();
        List<GoodsPropDetailRelVO> goodsPropDetailRelVOList =
                KsBeanUtil.convertList(goodsPropDetailRelList, GoodsPropDetailRelVO.class);
        response.setGoodsPropDetailRelVOList(goodsPropDetailRelVOList);
        return BaseResponse.success(response);
    }

    /**
     * 待审核商品统计
     *
     * @param request {@link GoodsUnAuditCountRequest}
     * @return 待审核商品统计数量 {@link GoodsUnAuditCountResponse}
     */
    @Override
    
    public BaseResponse<GoodsUnAuditCountResponse> countUnAudit(
            @RequestBody @Valid GoodsUnAuditCountRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request, GoodsQueryRequest.class);
        Long unAuditCount = s2bGoodsService.countByTodo(goodsQueryRequest);

        GoodsUnAuditCountResponse response = new GoodsUnAuditCountResponse();
        response.setUnAuditCount(unAuditCount);
        return BaseResponse.success(response);
    }

    /**
     * 根据不同条件查询商品信息
     *
     * @param goodsByConditionRequest {@link GoodsByConditionRequest}
     * @return  {@link GoodsByConditionResponse}
     */
    @Override
    public BaseResponse<GoodsByConditionResponse> listByCondition(@RequestBody @Valid GoodsByConditionRequest goodsByConditionRequest) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(goodsByConditionRequest,GoodsQueryRequest.class);
        List<Goods> goodsList = goodsService.findAll(goodsQueryRequest);
        if (CollectionUtils.isEmpty(goodsList)){
            return BaseResponse.success(new GoodsByConditionResponse(Collections.EMPTY_LIST));
        }
        //商品副标题拼接  1箱=24盒===》1箱=24盒=11.50元/盒  挪到erp入口拼接
      /*  goodsList.stream().forEach(c->{
            String goodsSubtitle = c.getGoodsSubtitle();
            if (Objects.nonNull(c.getMinimumPrice())) {
                String subtitle =
                        goodsSubtitle.concat("=").concat(String.valueOf(c.getMinimumPrice())).concat("元").concat("/").concat(goodsSubtitle.substring(goodsSubtitle.length() - 1));
                c.setGoodsSubtitle(subtitle);
            }
        });*/
        List<GoodsVO> goodsVOList = KsBeanUtil.convert(goodsList,GoodsVO.class);
        return BaseResponse.success(new GoodsByConditionResponse(goodsVOList));
    }



    /**
     * 根据不同条件查询商品信息
     *
     * @param goodsByConditionRequest
     * @return
     */
    @Override
    public BaseResponse<GoodsCountByConditionResponse> countByCondition(@RequestBody @Valid GoodsCountByConditionRequest goodsByConditionRequest) {
        long count = goodsService.countByCondition(KsBeanUtil.convert(goodsByConditionRequest, GoodsQueryRequest.class));
        return BaseResponse.success(GoodsCountByConditionResponse.builder().count(count).build());
    }

    /**
     * 根据goodsId批量查询商品信息列表
     *
     * @param request 包含goodsIds的批量查询请求结构 {@link GoodsListByIdsRequest}
     * @return 商品信息列表 {@link GoodsListByIdsResponse}
     */
    @Override
    public BaseResponse<GoodsListByIdsResponse> listByIds(@RequestBody @Valid GoodsListByIdsRequest request){
        List<Goods> goodsList = goodsService.listByGoodsIds(request.getGoodsIds());
        if(CollectionUtils.isEmpty(goodsList)){
            return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(Collections.emptyList()).build());
        }
        List<GoodsVO> voList = KsBeanUtil.convert(goodsList, GoodsVO.class);
        return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(voList).build());
    }

    /**
     * @Description: 店铺ID统计店铺商品总数
     * @param request {@link GoodsCountByStoreIdRequest}
     * @Author: Bob
     * @Date: 2019-04-03 10:47
     */
    @Override
    public BaseResponse<GoodsCountByStoreIdResponse> countByStoreId(GoodsCountByStoreIdRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request, GoodsQueryRequest.class);
        goodsQueryRequest.setDelFlag(DeleteFlag.NO.toValue());
        long count = goodsService.countByCondition(goodsQueryRequest);
        return BaseResponse.success(GoodsCountByStoreIdResponse.builder().goodsCount(count).build());
    }

    @Override
    public BaseResponse<GoodsListByIdsResponse> listByProviderGoodsId(@Valid GoodsListByIdsRequest request) {
        List<Goods> goodsList = goodsService.listByProviderGoodsIds(request.getGoodsIds());
        if(CollectionUtils.isEmpty(goodsList)){
            return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(Collections.emptyList()).build());
        }
        List<GoodsVO> voList = KsBeanUtil.convert(goodsList, GoodsVO.class);
        return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(voList).build());
    }

    @Override
    public BaseResponse<GoodsDetailProperResponse> getGoodsDetail(@RequestBody @Valid GoodsDetailProperBySkuIdRequest request) {
        return BaseResponse.success(KsBeanUtil.convert(goodsService.findGoodsDetail(request.getSkuId()), GoodsDetailProperResponse.class));
    }

    @Override
    public BaseResponse<GoodsDetailSimpleResponse> getGoodsDetailSimple(@Valid GoodsDetailSimpleRequest goodsByIdRequest) {
        String goodsId = goodsByIdRequest.getGoodsId();

        String goodsDetailInfo = redisService.getString(RedisKeyConstant.GOODS_DETAIL_CACHE + goodsId);
        GoodsDetailSimpleResponse goodsDetailSimpleResponse = null;
        if (StringUtils.isNotBlank(goodsDetailInfo)) {
            goodsDetailSimpleResponse = JSONObject.parseObject(goodsDetailInfo, GoodsDetailSimpleResponse.class);
        } else {
            GoodsResponse response = goodsService.findGoodsSimple(goodsId);
            if (Objects.nonNull(response)) {
                redisService.setString(RedisKeyConstant.GOODS_DETAIL_CACHE + goodsId,
                        JSONObject.toJSONString(response), 6 * 60 * 60);
            }
            goodsDetailSimpleResponse = KsBeanUtil.convert(response, GoodsDetailSimpleResponse.class);
        }
        return BaseResponse.success(goodsDetailSimpleResponse);
    }

    @Override
    public BaseResponse<GoodsExportListResponse> getExportGoods(@RequestBody @Valid GoodsPageRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request, GoodsQueryRequest.class);
        List<GoodsExportVO> goodsExports = goodsService.getExportGoods(goodsQueryRequest);
        return BaseResponse.success(GoodsExportListResponse.builder().goodsExports(goodsExports).build());
    }

    @Override
    public BaseResponse<GoodsByCreatTimeAndStaueExportListResponse> getExportGoodsByCreatetimeAndStaues(@RequestBody @Valid GoodsPageRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request, GoodsQueryRequest.class);
        List<GoodsExportByTimeAndStausVO> goodsExports = goodsService.getExportGoodsbyCreatTimeAndStuas(goodsQueryRequest);
        return BaseResponse.success(GoodsByCreatTimeAndStaueExportListResponse.builder().goodsExports(goodsExports).build());
    }


    @Override
    public BaseResponse<GoodsListResponse> listByErp(){
        List<GoodsInfo> allByErp = goodsInfoService.findAllByErp();
        List<String> goodsIds = allByErp.stream()
                .map(GoodsInfo::getGoodsId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> goodsErpMap = allByErp.stream().collect(Collectors.toMap(GoodsInfo::getGoodsId, GoodsInfo::getErpGoodsInfoNo));

        Map<String,Integer> goodsInfoTypeMap=allByErp.stream().collect(Collectors.toMap(GoodsInfo::getGoodsId, GoodsInfo::getGoodsInfoType));
        List<Goods> goods = goodsService.listByGoodsIdsNoValid(goodsIds);
        List<Long> brandIds = goods.stream()
                .map(Goods::getBrandId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, GoodsBrand> brandMap = goodsBrandService.query(GoodsBrandQueryRequest.builder()
                .brandIds(brandIds)
                .delFlag(0)
                .build()).stream()
                .collect(Collectors.toMap(GoodsBrand::getBrandId, Function.identity()));
        List<GoodsVO> voList = KsBeanUtil.convert(goods, GoodsVO.class);
        voList.forEach(vo -> {
            if (Objects.nonNull(vo.getBrandId()) && Objects.nonNull(brandMap.get(vo.getBrandId()))) {
               vo.setBrandSeqNum(brandMap.get(vo.getBrandId()).getBrandSeqNum()
               );
            }
            vo.setErpNo(goodsErpMap.get(vo.getGoodsId()));
            vo.setGoodsInfoType(goodsInfoTypeMap.get(vo.getGoodsId()));
        });
        return BaseResponse.success(GoodsListResponse.builder().goodsVOList(voList).build());
    }

    @Override
    public BaseResponse<GoodsListByIdsResponse> listByGoodsIdsNoValid(GoodsListByIdsRequest request) {

        if(CollectionUtils.isEmpty(request.getGoodsIds())){
            return BaseResponse.success(null);
        }
        List<Goods> goods = goodsService.listByGoodsIdsNoValid(request.getGoodsIds());

        if(CollectionUtils.isEmpty(goods)){
            return BaseResponse.success(null);
        }
        List<GoodsVO> goodsVOS = KsBeanUtil.convertList(goods, GoodsVO.class);
        return BaseResponse.success(GoodsListByIdsResponse.builder().goodsVOList(goodsVOS).build());
    }
    @Override
    public BaseResponse<StoreGoodsExportListResponse> getStoreExportGoods(@RequestBody @Valid GoodsPageRequest request) {
        GoodsQueryRequest goodsQueryRequest = KsBeanUtil.convert(request, GoodsQueryRequest.class);
        List<StoreGoodsExportVO> goodsExports = goodsService.getStoreExportGoods(goodsQueryRequest);
        return BaseResponse.success(StoreGoodsExportListResponse.builder().goodsExports(goodsExports).build());
    }

    @Override
    public BaseResponse<List<GoodsTagRelResponse>> listGoodsTagRel(GoodsTagRelReRequest request) {
        return BaseResponse.success(goodsTagRelService.listGoodsTagRel(request));
    }

    @Override
    public BaseResponse<List<GoodsStoreOnSaleResponse>> listStoreOnSaleGoodsNum(List<Long> storeIds) {
        return BaseResponse.success(goodsService.listStoreOnSaleGoodsNum(storeIds));
    }

    @Override
    public BaseResponse<List<String>> listRecentAddedNewGoods() {
        return BaseResponse.success(goodsService.listRecentAddedNewGoods());
    }
}
