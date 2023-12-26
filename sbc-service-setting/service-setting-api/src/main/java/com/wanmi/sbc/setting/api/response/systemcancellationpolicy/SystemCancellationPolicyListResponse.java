package com.wanmi.sbc.setting.api.response.systemcancellationpolicy;

import com.wanmi.sbc.setting.bean.vo.SystemCancellationPolicyVO;
import com.wanmi.sbc.setting.bean.vo.SystemPrivacyPolicyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>隐私政策列表结果</p>
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemCancellationPolicyListResponse implements Serializable {

    private static final long serialVersionUID = 8477975604208792259L;
    /**
     * 隐私政策列表结果
     */
    @ApiModelProperty(value = "隐私政策列表结果")
    private List<SystemCancellationPolicyVO> systemCancellationPolicyVOList;
}
