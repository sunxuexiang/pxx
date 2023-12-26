package com.wanmi.sbc.customer.api.response.parentcustomerrela;

import com.wanmi.sbc.customer.bean.enums.MergeAccountBeforeStatusVo;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCustomerRealStatusResponse implements Serializable {
    private static final long serialVersionUID = 1092015480977313083L;

    /**
     *0：已关联主账号 1：存在子账号 2：无特殊关系
     */
    @ApiModelProperty(value = "账号关系")
    private MergeAccountBeforeStatusVo mergeAccountBeforeStatusVo;
}
