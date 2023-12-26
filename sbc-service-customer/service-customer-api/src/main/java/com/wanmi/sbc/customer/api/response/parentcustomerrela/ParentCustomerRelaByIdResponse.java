package com.wanmi.sbc.customer.api.response.parentcustomerrela;

import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>根据id查询任意（包含已删除）子主账号关联关系信息response</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCustomerRelaByIdResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 子主账号关联关系信息
     */
    @ApiModelProperty(value = "子主账号关联关系信息")
    private ParentCustomerRelaVO parentCustomerRelaVO;
}
