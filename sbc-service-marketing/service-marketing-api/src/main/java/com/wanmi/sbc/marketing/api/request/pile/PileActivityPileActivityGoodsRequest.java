package com.wanmi.sbc.marketing.api.request.pile;

import com.wanmi.sbc.common.base.BaseQueryRequest;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PileActivityPileActivityGoodsRequest extends BaseQueryRequest {


    private List<String> goodsInfoIds;


    private String pileActivityId;
}
