package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.cate.RetailGoodsCateQueryProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 散批商品分类API
 * @author: XinJiang
 * @time: 2022/5/5 16:58
 */
@RestController
@RequestMapping("/retail/goods/cate")
@Api(tags = "RetailGoodsCateBaseController", description = "S2B web公用-散批商品分类API")
@Slf4j
public class RetailGoodsCateBaseController {

    @Autowired
    private RetailGoodsCateQueryProvider retailGoodsCateQueryProvider;

    /**
     * 查询商品分类
     * @return 商品分类树形JSON
     */
//    @ApiOperation(value = "从缓存中获取商品分类信息列表")
//    @RequestMapping(value = {"/allGoodsCates"}, method = RequestMethod.GET)
//    public BaseResponse<String> list() {
//        return BaseResponse.success(goodsCateQueryProvider.getByCache().getContext().getResult());
//    }
}
