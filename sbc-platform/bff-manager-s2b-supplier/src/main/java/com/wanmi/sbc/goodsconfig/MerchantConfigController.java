package com.wanmi.sbc.goodsconfig;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SpecialSymbols;
import com.wanmi.sbc.common.redis.CacheKeyConstant;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.merchantconfig.MerchantConfigGoodsSaveProvider;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantGoodsSortRequest;
import com.wanmi.sbc.goods.api.request.goodstypeconfig.MerchantRecommendGoodsSortRequest;
import com.wanmi.sbc.goods.api.request.merchantconfig.*;
import com.wanmi.sbc.goods.api.response.goodstypeconfig.MerchantTypeConfigAddResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsAddResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsListResponse;
import com.wanmi.sbc.goods.api.response.merchantconfg.MerchantConfigGoodsPageResponse;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.util.CommonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author shiGuangYi
 * @createDate 2023-06-13 8:22
 * @Description: 商户商品配置
 * @Version 1.0
 */
@Api(tags = "MerchantConfigController", description = "商户商品服务 API")
@RestController
@RequestMapping("/merchantConfig")
public class MerchantConfigController {
    @Autowired
    private MerchantConfigGoodsQueryProvider merchantConfigGoodsQueryProvider;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MerchantConfigGoodsSaveProvider merchantConfigGoodsSaveProvider;
    @ApiOperation(value = "分页查询商品推荐商品")
    @PostMapping("/page")
    public BaseResponse<MerchantConfigGoodsPageResponse> getPage(@RequestBody @Valid MerchantConfigGoodsPageRequest pageReq) {
        pageReq.putSort("sort", "asc");
        pageReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        pageReq.setStoreId(commonUtil.getStoreId());
        return merchantConfigGoodsQueryProvider.page(pageReq);
    }

    @ApiOperation(value = "列表查询商品推荐商品")
    @PostMapping("/list")
    public BaseResponse<MerchantConfigGoodsListResponse> getList(@RequestBody @Valid MerchantConfigGoodsQueryRequest listReq) {
        listReq.putSort("sort", "asc");
        listReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        listReq.setStoreId(commonUtil.getStoreId());
        return merchantConfigGoodsQueryProvider.list(listReq);
    }

    @ApiOperation(value = "新增商品推荐商品")
    @PostMapping("/add")
    public BaseResponse<MerchantConfigGoodsAddResponse> add(@RequestBody @Valid MerchantConfigGoodsAddRequest addReq) {
        addReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        addReq.setStoreId(commonUtil.getStoreId());
        BaseResponse<MerchantConfigGoodsAddResponse> add = merchantConfigGoodsSaveProvider.add(addReq);
        return add ;
    }



    @ApiOperation(value = "新增商品推荐商品批量添加")
    @PostMapping("/batachAdd")
    public BaseResponse<MerchantConfigGoodsAddResponse> batachAdd(@RequestBody @Valid MerchantConfigGoodsBatchAddRequest addReq) {
        //批量新增有限删除
        addReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        addReq.setStoreId(commonUtil.getStoreId());
        merchantConfigGoodsSaveProvider.deleteAll(addReq);
        return  merchantConfigGoodsSaveProvider.batachAdd(addReq);
    }
    @ApiOperation(value = "删除商品")
    @PostMapping("/delMerchantGoods")
    public BaseResponse<MerchantTypeConfigAddResponse> delMerchantType(@RequestBody @Valid MerchantConfigGoodsDelByIdListRequest addReq) {
        //批量新增有限删除
        addReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        addReq.setStoreId(commonUtil.getStoreId());
        return merchantConfigGoodsSaveProvider.deleteByIdList(addReq);


    }
    @ApiOperation(value = "商品排序")
    @PostMapping("/sortMerchantGoods")
    public BaseResponse sortMerchantGoods(@RequestBody @Valid MerchantGoodsSortRequest addReq) {
        addReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
        addReq.setStoreId(commonUtil.getStoreId());
       return merchantConfigGoodsSaveProvider.sortMerchantGoods(addReq);

    }

    @ApiOperation(value = "商品排序，从1开始")
    @PostMapping("/sortMerchantRecommendGoods")
    public BaseResponse sortMerchantRecommendGoods(@RequestBody @Valid MerchantRecommendGoodsSortRequest request) {

        request.setCompanyInfoId(commonUtil.getCompanyInfoId());
        request.setStoreId(commonUtil.getStoreId());
        BaseResponse baseResponse = merchantConfigGoodsSaveProvider.sortMerchantRecommendGoods(request);
        if (baseResponse.getCode().equals(CommonErrorCode.SUCCESSFUL)){
            MerchantConfigGoodsQueryRequest listReq =new MerchantConfigGoodsQueryRequest();
            listReq.putSort("sort", "asc");
            listReq.setCompanyInfoId(commonUtil.getCompanyInfoId());
            listReq.setStoreId(commonUtil.getStoreId());
            BaseResponse<MerchantConfigGoodsListResponse> list = merchantConfigGoodsQueryProvider.list(listReq);
            if (CollectionUtils.isNotEmpty(list.getContext().getGoodsRecommendGoodsVOList())){
                String key = CacheKeyConstant.SCREEN_ORDER_ADD_LAST_COMPANY+listReq.getStoreId()+ SpecialSymbols.UNDERLINE.toValue();
                redisService.setString(key, JSONObject.toJSONString(list.getContext().getGoodsRecommendGoodsVOList()));
            }
        }
        return baseResponse;
    }




}
    