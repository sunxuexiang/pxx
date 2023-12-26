package com.wanmi.sbc.setting.api.response.gatherboxset;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatherBoxSetInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 凑箱规格
     */
    private Long skuNum;
}
