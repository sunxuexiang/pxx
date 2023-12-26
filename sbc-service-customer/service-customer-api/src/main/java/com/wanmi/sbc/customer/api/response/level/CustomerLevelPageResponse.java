package com.wanmi.sbc.customer.api.response.level;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.CustomerLevelVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户等级分页
 * @Author: daiyitian
 * @Date: Created In 上午11:38 2017/11/14
 * @Description: 公司信息Response
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerLevelPageResponse implements Serializable {

    private static final long serialVersionUID = 6492765528117007884L;
    /**
     * 客户等级列表
     */
    @ApiModelProperty(value = "客户等级列表")
    private MicroServicePage<CustomerLevelVO> customerLevelVOPage;
}
