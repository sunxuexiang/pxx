package com.wanmi.sbc.goods;

import com.google.common.collect.Lists;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.goods.api.provider.devanninggoodsinfo.DevanningGoodsInfoProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandListRequest;
import com.wanmi.sbc.goods.api.request.devanningGoodsInfo.DevanningGoodsInfoPageRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByIdRequest;
import com.wanmi.sbc.goods.api.response.devanninggoodsinfo.DevanningGoodsInfoResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Api(tags = "DevannGoodsInfoController", description = "拆箱服务")
@RestController
@RequestMapping("/devanning")
public class DevannGoodsInfoController {

    @Autowired
    private DevanningGoodsInfoProvider devanningGoodsInfoProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 查询商品拆箱规格
     *
     * @param queryRequest
     * @return
     */
    @ApiOperation(value = "查询商品拆箱规格")
    @RequestMapping(value = "/specificationByInfoId", method = RequestMethod.POST)
    public BaseResponse<DevanningGoodsInfoResponse> query(GoodsInfoByIdRequest queryRequest) {
        DevanningGoodsInfoPageRequest devanningGoodsInfoPageRequest = new DevanningGoodsInfoPageRequest();
        devanningGoodsInfoPageRequest.setGoodsInfoIds(Lists.newArrayList(queryRequest.getGoodsInfoId()));
        return BaseResponse.success(devanningGoodsInfoProvider.getQueryList(devanningGoodsInfoPageRequest).getContext());
    }

    /**
     * 新增规格
     */
    @ApiOperation(value = "新增规格")
    @RequestMapping(value = "/addSpecification", method = RequestMethod.PUT)
    public BaseResponse addSpecification(GoodsInfoByIdRequest queryRequest) {
        operateLogMQUtil.convertAndSend("商品", "拆箱服务", "新增规格：SKU编码" + (nonNull(queryRequest) ? queryRequest.getGoodsInfoId() : ""));
        DevanningGoodsInfoPageRequest devanningGoodsInfoPageRequest = new DevanningGoodsInfoPageRequest();
        devanningGoodsInfoPageRequest.setGoodsInfoIds(Lists.newArrayList(queryRequest.getGoodsInfoId()));
        return BaseResponse.success(devanningGoodsInfoProvider.getQueryList(devanningGoodsInfoPageRequest).getContext());
    }


    /**
     * 删除规格
     */
    @ApiOperation(value = "删除规格")
    @RequestMapping(value = "/delSpecification", method = RequestMethod.POST)
    public BaseResponse delSpecification(GoodsInfoByIdRequest queryRequest) {
        operateLogMQUtil.convertAndSend("商品", "拆箱服务", "删除规格：SKU编码" + (nonNull(queryRequest) ? queryRequest.getGoodsInfoId() : ""));
        DevanningGoodsInfoPageRequest devanningGoodsInfoPageRequest = new DevanningGoodsInfoPageRequest();
        devanningGoodsInfoPageRequest.setGoodsInfoIds(Lists.newArrayList(queryRequest.getGoodsInfoId()));
        return BaseResponse.success(devanningGoodsInfoProvider.getQueryList(devanningGoodsInfoPageRequest).getContext());
    }
}
