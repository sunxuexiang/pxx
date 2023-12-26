package com.wanmi.sbc.returnorder.api.request.returnorder;

import com.wanmi.sbc.returnorder.bean.enums.ReturnFlowState;
import com.wanmi.sbc.returnorder.bean.enums.WMSPushState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新提货退单流程请求参数
 *
 * @author chenchang
 * @since 2022-10-6
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class UpdateReturnFlowStateRequest implements Serializable {

    private static final long serialVersionUID = 7362876682610236755L;

    @ApiModelProperty(value = "退单id")
    @NotBlank
    private String  rid;

    @ApiModelProperty(value = "退单流程节点")
    @NotNull
    private ReturnFlowState returnFlowState;

    //推送wms三方退款接口
    @ApiModelProperty(value = "推送wms三方退款接口")
    private WMSPushState wmsPushState;

}
