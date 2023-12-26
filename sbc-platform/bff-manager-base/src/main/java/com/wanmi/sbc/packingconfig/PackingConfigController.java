package com.wanmi.sbc.packingconfig;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.constant.RedisKeyConstant;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.exception.SbcRuntimeException;
import com.wanmi.sbc.common.util.CommonErrorCode;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.common.util.OsUtil;
import com.wanmi.sbc.customer.api.provider.company.CompanyInfoQueryProvider;
import com.wanmi.sbc.customer.api.provider.liveroom.LiveRoomQueryProvider;
import com.wanmi.sbc.customer.api.request.company.CompanyInfoByIdRequest;
import com.wanmi.sbc.customer.api.request.liveroom.LiveRoomListRequest;
import com.wanmi.sbc.customer.bean.enums.LiveRoomStatus;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.LiveRoomVO;
import com.wanmi.sbc.es.elastic.EsGoodsInfoElasticService;
import com.wanmi.sbc.es.elastic.EsGoodsModifyInventoryService;
import com.wanmi.sbc.es.elastic.request.EsGoodsInfoRequest;
import com.wanmi.sbc.goods.api.provider.ares.GoodsAresProvider;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsProvider;
import com.wanmi.sbc.goods.api.provider.goods.GoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.info.GoodsInfoQueryProvider;
import com.wanmi.sbc.goods.api.provider.livegoods.LiveGoodsQueryProvider;
import com.wanmi.sbc.goods.api.provider.storecate.StoreCateQueryProvider;
import com.wanmi.sbc.goods.api.request.ares.DispatcherFunctionRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsByIdRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateGoodsExistsByIdRequest;
import com.wanmi.sbc.goods.api.request.goods.*;
import com.wanmi.sbc.goods.api.request.info.GoodsInfoListByConditionRequest;
import com.wanmi.sbc.goods.api.request.storecate.StoreCateListByGoodsRequest;
import com.wanmi.sbc.goods.api.response.freight.FreightTemplateGoodsByIdResponse;
import com.wanmi.sbc.goods.api.response.goods.*;
import com.wanmi.sbc.goods.api.response.info.GoodsInfoListByConditionResponse;
import com.wanmi.sbc.goods.api.response.storecate.StoreCateListByGoodsResponse;
import com.wanmi.sbc.goods.bean.enums.AddedFlag;
import com.wanmi.sbc.goods.bean.vo.*;
import com.wanmi.sbc.goods.service.GoodsExcelService;
import com.wanmi.sbc.redis.RedisService;
import com.wanmi.sbc.setting.api.constant.SettingErrorCode;
import com.wanmi.sbc.setting.api.provider.packingconfig.PackingConfigProvider;
import com.wanmi.sbc.setting.api.provider.packingconfig.PackingConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.packingconfig.PackingConfigRequest;
import com.wanmi.sbc.setting.api.response.packingconfig.PackingConfigResponse;
import com.wanmi.sbc.setting.bean.vo.PackingConfigVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Api(tags = "PackingConfigController", description = "打包服务 Api")
@RestController
@RequestMapping("/packingConfig")
@Slf4j
public class PackingConfigController {

    @Autowired
    private PackingConfigProvider packingConfigProvider;
    @Autowired
    private PackingConfigQueryProvider packingConfigQueryProvider;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    /**
     * 新增打包配置
     */
    @ApiOperation(value = "新增或修改打包配置")
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public BaseResponse<String> modify(@RequestBody PackingConfigVO packingConfigVO) {
        if (Objects.isNull(packingConfigVO)){
            throw new SbcRuntimeException(SettingErrorCode.ILLEGAL_REQUEST_ERROR);
        }
        log.info(packingConfigVO.toString());
        packingConfigProvider.modify(PackingConfigRequest.builder().packingConfigVO(packingConfigVO).build());
        //记录操作日志
        operateLogMQUtil.convertAndSend("打包服务", "新增或修改打包配置", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }


    /**
     * 新增打包配置
     */
    @ApiOperation(value = "获取打包配置")
    @RequestMapping(value = "/getdata", method = RequestMethod.POST)
    public BaseResponse<PackingConfigResponse> getdata(@RequestBody PackingConfigRequest request) {
        PackingConfigResponse context = packingConfigQueryProvider.list().getContext();
        //记录操作日志
        operateLogMQUtil.convertAndSend("打包服务", "获取打包配置", "操作成功");
        return BaseResponse.success(context);
    }



}