package com.wanmi.sbc.goodsattribute;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.goods.api.provider.goodsattribute.GoodsAttributeQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsattribute.GoodsAttributeSaveProvider;
import com.wanmi.sbc.goods.api.request.goodsattribute.GoodsAttributeQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributeListResponse;
import com.wanmi.sbc.goods.api.response.goodsattribute.GoodsAttributePageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>商品属性api调用服务(运营端)</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */

@Api(tags = "StoreGoodsAttributeController", description = "商品属性服务 API")
@RestController("StoreGoodsAttributeController")
@RequestMapping("/storeGoodsAttribute")
public class StoreGoodsAttributeController {
    @Autowired
    private GoodsAttributeQueryProvider goodsAttributeQueryProvider;
    @Autowired
    private GoodsAttributeSaveProvider goodsAttributeSaveProvider;
    /**
     * 查询商品属性
     *
     * @param pageRequest 商品属性 {@link GoodsAttributeQueryRequest}
     * @return 查商品属性
     */
    @ApiOperation(value = "查询商品属性")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<GoodsAttributePageResponse> page(@RequestBody GoodsAttributeQueryRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        pageRequest.putSort("createTime", SortType.DESC.toValue());
        BaseResponse<GoodsAttributePageResponse> pageResponse = goodsAttributeQueryProvider.page(pageRequest);
         return pageResponse;
    }


    /**
     * 查询商品属性集合
     *
     * @param pageRequest 商品属性 {@link GoodsAttributeQueryRequest}
     * @return 查商品属性
     */
    @ApiOperation(value = "查询商品属性")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse<GoodsAttributeListResponse> list(@RequestBody GoodsAttributeQueryRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        pageRequest.putSort("createTime", SortType.DESC.toValue());
        BaseResponse<GoodsAttributeListResponse> pageResponse = goodsAttributeQueryProvider.getList(pageRequest);
        return pageResponse;
    }

}
