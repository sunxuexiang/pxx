package com.wanmi.sbc.setting.api.response.systemprivacypolicy;

import com.wanmi.sbc.setting.bean.vo.SystemPrivacyPolicyVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>隐私政策修改结果</p>
 * @author yangzhen
 * @date 2020-09-23 14:52:35
 */
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemPrivacyPolicyModifyResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 已修改的隐私政策信息
     */
    @ApiModelProperty(value = "已修改的隐私政策信息")
    private SystemPrivacyPolicyVO systemPrivacyPolicyVO;
}
