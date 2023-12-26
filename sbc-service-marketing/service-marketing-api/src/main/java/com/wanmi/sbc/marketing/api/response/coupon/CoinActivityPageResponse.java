package com.wanmi.sbc.marketing.api.response.coupon;

import com.wanmi.sbc.common.base.MicroServicePage;
import com.wanmi.sbc.marketing.bean.vo.CoinActivityVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author Administrator
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinActivityPageResponse implements Serializable {

    private static final long serialVersionUID = 2380002784243643198L;

    @ApiModelProperty(value = "活动信息分页列表")
    private MicroServicePage<CoinActivityVO> coinActivityVOPage;
}
