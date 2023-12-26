package com.wanmi.sbc.retailgoodsrecommend;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.goods.api.provider.retailgoodsrecommend.RetailGoodsRecommendSettingProvider;
import com.wanmi.sbc.goods.api.request.retailgoodscommend.RetailGoodsRecommendBatchModifySortRequest;
import com.wanmi.sbc.goods.api.request.retailgoodscommend.RetailGoodsRecommendDelByIdRequest;
import com.wanmi.sbc.goods.api.request.retailgoodscommend.RetailGoodsRecommendSettingBatchAddRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoViewByIdsResponse;
import com.wanmi.sbc.goods.bean.dto.RetailGoodsRecommendSortDTO;
import com.wanmi.sbc.goods.bean.vo.RetailGoodsRecommendSettingVO;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: 散批鲸喜推荐配置管理API
 * @author: XinJiang
 * @time: 2022/4/19 17:00
 */
@Api(description = "散批鲸喜推荐商品配置管理API", tags = "RetailGoodsRecommendSettingController")
@RestController
@RequestMapping(value = "/retail/goods/recommend")
public class RetailGoodsRecommendSettingController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private RetailGoodsRecommendSettingProvider retailGoodsRecommendSettingProvider;

    @ApiOperation("批量新增散批惊喜推荐商品")
    @PostMapping()
    public BaseResponse batchAdd(@RequestBody @Valid RetailGoodsRecommendSettingBatchAddRequest request) {
        retailGoodsRecommendSettingProvider.batchAdd(request);
        operateLogMQUtil.convertAndSend("散批鲸喜推荐商品配置管理", "批量新增散批惊喜推荐商品", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation("批量修改散批惊喜推荐商品排序，拖拽排序")
    @PutMapping()
    public BaseResponse batchModifySort(@RequestBody List<RetailGoodsRecommendSortDTO> retailGoodsRecommendSortDTOList) {
        retailGoodsRecommendSettingProvider.batchModifySort(new RetailGoodsRecommendBatchModifySortRequest(retailGoodsRecommendSortDTOList));
        operateLogMQUtil.convertAndSend("散批鲸喜推荐商品配置管理", "批量修改散批惊喜推荐商品排序，拖拽排序", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation("通过推荐id移除散批推荐商品信息")
    @DeleteMapping()
    public BaseResponse delById(@RequestBody @Valid RetailGoodsRecommendDelByIdRequest request) {
        retailGoodsRecommendSettingProvider.delById(request);
        operateLogMQUtil.convertAndSend("散批鲸喜推荐商品配置管理", "通过推荐id移除散批推荐商品信息", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation("批量移除散批推荐商品信息")
    @PostMapping("/del-by-ids")
    public BaseResponse delByIds(@RequestBody @Valid RetailGoodsRecommendDelByIdRequest request) {
        retailGoodsRecommendSettingProvider.delByIds(request);
        operateLogMQUtil.convertAndSend("散批鲸喜推荐商品配置管理", "批量移除散批推荐商品信息", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation("获取散批推荐商品列表商品信息")
    @PostMapping("/get-list-by-cache")
    public BaseResponse<GoodsInfoViewByIdsResponse> getListByCache(){
        GoodsInfoViewByIdsResponse  response = new GoodsInfoViewByIdsResponse();
        if (redisService.hasKey(CacheKeyConstant.RETAIL_GOODS_RECOMMEND)) {
            response = JSONObject.parseObject(redisService.getString(CacheKeyConstant.RETAIL_GOODS_RECOMMEND),GoodsInfoViewByIdsResponse.class);
        } else {
            retailGoodsRecommendSettingProvider.fillRedis();
            response = JSONObject.parseObject(redisService.getString(CacheKeyConstant.RETAIL_GOODS_RECOMMEND),GoodsInfoViewByIdsResponse.class);
        }
        return BaseResponse.success(response);
    }
}
