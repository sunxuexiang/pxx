package com.wanmi.sbc.setting.api.request.headline;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : Like
 * @create 2023/11/7 14:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeadLineSaveRequest {

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    @NotBlank(message = "内容不能为空")
    @Size(max = 50, message = "内容长度不能超过50个字符")
    // @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\pP‘’“”]+$", message = "内容只能包含中文、英文、数字和特定符号")
    private String content;


    @ApiModelProperty(value = "排序")
    private Integer sortNum;

    /**
     * 每个字的速度
     */
    @ApiModelProperty(value = "每个字的速度")
    private BigDecimal speeds;

    /**
     * 创建时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 创建人id
     */
    private String createPerson;

    /**
     * 更新人id
     */
    private String updatePerson;

    /**
     * 更新时间
     */
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;
}
