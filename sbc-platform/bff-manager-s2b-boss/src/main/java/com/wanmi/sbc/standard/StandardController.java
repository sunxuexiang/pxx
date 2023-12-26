package com.wanmi.sbc.standard;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.HttpUtil;
import com.wanmi.sbc.goods.api.provider.standard.StandardExcelProvider;
import com.wanmi.sbc.goods.api.provider.standard.StandardGoodsProvider;
import com.wanmi.sbc.goods.api.provider.standard.StandardGoodsQueryProvider;
import com.wanmi.sbc.goods.api.request.standard.*;
import com.wanmi.sbc.goods.api.response.standard.StandardGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.standard.StandardGoodsPageResponse;
import com.wanmi.sbc.standard.request.StandardExcelImplGoodsRequest;
import com.wanmi.sbc.standard.service.StandardExcelService;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * 商品库服务
 * Created by daiyitian on 17/4/12.
 */
@RestController
@RequestMapping("/standard")
@Api(description = "商品库服务",tags ="StandardController")
public class StandardController {

    @Autowired
    private StandardGoodsQueryProvider standardGoodsQueryProvider;

    @Autowired
    private StandardGoodsProvider standardGoodsProvider;

    @Autowired
    private StandardExcelProvider standardExcelProvider;

    @Autowired
    private StandardExcelService standardExcelService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 查询商品
     *
     * @param pageRequest 商品 {@link StandardGoodsPageRequest}
     * @return 商品详情
     */
    @ApiOperation(value = "获取商品库商品分类详情信息")
    @RequestMapping(value = "/spus", method = RequestMethod.POST)
    public BaseResponse<StandardGoodsPageResponse> list(@RequestBody StandardGoodsPageRequest pageRequest) {
        pageRequest.setDelFlag(DeleteFlag.NO.toValue());
        //按创建时间倒序、ID升序
        pageRequest.putSort("createTime", SortType.DESC.toValue());
        pageRequest.putSort("goodsId", SortType.ASC.toValue());
        return standardGoodsQueryProvider.page(pageRequest);
    }

    /**
     * @param addRequest 商品添加信息 {@link StandardGoodsAddRequest}
     * @return
     */
    @ApiOperation(value = "商品库商品添加信息")
    @RequestMapping(value = "/spu", method = RequestMethod.POST)
    public BaseResponse add(@Valid @RequestBody StandardGoodsAddRequest addRequest) {

        //操作日志记录
        operateLogMQUtil.convertAndSend("商品", "新增商品库商品",
                "新增商品库商品：" + addRequest.getGoods().getGoodsName());

        return standardGoodsProvider.add(addRequest);
    }

    /**
     * @param modifyRequest 商品修改信息 {@link StandardGoodsModifyRequest}
     * @return
     */
    @ApiOperation(value = "商品库商品修改信息")
    @RequestMapping(value = "/spu", method = RequestMethod.PUT)
    public BaseResponse edit(@Valid @RequestBody StandardGoodsModifyRequest modifyRequest) {

        if (modifyRequest.getGoods().getGoodsId() == null) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        //操作日志记录
        operateLogMQUtil.convertAndSend("商品", "编辑商品",
                "编辑商品：" + modifyRequest.getGoods().getGoodsName());

        return standardGoodsProvider.modify(modifyRequest);
    }

    /**
     * 获取商品库详情信息
     *
     * @param goodsId 商品库编号 {@link String}
     * @return 商品库详情
     */
    @ApiOperation(value = "获取商品库详情信息")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "goodsId",
            value = "商品库商品Id", required = true)
    @RequestMapping(value = "/spu/{goodsId}", method = RequestMethod.GET)
    public BaseResponse<StandardGoodsByIdResponse> info(@PathVariable String goodsId) {
        StandardGoodsByIdRequest standardGoodsByIdRequest = new StandardGoodsByIdRequest();
        standardGoodsByIdRequest.setGoodsId(goodsId);
        return standardGoodsQueryProvider.getById(standardGoodsByIdRequest);
    }

    /**
     * 删除商品库
     *
     * @param deleteByGoodsIdsRequest 商品id封装 {@link StandardGoodsDeleteByGoodsIdsRequest}
     * @return
     */
    @ApiOperation(value = "删除商品库商品")
    @RequestMapping(value = "/spu", method = RequestMethod.DELETE)
    public BaseResponse delete(@RequestBody StandardGoodsDeleteByGoodsIdsRequest deleteByGoodsIdsRequest) {
        if (CollectionUtils.isEmpty(deleteByGoodsIdsRequest.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        StandardGoodsByIdRequest request = new StandardGoodsByIdRequest();
        request.setGoodsId(deleteByGoodsIdsRequest.getGoodsIds().get(0));
        BaseResponse<StandardGoodsByIdResponse> baseResponse = standardGoodsQueryProvider.getById(request);
        StandardGoodsByIdResponse response = baseResponse.getContext();
        if (Objects.nonNull(response)) {
            //操作日志记录
            operateLogMQUtil.convertAndSend("商品", "删除商品", "删除商品：SPU编码" + response.getGoods().getGoodsName());
        }

        return standardGoodsProvider.delete(deleteByGoodsIdsRequest);

    }



    /**
     * 删除商品库
     *
     * @param deleteByGoodsIdsRequest 商品id封装 {@link StandardGoodsDeleteProviderByGoodsIdsRequest}
     * @return
     */
    @ApiOperation(value = "删除供应商商品库商品")
    @RequestMapping(value = "/spu/provider", method = RequestMethod.DELETE)
    public BaseResponse deleteProvider(@RequestBody StandardGoodsDeleteProviderByGoodsIdsRequest deleteByGoodsIdsRequest) {
        if (CollectionUtils.isEmpty(deleteByGoodsIdsRequest.getGoodsIds())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }
        StandardGoodsByIdRequest request = new StandardGoodsByIdRequest();
        request.setGoodsId(deleteByGoodsIdsRequest.getGoodsIds().get(0));
        BaseResponse<StandardGoodsByIdResponse> baseResponse = standardGoodsQueryProvider.getById(request);
        StandardGoodsByIdResponse response = baseResponse.getContext();
        if (Objects.nonNull(response)) {
            //操作日志记录
            operateLogMQUtil.convertAndSend("商品", "删除商品", "删除商品：SPU编码" + response.getGoods().getGoodsName());
        }

        return standardGoodsProvider.deleteProvider(deleteByGoodsIdsRequest);

    }



    /**
     * 下载模板
     */
    @ApiOperation(value = "下载商品库模板")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "encrypted",
            value = "加密", required = true)
    @RequestMapping(value = "/excel/template/{encrypted}", method = RequestMethod.GET)
    public void template(@PathVariable String encrypted) {
        String file = standardExcelProvider.exportTemplate().getContext().getFile();
        if (StringUtils.isNotBlank(file)) {
            try {
                String fileName = URLEncoder.encode("商品库导入模板.xls", "UTF-8");
                HttpUtil.getResponse().setHeader("Content-Disposition", String.format("attachment;filename=\"%s\";" +
                        "filename*=\"utf-8''%s\"", fileName, fileName));
                HttpUtil.getResponse().getOutputStream().write(new BASE64Decoder().decodeBuffer(file));
            } catch (Exception e) {
                throw new SbcRuntimeException(CommonErrorCode.FAILED);
            }
            //操作日志记录
            operateLogMQUtil.convertAndSend("商品", "下载模板", "下载商品库模板" );
        }
    }

    /**
     * 确认导入商品
     *
     * @param ext 文件格式 {@link String}
     * @return
     */
    @ApiOperation(value = "确认导入商品库商品")
    @ApiImplicitParam(paramType = "path", dataType = "String", name = "ext",
            value = "文件名后缀", required = true)
    @RequestMapping(value = "/import/{ext}", method = RequestMethod.GET)
    public BaseResponse<Boolean> implGoods(@PathVariable String ext) {
        if (!("xls".equalsIgnoreCase(ext) || "xlsx".equalsIgnoreCase(ext))) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        StandardExcelImplGoodsRequest standardExcelImplGoodsRequest = new StandardExcelImplGoodsRequest();
        standardExcelImplGoodsRequest.setExt(ext);
        standardExcelImplGoodsRequest.setUserId(commonUtil.getOperatorId());
        standardExcelService.implGoods(standardExcelImplGoodsRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("商品", "批量导入", "批量导入");
        return BaseResponse.success(Boolean.TRUE);
    }
}
