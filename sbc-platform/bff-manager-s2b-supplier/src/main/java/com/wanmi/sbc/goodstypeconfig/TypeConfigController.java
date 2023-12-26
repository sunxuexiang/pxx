package com.wanmi.sbc.goodstypeconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.goods.api.provider.goodstypeconfig.GoodsTypeConfigQueryProvider;
import com.wanmi.sbc.goods.api.provider.goodstypeconfig.GoodsTypeGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.*;
import com.wanmi.sbc.goods.api.request.merchantconfig.MerchantConfigGoodsDelByIdListRequest;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigAddResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigListResponse;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigPageResponse;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-13 8:22
 * @Description: 商户分类配置
 * @Version 1.0
 */
@Api(tags = "TypeConfigController", description = "商户分类服务 API")
@RestController
@RequestMapping("/typeConfig")
public class TypeConfigController {
    @Autowired
    private GoodsTypeConfigQueryProvider merchantConfigGoodsQueryProvider;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private GoodsTypeGoodsSaveProvider merchantConfigGoodsSaveProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "分页查询分类推荐分类")
    @PostMapping("/page")
    public BaseResponse<MerchantTypeConfigPageResponse> getPage(@RequestBody @Valid MerchantTypeConfigPageRequest pageReq) {
        pageReq.putSort("sort", "asc");
        pageReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        pageReq.setStoreId(commonUtil.getStoreId());
        return merchantConfigGoodsQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询分类推荐分类")
    @PostMapping("/list")
    public BaseResponse<MerchantTypeConfigListResponse> getList(@RequestBody @Valid MerchantTypeConfigListRequest listReq) {
        listReq.putSort("sort", "asc");
        listReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        listReq.setStoreId(commonUtil.getStoreId());
        return merchantConfigGoodsQueryProvider.list(listReq);
    }

//    @ApiOperation(value = "新增分类推荐分类")
//    @PostMapping("/add")
//    public BaseResponse<MerchantTypeConfigAddResponse> add(@RequestBody @Valid MerchantTypeConfigAddRequest addReq) {
//        return merchantConfigGoodsSaveProvider.add(addReq);
//    }


    
    @ApiOperation(value = "新增分类推荐分类批量添加")
    @PostMapping("/batachAdd")
    public BaseResponse<MerchantTypeConfigAddResponse> batachAdd(@RequestBody @Valid MerchantTypeConfigBatchAddRequest addReq) {
        //批量新增有限删除
        addReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        addReq.setStoreId(commonUtil.getStoreId());
        merchantConfigGoodsSaveProvider.deleteAll(addReq);
        operateLogMQUtil.convertAndSend("商户分类配置","新增分类推荐分类批量添加","新增分类推荐分类批量添加");
        return merchantConfigGoodsSaveProvider.batachAdd(addReq);
    }


    @ApiOperation(value = "删除分类")
    @PostMapping("/delMerchantType")
    public BaseResponse<MerchantTypeConfigAddResponse> delMerchantType(@RequestBody @Valid MerchantConfigGoodsDelByIdListRequest addReq) {
        //批量新增有限删除
        addReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        addReq.setStoreId(commonUtil.getStoreId());
        operateLogMQUtil.convertAndSend("商户分类配置","删除分类","删除分类");
     return    merchantConfigGoodsSaveProvider.deleteByIdList(addReq);

    }
    @ApiOperation(value = "分类排序")
    @PostMapping("/sortMerchantType")
    public BaseResponse sortMerchantType(@RequestBody @Valid MerchantTypeSortRequest addReq) {
        addReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        addReq.setStoreId(commonUtil.getStoreId());
        operateLogMQUtil.convertAndSend("商户分类配置","分类排序","分类排序");
        return    merchantConfigGoodsSaveProvider.sortMerchantType(addReq);
    }

    @ApiOperation(value = "分类排序-根据编号从1开始")
    @PostMapping("/sort-recommend-cat")
    public BaseResponse sortMerchantRecommendCat(@RequestBody @Valid MerchantRecommendCatSortRequest request) {
        final BaseResponse baseResponse = merchantConfigGoodsSaveProvider.sortMerchantRecommendCat(request);
        operateLogMQUtil.convertAndSend("商户分类配置", "分类排序", "分类排序");
        return baseResponse;
    }
}
    