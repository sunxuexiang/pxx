package com.wanmi.sbc.goodsrecommendgoods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.goodsrecommendgoods.GoodsRecommendGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsrecommendgoods.GoodsRecommendGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.goodsrecommendgoods.*;
import com.wanmi.sbc.goods.api.response.goodsrecommendgoods.*;
import javax.validation.Valid;

import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@Api(description = "商品推荐商品管理API", tags = "GoodsRecommendGoodsController")
@RestController
@RequestMapping(value = "/goodsrecommendgoods")
public class GoodsRecommendGoodsController {

    @Autowired
    private GoodsRecommendGoodsQueryProvider goodsRecommendGoodsQueryProvider;

    @Autowired
    private GoodsRecommendGoodsSaveProvider goodsRecommendGoodsSaveProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询商品推荐商品")
    @PostMapping("/page")
    public BaseResponse<GoodsRecommendGoodsPageResponse> getPage(@RequestBody @Valid GoodsRecommendGoodsPageRequest pageReq) {
        pageReq.putSort("recommendId", "desc");
        return goodsRecommendGoodsQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询商品推荐商品")
    @PostMapping("/list")
    public BaseResponse<GoodsRecommendGoodsListResponse> getList(@RequestBody @Valid GoodsRecommendGoodsListRequest listReq) {
        listReq.putSort("recommendId", "desc");
        return goodsRecommendGoodsQueryProvider.list(listReq);
    }

    @ApiOperation(value = "新增商品推荐商品")
    @PostMapping("/add")
    public BaseResponse<GoodsRecommendGoodsAddResponse> add(@RequestBody @Valid GoodsRecommendGoodsAddRequest addReq) {
        operateLogMQUtil.convertAndSend("商品推荐商品管理", "新增商品推荐商品","新增商品推荐商品:推荐的商品编号" + (Objects.nonNull(addReq) ? addReq.getGoodsInfoId() : ""));
        return goodsRecommendGoodsSaveProvider.add(addReq);
    }

}
