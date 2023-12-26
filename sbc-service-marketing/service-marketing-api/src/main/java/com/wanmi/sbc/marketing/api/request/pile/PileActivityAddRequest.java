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
import lombok.*;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PileActivityAddRequest extends BaseRequest {

    private static final long serialVersionUID = -1144976258555148289L;

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
    @ApiModelProperty(value = "囤货类型")
    private PileActivityType pileActivityType;

    @ApiModelProperty(value = "店铺id", hidden = true)
    private Long storeId;

    @NotNull
    @ApiModelProperty(value = "公共虚拟库存")
    private Long publicVirtualStock;

    @ApiModelProperty(value = "操作人",hidden = true)
    private String createPerson;

    @ApiModelProperty(value = "是否强制囤货：0否1是")
    @Enumerated
    private BoolFlag forcePileFlag;
}
