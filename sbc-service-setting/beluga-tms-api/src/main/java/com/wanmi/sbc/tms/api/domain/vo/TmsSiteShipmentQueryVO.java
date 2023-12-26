package com.wanmi.sbc.tms.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 站点信息表
 * </p>
 *
 * @author xyy
 * @since 2023-09-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmsSiteShipmentQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 市场Id
    private Long marketId;

    // 承运商id
    private String carrierId;

}
