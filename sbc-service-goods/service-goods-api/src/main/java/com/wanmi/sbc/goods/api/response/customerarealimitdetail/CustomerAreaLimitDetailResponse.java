package com.wanmi.sbc.goods.api.response.customerarealimitdetail;

import com.wanmi.sbc.goods.bean.vo.CustomerAreaLimitDetailVO;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAreaLimitDetailResponse implements Serializable {
    private static final long serialVersionUID = -3946068075094538346L;

    @ApiModelProperty(value = "用户区域购买详细信息")
    private List<CustomerAreaLimitDetailVO> detailVOS;
}
