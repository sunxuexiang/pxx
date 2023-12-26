package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.goods.api.provider.goodsunit.GoodsUnitQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodsunit.GoodsUnitSaveProvider;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitAddRequest;
import com.wanmi.sbc.goods.api.request.goodsunit.StoreGoodsUnitQueryRequest;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitAddResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitListResponse;
import com.wanmi.sbc.goods.api.response.goodsunit.GoodsUnitPageResponse;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * <p>商品单位</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */

@Api(tags = "StoreGoodsUnitController", description = "商品单位服务 API")
@RestController
@RequestMapping("/storeGoodsUnit")
public class StoreGoodsUnitController {
    @Autowired
    private GoodsUnitQueryProvider goodsUnitQueryProvider;
    @Autowired
    private GoodsUnitSaveProvider goodsUnitSaveProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;
    /**
     * 查询商品单位
     *
     * @param pageRequest 商品单位 {@link StoreGoodsUnitQueryRequest}
     * @return 查商品单位
     */
    @ApiOperation(value = "查询商品单位")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitPageResponse> page(@RequestBody StoreGoodsUnitQueryRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        pageRequest.putSort("createTime", SortType.DESC.toValue());
        BaseResponse<GoodsUnitPageResponse> pageResponse = goodsUnitQueryProvider.page(pageRequest);
        return pageResponse;
    }


    /**
     * 查询商品单位集合
     *
     * @param pageRequest 商品单位 {@link StoreGoodsUnitQueryRequest}
     * @return 查商品单位
     */
    @ApiOperation(value = "查询商品单位")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitListResponse> list(@RequestBody StoreGoodsUnitQueryRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        pageRequest.putSort("createTime", SortType.DESC.toValue());
        BaseResponse<GoodsUnitListResponse> pageResponse = goodsUnitQueryProvider.getList(pageRequest);
        return pageResponse;
    }


    /**
     * 修改商品单位
     *
     * @param pageRequest 商品单位 {@link StoreGoodsUnitAddRequest}
     * @return 商品单位
     */
    @ApiOperation(value = "修改商品单位")
    @RequestMapping(value = "/updateUnitById", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitAddResponse> updateUnitById(@RequestBody StoreGoodsUnitAddRequest pageRequest) {
        BaseResponse<GoodsUnitAddResponse> pageResponse = goodsUnitSaveProvider.updateUnit(pageRequest);
        operateLogMQUtil.convertAndSend("商品","修改商品单位","操作成功：单位编号" + (Objects.nonNull(pageRequest) ? pageRequest.getStoreGoodsUnitId() : ""));
        return pageResponse;
    }


    /**
     * 修改商品单位（禁用启用
     *
     * @param pageRequest 商品单位 {@link StoreGoodsUnitAddRequest}
     * @return 商品单位
     */
    @ApiOperation(value = "修改商品单位（禁用启用）")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitAddResponse> updateStatus(@RequestBody StoreGoodsUnitAddRequest pageRequest) {
        BaseResponse<GoodsUnitAddResponse> pageResponse = goodsUnitSaveProvider.updateStatus(pageRequest);
        operateLogMQUtil.convertAndSend("商品","修改商品单位（禁用启用）","操作成功：是否启用 0：停用，1：启用" + (Objects.nonNull(pageRequest) ? pageRequest.getStatus() : ""));
        return pageResponse;
    }

    /**
     * 添加商品单位
     *
     * @param pageRequest 商品单位 {@link StoreGoodsUnitAddRequest}
     * @return 商品单位
     */
    @ApiOperation(value = "添加商品单位")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitAddResponse> add(@RequestBody StoreGoodsUnitAddRequest pageRequest) {
        BaseResponse<GoodsUnitAddResponse> pageResponse = goodsUnitSaveProvider.add(pageRequest);
        operateLogMQUtil.convertAndSend("商品","添加商品单位","操作成功：商品单位" + (Objects.nonNull(pageRequest) ? pageRequest.getUnit() : ""));
        return pageResponse;
    }

    /**
     * 删除商品单位
     *
     * @param pageRequest 商品单位 {@link StoreGoodsUnitAddRequest}
     * @return 商品单位
     */
    @ApiOperation(value = "删除商品单位")
    @RequestMapping(value = "/deleteById", method = RequestMethod.POST)
    public BaseResponse<GoodsUnitAddResponse> deleteById(@RequestBody StoreGoodsUnitAddRequest pageRequest) {
        pageRequest.setSupplierUpdate(true);
        BaseResponse<GoodsUnitAddResponse> pageResponse = goodsUnitSaveProvider.deleteById(pageRequest);
        operateLogMQUtil.convertAndSend("商品","删除商品单位","操作成功：单位编号" + (Objects.nonNull(pageRequest) ? pageRequest.getStoreGoodsUnitId() : ""));
        return pageResponse;
    }
}
