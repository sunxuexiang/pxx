package com.wanmi.sbc.setting.api.response.activityconfig;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>导航配置列表结果</p>
 * @author lvheng
 * @date 2021-04-19 18:49:30
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityConfigListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 开关
     */
    private String isOpen;

    /**
     * 满减
     */
    private String fullReductionIcon;

    /**
     * 立减
     */
    private String onceReductionIcon;

    /**
     * 买折
     */
    private String discountIcon;

    /**
     * 买赠
     */
    private String discountGiftIcon;

}
