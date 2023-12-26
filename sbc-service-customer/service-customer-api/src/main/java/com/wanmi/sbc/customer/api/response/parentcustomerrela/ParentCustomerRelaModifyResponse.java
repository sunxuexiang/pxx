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
 * <p>子主账号关联关系修改结果</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCustomerRelaModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的子主账号关联关系信息
     */
    @ApiModelProperty(value = "已修改的子主账号关联关系信息")
    private ParentCustomerRelaVO parentCustomerRelaVO;
}
