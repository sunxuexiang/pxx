package com.wanmi.sbc.villagesaddress;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.enums.SortType;
import com.wanmi.sbc.setting.api.provider.villagesaddress.VillagesAddressConfigProvider;
import com.wanmi.sbc.setting.api.provider.villagesaddress.VillagesAddressConfigQueryProvider;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigDelByIdsRequest;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigQueryRequest;
import com.wanmi.sbc.setting.api.response.villagesaddress.VillagesAddressConfigListResponse;
import com.wanmi.sbc.setting.api.response.villagesaddress.VillagesAddressConfigPageResponse;
import com.wanmi.sbc.setting.bean.vo.VillagesAddressConfigVO;
import com.wanmi.sbc.util.CommonUtil;
import com.wanmi.sbc.util.OperateLogMQUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 乡镇件地址配置API
 * @author: XinJiang
 * @time: 2022/4/29 10:18
 */
@Api(description = "乡镇件地址配置API",tags = "S2bVillagesAddressConfigController")
@RestController
@RequestMapping(value = "/villages/address")
public class S2bVillagesAddressConfigController {

    @Autowired
    private VillagesAddressConfigProvider villagesAddressConfigProvider;

    @Autowired
    private VillagesAddressConfigQueryProvider villagesAddressConfigQueryProvider;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private OperateLogMQUtil operateLogMQUtil;

    @ApiOperation("校验乡镇件地址是否重复添加")
    @PostMapping("/check-repeat-address")
    public BaseResponse<List<String>> checkRepeatAddress(@RequestBody List<Long> villageIds) {

        VillagesAddressConfigListResponse response = villagesAddressConfigQueryProvider
                .list(VillagesAddressConfigQueryRequest.builder().villagesIds(villageIds).storeId(commonUtil.getStoreIdWithDefault()).build()).getContext();
        if (CollectionUtils.isNotEmpty(response.getVillagesAddressConfigVOList())) {
            List<String> villageNames = response.getVillagesAddressConfigVOList().stream()
                    .map(VillagesAddressConfigVO::getVillageName).collect(Collectors.toList());
            return BaseResponse.success(villageNames);
        }
        return BaseResponse.success(Collections.emptyList());
    }

    @ApiOperation("批量添加")
    @PostMapping("/batch-add")
    public BaseResponse batchAdd(@RequestBody List<VillagesAddressConfigVO> villagesAddressConfigVOList) {
        String operator = commonUtil.getOperator().getAccount();
        Long storeId = commonUtil.getStoreIdWithDefault();
        villagesAddressConfigVOList.forEach(config -> {
            config.setCreatePerson(operator);
            config.setCreateTime(LocalDateTime.now());
            config.setStoreId(storeId);
        });
        villagesAddressConfigProvider.batchAdd(villagesAddressConfigVOList);
        operateLogMQUtil.convertAndSend("乡镇件地址配置", "批量添加", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation("通过主键id批量删除配置地址信息")
    @DeleteMapping()
    public BaseResponse delById(@RequestBody VillagesAddressConfigDelByIdsRequest request) {
        villagesAddressConfigProvider.delByIds(request);
        operateLogMQUtil.convertAndSend("乡镇件地址配置", "通过主键id批量删除配置地址信息", "操作成功");
        return BaseResponse.SUCCESSFUL();
    }

    @ApiOperation("乡镇件地址配置信息列表")
    @PostMapping("/page")
    public BaseResponse<VillagesAddressConfigPageResponse> page(@RequestBody VillagesAddressConfigQueryRequest request) {
        request.putSort("createTime", SortType.DESC.toValue());
        request.setStoreId(commonUtil.getStoreIdWithDefault());
        request.setCompanyType(commonUtil.getCompanyType());
        VillagesAddressConfigPageResponse response = villagesAddressConfigQueryProvider.page(request).getContext();
        return BaseResponse.success(response);
    }

}
