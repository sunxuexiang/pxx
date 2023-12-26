package com.wanmi.sbc.marketing.api.request.pile;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.base.BaseRequest;
import com.wanmi.sbc.common.enums.BoolFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.marketing.bean.enums.PileActivityType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PileActivityModifyRequest extends BaseRequest {

    private static final long serialVersionUID = -1144976258555148289L;

    @ApiModelProperty(value = "囤货活动id")
    @NotBlank(message = "囤货活动id不能为空")
    private String activityId;

    @ApiModelProperty(value = "修改人")
    private String updatePerson;

    @NotNull
    @NotBlank
    @ApiModelProperty(value = "囤货活动名称")
    private String activityName;

    @NotNull
    @ApiModelProperty(value = "开始时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @NotNull
    @ApiModelProperty(value = "结束时间")
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    @NotNull
    @ApiModelProperty(value = "囤货类型：1 全款囤货")
    private PileActivityType pileActivityType;

    @ApiModelProperty(value = "店铺id")
    private Long storeId;

    @NotNull
    @ApiModelProperty(value = "公共虚拟库存")
    private Long publicVirtualStock;

    @ApiModelProperty(value = "是否强制囤货：0否1是")
    @Enumerated
    private BoolFlag forcePileFlag;
}
