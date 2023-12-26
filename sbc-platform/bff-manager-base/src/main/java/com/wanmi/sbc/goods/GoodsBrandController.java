package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.es.elastic.EsCateBrandService;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandProvider;
import com.wanmi.sbc.goods.api.provider.brand.GoodsBrandQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.*;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.validator.GoodsBrandValidator;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * 商品品牌服务
 * Created by daiyitian on 17/4/12.
 */
@Api(tags = "GoodsBrandController", description = "商品品牌服务")
@RestController
@RequestMapping("/goods")
public class GoodsBrandController {

    @Autowired
    private GoodsBrandQueryProvider goodsBrandQueryProvider;

    @Autowired
    private GoodsBrandProvider goodsBrandProvider;

    @Autowired
    private GoodsBrandValidator goodsBrandValidator;

    @Autowired
    private EsCateBrandService esCateBrandService;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @Autowired
    private CommonUtil commonUtil;

    @InitBinder
    public void initBinder(DataBinder binder) {
        if (binder.getTarget() instanceof GoodsBrandSaveRequest) {
            binder.setValidator(goodsBrandValidator);
        }
    }


    /**
     * 查询商品品牌
     *
     * @param queryRequest 商品品牌参数
     * @return 商品详情
     */
    @ApiOperation(value = "查询全部商品品牌")
    @RequestMapping(value = "/listGoodsBrands", method = RequestMethod.GET)
    public BaseResponse<List<GoodsBrandVO>> queryAll(GoodsBrandListRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.putSort("createTime", SortType.DESC.toValue());
        queryRequest.putSort("brandId", SortType.ASC.toValue());
        return BaseResponse.success(goodsBrandQueryProvider.listAll(queryRequest).getContext().getGoodsBrandVOList());
    }

    /**
     * 查询商品品牌
     *
     * @param queryRequest 商品品牌参数
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品品牌")
    @RequestMapping(value = "/allGoodsBrands", method = RequestMethod.GET)
    public BaseResponse<List<GoodsBrandVO>> query(GoodsBrandListRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.putSort("createTime", SortType.DESC.toValue());
        queryRequest.putSort("brandId", SortType.ASC.toValue());
        return BaseResponse.success(goodsBrandQueryProvider.list(queryRequest).getContext().getGoodsBrandVOList());
    }

    /**
     * 新增商品品牌
     */
    @ApiOperation(value = "新增商品品牌")
    @RequestMapping(value = "/goodsBrand", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Long>> add(@Valid @RequestBody GoodsBrandSaveRequest saveRequest) {
        GoodsBrandAddRequest addRequest = new GoodsBrandAddRequest();
        KsBeanUtil.copyPropertiesThird(saveRequest.getGoodsBrand(), addRequest);
        if(addRequest.getBrandSeqNum()!=null&&addRequest.getBrandSeqNum()==0){
            addRequest.setBrandSeqNum(null);
        }

        operateLogMQUtil.convertAndSend("商品", "新增品牌",
                "新增品牌：" + addRequest.getBrandName()+
                        "，排序序号："+ addRequest.getBrandSeqNum()+
                        "，操作时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                        "，操作人:"+commonUtil.getOperator().getName());

        return ResponseEntity.ok(BaseResponse.success(goodsBrandProvider.add(addRequest).getContext().getBrandId()));
    }

    /**
     * 编辑商品品牌
     */
    @ApiOperation(value = "编辑商品品牌")
    @RequestMapping(value = "/goodsBrand", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> edit(@Valid @RequestBody GoodsBrandSaveRequest saveRequest) {
        if (saveRequest.getGoodsBrand().getBrandId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GoodsBrandModifyRequest request = new GoodsBrandModifyRequest();
        KsBeanUtil.copyPropertiesThird(saveRequest.getGoodsBrand(), request);
        GoodsBrandVO oldGoodsBrand= goodsBrandQueryProvider.getById(GoodsBrandByIdRequest.builder().brandId(request.getBrandId())
                .build()).getContext();
        if(request.getBrandSeqNum()!=null&&request.getBrandSeqNum()==0){
            request.setBrandSeqNum(null);
        }
        GoodsBrandVO newGoodsBrand = goodsBrandProvider.modify(request).getContext();
        esCateBrandService.updateBrandFromEs(false, newGoodsBrand);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品", "编辑品牌",
                "编辑品牌：" + request.getBrandName()+
                        "，操作前排序:"+oldGoodsBrand.getBrandSeqNum()+
                        "，操作后排序:"+newGoodsBrand.getBrandSeqNum()+
                        "，操作时间:"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                        "，操作人:"+commonUtil.getOperator().getName());
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 获取商品品牌详情信息
     *
     * @param brandId 商品品牌编号
     * @return 商品详情
     */
    @ApiOperation(value = "获取商品品牌详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "Long",
            name = "brandId", value = "商品品牌编号", required = true)
    @RequestMapping(value = "/goodsBrand/{brandId}", method = RequestMethod.GET)
    public BaseResponse<GoodsBrandVO> getGoodsBrand(@PathVariable Long brandId) {
        if (brandId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return BaseResponse.success(goodsBrandQueryProvider.getById(GoodsBrandByIdRequest.builder().brandId(brandId)
                .build()).getContext());
    }
}
