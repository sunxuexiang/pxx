package com.wanmi.sbc.es.elastic.response.message;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class IMHistoryChatPageResponse {

    private List<String> groupIdList;

    private Integer total;
}
