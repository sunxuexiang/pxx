package com.wanmi.sbc.goodsunit;

import com.google.common.collect.Lists;
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
import com.wanmi.sbc.goods.bean.vo.GoodsUnitVo;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * <p>商品单位api调用服务</p>
 * @Author shiGuangYi
 * @createDate 2023-06-14 17:22
 * @Description: TODO
 * @Version 1.0
 */

@Api(tags = "GoodsUnitController", description = "商品单位服务 API")
@RestController
@RequestMapping("/goodsUnit")
public class GoodsUnitController {
    @Autowired
    private GoodsUnitQueryProvider unitQueryProvider;
    @Autowired
    private GoodsUnitSaveProvider unitSaveProvider;

    @Autowired
    private CommonUtil commonUtil;

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
        pageRequest.setCompanyInfoIds(Lists.newArrayList(commonUtil.getCompanyInfoId(),-1L));
        BaseResponse<GoodsUnitPageResponse> pageResponse = unitQueryProvider.page(pageRequest);
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
        pageRequest.setCompanyInfoIds(Lists.newArrayList(commonUtil.getCompanyInfoId(),-1L));
        BaseResponse<GoodsUnitListResponse> pageResponse = unitQueryProvider.getList(pageRequest);
        final List<GoodsUnitVo> goodsUnitVos = pageResponse.getContext().getGoodsUnitVos();
        List<GoodsUnitVo> goodsUnitVoList = new ArrayList<>();
        Set<String> haveUnit = new HashSet<>();
        goodsUnitVos.forEach(f -> {
            if (!haveUnit.contains(f.getUnit())){
                goodsUnitVoList.add(f);
                haveUnit.add(f.getUnit());
            }
        });
        final GoodsUnitListResponse goodsUnitListResponse = new GoodsUnitListResponse();
        goodsUnitListResponse.setGoodsUnitVos(goodsUnitVoList);
        return BaseResponse.success(goodsUnitListResponse);
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
        pageRequest.setSupplierUpdate(true);
        pageRequest.setUpdatePerson(commonUtil.getOperator().getName());
        pageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        BaseResponse<GoodsUnitAddResponse> pageResponse = unitSaveProvider.updateUnit(pageRequest);
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
        pageRequest.setUpdatePerson(commonUtil.getOperator().getName());
        pageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        BaseResponse<GoodsUnitAddResponse> pageResponse = unitSaveProvider.updateStatus(pageRequest);
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
        pageRequest.setCreatePerson(commonUtil.getOperator().getName());
        pageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());

        BaseResponse<GoodsUnitAddResponse> pageResponse = unitSaveProvider.add(pageRequest);
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
        operateLogMQUtil.convertAndSend("商品","删除商品单位","删除商品单位：单位编号" + (Objects.nonNull(pageRequest) ? pageRequest.getStoreGoodsUnitId() : ""));
        pageRequest.setCompanyInfoId(commonUtil.getCompanyInfoId());
        pageRequest.setUpdatePerson(commonUtil.getOperator().getName());
        return unitSaveProvider.deleteById(pageRequest);
    }

}
