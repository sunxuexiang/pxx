package com.wanmi.sbc.returnorder.api.request.returnorder;

import com.wanmi.sbc.common.base.Operator;
import com.wanmi.sbc.returnorder.bean.dto.ReturnOrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 退单修改请求结构
 * @Author: daiyitian
 * @Description:
 * @Date: 2018-11-16 16:39
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ReturnOrderRemedyRequest implements Serializable {

    private static final long serialVersionUID = -1076979847505660373L;

    /**
     * 退单信息
     */
    @ApiModelProperty(value = "退单信息")
    @NotNull
    private ReturnOrderDTO newReturnOrder;

    /**
     * 操作人信息
     */
    @ApiModelProperty(value = "操作人信息")
    @NotNull
    private Operator operator;
}
