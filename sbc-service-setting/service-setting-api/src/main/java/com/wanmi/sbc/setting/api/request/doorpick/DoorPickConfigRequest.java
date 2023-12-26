package com.wanmi.sbc.setting.api.request.doorpick;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 查询上门自提多条件类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoorPickConfigRequest extends BaseQueryRequest {

    /**
     * 主键id
     */

    private Long networkId;

    private List<Long> networkIds;

    /**
     * 网点联系人
     */

    private String contacts;

    /**
     * 网点名字
     */

    private String networkName;

    /**
     * 网点手机号码
     */

    private String phone;


    /**
     * 网点座机号码
     */

    private String landline;


    /**
     * 网点地址
     */

    private String networkAddress;

    /**
     * 省
     */

    private String province;


    /**
     * 市
     */

    private String city;

    /**
     * 纬度值
     */

    private BigDecimal lat;

    /**
     * 经度值
     */

    private BigDecimal lng;

    /**
     * 可配送距离 米
     */

    private int distance;

    /**
     * 是否删除
     */

    private Integer delFlag;

    /**
     * 店铺id
     */
    private Long storeId;
}
