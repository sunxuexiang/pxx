package com.wanmi.sbc.setting.api.provider.villagesaddress;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigDelByIdsRequest;
import com.wanmi.sbc.setting.bean.vo.VillagesAddressConfigVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
*@Description: 乡镇件配置地址provider
*@Author: XinJiang
*@Date: 2022/4/29 10:22
*/
@FeignClient(value = "${application.setting.name}", url="${feign.url.setting:#{null}}", contextId = "VillagesAddressConfigProvider")
public interface VillagesAddressConfigProvider {

    /**
     * 批量保存乡镇件地址配置信息
     * @param villagesAddressConfigVOList
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/villages/batch-add")
    BaseResponse batchAdd(@RequestBody @Valid List<VillagesAddressConfigVO> villagesAddressConfigVOList);

    /**
     * 批量保存乡镇件地址配置信息
     * @param request
     * @return
     */
    @PostMapping("/setting/${application.setting.version}/villages/del-by-id")
    BaseResponse delByIds(@RequestBody @Valid VillagesAddressConfigDelByIdsRequest request);
}
