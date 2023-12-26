package com.wanmi.sbc.setting.api.response.region;

import lombok.*;

import java.util.List;

/**
 * 描述
 *
 * @author yitang
 * @version 1.0
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionQueryResponse {

    List<Long> number;
}
