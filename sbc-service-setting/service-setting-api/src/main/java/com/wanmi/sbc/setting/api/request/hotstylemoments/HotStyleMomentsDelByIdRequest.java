package com.wanmi.sbc.setting.api.request.hotstylemoments;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 通过id删除爆款时刻请求参数实体类
 * @author: XinJiang
 * @time: 2022/5/9 18:45
 */
@Data
@Builder
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class HotStyleMomentsDelByIdRequest implements Serializable {

    private static final long serialVersionUID = -7647296851378437262L;

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id，单个删除")
    private Long hotId;

    /**
     * 主键id集合
     */
    @ApiModelProperty(value = "主键id集合，批量删除")
    private List<Long> hotIds;

    /**
     * 删除时间
     */
    @ApiModelProperty(value = "删除时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime deleteTime;

    /**
     * 删除人
     */
    @ApiModelProperty(value = "删除人")
    private String deletePerson;
}
