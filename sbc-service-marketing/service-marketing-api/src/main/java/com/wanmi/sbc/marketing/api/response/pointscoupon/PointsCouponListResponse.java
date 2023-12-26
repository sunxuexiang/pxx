package com.wanmi.sbc.marketing.api.response.pointscoupon;

import com.wanmi.sbc.marketing.bean.vo.PointsCouponVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>积分兑换券表列表结果</p>
 * @author yang
 * @date 2019-06-11 10:07:09
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsCouponListResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 积分兑换券表列表结果
     */
    @ApiModelProperty(value = "积分兑换券表列表结果")
    private List<PointsCouponVO> pointsCouponVOList;
}
