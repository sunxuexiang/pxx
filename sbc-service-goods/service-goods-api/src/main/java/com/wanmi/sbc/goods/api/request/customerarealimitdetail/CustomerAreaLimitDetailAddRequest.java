package com.wanmi.sbc.goods.api.request.customerarealimitdetail;

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
public class CustomerAreaLimitDetailAddRequest implements Serializable {


    private static final long serialVersionUID = 3318951114018837613L;
    @ApiModelProperty
    private List<CustomerAreaLimitDetailVO> list;
 

}
