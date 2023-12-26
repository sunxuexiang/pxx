package com.wanmi.sbc.returnorder.api.response.distribution;

import com.wanmi.sbc.returnorder.bean.vo.CountCustomerConsumeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel
@Data
public class CountConsumeResponse {

    @ApiModelProperty(value = "统计结果")
    private List<CountCustomerConsumeVO> countCustomerConsumeList;
}
