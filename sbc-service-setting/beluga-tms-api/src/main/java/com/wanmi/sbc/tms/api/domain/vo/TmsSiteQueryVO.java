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
public class TmsSiteQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 省code
     */
    private Integer provinceCode;

    /**
     * 承运商id
     */
    private Long carrierId;
}
