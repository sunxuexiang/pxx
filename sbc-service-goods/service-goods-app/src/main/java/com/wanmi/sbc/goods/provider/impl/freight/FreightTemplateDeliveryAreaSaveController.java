package com.wanmi.sbc.goods.provider.impl.freight;

import com.alibaba.fastjson.JSONObject;
import com.wanmi.sbc.common.base.BaseResponse;
import com.wanmi.sbc.common.util.KsBeanUtil;
import com.wanmi.sbc.goods.api.provider.freight.FreightTemplateDeliveryAreaSaveProvider;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveListRequest;
import com.wanmi.sbc.goods.api.request.freight.FreightTemplateDeliveryAreaSaveRequest;
import com.wanmi.sbc.goods.bean.enums.freightTemplateDeliveryType;
import com.wanmi.sbc.goods.freight.model.root.FreightTemplateDeliveryArea;
import com.wanmi.sbc.goods.freight.service.FreightTemplateDeliveryAreaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>配送到家范围保存服务接口实现</p>
 *
 * @author zhaowei
 * @date 2021-03-25 16:57:57
 */
@RestController
@Validated
public class FreightTemplateDeliveryAreaSaveController implements FreightTemplateDeliveryAreaSaveProvider {
    @Autowired
    private FreightTemplateDeliveryAreaService freightTemplateDeliveryAreaService;

    @Override
    public BaseResponse save(@RequestBody @Valid FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest) {
        freightTemplateDeliveryAreaService.saveRequest(freightTemplateDeliveryAreaSaveRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse deleteRedisByStoreId(Long storeId, Integer destinationType) {
        freightTemplateDeliveryAreaService.deleteRedis(storeId,destinationType);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse saveOpenFlag(FreightTemplateDeliveryAreaSaveListRequest listRequest) {
        freightTemplateDeliveryAreaService.updateOpenFlag(listRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse initByStoreIdAndWareId(FreightTemplateDeliveryAreaSaveRequest freightTemplateDeliveryAreaSaveRequest) {
        freightTemplateDeliveryAreaService.initByStoreId(freightTemplateDeliveryAreaSaveRequest);
        return BaseResponse.SUCCESSFUL();
    }

    @Override
    public BaseResponse initByBossStoreIdAndWareId() {
        freightTemplateDeliveryAreaService.initByBossStoreId();
        return BaseResponse.SUCCESSFUL();
    }
}

