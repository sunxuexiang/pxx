package com.wanmi.sbc.returnorder.api.response.payorder;

import com.wanmi.sbc.returnorder.bean.vo.PayOrderVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class FindByOrderNosResponse implements Serializable {

    @ApiModelProperty(value = "支付单列表")
    List<PayOrderVO> orders;

}
