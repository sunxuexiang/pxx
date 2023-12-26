package com.wanmi.sbc.setting.api.response.systemcancellationpolicy;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.setting.bean.vo.SystemCancellationPolicyVO;
import com.wanmi.sbc.setting.bean.vo.SystemPrivacyPolicyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>隐私政策分页结果</p>
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemCancellationPolicyPageResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 隐私政策分页结果
     */
    @ApiModelProperty(value = "隐私政策分页结果")
    private MicroServicePage<SystemCancellationPolicyVO> SystemCancellationPolicyVOPage;
}
