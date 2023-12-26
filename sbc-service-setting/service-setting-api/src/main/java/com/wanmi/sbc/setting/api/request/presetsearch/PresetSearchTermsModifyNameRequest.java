package com.wanmi.sbc.setting.api.request.presetsearch;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import io.swagger.annotations.ApiModel;
import lombok.*;


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
public class PresetSearchTermsModifyNameRequest extends SettingBaseRequest {

    private static final long serialVersionUID = 6607049145772092738L;


    private Long id;

    /**
     * 预置词
     */
    private String presetSearchKeyword;
}
