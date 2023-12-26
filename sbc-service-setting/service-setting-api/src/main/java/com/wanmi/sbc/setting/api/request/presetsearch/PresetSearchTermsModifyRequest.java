package com.wanmi.sbc.setting.api.request.presetsearch;

import com.wanmi.sbc.setting.api.request.SettingBaseRequest;
import com.wanmi.sbc.setting.bean.vo.PresetSearchTermsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;


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
public class PresetSearchTermsModifyRequest extends SettingBaseRequest {

    private static final long serialVersionUID = 6607049145772092738L;



    /**
     * 预置搜索词id
     */
    @ApiModelProperty(value = "预置搜索词Id")
    private Long id;

    /**
     * 预置搜索词
     */
    @ApiModelProperty(value = "预置搜索词")
    @Length(max = 10)
    private String presetSearchKeyword;

    private List<PresetSearchTermsVO> presetSearchTermsList;
}
