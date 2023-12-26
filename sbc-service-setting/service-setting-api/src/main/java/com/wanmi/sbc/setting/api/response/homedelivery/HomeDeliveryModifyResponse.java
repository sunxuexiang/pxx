package com.wanmi.sbc.setting.api.response.homedelivery;

import com.wanmi.sbc.setting.bean.vo.HomeDeliveryVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>配送到家修改结果</p>
 * @author lh
 * @date 2020-08-01 14:13:32
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDeliveryModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的配送到家信息
     */
    @ApiModelProperty(value = "已修改的配送到家信息")
    private HomeDeliveryVO homeDeliveryVO;
}
