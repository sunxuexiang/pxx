package com.wanmi.sbc.goods;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.goods.api.provider.warehouse.WareHouseDetailProvider;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseDetailAddRequest;
import com.wanmi.sbc.goods.api.request.warehouse.WareHouseDetailRequest;
import com.wanmi.sbc.goods.api.response.warehouse.WareHouseDetailPageResponse;
import com.wanmi.sbc.setting.api.provider.yunservice.YunServiceProvider;
import com.wanmi.sbc.setting.api.request.yunservice.YunUploadResourceRequest;
import com.wanmi.sbc.setting.bean.enums.ResourceType;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 散批商品分类服务
 * @author: XinJiang
 * @time: 2022/5/5 16:25
 */
@RequestMapping("/goods/ware/img")
@RestController
@Validated
@Log4j
@Api(tags = "BossGoodsWareImgController",description = "仓库用户须知服务" )
public class BossGoodsWareImgController {

    @Autowired
    private YunServiceProvider yunServiceProvider;

    private CommonUtil commonUtil;

    @Autowired
    private WareHouseDetailProvider wareHouseDetailProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation(value = "仓库用户须知服务列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public BaseResponse<WareHouseDetailPageResponse> list(WareHouseDetailRequest queryRequest) {
        queryRequest.setDelFlag(DeleteFlag.NO.toValue());
        queryRequest.putSort("wareId", SortType.ASC.toValue());
        return wareHouseDetailProvider.list(queryRequest);
    }

    /**
     * 上传店铺素材
     *
     * @param multipartFiles
     * @param wareId         分类id
     * @return
     */
    @ApiOperation(value = "上传仓库图片", notes = "resourceType-->0: 图片, 1: 视频")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "List",
                    name = "uploadFile", value = "上传素材", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long",
                    name = "wareId", value = "仓库Id", required = true)
    })
    @RequestMapping(value = "/uploadStoreResource", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadFile(@RequestParam("uploadFile") List<MultipartFile> multipartFiles, Long
            wareId, ResourceType resourceType) {

        //验证上传参数
        if (CollectionUtils.isEmpty(multipartFiles)) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        //验证上传参数
        if (null == wareId) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
        }

        List<String> resourceUrls = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            if (file == null || file.getSize() == 0) {
                throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR);
            }
            try {
                // 上传
                String resourceUrl = yunServiceProvider.uploadFile(YunUploadResourceRequest.builder()
                        .cateId(wareId)
                        .storeId(commonUtil.getStoreId())
                        .companyInfoId(commonUtil.getCompanyInfoId())
                        .resourceType(resourceType)
                        .resourceName(file.getOriginalFilename())
                        .content(file.getBytes())
                        .build()).getContext();
                resourceUrls.add(resourceUrl);
            } catch (Exception e) {
                log.error("uploadStoreResource wareId error: {}", e);
                return ResponseEntity.ok(BaseResponse.FAILED());
            }
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("散批商品分类服务", "上传仓库图片", "操作成功:仓库ID" + wareId);
        return ResponseEntity.ok(resourceUrls);
    }

    /**
     * 新增用户须知
     */
    @ApiOperation(value = "新增用户须知")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public BaseResponse add(@Valid @RequestBody WareHouseDetailAddRequest saveRequest) {
        if (Objects.isNull(saveRequest.getWareId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"仓库Id不能为空！");
        }
        wareHouseDetailProvider.add(saveRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("散批商品分类服务", "新增用户须知", "操作成功:仓库ID" + saveRequest.getWareId());
        return BaseResponse.SUCCESSFUL();
    }

    /**
     * 修改图片
     */
    @ApiOperation(value = "修改图片")
    @RequestMapping(value = "/modify", method = RequestMethod.PUT)
    public BaseResponse modify(@Valid @RequestBody WareHouseDetailAddRequest saveRequest) {
        if (Objects.isNull(saveRequest.getWareId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"仓库Id不能为空！");
        }
        if (Objects.isNull(saveRequest.getId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"唯一Id不能为空！");
        }
        //操作日志记录
        operateLogMQUtil.convertAndSend("散批商品分类服务", "修改图片", "操作成功:仓库ID" + saveRequest.getWareId());
        return wareHouseDetailProvider.modify(saveRequest);
    }

    /**
     * 删除用户须知
     */
    @ApiOperation(value = "删除用户须知")
    @RequestMapping(value = "/del", method = RequestMethod.DELETE)
    public BaseResponse del(@Valid @RequestBody WareHouseDetailAddRequest saveRequest) {
        if (Objects.nonNull(saveRequest.getId())) {
            throw new SbcRuntimeException(CommonErrorCode.PARAMETER_ERROR,"唯一不能为空！");
        }
        wareHouseDetailProvider.deleteById(saveRequest);
        //操作日志记录
        operateLogMQUtil.convertAndSend("散批商品分类服务", "删除用户须知", "操作成功:唯一ID" + saveRequest.getId());
        return BaseResponse.SUCCESSFUL();
    }

}
