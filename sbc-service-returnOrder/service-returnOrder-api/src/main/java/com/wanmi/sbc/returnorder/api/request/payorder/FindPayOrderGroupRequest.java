package com.wanmi.sbc.returnorder.api.request.payorder;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 聚合查询订单付款记录
 */
@Data
public class FindPayOrderGroupRequest implements Serializable {

    private static final long serialVersionUID = -8543775758061616484L;

    @ApiModelProperty
    @NotNull
    @Valid
    private List<String> tids;
}
