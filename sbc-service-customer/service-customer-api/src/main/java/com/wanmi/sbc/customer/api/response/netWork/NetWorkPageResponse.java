package com.wanmi.sbc.customer.api.response.netWork;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.customer.bean.vo.CompanyInfoVO;
import com.wanmi.sbc.customer.bean.vo.NetWorkVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetWorkPageResponse implements Serializable {

    private static final long serialVersionUID = -209744202815448213L;
    /**
     * 公司信息列表
     */
    @ApiModelProperty(value = "会员收货地址")
    private MicroServicePage<NetWorkVO> netWorkVOMicroServicePage;
}
