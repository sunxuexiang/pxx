package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.ErrorCodeConstant;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.provider.cate.RetailGoodsCateProvider;
import com.wanmi.sbc.goods.api.provider.cate.RetailGoodsCateQueryProvider;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateAddRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateBatchModifySortRequest;
import com.wanmi.sbc.goods.api.request.cate.GoodsCateListByConditionRequest;
import com.wanmi.sbc.goods.bean.dto.GoodsCateSortDTO;
import com.wanmi.sbc.goods.bean.vo.GoodsCateVO;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @description: 散批商品分类服务
 * @author: XinJiang
 * @time: 2022/5/5 16:25
 */
@RequestMapping("/retail/goods/cate")
@RestController
@Validated
@Api(tags = "BossRetailGoodsCateController",description = "散批商品分类服务" )
public class BossRetailGoodsCateController {

    @Autowired
    private RetailGoodsCateProvider retailGoodsCateProvider;

    @Autowired
    private RetailGoodsCateQueryProvider retailGoodsCateQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "查询散批商品分类")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public BaseResponse<List<GoodsCateVO>> list(GoodsCateListByConditionRequest queryRequest) {
        if (Objects.nonNull(queryRequest.getCateGrade())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"分类层级不能为空！");
        }
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.putSort("sort", SortType.ASC.toValue());
        return BaseResponse.success(retailGoodsCateQueryProvider.listByCondition(queryRequest).getContext().getGoodsCateVOList());
    }

    /**
     * 新增散批商品推荐分类
     */
    @ApiOperation(value = "新增散批商品推荐分类")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse addRetailRecommend(@Valid @RequestBody GoodsCateAddRequest saveRequest) {
        if (Objects.nonNull(saveRequest.getCateGrade())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"分类层级不能为空！");
        }
        retailGoodsCateProvider.addRetailGoodsCate(saveRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("散批商品分类服务", "新增散批商品推荐分类", "操作成功:分类层级" + saveRequest.getCateGrade());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 拖拽排序散批推荐商品分类
     */
    @ApiOperation(value = "拖拽排序散批推荐商品分类")
    @RequestMapping(value = "/modify-sort", method = RequestMethod.PUT)
    public BaseResponse retailRecommendGoodsCateSort(@RequestBody GoodsCateBatchModifySortRequest request) {
        if (Objects.nonNull(request.getCateGrade())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"分类层级不能为空！");
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("散批商品分类服务", "拖拽排序散批推荐商品分类", "操作成功:分类层级" + request.getCateGrade());
        return retailGoodsCateProvider.batchRetailGoodsCateModifySort(request);
    }

    /**
     * 新增散批商品推荐分类
     */
    @ApiOperation(value = "删除散批商品推荐分类")
    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    public BaseResponse delRetailRecommend(@Valid @RequestBody GoodsCateAddRequest saveRequest) {
        if (Objects.nonNull(saveRequest.getCateGrade())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"分类层级不能为空！");
        }
        retailGoodsCateProvider.delRetailGoodsCate(saveRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("散批商品分类服务", "删除散批商品推荐分类", "操作成功:分类层级" + saveRequest.getCateGrade());
        return BaseResponse.SUCCESSFUL();
    }

}
