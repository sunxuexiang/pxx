package com.wanmi.sbc.advertising;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.provider.cate.GoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.RetailGoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateByIdRequest;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoByErpNoRequest;
import com.wanmi.sbc.goods.api.response.cate.GoodsCateByIdResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsInfoVO;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingRetailProvider;
import com.wanmi.sbc.setting.api.provider.advertising.AdvertisingRetailQueryProvider;
import com.wanmi.sbc.setting.api.request.advertising.*;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingRetailPageResponse;
import com.wanmi.sbc.setting.api.response.advertising.AdvertisingRetailResponse;
import com.wanmi.sbc.setting.bean.enums.AdvertisingRetailJumpType;
import com.wanmi.sbc.setting.bean.enums.AdvertisingType;
import com.wanmi.sbc.setting.bean.vo.AdvertisingRetailGoodsConfigVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 散批广告位API
 * @author: XinJiang
 * @time: 2022/4/19 11:49
 */
@Api(description = "散批广告位API",tags = "AdvertisingRetailController")
@RestController
@RequestMapping(value = "/retail/advertising")
public class AdvertisingRetailController {

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private AdvertisingRetailProvider advertisingRetailProvider;

    @Autowired
    private AdvertisingRetailQueryProvider advertisingRetailQueryProvider;

    @Autowired
    private RetailGoodsInfoQueryProvider retailGoodsInfoQueryProvider;

    @Autowired
    private GoodsCateQueryProvider goodsCateQueryProvider;

    @Autowired
    OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "校验分类ID/商品erp编码是否有效")
    @PostMapping("/check/cateIdOrErpGoodsInfoNo")
    public BaseResponse<GoodsInfoVO> checkCateIdOrErpGoodsInfoNo(@RequestBody @Valid CheckCateIdOrErpNoRequest request){

        if (AdvertisingRetailJumpType.CATE.equals(request.getJumpType())) {
            GoodsCateByIdResponse response = goodsCateQueryProvider.getById(GoodsCateByIdRequest
                    .builder().cateId(Long.valueOf(request.getJumpCode())).build()).getContext();
            if (Objects.nonNull(response)) {
                if (3 != response.getCateGrade()) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "非三级分类，请重新选择后提交！");
                }
                if (DeleteFlag.YES.equals(response.getDelFlag())) {
                    throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "该分类已失效，请重新选择后提交！");
                }
            } else {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "分类id不存在，请重新选择后提交！");
            }
        } else if (AdvertisingRetailJumpType.GOODS.equals(request.getJumpType())) {
            GoodsInfoVO goodsInfo = retailGoodsInfoQueryProvider.getGoodsInfoByErpNo(GoodsInfoByErpNoRequest
                    .builder().erpNo(request.getJumpCode()).build()).getContext().getGoodsInfo();
            if (Objects.isNull(goodsInfo)) {
                throw new SbcRuntimeException(CommonErrorCode.SPECIFIED, "商品不存在，请重新填写erp编码！");
            }
            return BaseResponse.success(goodsInfo);
        } else {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        operateLogMQUtil.convertAndSend("设置", "散批广告位", "校验分类ID/商品erp编码是否有效");
        return BaseResponse.success(null);
    }

    @ApiOperation(value = "新增散批广告位信息")
    @PostMapping
    public BaseResponse add(@RequestBody @Valid AdvertisingRetailAddRequest request){
        Operator operator = commonUtil.getOperator();
        request.setCreatePerson(operator.getAccount());
        request.setCreateTime(LocalDateTime.now());
        request.setDelFlag(DeleteFlag.NO);
        advertisingRetailProvider.add(request);
        operateLogMQUtil.convertAndSend("设置", "散批广告位", "新增散批广告位信息");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "修改散批广告位信息")
    @PutMapping
    public BaseResponse modify(@RequestBody @Valid AdvertisingRetailModifyRequest request) {
        Operator operator = commonUtil.getOperator();
        request.setUpdatePerson(operator.getAccount());
        request.setUpdateTime(LocalDateTime.now());
        request.setDelFlag(DeleteFlag.NO);
        advertisingRetailProvider.modify(request);
        operateLogMQUtil.convertAndSend("设置", "散批广告位", "修改散批广告位信息");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "通过id删除散批广告位信息")
    @DeleteMapping
    public BaseResponse delById(@RequestBody @Valid AdvertisingRetailDelByIdRequest request){
        Operator operator = commonUtil.getOperator();
        request.setDelPerson(operator.getAccount());
        advertisingRetailProvider.delById(request);
        operateLogMQUtil.convertAndSend("设置", "散批广告位", "通过id删除散批广告位信息");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "修改散批广告配置信息状态")
    @PostMapping("/modify-status")
    public BaseResponse modifyStatus(@RequestBody @Valid AdvertisingRetailModifyStatusRequest request){
        advertisingRetailProvider.modifyStatus(request);
        operateLogMQUtil.convertAndSend("设置", "散批广告位", "修改散批广告配置信息状态");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation(value = "通过id获取散批广告位详情信息")
    @PostMapping("/get-by-id")
    public BaseResponse<AdvertisingRetailResponse> getById(@RequestBody @Valid AdvertisingRetailGetByIdRequest request) {
        AdvertisingRetailResponse response = advertisingRetailQueryProvider.getById(request).getContext();
        if (AdvertisingType.COLUMNS.equals(response.getAdvertisingType())) {
            response.getAdvertisingRetailConfigs().forEach(config -> {
                config.setSkuIds(config.getAdvertisingRetailGoodsConfigs()
                        .stream().map(AdvertisingRetailGoodsConfigVO::getGoodsInfoId).collect(Collectors.toList()));
            });
        }
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "分页获取散批广告位列表信息")
    @PostMapping("/page")
    public BaseResponse<AdvertisingRetailPageResponse> page(@RequestBody @Valid AdvertisingRetailQueryRequest request) {
        return BaseResponse.success(advertisingRetailQueryProvider.page(request).getContext());
    }

}
