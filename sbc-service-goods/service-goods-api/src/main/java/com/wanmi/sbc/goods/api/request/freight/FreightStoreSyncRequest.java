package com.wanmi.sbc.goods.api.request.freight;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 根据单品运费模板id验证单品运费模板请求
 * Created by daiyitian on 2018/10/31.
 */
@ApiModel
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FreightStoreSyncRequest implements Serializable {

    private static final long serialVersionUID = 9109386834043212524L;

    /**
     * 源店铺标识
     */
    @ApiModelProperty(value = "源店铺标识")
    private Long sourceStoreId;

    /**
     * 目标店铺标识
     */
    @ApiModelProperty(value = "目标店铺标识")
    private List<Long> targetStoreIdList;

    @ApiModelProperty(value = "业务类型")
    private Integer destinationType;

    @ApiModelProperty(value = "仓库ID")
    private Long wareId;
}
