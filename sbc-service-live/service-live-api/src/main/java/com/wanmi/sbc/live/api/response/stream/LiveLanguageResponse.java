package com.wanmi.sbc.live.api.response.stream;

import com.wanmi.sbc.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;

@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveLanguageResponse implements Serializable {

    private Long languageId;

    private String message;

}
