package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandRecommendProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandRecommendRequest;
import com.wanmi.sbc.goods.api.response.brand.GoodsBrandRecommendResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(description = "商品品牌推荐API", tags = "GoodsBrandRecommendController")
@RestController("goodsBrandRecommendController")
@RequestMapping("/goodsBrandRecommend")
@Validated
@Slf4j
public class GoodsBrandRecommendController {

    @Autowired
    private GoodsBrandRecommendProvider goodsBrandRecommendProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "添加商品品牌推荐")
    @RequestMapping(value = "/addGoodsBrandRecommend", method = RequestMethod.POST)
    public BaseResponse addGoodsBrandRecommend(@RequestBody GoodsBrandRecommendRequest goodsBrandRecommendRequest) {
        if (goodsBrandRecommendRequest.getAddGoodsBrandRecommendVOList().isEmpty()) {
            return BaseResponse.SUCCESSFUL();
        }
        goodsBrandRecommendProvider.addGoodsBrandRecommend(goodsBrandRecommendRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品品牌推荐", "添加商品品牌推荐", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "修改商品品牌推荐上下架")
    @RequestMapping(value = "/updateAddedGoodsBrandRecommend", method = RequestMethod.POST)
    public BaseResponse updateAddedGoodsBrandRecommend(@RequestBody GoodsBrandRecommendRequest goodsBrandRecommendRequest) {
        goodsBrandRecommendProvider.updateAddedGoodsBrandRecommend(goodsBrandRecommendRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品品牌推荐", "修改商品品牌推荐上下架", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "删除商品品牌推荐")
    @RequestMapping(value = "/deleteGoodsBrandRecommend", method = RequestMethod.POST)
    public BaseResponse deleteGoodsBrandRecommend(@RequestBody GoodsBrandRecommendRequest goodsBrandRecommendRequest) {
        goodsBrandRecommendProvider.deleteGoodsBrandRecommend(goodsBrandRecommendRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品品牌推荐", "删除商品品牌推荐", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "查询商品品牌推荐列表")
    @RequestMapping(value = "/listGoodsBrandRecommend", method = RequestMethod.POST)
    public BaseResponse<GoodsBrandRecommendResponse> listGoodsBrandRecommend(@RequestBody GoodsBrandRecommendRequest goodsBrandRecommendRequest) {
        //BaseResponse<GoodsBrandRecommendResponse> goodsBrandRecommendResponseBaseResponse = goodsBrandRecommendProvider.listGoodsBrandRecommend(goodsBrandRecommendRequest);
        BaseResponse<GoodsBrandRecommendResponse> goodsBrandRecommendResponseBaseResponse = goodsBrandRecommendProvider.listGoodsBrandRecommendDataSupplement(goodsBrandRecommendRequest);
        return goodsBrandRecommendResponseBaseResponse;
    }

    @ApiOperation(value = "修改商品品牌推荐名称是否显示")
    @RequestMapping(value = "/updateGoodsBrandRecommendNameStatus", method = RequestMethod.POST)
    public BaseResponse updateGoodsBrandRecommendNameStatus(@RequestParam("nameStatus") Integer nameStatus){
        goodsBrandRecommendProvider.updateGoodsBrandRecommendNameStatus(nameStatus);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品品牌推荐", "修改商品品牌推荐名称是否显示", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }
}
