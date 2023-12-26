package com.wanmi.sbc.goods.api.response.info;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityGoodsViewResponse implements Serializable {

    List<ActivityGoodsResponse> activityGoodsResponse;
}
