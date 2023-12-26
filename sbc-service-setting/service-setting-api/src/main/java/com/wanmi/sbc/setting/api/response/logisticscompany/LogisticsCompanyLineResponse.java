package com.wanmi.sbc.setting.api.response.logisticscompany;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wanmi.sbc.common.enums.DeleteFlag;
import com.wanmi.sbc.common.util.CustomLocalDateTimeDeserializer;
import com.wanmi.sbc.common.util.CustomLocalDateTimeSerializer;
import com.wanmi.sbc.setting.bean.vo.LogisticsCompanyLineVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @desc  物流线路
 * @author shiy  2023/11/7 9:44
*/
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticsCompanyLineResponse implements Serializable {
    private LogisticsCompanyLineVO logisticsCompanyLineVO;
}
