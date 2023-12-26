package com.wanmi.sbc.customer.api.response.parentcustomerrela;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.ParentCustomerRelaVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>子主账号关联关系分页结果</p>
 * @author baijz
 * @date 2020-05-26 15:39:43
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentCustomerRelaPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 子主账号关联关系分页结果
     */
    @ApiModelProperty(value = "子主账号关联关系分页结果")
    private MicroServicePage<ParentCustomerRelaVO> parentCustomerRelaVOPage;
}
