package com.wanmi.sbc.goods.api.request.postionpicture;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.goods.api.request.pointsgoods.PointsGoodsAddRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostionPictureRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "WareId")
    Long wareId;

    @ApiModelProperty(value = "allTyep")
    Integer allTyep;

    @ApiModelProperty(value = "type")
    Integer type;
}
