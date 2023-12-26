package com.wanmi.sbc.crm.customergroup.response;

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
@NoArgsConstructor
@AllArgsConstructor
public class CustomerGroupNameResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分群名称
     */
    @ApiModelProperty(value = "分群名称")
    private List<String> groupNames;


}
