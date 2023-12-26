package com.wanmi.sbc.returnorder.api.response.returnorder;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.returnorder.bean.vo.ReturnOrderNewVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class ReturnOrderPageNewResponse implements Serializable {
    private static final long serialVersionUID = -358530738446865282L;

    /**
     * 退单分页列表
     */
    @ApiModelProperty(value = "退单分页列表")
    private MicroServicePage<ReturnOrderNewVO> returnOrderPage;
}
