package com.wanmi.sbc.customer.api.response.log;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.CustomerLogVO;
import com.wanmi.sbc.customer.bean.vo.StoreLevelVO;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description  
 * @author  shiy
 * @date    2023/4/8 9:26
 * @params  
 * @return  
*/
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerLogPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private MicroServicePage<CustomerLogVO> customerLogVOList;
}
