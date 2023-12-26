package com.wanmi.sbc.ares;

import com.wanmi.ares.provider.GoodsServiceProvider;
import com.wanmi.ares.request.goods.GoodsReportRequest;
import com.wanmi.ares.view.goods.GoodsReportPageView;
import com.wanmi.ares.view.goods.GoodsTotalView;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseQueryProvider;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseListRequest;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseListResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.goods.bean.vo.WareHouseVO;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sunkun on 2017/10/16.
 */
@Api(tags = "GoodsReportController", description = "商品报表 Api")
@RestController
@RequestMapping("/goodsReport")
@Slf4j
public class GoodsReportController {

    @Autowired
    private GoodsServiceProvider goodsServiceProvider;

    @Autowired
    private GoodsInfoQueryProvider goodsInfoQueryProvider;

    @Autowired
    private WareHouseQueryProvider wareHouseQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @ApiOperation(value = "sku 列表")
    @RequestMapping(value = "/skuList", method = RequestMethod.POST)
    public BaseResponse<GoodsReportPageView> skuList(@RequestBody GoodsReportRequest request) {
        try {
            //this.initPercolateGoodInfoIds(request);
            this.defaultPage(request);
            GoodsReportPageView view = goodsServiceProvider.querySkuReport(request);
            return BaseResponse.success(view);
        } catch (Exception e) {
            log.error("查询商品报表异常,", e);
            throw new SbcRuntimeException("查询商品报表异常");
        }
    }

    @ApiOperation(value = "分类列表")
    @RequestMapping(value = "/cateList", method = RequestMethod.POST)
    public BaseResponse<GoodsReportPageView> cateList(@RequestBody GoodsReportRequest request) {
        try {
            this.defaultPage(request);
            GoodsReportPageView view = goodsServiceProvider.queryCateReport(request);
            return BaseResponse.success(view);
        } catch (Exception e) {
            log.error("查询商品分类报表异常,", e);
            throw new SbcRuntimeException("查询商品分类报表异常");
        }
    }

    @ApiOperation(value = "品牌列表")
    @RequestMapping(value = "/brandList", method = RequestMethod.POST)
    public BaseResponse<GoodsReportPageView> brandList(@RequestBody GoodsReportRequest request) {
        try {
            this.defaultPage(request);
            GoodsReportPageView view = goodsServiceProvider.queryBrandReport(request);
            return BaseResponse.success(view);
        } catch (Exception e) {
            log.error("查询商品品牌报表异常,", e);
            throw new SbcRuntimeException("查询商品品牌报表异常");
        }
    }

    @ApiOperation(value = "仓库列表")
    @RequestMapping(value = "/wareList", method = RequestMethod.GET)
    public BaseResponse<List<WareHouseVO>> wareList(WareHouseListRequest wareHouseListRequest) {
        try {
            BaseResponse<WareHouseListResponse> wareHoseResponse = wareHouseQueryProvider.list(wareHouseListRequest);
            if (!Objects.isNull(wareHoseResponse)) {
                WareHouseListResponse context = wareHoseResponse.getContext();
                List<WareHouseVO> wareHouseVOList = context.getWareHouseVOList();
                return BaseResponse.success(wareHouseVOList);
            }
            return BaseResponse.success(new ArrayList<>());
        } catch (Exception e) {
            log.error("查询仓库列表异常,", e);
            throw new SbcRuntimeException("查询仓库列表异常");
        }
    }

    @ApiOperation(value = "商品总数")
    @RequestMapping(value = "/total", method = RequestMethod.POST)
    public BaseResponse<GoodsTotalView> total(@RequestBody GoodsReportRequest request) {
        try {
            this.defaultPage(request);
            GoodsTotalView view = goodsServiceProvider.queryGoodsTotal(request);
            return BaseResponse.success(view);
        } catch (Exception e) {
            log.error("查询商品概览异常,", e);
            throw new SbcRuntimeException("查询商品概览异常");
        }
    }

    @ApiOperation(value = "店铺分类列表")
    @RequestMapping(value = "/storeCateList", method = RequestMethod.POST)
    public BaseResponse<GoodsReportPageView> storeCateList(@RequestBody GoodsReportRequest request) {
        try {
            this.defaultPage(request);
            GoodsReportPageView view = goodsServiceProvider.queryStoreCateReport(request);
            return BaseResponse.success(view);
        } catch (Exception e) {
            log.error("查询商品分类报表异常,", e);
            throw new SbcRuntimeException("查询商品分类报表异常");
        }
    }

    private void defaultPage(GoodsReportRequest request) {
        Long companyInfoId = commonUtil.getCompanyInfoId();
        if (companyInfoId != null) {
            request.setCompanyId(companyInfoId.toString());
        } else if (StringUtils.isBlank(request.getCompanyId())) {
            request.setCompanyId("0");
        }
        //从第一页开始
        if (request.getPageNum() < 1) {
            request.setPageNum(1);
        }
        request.setPageNum(request.getPageNum() - 1);
        if (request.getPageSize() == 0) {
            request.setPageSize(10);
        }
    }

    private void initPercolateGoodInfoIds(GoodsReportRequest request){
        GoodsInfoListByConditionRequest conditionRequest = new GoodsInfoListByConditionRequest();
        //conditionRequest.setWareId(request.getWareId());
        BaseResponse<GoodsInfoListByConditionResponse> goodsInfoResponse = goodsInfoQueryProvider.listByCondition(conditionRequest);
        List<GoodsInfoVO> goodsInfos = goodsInfoResponse.getContext().getGoodsInfos();
        List<String> goodInfoIds = null;
        if(CollectionUtils.isNotEmpty(goodsInfos)){
            goodInfoIds = goodsInfos.stream().map(x -> x.getGoodsInfoId()).collect(Collectors.toList());
        }else {
            goodInfoIds = new ArrayList<>();
        }
        //request.setGoodInfoIds(goodInfoIds);
    }
}
