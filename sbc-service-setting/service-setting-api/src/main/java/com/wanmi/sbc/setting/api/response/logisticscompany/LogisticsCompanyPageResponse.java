package com.wanmi.sbc.setting.api.response.logisticscompany;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>物流公司分页结果</p>
 * @author fcq
 * @date 2020-11-06 13:37:51
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsCompanyPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 物流公司分页结果
     */
    @ApiModelProperty(value = "物流公司分页结果")
    private MicroServicePage<LogisticsCompanyVO> logisticsCompanyVOPage;
}
