package com.wanmi.sbc.setting.provider.impl.villagesaddress;

import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.setting.api.provider.villagesaddress.VillagesAddressConfigProvider;
import com.wanmi.sbc.setting.api.request.villagesaddress.VillagesAddressConfigDelByIdsRequest;
import com.wanmi.sbc.setting.bean.vo.VillagesAddressConfigVO;
import com.wanmi.sbc.setting.villagesaddress.model.root.VillagesAddressConfig;
import com.wanmi.sbc.setting.villagesaddress.service.VillagesAddressConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 乡镇件地址配置信息操作接口实现类
 * @author: XinJiang
 * @time: 2022/4/29 10:26
 */
@RestController
@Validated
public class VillagesAddressConfigController implements VillagesAddressConfigProvider {

    @Autowired
    private VillagesAddressConfigService villagesAddressConfigService;

    @Override
    public BaseResponse batchAdd(List<VillagesAddressConfigVO> villagesAddressConfigVOList) {
        villagesAddressConfigService.batchAdd(KsBeanUtil.convert(villagesAddressConfigVOList, VillagesAddressConfig.class));
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse delByIds(VillagesAddressConfigDelByIdsRequest request) {
        villagesAddressConfigService.delByIds(request.getIds());
        return BaseResponse.SUCCESSFUL();
    }
}
