package com.wanmi.sbc.setting.api.request.presetsearch;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;


/**
 * <p>预置搜索词</p>
 * @author weiwenhao
 * @date 2020-04-16
 */

@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
@Data
public class PresetSearchTermsDeleteByIdRequest extends SettingBaseRequest {

    private static final long serialVersionUID = 6607049145772092738L;


    /**
     * ID
     */
    private Long id;
}
