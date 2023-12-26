package com.wanmi.sbc.returnorder.api.request.trade;

import com.wanmi.sbc.common.base.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 查询供应商子订单的请求参数
 * @Autho caiping
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class FindProviderTradeRequest extends BaseRequest {
    private static final long serialVersionUID = 4478928439116796662L;
    /**
     *商品订单的主订单Id
     */
    @ApiModelProperty(value = "主订单Id")
    private List<String> parentId;
}
