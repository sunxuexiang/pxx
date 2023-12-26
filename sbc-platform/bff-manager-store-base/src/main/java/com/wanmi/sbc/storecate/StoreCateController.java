package com.wanmi.sbc.storecate;

import com.wanmi.sbc.common.annotation.MultiSubmit;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsRetailGoodsInfoElasticService;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.storecate.*;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateAddResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateByStoreCateIdResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByStoreIdResponse;
import com.wanmi.sbc.goods.bean.dto.StoreCateSortDTO;
import com.wanmi.sbc.goods.bean.vo.StoreCateResponseVO;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponMarketingScopeProvider;
import com.wanmi.sbc.marketing.api.provider.coupon.CouponMarketingScopeQueryProvider;
import com.wanmi.sbc.marketing.api.request.coupon.CouponMarketingScopeBatchAddRequest;
import com.wanmi.sbc.marketing.api.request.coupon.CouponMarketingScopeByScopeIdRequest;
import com.wanmi.sbc.marketing.bean.dto.CouponMarketingScopeDTO;
import com.wanmi.sbc.marketing.bean.vo.CouponMarketingScopeVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 店铺分类管理Controller
 * Author: bail
 * Time: 2017/11/14.10:21
 */
@Api(tags = "StoreCateController", description = "店铺分类管理服务API")
@RestController
@RequestMapping("/storeCate")
public class StoreCateController {
    @Autowired
    private StoreCateProvider storeCateProvider;

    @Autowired
    private StoreCateQueryProvider storeCateQueryProvider;

    @Autowired
    GoodsAresProvider goodsAresProvider;

    @Autowired
    private CouponMarketingScopeProvider couponMarketingScopeProvider;

    @Autowired
    private CouponMarketingScopeQueryProvider couponMarketingScopeQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private EsGoodsInfoElasticService esGoodsInfoElasticService;

    @Autowired
    private EsRetailGoodsInfoElasticService esRetailGoodsInfoElasticService;

    /**
     * 查询店铺商品分类List
     */
    @ApiOperation(value = "查询店铺商品分类List")
    @RequestMapping(method = RequestMethod.GET)
    public BaseResponse<List<StoreCateResponseVO>> list() {
        Long storeId = commonUtil.getStoreId();
        BaseResponse<StoreCateListByStoreIdResponse> baseResponse =
                storeCateQueryProvider.listByStoreId(new StoreCateListByStoreIdRequest(storeId));
        StoreCateListByStoreIdResponse response = baseResponse.getContext();
        if (Objects.isNull(response)) {
            return BaseResponse.success(Collections.emptyList());
        }
        final List<StoreCateResponseVO> storeCateResponseVOList = response.getStoreCateResponseVOList();
        final Map<Long, List<StoreCateResponseVO>> collect = storeCateResponseVOList.stream().collect(Collectors.groupingBy(StoreCateResponseVO::getCateParentId));
        Set<Long> sameLevelIds = new HashSet<>();
        final List<StoreCateResponseVO> storeCateResponseVOS = collect.get(0L);
        if (CollectionUtils.isNotEmpty(storeCateResponseVOS)){
            storeCateResponseVOS.forEach(o1 -> {
                final List<StoreCateResponseVO> storeCateResponseVOS1 = collect.get(o1.getStoreCateId());
                if (CollectionUtils.isEmpty(storeCateResponseVOS1)){
                    return;
                }
                if (storeCateResponseVOS1.size() ==1 && Objects.equals(o1.getCateName(), storeCateResponseVOS1.get(0).getCateName())){
                    sameLevelIds.add(o1.getStoreCateId());
                }
            });
        }
        storeCateResponseVOList.forEach(f -> {
            if (sameLevelIds.contains(f.getStoreCateId())){
                f.setSameLevel(true);
            }
        });
        return BaseResponse.success(storeCateResponseVOList);
    }

    /**
     * 新增店铺商品分类
     *
     * @param saveRequest 新增的分类信息
     */
    @ApiOperation(value = "新增店铺商品分类")
    @RequestMapping(method = RequestMethod.POST)
    @MultiSubmit
    public BaseResponse<StoreCateResponseVO> add(@RequestBody StoreCateAddRequest saveRequest) {
        int leafAdd = BooleanUtils.isTrue(saveRequest.getAutoInitLeaf()) ? 1 : 0;
        Long storeId = commonUtil.getStoreId();
        saveRequest.setStoreId(storeId);
        saveRequest.setAutoInitLeaf(null);
        StoreCateAddResponse cateResponse = storeCateProvider.add(saveRequest).getContext();
        if (leafAdd == 1){
            final StoreCateAddRequest auto = new StoreCateAddRequest();
            auto.setStoreId(storeId);
            auto.setCateImg(saveRequest.getCateImg());
            auto.setCateParentId(cateResponse.getStoreCateResponseVO().getStoreCateId());
            auto.setCateName(saveRequest.getCateName());
            saveRequest.setAutoInitLeaf(true);
            storeCateProvider.add(auto);
        }
        //查询父分类是否关联优惠券
        List<CouponMarketingScopeVO> couponMarketingScopes = couponMarketingScopeQueryProvider.listByScopeId(
                CouponMarketingScopeByScopeIdRequest.builder().scopeId(String.valueOf(cateResponse.getStoreCateResponseVO()
                        .getCateParentId())).build()).getContext().getScopeVOList();
        if (CollectionUtils.isNotEmpty(couponMarketingScopes)) {
            couponMarketingScopes.stream().map(couponScope -> {
                couponScope.setMarketingScopeId(null);
                couponScope.setCateGrade(couponScope.getCateGrade() + 1);
                couponScope.setScopeId(String.valueOf(cateResponse.getStoreCateResponseVO().getStoreCateId()));
                return couponScope;
            });
            couponMarketingScopeProvider.batchAdd(
                    CouponMarketingScopeBatchAddRequest.builder()
                            .scopeDTOList(KsBeanUtil.convert(couponMarketingScopes, CouponMarketingScopeDTO.class))
                            .build());
        }
        if (saveRequest.getCateParentId() == null || saveRequest.getCateParentId().equals(0L)) {
            operateLogMQUtil.convertAndSend("商品", "新增一级分类", "新增一级分类：" + saveRequest.getCateName());
        } else {
            operateLogMQUtil.convertAndSend("商品", "添加子分类", "添加子分类：" + saveRequest.getCateName());
        }

        return BaseResponse.success(cateResponse.getStoreCateResponseVO());
    }

    /**
     * 获取商品分类详情信息
     *
     * @param storeCateId 店铺商品分类标识
     */
    @ApiOperation(value = "获取商品分类详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "storeCateId", value = "店铺分类Id", required = true)
    @RequestMapping(value = "/{storeCateId}", method = RequestMethod.GET)
    public BaseResponse<StoreCateByStoreCateIdResponse> list(@PathVariable Long storeCateId) {
        BaseResponse<StoreCateByStoreCateIdResponse> baseResponse =
                storeCateQueryProvider.getByStoreCateId(new StoreCateByStoreCateIdRequest(storeCateId));
        return BaseResponse.success(baseResponse.getContext());
    }

    /**
     * 编辑店铺商品分类
     *
     * @param saveRequest 编辑的分类信息
     */
    @ApiOperation(value = "编辑店铺商品分类")
    @RequestMapping(method = RequestMethod.PUT)
    public BaseResponse edit(@RequestBody StoreCateModifyRequest saveRequest) {
        Long storeId = commonUtil.getStoreId();
        saveRequest.setStoreId(storeId);
        storeCateProvider.modify(saveRequest);
        if (BooleanUtils.isTrue(saveRequest.getAutoInitLeaf())){
            final StoreCateListByStoreIdRequest storeIdRequest = new StoreCateListByStoreIdRequest();
            storeIdRequest.setStoreId(storeId);
            final BaseResponse<StoreCateListByStoreIdResponse> baseResponse = storeCateQueryProvider.listByStoreId(storeIdRequest);
            final List<StoreCateResponseVO> storeCateResponseVOList = baseResponse.getContext().getStoreCateResponseVOList();
            StoreCateResponseVO storeCateResponseVO = null;
            int i = 0;
            for (StoreCateResponseVO o : storeCateResponseVOList) {
                if (Objects.equals(saveRequest.getStoreCateId(), o.getCateParentId())) {
                    storeCateResponseVO = o;
                    i ++;
                }
            }
            if (storeCateResponseVO == null || i != 1){
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED,"修改分类失败");
            }
            final StoreCateModifyRequest auto = KsBeanUtil.convert(storeCateResponseVO, StoreCateModifyRequest.class);
            if (StringUtils.isNotBlank(saveRequest.getCateImg())) auto.setCateImg(saveRequest.getCateImg());
            if (StringUtils.isNotBlank(saveRequest.getCateName())) auto.setCateName(saveRequest.getCateName());
            auto.setAutoInitLeaf(true);
            storeCateProvider.modify(auto);
        }
        //ares埋点-商品-新增店铺分类
        operateLogMQUtil.convertAndSend("商品", "编辑分类", "编辑分类：" + saveRequest.getCateName());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 检测店铺分类是否有子类
     *
     * @param queryRequest 查询条件(分类标识)
     */
    @ApiOperation(value = "检测店铺分类是否有子类")
    @RequestMapping(value = "/checkHasChild", method = RequestMethod.POST)
    public BaseResponse<Integer> checkHasChild(@RequestBody StoreCateQueryHasChildRequest queryRequest) {
        return BaseResponse.success(storeCateQueryProvider.queryHasChild(queryRequest).getContext().getResult());
    }

    /**
     * 检测店铺分类是否关联了商品
     *
     * @param queryRequest 查询条件(分类标识)
     */
    @ApiOperation(value = "检测店铺分类是否关联了商品")
    @RequestMapping(value = "/checkHasGoods", method = RequestMethod.POST)
    public BaseResponse<Integer> checkHasGoods(@RequestBody StoreCateQueryHasGoodsRequest queryRequest) {
        return BaseResponse.success(storeCateQueryProvider.queryHasGoods(queryRequest).getContext().getResult());
    }

    /**
     * 删除店铺商品分类
     */
    @ApiOperation(value = "删除店铺商品分类")
    @ApiImplicitParam(paramType = "path", dataType = "Long", name = "storeCateId", value = "店铺分类Id", required = true)
    @RequestMapping(value = "/{storeCateId}", method = RequestMethod.DELETE)
    public BaseResponse delete(@PathVariable Long storeCateId) {
        StoreCateDeleteRequest saveRequest = new StoreCateDeleteRequest();
        Long storeId = commonUtil.getStoreId();
        saveRequest.setStoreId(storeId);
        saveRequest.setStoreCateId(storeCateId);
        Map<String, Object> returnMap = storeCateProvider.delete(saveRequest).getContext().getHashMap();
        esGoodsInfoElasticService.delStoreCateIds((List) (returnMap.get("allCate")), storeId);
        esRetailGoodsInfoElasticService.delStoreCateIds((List)(returnMap.get("allCate")), storeId);
        StoreCateByStoreCateIdRequest request = new StoreCateByStoreCateIdRequest();
        request.setStoreCateId(storeCateId);
        BaseResponse<StoreCateByStoreCateIdResponse> baseResponse =
                storeCateQueryProvider.getByStoreCateId(request);
        StoreCateByStoreCateIdResponse response = baseResponse.getContext();
        if (Objects.nonNull(response)) {
            StoreCateResponseVO storeCateResponseVO = response.getStoreCateResponseVO();
            operateLogMQUtil.convertAndSend("商品", "删除分类",
                    "删除分类：" + (Objects.nonNull(storeCateResponseVO) ? storeCateResponseVO.getCateName() : ""));
        }
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 商家APP里店铺分类排序
     *
     * @param saveRequest
     */
    @ApiOperation(value = "商家APP里店铺分类排序")
    @RequestMapping(value = "/allInLine", method = RequestMethod.POST)
    public BaseResponse<Integer> batchSortCate(@RequestBody StoreCateBatchSortRequest saveRequest) {
        storeCateProvider.batchSort(saveRequest);
        operateLogMQUtil.convertAndSend("商品", "商家APP里店铺分类排序", "商家APP里店铺分类排序");
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 拖拽排序店铺分类
     */
    @ApiOperation(value = "拖拽排序店铺分类")
    @RequestMapping(value = "/sort", method = RequestMethod.PUT)
    public BaseResponse goodsCateSort(@RequestBody List<StoreCateSortDTO> sortRequestList) {
        operateLogMQUtil.convertAndSend("商品", "拖拽排序店铺分类", "拖拽排序店铺分类");
        return storeCateProvider.batchModifySort(new StoreCateBatchModifySortRequest(sortRequestList));
    }

}
