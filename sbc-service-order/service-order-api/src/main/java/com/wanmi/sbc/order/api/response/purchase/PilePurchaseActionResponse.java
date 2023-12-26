package com.wanmi.sbc.order.api.response.purchase;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.order.bean.vo.PilePurchaseActionVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 囤货明细分页查询响应实体类
 * @author: XinJiang
 * @time: 2021/12/20 16:46
 */
@ApiModel
@Data
public class PilePurchaseActionResponse implements Serializable {

    private static final long serialVersionUID = -7116920124552521798L;

    /**
     * 囤货明细分页数据
     */
    @ApiModelProperty(value = "囤货明细分页数据")
    private MicroServicePage<PilePurchaseActionVO> pilePurchaseActionVOPage;
}
