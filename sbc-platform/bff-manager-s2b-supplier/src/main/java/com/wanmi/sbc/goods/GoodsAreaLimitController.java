package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoAreaEditRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoAreaLimitPageRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoAreaLimitPageResponse;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/23 10:05
 */
@Api(tags = "商品指定区域销售")
@RestController
@RequestMapping(value = "/goods/area/limit")
public class GoodsAreaLimitController {

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private CommonUtil commonUtil;


    @ApiOperation(value = "区域限购列表")
    @PostMapping(value = "/page")
    public BaseResponse<MicroServicePage<GoodsInfoAreaLimitPageResponse>> page(@RequestBody GoodsInfoAreaLimitPageRequest request) {
        request.setStoreId(commonUtil.getStoreId());
        return goodsInfoQueryProvider.goodsInfoAreaLimitPage(request);
    }

    @ApiOperation(value = "新增")
    @PostMapping(value = "/add")
    public BaseResponse add(@RequestBody GoodsInfoAreaEditRequest request) {
        if (CollectionUtils.isEmpty(request.getGoodsInfoIds())) {
            return BaseResponse.error("商品不能为空");
        }
        if (StringUtils.isBlank(request.getAllowedPurchaseArea())) {
            return BaseResponse.error("商品允许销售的地区ID不能为空");
        }
        if ((StringUtils.isNotBlank(request.getSingleOrderAssignArea()) && Objects.isNull(request.getSingleOrderPurchaseNum()))
            || (Objects.nonNull(request.getSingleOrderPurchaseNum()) && StringUtils.isBlank(request.getSingleOrderAssignArea()))) {
            return BaseResponse.error("商品限数量区域和数量需同时设定");
        }
        return goodsInfoQueryProvider.goodsInfoAreaLimitAdd(request);
    }

    @ApiOperation(value = "修改")
    @PostMapping(value = "/update")
    public BaseResponse update(@RequestBody GoodsInfoAreaEditRequest request) {
        if (StringUtils.isBlank(request.getGoodsInfoId())) {
            return BaseResponse.error("商品不能为空");
        }
        if (StringUtils.isBlank(request.getAllowedPurchaseArea())) {
            return BaseResponse.error("商品允许销售的地区ID不能为空");
        }
        if ((StringUtils.isNotBlank(request.getSingleOrderAssignArea()) && Objects.isNull(request.getSingleOrderPurchaseNum()))
                || (Objects.nonNull(request.getSingleOrderPurchaseNum()) && StringUtils.isBlank(request.getSingleOrderAssignArea()))) {
            return BaseResponse.error("商品限数量区域和数量需同时设定");
        }
        request.setGoodsInfoIds(Collections.singletonList(request.getGoodsInfoId()));
        return goodsInfoQueryProvider.goodsInfoAreaLimitAdd(request);
    }

    @ApiOperation(value = "详情")
    @GetMapping(value = "/get/{goodsInfoId}")
    public BaseResponse<GoodsInfoAreaLimitPageResponse> getById(@PathVariable("goodsInfoId") String goodsInfoId) {
        return goodsInfoQueryProvider.goodsInfoAreaLimitGetById(goodsInfoId);
    }

    @ApiOperation(value = "删除")
    @GetMapping(value = "/delete/{goodsInfoId}")
    public BaseResponse deleteById(@PathVariable("goodsInfoId") String goodsInfoId) {
        return goodsInfoQueryProvider.goodsInfoAreaLimitDeleteById(goodsInfoId);
    }
}
