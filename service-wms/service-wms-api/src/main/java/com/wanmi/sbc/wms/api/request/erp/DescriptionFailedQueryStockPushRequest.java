package com.wanmi.sbc.wms.api.request.erp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 查询囤货推erp失败
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
public class DescriptionFailedQueryStockPushRequest implements Serializable {
    private static final long serialVersionUID = -4071986725280998361L;

    private Long pushKingdeeId;

    private LocalDateTime createTime;
}
