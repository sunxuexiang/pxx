package com.wanmi.sbc.shopcart.api.response.purchase;

import com.wanmi.sbc.shopcart.bean.enums.ProcurementTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 采购车配置
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class ProcurementConfigResponse implements Serializable {

    private static final long serialVersionUID = 3350751136441295341L;

    /**
     * 采购车类型
     */
    private ProcurementTypeEnum procurementType;
}
