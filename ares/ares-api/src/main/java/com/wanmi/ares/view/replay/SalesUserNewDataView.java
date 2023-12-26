package com.wanmi.ares.view.replay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesUserNewDataView implements Serializable {

    private static final long serialVersionUID = -7049586804302825663L;

    /**
     * 省份ID
     */
    private String provinceId;

    /**
     *  省份名称
     */
    private String provinceName;

    /**
     * 大白鲸今日下单用户
     */
    private Long userCount;

    /**
     * 入驻商家今日下单用户
     */
    private Long thirdUserCount;
}
