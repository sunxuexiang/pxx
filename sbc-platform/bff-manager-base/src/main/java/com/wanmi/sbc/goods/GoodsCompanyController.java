package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.company.GoodsCompanyProvider;
import com.wanmi.sbc.goods.api.provider.company.GoodsCompanyQueryProvider;
import com.wanmi.sbc.goods.api.request.brand.GoodsBrandSaveRequest;
import com.wanmi.sbc.goods.api.request.company.*;
import com.wanmi.sbc.goods.api.response.company.GoodsCompanyPageResponse;
import com.wanmi.sbc.goods.bean.vo.GoodsBrandVO;
import com.wanmi.sbc.goods.bean.vo.GoodsCompanyVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Api(description = "商品厂商API", tags = "BossGoodsCompanyController")
@RestController("bossGoodsCompanyController")
@RequestMapping("/goods")
@Validated
@Slf4j
public class GoodsCompanyController {
    @Autowired
    private GoodsCompanyQueryProvider goodsCompanyQueryProvider;

    @Autowired
    private GoodsCompanyProvider goodsCompanyProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;


    /**
     * 查询
     *
     */
    @ApiOperation(value = "分页查询厂商")
    @RequestMapping(value = "/goodsCompanyPage", method = RequestMethod.POST)
    public BaseResponse<GoodsCompanyPageResponse> list(@RequestBody GoodsCompanyPageRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        pageRequest.putSort("createTime", SortType.DESC.toValue());
        BaseResponse<GoodsCompanyPageResponse> pageResponse = goodsCompanyQueryProvider.page(pageRequest);
        return pageResponse;
    }

    /**
     * 查询厂商
     *
     * @param queryRequest 厂商参数
     * @return 厂商详情
     */
    @ApiOperation(value = "查询厂商")
    @RequestMapping(value = "/allGoodsCompany", method = RequestMethod.GET)
    public BaseResponse<List<GoodsCompanyVO>> query(GoodsCompanyListRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.putSort("createTime", SortType.DESC.toValue());
        queryRequest.putSort("companyId", SortType.ASC.toValue());
        return BaseResponse.success(goodsCompanyQueryProvider.list(queryRequest).getContext().getGoodsCompanyVOList());
    }

    /**
     * 新增商品品牌
     */
    @ApiOperation(value = "新增厂商")
    @RequestMapping(value = "/addGoodsCompany", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse<Long>> add(@Valid @RequestBody GoodsCompanySaveRequest saveRequest) {
        operateLogMQUtil.convertAndSend("商品", "新增厂商", "新增厂商：厂商名称" + (nonNull(saveRequest.getGoodsCompany()) ? saveRequest.getGoodsCompany().getCompanyName() : ""));
        GoodsCompanyAddRequest addRequest=new GoodsCompanyAddRequest();
        KsBeanUtil.copyPropertiesThird(saveRequest.getGoodsCompany(), addRequest);
        return ResponseEntity.ok(BaseResponse.success(goodsCompanyProvider.add(addRequest).getContext().getCompanyId()));
    }


    /**
     * 编辑商品品牌
     */
    @ApiOperation(value = "编辑厂商")
    @RequestMapping(value = "/editGoodsCompany", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> edit(@Valid @RequestBody GoodsCompanySaveRequest saveRequest) {
        GoodsCompanyModifyRequest modifyRequest=new GoodsCompanyModifyRequest();
        KsBeanUtil.copyPropertiesThird(saveRequest.getGoodsCompany(), modifyRequest);
        if (modifyRequest.getCompanyId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        goodsCompanyProvider.modify(modifyRequest);
        operateLogMQUtil.convertAndSend("商品", "编辑厂商", "编辑厂商：厂商名称" + (nonNull(saveRequest.getGoodsCompany()) ? saveRequest.getGoodsCompany().getCompanyName() : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }


    /**
     * 编辑商品品牌
     */
    @ApiOperation(value = "删除厂商")
    @RequestMapping(value = "/deleteGoodsCompany", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> edit(Long companyId) {
        if (companyId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GoodsCompanyModifyRequest modifyRequest=new GoodsCompanyModifyRequest();
        modifyRequest.setCompanyId(companyId);
        modifyRequest.setDelFlag(DeleteFlag.YES);
        goodsCompanyProvider.modify(modifyRequest);
        operateLogMQUtil.convertAndSend("商品", "删除厂商", "删除厂商：厂商ID" + (nonNull(companyId) ? companyId : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }

    /**
     * 编辑商品品牌
     */
    @ApiOperation(value = "启用禁用厂商")
    @RequestMapping(value = "/setGoodsCompany", method = RequestMethod.PUT)
    public ResponseEntity<BaseResponse> edit(Long companyId,Integer status) {
        if (companyId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        GoodsCompanyModifyRequest modifyRequest=new GoodsCompanyModifyRequest();
        modifyRequest.setCompanyId(companyId);
        modifyRequest.setStatus(status);
        goodsCompanyProvider.modify(modifyRequest);
        operateLogMQUtil.convertAndSend("商品", "启用禁用厂商", "启用禁用厂商：厂商ID" + (nonNull(companyId) ? companyId : "") + "启用禁用厂商：状态" + (nonNull(status) ? status : ""));
        return ResponseEntity.ok(BaseResponse.SUCCESSFUL());
    }


    /**
     * 获取厂商详情信息
     * @param companyId
     * @return
     */
    @ApiOperation(value = "获取厂商详情信息")
    @RequestMapping(value = "/goodsCompany/{companyId}", method = RequestMethod.GET)
    public BaseResponse<GoodsCompanyVO> getGoodsCompany(@PathVariable Long companyId) {
        if (companyId == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        return BaseResponse.success(goodsCompanyQueryProvider.getById(GoodsCompanyByIdRequest.builder().CompanyId(companyId)
                .build()).getContext());
    }
}
